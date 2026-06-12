"""Report Generator for Admin Analytics"""
import os
from io import BytesIO
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Table, TableStyle, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet
from reportlab.lib import colors
import openpyxl

def generate_pdf_report(scans_data):
    buffer = BytesIO()
    doc = SimpleDocTemplate(buffer, pagesize=letter)
    elements = []
    
    styles = getSampleStyleSheet()
    elements.append(Paragraph("AgriScan AI - Scans Report", styles['Title']))
    elements.append(Spacer(1, 20))
    
    data = [['ID', 'User ID', 'Crop', 'Disease', 'Confidence', 'Date']]
    for scan in scans_data:
        data.append([
            str(scan['id']),
            str(scan['user_id']),
            scan['crop_name'] or 'Unknown',
            scan['disease_name'] or 'Unknown',
            f"{scan['confidence']:.2f}" if scan['confidence'] else 'N/A',
            scan['scan_date'][:10] if scan['scan_date'] else 'N/A'
        ])
        
    table = Table(data)
    table.setStyle(TableStyle([
        ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
        ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
        ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
        ('FONTNAME', (0, 0), (-1, 0), 'Helvetica-Bold'),
        ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
        ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
        ('GRID', (0, 0), (-1, -1), 1, colors.black),
    ]))
    
    elements.append(table)
    doc.build(elements)
    
    buffer.seek(0)
    return buffer

def generate_excel_report(scans_data):
    wb = openpyxl.Workbook()
    ws = wb.active
    ws.title = "Scans Report"
    
    headers = ['ID', 'User ID', 'Crop', 'Disease', 'Confidence', 'Is Healthy', 'Severity', 'Date']
    ws.append(headers)
    
    for scan in scans_data:
        ws.append([
            scan['id'],
            scan['user_id'],
            scan['crop_name'],
            scan['disease_name'],
            float(scan['confidence']) if scan['confidence'] else None,
            'Yes' if scan['is_healthy'] else 'No',
            scan['severity'],
            scan['scan_date']
        ])
        
    buffer = BytesIO()
    wb.save(buffer)
    buffer.seek(0)
    return buffer
