package com.nirman.ledger.api;

import com.nirman.ledger.model.AttendanceResponse;
import com.nirman.ledger.model.AuthResponse;
import com.nirman.ledger.model.DashboardResponse;
import com.nirman.ledger.model.ExpenseResponse;
import com.nirman.ledger.model.PayrollResponse;
import com.nirman.ledger.model.SiteResponse;
import com.nirman.ledger.model.WorkerResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ---- AUTH ----
    @POST("auth/register")
    Call<AuthResponse> register(@Body Map<String, Object> body);

    @POST("auth/login")
    Call<AuthResponse> login(@Body Map<String, String> body);

    // ---- SITES ----
    @GET("sites")
    Call<List<SiteResponse>> getSites();

    @GET("sites/{id}")
    Call<SiteResponse> getSite(@Path("id") Long id);

    @POST("sites")
    Call<SiteResponse> createSite(@Body Map<String, Object> body);

    @PUT("sites/{id}")
    Call<SiteResponse> updateSite(@Path("id") Long id, @Body Map<String, Object> body);

    @DELETE("sites/{id}")
    Call<Void> deleteSite(@Path("id") Long id);

    // ---- WORKERS ----
    @GET("workers")
    Call<List<WorkerResponse>> getWorkers(@Query("siteId") Long siteId);

    @POST("workers")
    Call<WorkerResponse> createWorker(@Body Map<String, Object> body);

    @PUT("workers/{id}")
    Call<WorkerResponse> updateWorker(@Path("id") Long id, @Body Map<String, Object> body);

    @DELETE("workers/{id}")
    Call<Void> deleteWorker(@Path("id") Long id);

    // ---- ATTENDANCE ----
    @POST("attendance")
    Call<List<AttendanceResponse>> markAttendance(@Body Map<String, Object> body);

    @GET("attendance")
    Call<List<AttendanceResponse>> getAttendance(
            @Query("siteId") Long siteId,
            @Query("date") String date,
            @Query("endDate") String endDate
    );

    // ---- ADVANCE ----
    @POST("advance")
    Call<Map<String, Object>> createAdvance(@Body Map<String, Object> body);

    @GET("advance")
    Call<List<Map<String, Object>>> getAdvances(@Query("siteId") Long siteId);

    // ---- PAYROLL ----
    @GET("payroll/weekly")
    Call<List<PayrollResponse>> getWeeklyPayroll(
            @Query("siteId") Long siteId,
            @Query("date") String date
    );

    @POST("payroll/pay")
    Call<PayrollResponse> payPayroll(@Query("id") Long id);

    // ---- EXPENSES ----
    @Multipart
    @POST("expenses")
    Call<ExpenseResponse> createExpense(
            @Part("siteId") RequestBody siteId,
            @Part("category") RequestBody category,
            @Part("amount") RequestBody amount,
            @Part("date") RequestBody date,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part receipt
    );

    @GET("expenses")
    Call<List<ExpenseResponse>> getExpenses(@Query("siteId") Long siteId);

    // ---- DASHBOARD ----
    @GET("dashboard")
    Call<DashboardResponse> getDashboard(@Query("siteId") Long siteId);

    // ---- REPORTS ----
    @GET("reports/daily")
    Call<Map<String, String>> getDailyReport(
            @Query("siteId") Long siteId,
            @Query("date") String date
    );

    @GET("reports/weekly")
    Call<Map<String, String>> getWeeklyReport(
            @Query("siteId") Long siteId,
            @Query("date") String date
    );

    @GET("reports/monthly")
    Call<Map<String, String>> getMonthlyReport(
            @Query("siteId") Long siteId,
            @Query("year") int year,
            @Query("month") int month
    );
}
