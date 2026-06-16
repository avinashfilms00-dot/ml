package com.nirman.ledger.ui.fragment;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nirman.ledger.R;
import com.nirman.ledger.api.ApiService;
import com.nirman.ledger.api.RetrofitClient;
import com.nirman.ledger.util.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment {

    private EditText etDailyDate, etWeeklyDate;
    private Spinner spMonth, spYear;
    private Button btnDaily, btnWeekly, btnMonthly;
    private ProgressBar progressBar;
    private SessionManager sessionManager;

    private LocalDate selectedDailyDate;
    private LocalDate selectedWeeklyDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);

        sessionManager = new SessionManager(requireContext());

        etDailyDate = view.findViewById(R.id.et_daily_date);
        etWeeklyDate = view.findViewById(R.id.et_weekly_date);
        spMonth = view.findViewById(R.id.sp_monthly_month);
        spYear = view.findViewById(R.id.sp_monthly_year);
        btnDaily = view.findViewById(R.id.btn_generate_daily);
        btnWeekly = view.findViewById(R.id.btn_generate_weekly);
        btnMonthly = view.findViewById(R.id.btn_generate_monthly);
        progressBar = view.findViewById(R.id.progress_bar);

        selectedDailyDate = LocalDate.now();
        selectedWeeklyDate = LocalDate.now();
        etDailyDate.setText(selectedDailyDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
        etWeeklyDate.setText(selectedWeeklyDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));

        // Month spinner
        String[] months = {"January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonth.setAdapter(monthAdapter);
        spMonth.setSelection(LocalDate.now().getMonthValue() - 1);

        // Year spinner
        int currentYear = LocalDate.now().getYear();
        String[] years = new String[5];
        for (int i = 0; i < 5; i++) {
            years[i] = String.valueOf(currentYear - 2 + i);
        }
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(yearAdapter);
        spYear.setSelection(2); // current year

        // Date pickers
        etDailyDate.setOnClickListener(v -> showDatePicker(true));
        etWeeklyDate.setOnClickListener(v -> showDatePicker(false));

        // Generate buttons
        btnDaily.setOnClickListener(v -> generateDaily());
        btnWeekly.setOnClickListener(v -> generateWeekly());
        btnMonthly.setOnClickListener(v -> generateMonthly());

        return view;
    }

    private void showDatePicker(boolean isDaily) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (datePicker, year, month, day) -> {
            LocalDate picked = LocalDate.of(year, month + 1, day);
            if (isDaily) {
                selectedDailyDate = picked;
                etDailyDate.setText(picked.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            } else {
                selectedWeeklyDate = picked;
                etWeeklyDate.setText(picked.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")));
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void generateDaily() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) {
            Toast.makeText(requireContext(), "Select a site first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnDaily.setEnabled(false);
        ApiService api = RetrofitClient.getApi(requireContext());
        api.getDailyReport(siteId, selectedDailyDate.toString()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                progressBar.setVisibility(View.GONE);
                btnDaily.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    String pdfUrl = response.body().get("pdfUrl");
                    openOrSharePdf(pdfUrl);
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to generate report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnDaily.setEnabled(true);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void generateWeekly() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) {
            Toast.makeText(requireContext(), "Select a site first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnWeekly.setEnabled(false);
        ApiService api = RetrofitClient.getApi(requireContext());
        api.getWeeklyReport(siteId, selectedWeeklyDate.toString()).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                progressBar.setVisibility(View.GONE);
                btnWeekly.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    String pdfUrl = response.body().get("pdfUrl");
                    openOrSharePdf(pdfUrl);
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to generate report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnWeekly.setEnabled(true);
            }
        });
    }

    private void generateMonthly() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) {
            Toast.makeText(requireContext(), "Select a site first", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedMonth = spMonth.getSelectedItemPosition() + 1;
        int selectedYear = Integer.parseInt(spYear.getSelectedItem().toString());

        progressBar.setVisibility(View.VISIBLE);
        btnMonthly.setEnabled(false);
        ApiService api = RetrofitClient.getApi(requireContext());
        api.getMonthlyReport(siteId, selectedYear, selectedMonth).enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                progressBar.setVisibility(View.GONE);
                btnMonthly.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    String pdfUrl = response.body().get("pdfUrl");
                    openOrSharePdf(pdfUrl);
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to generate report", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnMonthly.setEnabled(true);
            }
        });
    }

    private void openOrSharePdf(String pdfUrl) {
        if (pdfUrl == null || pdfUrl.isEmpty()) {
            Toast.makeText(requireContext(), "No PDF URL returned", Toast.LENGTH_SHORT).show();
            return;
        }

        // If it's a relative path, prepend the base URL
        if (pdfUrl.startsWith("/")) {
            pdfUrl = "http://10.0.2.2:8080" + pdfUrl;
        }

        // Open in browser or allow sharing via WhatsApp
        Intent chooser = new Intent(Intent.ACTION_SEND);
        chooser.setType("text/plain");
        chooser.putExtra(Intent.EXTRA_TEXT, "Nirman Ledger Report: " + pdfUrl);
        chooser.putExtra(Intent.EXTRA_SUBJECT, "Construction Site Report");
        startActivity(Intent.createChooser(chooser, "Share Report PDF via"));
    }
}
