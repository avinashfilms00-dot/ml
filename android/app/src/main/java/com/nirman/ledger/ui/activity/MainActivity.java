package com.nirman.ledger.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.nirman.ledger.R;
import com.nirman.ledger.ui.fragment.AttendanceFragment;
import com.nirman.ledger.ui.fragment.DashboardFragment;
import com.nirman.ledger.ui.fragment.ExpenseFragment;
import com.nirman.ledger.ui.fragment.PayrollFragment;
import com.nirman.ledger.ui.fragment.ReportFragment;
import com.nirman.ledger.util.SessionManager;

public class MainActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        updateToolbarTitle();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(this::onNavItemSelected);

        // Load dashboard by default
        if (savedInstanceState == null) {
            loadFragment(new DashboardFragment());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateToolbarTitle();
    }

    private void updateToolbarTitle() {
        if (getSupportActionBar() != null) {
            String siteName = sessionManager.getSelectedSiteName();
            getSupportActionBar().setTitle(siteName);
            getSupportActionBar().setSubtitle("Nirman Ledger");
        }
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.menu_dashboard) {
            fragment = new DashboardFragment();
        } else if (itemId == R.id.menu_attendance) {
            fragment = new AttendanceFragment();
        } else if (itemId == R.id.menu_payroll) {
            fragment = new PayrollFragment();
        } else if (itemId == R.id.menu_expenses) {
            fragment = new ExpenseFragment();
        } else if (itemId == R.id.menu_reports) {
            fragment = new ReportFragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
            return true;
        }
        return false;
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_content, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Profile icon in the toolbar
        menu.add(0, 1001, 0, "Profile")
                .setIcon(android.R.drawable.ic_menu_myplaces)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1001) {
            loadFragment(new com.nirman.ledger.ui.fragment.ProfileFragment());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
