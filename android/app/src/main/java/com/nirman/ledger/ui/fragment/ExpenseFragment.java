package com.nirman.ledger.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nirman.ledger.R;
import com.nirman.ledger.api.ApiService;
import com.nirman.ledger.api.RetrofitClient;
import com.nirman.ledger.model.ExpenseResponse;
import com.nirman.ledger.util.SessionManager;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseFragment extends Fragment {

    private RecyclerView rvExpenses;
    private ProgressBar progressBar;
    private TextView tvEmptyState;
    private FloatingActionButton fabAddExpense;
    private SessionManager sessionManager;
    private List<ExpenseResponse> expenseList = new ArrayList<>();
    private ExpenseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expense, container, false);

        sessionManager = new SessionManager(requireContext());

        rvExpenses = view.findViewById(R.id.rv_expenses);
        progressBar = view.findViewById(R.id.progress_bar);
        tvEmptyState = view.findViewById(R.id.tv_empty_state);
        fabAddExpense = view.findViewById(R.id.fab_add_expense);

        if (!sessionManager.isContractor()) {
            fabAddExpense.setVisibility(View.GONE);
        }

        rvExpenses.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ExpenseAdapter();
        rvExpenses.setAdapter(adapter);

        fabAddExpense.setOnClickListener(v -> {
            Toast.makeText(requireContext(), "Use the API to log expenses via Swagger or extend this dialog.", Toast.LENGTH_LONG).show();
        });

        loadExpenses();
        return view;
    }

    private void loadExpenses() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) {
            tvEmptyState.setVisibility(View.VISIBLE);
            tvEmptyState.setText("Please select a site first.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getApi(requireContext());
        api.getExpenses(siteId).enqueue(new Callback<List<ExpenseResponse>>() {
            @Override
            public void onResponse(Call<List<ExpenseResponse>> call, Response<List<ExpenseResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    expenseList.clear();
                    expenseList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    if (expenseList.isEmpty()) {
                        tvEmptyState.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyState.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<ExpenseResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load expenses", Toast.LENGTH_SHORT).show();
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
    private class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_expense, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ExpenseResponse exp = expenseList.get(position);
            holder.tvCategory.setText(exp.getCategory());
            holder.tvDesc.setText(exp.getDescription() != null ? exp.getDescription() : "-");
            holder.tvDate.setText(exp.getDate());
            holder.tvAmount.setText("₹ " + formatCurrency(exp.getAmount()));

            // Load receipt thumbnail via Glide if available
            if (exp.getReceiptUrl() != null && !exp.getReceiptUrl().isEmpty()) {
                Glide.with(holder.itemView.getContext())
                        .load(exp.getReceiptUrl())
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .centerCrop()
                        .into(holder.ivReceipt);
            } else {
                holder.ivReceipt.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        @Override
        public int getItemCount() {
            return expenseList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvCategory, tvDesc, tvDate, tvAmount;
            ImageView ivReceipt;

            VH(View itemView) {
                super(itemView);
                tvCategory = itemView.findViewById(R.id.tv_expense_category);
                tvDesc = itemView.findViewById(R.id.tv_expense_desc);
                tvDate = itemView.findViewById(R.id.tv_expense_date);
                tvAmount = itemView.findViewById(R.id.tv_expense_amount);
                ivReceipt = itemView.findViewById(R.id.iv_receipt_thumbnail);
            }
        }
    }
}
