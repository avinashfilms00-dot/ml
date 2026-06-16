package com.nirman.ledger.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.nirman.ledger.R;
import com.nirman.ledger.api.ApiService;
import com.nirman.ledger.api.RetrofitClient;
import com.nirman.ledger.model.DashboardResponse;
import com.nirman.ledger.util.SessionManager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private TextView tvSiteName, tvOwnerInfo;
    private TextView tvTotalWorkers, tvPresentToday, tvWeeklyPayroll, tvExpenses, tvPending;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        sessionManager = new SessionManager(requireContext());

        tvSiteName = view.findViewById(R.id.tv_dashboard_site_name);
        tvOwnerInfo = view.findViewById(R.id.tv_dashboard_owner_info);
        tvTotalWorkers = view.findViewById(R.id.tv_kpi_total_workers);
        tvPresentToday = view.findViewById(R.id.tv_kpi_present_today);
        tvWeeklyPayroll = view.findViewById(R.id.tv_kpi_weekly_payroll);
        tvExpenses = view.findViewById(R.id.tv_kpi_expenses);
        tvPending = view.findViewById(R.id.tv_kpi_pending_amount);

        tvSiteName.setText("Site: " + sessionManager.getSelectedSiteName());

        loadDashboard();
        return view;
    }

    private void loadDashboard() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) {
            tvOwnerInfo.setText("Please select a site from Profile.");
            return;
        }

        ApiService api = RetrofitClient.getApi(requireContext());
        api.getDashboard(siteId).enqueue(new Callback<DashboardResponse>() {
            @Override
            public void onResponse(Call<DashboardResponse> call, Response<DashboardResponse> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    DashboardResponse d = response.body();
                    tvTotalWorkers.setText(String.valueOf(d.getTotalWorkers()));
                    tvPresentToday.setText(String.valueOf(d.getPresentToday()));
                    tvWeeklyPayroll.setText("₹ " + formatCurrency(d.getWeeklyPayrollCost()));
                    tvExpenses.setText("₹ " + formatCurrency(d.getTotalExpenses()));
                    tvPending.setText("₹ " + formatCurrency(d.getPendingAmount()));
                }
            }

            @Override
            public void onFailure(Call<DashboardResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load dashboard", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) return "0";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        return nf.format(value);
    }
}
