package com.nirman.ledger.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nirman.ledger.R;
import com.nirman.ledger.api.ApiService;
import com.nirman.ledger.api.RetrofitClient;
import com.nirman.ledger.model.SiteResponse;
import com.nirman.ledger.ui.activity.LoginActivity;
import com.nirman.ledger.util.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvRole, tvEmail, tvMobile;
    private Button btnSwitchSite, btnLogout;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        sessionManager = new SessionManager(requireContext());

        tvUsername = view.findViewById(R.id.tv_profile_username);
        tvRole = view.findViewById(R.id.tv_profile_role);
        tvEmail = view.findViewById(R.id.tv_profile_email);
        tvMobile = view.findViewById(R.id.tv_profile_mobile);
        btnSwitchSite = view.findViewById(R.id.btn_switch_site);
        btnLogout = view.findViewById(R.id.btn_logout);

        // Populate user info
        tvUsername.setText(sessionManager.getUsername());
        String role = sessionManager.getRole();
        tvRole.setText(role.replace("ROLE_", ""));
        tvEmail.setText("Email: " + sessionManager.getEmail());
        tvMobile.setText("Mobile: " + sessionManager.getMobile());

        btnSwitchSite.setOnClickListener(v -> showSiteSelectionDialog());
        btnLogout.setOnClickListener(v -> logout());

        return view;
    }

    private void showSiteSelectionDialog() {
        ApiService api = RetrofitClient.getApi(requireContext());
        api.getSites().enqueue(new Callback<List<SiteResponse>>() {
            @Override
            public void onResponse(Call<List<SiteResponse>> call, Response<List<SiteResponse>> response) {
                if (response.isSuccessful() && response.body() != null && isAdded()) {
                    List<SiteResponse> sites = response.body();
                    if (sites.isEmpty()) {
                        Toast.makeText(requireContext(), "No sites found. Create one first.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String[] siteNames = new String[sites.size()];
                    for (int i = 0; i < sites.size(); i++) {
                        siteNames[i] = sites.get(i).getSiteName();
                    }

                    new AlertDialog.Builder(requireContext())
                            .setTitle("Select Active Site")
                            .setItems(siteNames, (dialog, which) -> {
                                SiteResponse selected = sites.get(which);
                                sessionManager.setSelectedSite(selected.getId(), selected.getSiteName());
                                Toast.makeText(requireContext(),
                                        "Switched to: " + selected.getSiteName(), Toast.LENGTH_SHORT).show();

                                // Refresh the main activity toolbar
                                if (getActivity() != null) {
                                    getActivity().recreate();
                                }
                            })
                            .show();
                }
            }

            @Override
            public void onFailure(Call<List<SiteResponse>> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Failed to load sites", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (dialog, which) -> {
                    sessionManager.clearSession();
                    RetrofitClient.reset();
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
