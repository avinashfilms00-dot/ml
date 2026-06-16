package com.nirman.ledger.service;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.nirman.ledger.model.*;
import com.nirman.ledger.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final SiteRepository siteRepository;
    private final SiteService siteService;
    private final AttendanceRepository attendanceRepository;
    private final ExpenseRepository expenseRepository;
    private final PayrollRepository payrollRepository;
    private final FirebaseStorageService storageService;

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, new Color(33, 37, 41));
    private static final Font SUBTITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(108, 117, 125));
    private static final Font HEADER_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Font BOLD_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(33, 37, 41));
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10, new Color(33, 37, 41));
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255); // Premium Blue
    private static final Color ALT_ROW_COLOR = new Color(248, 249, 250);

    public String generateDailyReport(Long siteId, LocalDate date, User user) {
        siteService.getSiteById(siteId, user);
        Site site = siteRepository.findById(siteId).orElseThrow();

        List<Attendance> attendances = attendanceRepository.findBySiteIdAndDate(siteId, date);
        List<Expense> expenses = expenseRepository.findBySiteIdAndDateBetween(siteId, date, date);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Header Section
        addDocumentHeader(document, "DAILY SITE REPORT", site, "Date: " + date.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        // Attendance Table
        document.add(new Paragraph("Labour Attendance Details", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable attTable = new PdfPTable(4);
        attTable.setWidthPercentage(100);
        attTable.setWidths(new float[]{3f, 2f, 2f, 3f});
        
        addTableHeaderCell(attTable, "Worker Name");
        addTableHeaderCell(attTable, "Skill");
        addTableHeaderCell(attTable, "Daily Wage");
        addTableHeaderCell(attTable, "Status");

        BigDecimal totalLabourCost = BigDecimal.ZERO;
        int rowIndex = 0;
        for (Attendance att : attendances) {
            Color rowColor = (rowIndex++ % 2 == 0) ? Color.WHITE : ALT_ROW_COLOR;
            addTableCell(attTable, att.getWorker().getName(), rowColor);
            addTableCell(attTable, att.getWorker().getSkill(), rowColor);
            addTableCell(attTable, "Rs. " + att.getWorker().getDailyWage(), rowColor);
            addTableCell(attTable, att.getStatus().name(), rowColor);

            BigDecimal wage = att.getWorker().getDailyWage();
            if (att.getStatus() == AttendanceStatus.PRESENT) {
                totalLabourCost = totalLabourCost.add(wage);
            } else if (att.getStatus() == AttendanceStatus.HALF_DAY) {
                totalLabourCost = totalLabourCost.add(wage.divide(new BigDecimal(2), BigDecimal.ROUND_HALF_UP));
            }
        }
        document.add(attTable);
        document.add(new Paragraph(" "));

        // Expenses Table
        document.add(new Paragraph("Expenses Details", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable expTable = new PdfPTable(3);
        expTable.setWidthPercentage(100);
        expTable.setWidths(new float[]{2f, 5f, 3f});

        addTableHeaderCell(expTable, "Category");
        addTableHeaderCell(expTable, "Description");
        addTableHeaderCell(expTable, "Amount");

        BigDecimal totalExpenses = BigDecimal.ZERO;
        rowIndex = 0;
        for (Expense exp : expenses) {
            Color rowColor = (rowIndex++ % 2 == 0) ? Color.WHITE : ALT_ROW_COLOR;
            addTableCell(expTable, exp.getCategory().name(), rowColor);
            addTableCell(expTable, exp.getDescription() != null ? exp.getDescription() : "-", rowColor);
            addTableCell(expTable, "Rs. " + exp.getAmount(), rowColor);
            totalExpenses = totalExpenses.add(exp.getAmount());
        }
        document.add(expTable);
        document.add(new Paragraph(" "));

        // Summary Section
        document.add(new Paragraph("Summary", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50);
        summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        addTableCell(summaryTable, "Total Labour Cost:", Color.WHITE);
        addTableCell(summaryTable, "Rs. " + totalLabourCost, Color.WHITE);
        addTableCell(summaryTable, "Total Material/Misc Expenses:", Color.WHITE);
        addTableCell(summaryTable, "Rs. " + totalExpenses, Color.WHITE);
        
        PdfPCell totalCellLabel = new PdfPCell(new Phrase("Daily Total Cost:", BOLD_FONT));
        totalCellLabel.setBackgroundColor(ALT_ROW_COLOR);
        summaryTable.addCell(totalCellLabel);

        PdfPCell totalCellValue = new PdfPCell(new Phrase("Rs. " + totalLabourCost.add(totalExpenses), BOLD_FONT));
        totalCellValue.setBackgroundColor(ALT_ROW_COLOR);
        summaryTable.addCell(totalCellValue);

        document.add(summaryTable);

        document.close();
        return storageService.uploadPdfReport(baos.toByteArray(), "Daily_Report_" + site.getSiteName().replace(" ", "_"));
    }

    public String generateWeeklyReport(Long siteId, LocalDate date, User user) {
        siteService.getSiteById(siteId, user);
        Site site = siteRepository.findById(siteId).orElseThrow();

        // Sunday-Saturday bounds
        int dayOfWeekVal = date.getDayOfWeek().getValue();
        LocalDate sunday = (dayOfWeekVal == 7) ? date : date.minusDays(dayOfWeekVal);
        LocalDate saturday = sunday.plusDays(6);

        List<Payroll> weeklyPayrolls = payrollRepository.findBySiteIdAndStartDateAndEndDate(siteId, sunday, saturday);
        List<Expense> expenses = expenseRepository.findBySiteIdAndDateBetween(siteId, sunday, saturday);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        addDocumentHeader(document, "WEEKLY SITE REPORT", site, 
                "Period: " + sunday.format(DateTimeFormatter.ofPattern("dd-MMM")) + " to " + saturday.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        // Payroll Summary Table
        document.add(new Paragraph("Weekly Payroll aggregates", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable payrollTable = new PdfPTable(6);
        payrollTable.setWidthPercentage(100);
        payrollTable.setWidths(new float[]{3f, 1.5f, 1.5f, 2f, 2f, 2f});

        addTableHeaderCell(payrollTable, "Worker Name");
        addTableHeaderCell(payrollTable, "Full Days");
        addTableHeaderCell(payrollTable, "Half Days");
        addTableHeaderCell(payrollTable, "Adv Deducted");
        addTableHeaderCell(payrollTable, "Net Payable");
        addTableHeaderCell(payrollTable, "Status");

        BigDecimal totalPayroll = BigDecimal.ZERO;
        int rowIndex = 0;
        for (Payroll pr : weeklyPayrolls) {
            Color rowColor = (rowIndex++ % 2 == 0) ? Color.WHITE : ALT_ROW_COLOR;
            addTableCell(payrollTable, pr.getWorker().getName(), rowColor);
            addTableCell(payrollTable, String.valueOf(pr.getFullDays()), rowColor);
            addTableCell(payrollTable, String.valueOf(pr.getHalfDays()), rowColor);
            addTableCell(payrollTable, "Rs. " + pr.getAdvanceDeducted(), rowColor);
            addTableCell(payrollTable, "Rs. " + pr.getFinalAmount(), rowColor);
            addTableCell(payrollTable, pr.getStatus().name(), rowColor);
            totalPayroll = totalPayroll.add(pr.getFinalAmount());
        }
        document.add(payrollTable);
        document.add(new Paragraph(" "));

        // Expense Summary Table
        document.add(new Paragraph("Weekly Expenses", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable expTable = new PdfPTable(4);
        expTable.setWidthPercentage(100);
        expTable.setWidths(new float[]{2f, 2f, 4f, 2f});

        addTableHeaderCell(expTable, "Date");
        addTableHeaderCell(expTable, "Category");
        addTableHeaderCell(expTable, "Description");
        addTableHeaderCell(expTable, "Amount");

        BigDecimal totalExpenses = BigDecimal.ZERO;
        rowIndex = 0;
        for (Expense exp : expenses) {
            Color rowColor = (rowIndex++ % 2 == 0) ? Color.WHITE : ALT_ROW_COLOR;
            addTableCell(expTable, exp.getDate().format(DateTimeFormatter.ofPattern("dd-MMM")), rowColor);
            addTableCell(expTable, exp.getCategory().name(), rowColor);
            addTableCell(expTable, exp.getDescription() != null ? exp.getDescription() : "-", rowColor);
            addTableCell(expTable, "Rs. " + exp.getAmount(), rowColor);
            totalExpenses = totalExpenses.add(exp.getAmount());
        }
        document.add(expTable);
        document.add(new Paragraph(" "));

        // Summary Panel
        document.add(new Paragraph("Summary", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50);
        summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        addTableCell(summaryTable, "Total Weekly Payroll Cost:", Color.WHITE);
        addTableCell(summaryTable, "Rs. " + totalPayroll, Color.WHITE);
        addTableCell(summaryTable, "Total Weekly Expenses:", Color.WHITE);
        addTableCell(summaryTable, "Rs. " + totalExpenses, Color.WHITE);

        PdfPCell totalCellLabel = new PdfPCell(new Phrase("Weekly Combined Cost:", BOLD_FONT));
        totalCellLabel.setBackgroundColor(ALT_ROW_COLOR);
        summaryTable.addCell(totalCellLabel);

        PdfPCell totalCellValue = new PdfPCell(new Phrase("Rs. " + totalPayroll.add(totalExpenses), BOLD_FONT));
        totalCellValue.setBackgroundColor(ALT_ROW_COLOR);
        summaryTable.addCell(totalCellValue);

        document.add(summaryTable);

        document.close();
        return storageService.uploadPdfReport(baos.toByteArray(), "Weekly_Report_" + site.getSiteName().replace(" ", "_"));
    }

    public String generateMonthlyReport(Long siteId, int year, int month, User user) {
        siteService.getSiteById(siteId, user);
        Site site = siteRepository.findById(siteId).orElseThrow();

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        // Get payrolls starting in this month
        List<Payroll> monthlyPayrolls = payrollRepository.findBySiteId(siteId).stream()
                .filter(p -> !p.getStartDate().isBefore(start) && !p.getStartDate().isAfter(end))
                .collect(Collectors.toList());

        List<Expense> expenses = expenseRepository.findBySiteIdAndDateBetween(siteId, start, end);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        addDocumentHeader(document, "MONTHLY SITE REPORT", site, 
                "Month: " + start.format(DateTimeFormatter.ofPattern("MMMM-yyyy")));

        // Monthly Payroll aggregates
        document.add(new Paragraph("Weekly Payroll Aggregates in Month", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable prTable = new PdfPTable(5);
        prTable.setWidthPercentage(100);
        prTable.setWidths(new float[]{3f, 3f, 2f, 2f, 2f});

        addTableHeaderCell(prTable, "Worker Name");
        addTableHeaderCell(prTable, "Period");
        addTableHeaderCell(prTable, "Days Worked");
        addTableHeaderCell(prTable, "Wage Rate");
        addTableHeaderCell(prTable, "Net Paid");

        BigDecimal totalPayroll = BigDecimal.ZERO;
        int rowIndex = 0;
        for (Payroll pr : monthlyPayrolls) {
            Color rowColor = (rowIndex++ % 2 == 0) ? Color.WHITE : ALT_ROW_COLOR;
            addTableCell(prTable, pr.getWorker().getName(), rowColor);
            addTableCell(prTable, pr.getStartDate().format(DateTimeFormatter.ofPattern("dd/MM")) + " to " + pr.getEndDate().format(DateTimeFormatter.ofPattern("dd/MM")), rowColor);
            addTableCell(prTable, (pr.getFullDays() + (pr.getHalfDays() * 0.5)) + " days", rowColor);
            addTableCell(prTable, "Rs. " + pr.getDailyWage(), rowColor);
            addTableCell(prTable, "Rs. " + pr.getFinalAmount(), rowColor);
            totalPayroll = totalPayroll.add(pr.getFinalAmount());
        }
        document.add(prTable);
        document.add(new Paragraph(" "));

        // Expenses Summary Table
        document.add(new Paragraph("Monthly Materials/Misc Expenses", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable expTable = new PdfPTable(4);
        expTable.setWidthPercentage(100);
        expTable.setWidths(new float[]{2f, 2f, 4f, 2f});

        addTableHeaderCell(expTable, "Date");
        addTableHeaderCell(expTable, "Category");
        addTableHeaderCell(expTable, "Description");
        addTableHeaderCell(expTable, "Amount");

        BigDecimal totalExpenses = BigDecimal.ZERO;
        rowIndex = 0;
        for (Expense exp : expenses) {
            Color rowColor = (rowIndex++ % 2 == 0) ? Color.WHITE : ALT_ROW_COLOR;
            addTableCell(expTable, exp.getDate().format(DateTimeFormatter.ofPattern("dd-MMM")), rowColor);
            addTableCell(expTable, exp.getCategory().name(), rowColor);
            addTableCell(expTable, exp.getDescription() != null ? exp.getDescription() : "-", rowColor);
            addTableCell(expTable, "Rs. " + exp.getAmount(), rowColor);
            totalExpenses = totalExpenses.add(exp.getAmount());
        }
        document.add(expTable);
        document.add(new Paragraph(" "));

        // Summary
        document.add(new Paragraph("Monthly Summary", BOLD_FONT));
        document.add(new Paragraph(" "));
        PdfPTable summaryTable = new PdfPTable(2);
        summaryTable.setWidthPercentage(50);
        summaryTable.setHorizontalAlignment(Element.ALIGN_LEFT);

        addTableCell(summaryTable, "Total Labour Cost:", Color.WHITE);
        addTableCell(summaryTable, "Rs. " + totalPayroll, Color.WHITE);
        addTableCell(summaryTable, "Total Expenses:", Color.WHITE);
        addTableCell(summaryTable, "Rs. " + totalExpenses, Color.WHITE);

        PdfPCell totalCellLabel = new PdfPCell(new Phrase("Monthly Combined Cost:", BOLD_FONT));
        totalCellLabel.setBackgroundColor(ALT_ROW_COLOR);
        summaryTable.addCell(totalCellLabel);

        PdfPCell totalCellValue = new PdfPCell(new Phrase("Rs. " + totalPayroll.add(totalExpenses), BOLD_FONT));
        totalCellValue.setBackgroundColor(ALT_ROW_COLOR);
        summaryTable.addCell(totalCellValue);

        document.add(summaryTable);

        document.close();
        return storageService.uploadPdfReport(baos.toByteArray(), "Monthly_Report_" + site.getSiteName().replace(" ", "_"));
    }

    private void addDocumentHeader(Document document, String title, Site site, String dateString) {
        Paragraph titleParagraph = new Paragraph(title, TITLE_FONT);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        document.add(titleParagraph);

        Paragraph subtitle = new Paragraph("NIRMAN LEDGER SYSTEM", SUBTITLE_FONT);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        document.add(subtitle);
        document.add(new Paragraph(" "));

        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.getDefaultCell().setBorder(PdfPCell.NO_BORDER);

        PdfPCell cellLeft = new PdfPCell();
        cellLeft.setBorder(PdfPCell.NO_BORDER);
        cellLeft.addElement(new Paragraph("Site Name: " + site.getSiteName(), BOLD_FONT));
        cellLeft.addElement(new Paragraph("Address: " + site.getAddress(), NORMAL_FONT));
        headerTable.addCell(cellLeft);

        PdfPCell cellRight = new PdfPCell();
        cellRight.setBorder(PdfPCell.NO_BORDER);
        cellRight.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cellRight.addElement(new Paragraph("Owner: " + site.getOwnerName() + " (" + site.getOwnerMobile() + ")", NORMAL_FONT));
        cellRight.addElement(new Paragraph(dateString, BOLD_FONT));
        headerTable.addCell(cellRight);

        document.add(headerTable);
        document.add(new Paragraph("______________________________________________________________________________", NORMAL_FONT));
        document.add(new Paragraph(" "));
    }

    private void addTableHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, HEADER_FONT));
        cell.setBackgroundColor(PRIMARY_COLOR);
        cell.setPadding(6);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, Color backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL_FONT));
        cell.setBackgroundColor(backgroundColor);
        cell.setPadding(6);
        table.addCell(cell);
    }
}
