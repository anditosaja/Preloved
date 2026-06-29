package com.example.preloved;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.preloved.models.ApiResponse;
import com.example.preloved.models.ChartItem;
import com.example.preloved.network.ApiService;
import com.example.preloved.network.RetrofitClient;
import com.example.preloved.utils.SessionManager;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDetailGrafikActivity extends AppCompatActivity {

    private LineChart lineChart;
    private TextView tvJudulGrafik;
    private String tipeMetrik;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_detail_grafik);

        sessionManager = new SessionManager(this);
        lineChart = findViewById(R.id.lineChart);
        tvJudulGrafik = findViewById(R.id.tvJudulGrafik);

        // Ambil data yang dikirim saat kartu diklik
        tipeMetrik = getIntent().getStringExtra("TIPE_METRIK");
        String judul = getIntent().getStringExtra("JUDUL_GRAFIK");

        if (judul != null) tvJudulGrafik.setText(judul);

        findViewById(R.id.btnBackGrafik).setOnClickListener(v -> finish());

        setupChart();
        loadChartData();
    }

    private void setupChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.getAxisRight().setEnabled(false); // Matikan angka di kanan (biar rapi)

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
    }

    private void loadChartData() {
        ApiService apiService = RetrofitClient.getClient().create(ApiService.class);
        String bearerToken = "Bearer " + sessionManager.getAdminToken();

        apiService.getAdminChartData(bearerToken, tipeMetrik).enqueue(new Callback<ApiResponse<List<ChartItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<ChartItem>>> call, Response<ApiResponse<List<ChartItem>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    renderGrafik(response.body().getData());
                } else {
                    // MUNCULIN KODE ERRORNYA BIAR KITA TAHU!
                    Toast.makeText(AdminDetailGrafikActivity.this, "Gagal kode: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<List<ChartItem>>> call, Throwable t) {
                Toast.makeText(AdminDetailGrafikActivity.this, "Koneksi terputus", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void renderGrafik(List<ChartItem> dataList) {
        ArrayList<Entry> entries = new ArrayList<>();
        ArrayList<String> tanggalLabels = new ArrayList<>();

        for (int i = 0; i < dataList.size(); i++) {
            ChartItem item = dataList.get(i);
            entries.add(new Entry(i, item.getJumlah()));

            // Ambil tanggal/bulannya saja biar muat di sumbu X (contoh: dari 2026-06-25 jadi 06-25)
            String tglSingkat = item.getTanggal().substring(5);
            tanggalLabels.add(tglSingkat);
        }

        LineDataSet dataSet = new LineDataSet(entries, "Perkembangan 7 Hari Terakhir");
        dataSet.setColor(android.graphics.Color.parseColor("#6952D9")); // Warna ungu Preloved
        dataSet.setCircleColor(android.graphics.Color.parseColor("#6952D9"));
        dataSet.setLineWidth(3f);
        dataSet.setCircleRadius(5f);
        dataSet.setValueTextSize(12f);

        // Mode garis melengkung yang cantik
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Pasang label tanggal di sumbu X
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(tanggalLabels));
        lineChart.invalidate(); // Refresh chart
    }
}
