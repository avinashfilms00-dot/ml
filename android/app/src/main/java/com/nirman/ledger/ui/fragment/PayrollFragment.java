package com.nirman.ledger.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nirman.ledger.R;
import com.nirman.ledger.api.ApiService;
import com.nirman.ledger.api.RetrofitClient;
import com.nirman.ledger.model.PayrollResponse;
import com.nirman.ledger.util.SessionManager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PayrollFragment extends Fragment {

    private RecyclerView rvPayroll;
    private ProgressBar progressBar;
    private TextView tvCurrentWeek;
    private SessionManager sessionManager;
    private LocalDate currentDate;
    private List<PayrollResponse> payrollList = new ArrayList<>();
    private PayrollAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payroll, container, false);

        sessionManager = new SessionManager(requireContext());
        currentDate = LocalDate.now();

        rvPayroll = view.findViewById(R.id.rv_payroll);
        progressBar = view.findViewById(R.id.progress_bar);
        tvCurrentWeek = view.findViewById(R.id.tv_current_week);
        ImageButton btnPrev = view.findViewById(R.id.btn_prev_week);
        ImageButton btnNext = view.findViewById(R.id.btn_next_week);

        rvPayroll.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PayrollAdapter();
        rvPayroll.setAdapter(adapter);

        btnPrev.setOnClickListener(v -> {
            currentDate = currentDate.minusWeeks(1);
            refreshView();
        });

        btnNext.setOnClickListener(v -> {
            currentDate = currentDate.plusWeeks(1);
            refreshView();
        });

        refreshView();
        return view;
    }

    private void refreshView() {
        // Calculate Sunday-Saturday bounds for display
        int dayVal = currentDate.getDayOfWeek().getValue();
        LocalDate sunday = (dayVal == 7) ? currentDate : currentDate.minusDays(dayVal);
        LocalDate saturday = sunday.plusDays(6);

        String label = sunday.format(DateTimeFormatter.ofPattern("dd-MMM")) + " to " +
                saturday.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
        tvCurrentWeek.setText(label);

        loadPayroll();
    }

    private void loadPayroll() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) {
            Toast.makeText(requireContext(), "Please select a site first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getApi(requireContext());
        api.getWeeklyPayroll(siteId, currentDate.toString()).enqueue(new Callback<List<PayrollResponse>>() {
            @Override
            public void onResponse(Call<List<PayrollResponse>> call, Response<List<PayrollResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    payrollList.clear();
                    payrollList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<PayrollResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load payroll", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private String formatCurrency(BigDecimal value) {
        if (value == null) return "0";
        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "IN"));
        return nf.format(value);
    }

    // ---- Inner RecyclerView Adapter ----
    private class PayrollAdapter extends RecyclerView.Adapter<PayrollAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_payroll, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            PayrollResponse pr = payrollList.get(position);
            holder.tvName.setText(pr.getWorkerName());
            holder.tvDays.setText("Days: " + pr.getFullDays() + " Full, " + pr.getHalfDays() + " Half");
            holder.tvRate.setText("Rate: ₹ " + formatCurrency(pr.getDailyWage()));
            holder.tvAdvance.setText("Advance: ₹ " + formatCurrency(pr.getAdvanceDeducted()));
            holder.tvNet.setText("Net: ₹ " + formatCurrency(pr.getFinalAmount()));
            holder.tvStatus.setText(pr.getStatus());

            boolean isPaid = "PAID".equals(pr.getStatus());
            if (isPaid) {
                holder.tvStatus.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                holder.itemView.getContext().getColor(R.color.success)));
                holder.btnPay.setVisibility(View.GONE);
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.tvStatus.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                holder.itemView.getContext().getColor(R.color.warning)));
                if (sessionManager.isContractor()) {
                    holder.btnPay.setVisibility(View.VISIBLE);
                    holder.divider.setVisibility(View.VISIBLE);
                } else {
                    holder.btnPay.setVisibility(View.GONE);
                    holder.divider.setVisibility(View.GONE);
                }
            }

            holder.btnPay.setOnClickListener(v -> {
                holder.btnPay.setEnabled(false);
                ApiService api = RetrofitClient.getApi(requireContext());
                api.payPayroll(pr.getId()).enqueue(new Callback<PayrollResponse>() {
                    @Override
                    public void onResponse(Call<PayrollResponse> call, Response<PayrollResponse> response) {
                        holder.btnPay.setEnabled(true);
                        if (response.isSuccessful() && isAdded()) {
                            Toast.makeText(requireContext(), pr.getWorkerName() + " marked as PAID", Toast.LENGTH_SHORT).show();
                            loadPayroll(); // Refresh the list
                        }
                    }

                    @Override
                    public void onFailure(Call<PayrollResponse> call, Throwable t) {
                        holder.btnPay.setEnabled(true);
                    }
                });
            });
        }

        @Override
        public int getItemCount() {
            return payrollList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvDays, tvRate, tvAdvance, tvNet, tvStatus;
            Button btnPay;
            View divider;

            VH(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_pay_worker_name);
                tvDays = itemView.findViewById(R.id.tv_pay_days);
                tvRate = itemView.findViewById(R.id.tv_pay_rate);
                tvAdvance = itemView.findViewById(R.id.tv_pay_advance);
                tvNet = itemView.findViewById(R.id.tv_pay_net);
                tvStatus = itemView.findViewById(R.id.tv_pay_status);
                btnPay = itemView.findViewById(R.id.btn_pay_now);
                divider = itemView.findViewById(R.id.pay_divider);
            }
        }
    }
}
