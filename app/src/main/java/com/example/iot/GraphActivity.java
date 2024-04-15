package com.example.iot;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent intent = getIntent();

        // Nhận dữ liệu từ Intent
        double[] sensor1Data = intent.getDoubleArrayExtra("sensor1Data");
        double[] sensor2Data = intent.getDoubleArrayExtra("sensor2Data");

        // Tiến hành vẽ biểu đồ với dữ liệu đã nhận được từ hai cảm biến
        drawGraph(sensor1Data, sensor2Data);
    }
    private void drawGraph(double[] sensor1Data, double[] sensor2Data) {
        // Khởi tạo biểu đồ
        GraphView graph = findViewById(R.id.graph);

        // Tạo series cho cảm biến 1
        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>();
        for (int i = 0; i < sensor1Data.length; i++) {
            series1.appendData(new DataPoint(i, sensor1Data[i]), true, sensor1Data.length);
        }
        // Đặt màu sắc cho series 1
        series1.setColor(Color.BLUE);

        // Tạo series cho cảm biến 2
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>();
        for (int i = 0; i < sensor2Data.length; i++) {
            series2.appendData(new DataPoint(i, sensor2Data[i]), true, sensor2Data.length);
        }
        // Đặt màu sắc cho series 2
        series2.setColor(Color.RED);

        // Đặt tên cho series
        series1.setTitle("Cảm biến 1");
        series2.setTitle("Cảm biến 2");

        // Thêm series vào biểu đồ
        graph.addSeries(series1);
        graph.addSeries(series2);

        // Đặt tiêu đề cho trục x và y
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(100);

        // Hiển thị legend
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }
}
