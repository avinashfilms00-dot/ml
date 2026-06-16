package com.nirman.ledger.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nirman.ledger.R;
import com.nirman.ledger.api.ApiService;
import com.nirman.ledger.api.RetrofitClient;
import com.nirman.ledger.model.AttendanceResponse;
import com.nirman.ledger.model.WorkerResponse;
import com.nirman.ledger.util.SessionManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttendanceFragment extends Fragment {

    private RecyclerView rvAttendance;
    private ProgressBar progressBar;
    private TextView tvCurrentDate;
    private FloatingActionButton fabSave;
    private SessionManager sessionManager;
    private LocalDate currentDate;
    private List<WorkerResponse> workerList = new ArrayList<>();
    private Map<Long, String> attendanceStatuses = new HashMap<>(); // workerId -> status
    private AttendanceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_attendance, container, false);

        sessionManager = new SessionManager(requireContext());
        currentDate = LocalDate.now();

        rvAttendance = view.findViewById(R.id.rv_attendance);
        progressBar = view.findViewById(R.id.progress_bar);
        tvCurrentDate = view.findViewById(R.id.tv_current_date);
        fabSave = view.findViewById(R.id.fab_save_attendance);
        ImageButton btnPrev = view.findViewById(R.id.btn_prev_date);
        ImageButton btnNext = view.findViewById(R.id.btn_next_date);

        // Hide save button for owners
        if (!sessionManager.isContractor()) {
            fabSave.setVisibility(View.GONE);
        }

        rvAttendance.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new AttendanceAdapter();
        rvAttendance.setAdapter(adapter);

        btnPrev.setOnClickListener(v -> {
            currentDate = currentDate.minusDays(1);
            refreshView();
        });

        btnNext.setOnClickListener(v -> {
            currentDate = currentDate.plusDays(1);
            refreshView();
        });

        fabSave.setOnClickListener(v -> saveAttendance());

        refreshView();
        return view;
    }

    private void refreshView() {
        tvCurrentDate.setText(currentDate.format(DateTimeFormatter.ofPattern("EEEE, dd-MMM-yyyy")));
        loadWorkersAndAttendance();
    }

    private void loadWorkersAndAttendance() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) {
            Toast.makeText(requireContext(), "Please select a site first", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        ApiService api = RetrofitClient.getApi(requireContext());

        // First load workers, then overlay existing attendance
        api.getWorkers(siteId).enqueue(new Callback<List<WorkerResponse>>() {
            @Override
            public void onResponse(Call<List<WorkerResponse>> call, Response<List<WorkerResponse>> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    workerList.clear();
                    workerList.addAll(response.body());
                    attendanceStatuses.clear();

                    // Now load existing attendance for this date
                    String dateStr = currentDate.toString();
                    api.getAttendance(siteId, dateStr, null).enqueue(new Callback<List<AttendanceResponse>>() {
                        @Override
                        public void onResponse(Call<List<AttendanceResponse>> call2, Response<List<AttendanceResponse>> resp2) {
                            progressBar.setVisibility(View.GONE);
                            if (resp2.isSuccessful() && resp2.body() != null && isAdded()) {
                                for (AttendanceResponse att : resp2.body()) {
                                    attendanceStatuses.put(att.getWorkerId(), att.getStatus());
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onFailure(Call<List<AttendanceResponse>> call2, Throwable t) {
                            progressBar.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    });
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<List<WorkerResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load workers", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveAttendance() {
        Long siteId = sessionManager.getSelectedSiteId();
        if (siteId == null) return;

        List<Map<String, Object>> records = new ArrayList<>();
        for (WorkerResponse w : workerList) {
            String status = attendanceStatuses.get(w.getId());
            if (status != null) {
                Map<String, Object> rec = new HashMap<>();
                rec.put("workerId", w.getId());
                rec.put("status", status);
                records.add(rec);
            }
        }

        if (records.isEmpty()) {
            Toast.makeText(requireContext(), "Please mark attendance for at least one worker", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("siteId", siteId);
        body.put("date", currentDate.toString());
        body.put("records", records);

        fabSave.setEnabled(false);
        ApiService api = RetrofitClient.getApi(requireContext());
        api.markAttendance(body).enqueue(new Callback<List<AttendanceResponse>>() {
            @Override
            public void onResponse(Call<List<AttendanceResponse>> call, Response<List<AttendanceResponse>> response) {
                fabSave.setEnabled(true);
                if (response.isSuccessful() && isAdded()) {
                    Toast.makeText(requireContext(), "Attendance saved successfully!", Toast.LENGTH_SHORT).show();
                } else if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to save attendance", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AttendanceResponse>> call, Throwable t) {
                fabSave.setEnabled(true);
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // ---- Inner RecyclerView Adapter ----
    private class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_attendance, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            WorkerResponse worker = workerList.get(position);
            holder.tvName.setText(worker.getName());
            holder.tvSkill.setText("Skill: " + worker.getSkill());

            // Clear listener before setting checked state to avoid infinite loops
            holder.rgAttendance.setOnCheckedChangeListener(null);

            String currentStatus = attendanceStatuses.get(worker.getId());
            if ("PRESENT".equals(currentStatus)) {
                holder.rbPresent.setChecked(true);
            } else if ("HALF_DAY".equals(currentStatus)) {
                holder.rbHalfDay.setChecked(true);
            } else if ("ABSENT".equals(currentStatus)) {
                holder.rbAbsent.setChecked(true);
            } else {
                holder.rgAttendance.clearCheck();
            }

            // Disable radio buttons for owners
            boolean isContractor = sessionManager.isContractor();
            holder.rbPresent.setEnabled(isContractor);
            holder.rbHalfDay.setEnabled(isContractor);
            holder.rbAbsent.setEnabled(isContractor);

            holder.rgAttendance.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rb_present) {
                    attendanceStatuses.put(worker.getId(), "PRESENT");
                } else if (checkedId == R.id.rb_half_day) {
                    attendanceStatuses.put(worker.getId(), "HALF_DAY");
                } else if (checkedId == R.id.rb_absent) {
                    attendanceStatuses.put(worker.getId(), "ABSENT");
                }
            });
        }

        @Override
        public int getItemCount() {
            return workerList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvSkill;
            RadioGroup rgAttendance;
            RadioButton rbPresent, rbHalfDay, rbAbsent;

            VH(View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_att_worker_name);
                tvSkill = itemView.findViewById(R.id.tv_att_worker_skill);
                rgAttendance = itemView.findViewById(R.id.rg_attendance);
                rbPresent = itemView.findViewById(R.id.rb_present);
                rbHalfDay = itemView.findViewById(R.id.rb_half_day);
                rbAbsent = itemView.findViewById(R.id.rb_absent);
            }
        }
    }
}
