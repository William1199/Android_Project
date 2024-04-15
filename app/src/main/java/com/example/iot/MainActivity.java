package com.example.iot;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    MQTTHelper mqttHelper;
    TextView txtTemp, txtHumi;
    LabeledSwitch btn1, btn2, btn3;
    double[] sensor1Data; // Lưu trữ dữ liệu từ cảm biến 1
    double[] sensor2Data; // Lưu trữ dữ liệu từ cảm biến 2

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtTemp = findViewById(R.id.txtTemperature);
        txtHumi = findViewById(R.id.txtHumidity);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);

        btn1.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn) {
                    sendDataMQTT("mse14-group6/feeds/relay02","1");
                } else {
                    sendDataMQTT("mse14-group6/feeds/relay02","0");
                }
            }
        });

        btn2.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn) {
                    sendDataMQTT("mse14-group6/feeds/relay03","1");
                } else {
                    sendDataMQTT("mse14-group6/feeds/relay03","0");
                }
            }
        });

        btn3.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if(isOn) {
                    sendDataMQTT("mse14-group6/feeds/relay04","1");
                } else {
                    sendDataMQTT("mse14-group6/feeds/relay04","0");
                }
            }
        });

        startMQTT();
    }

    public void sendDataMQTT(String topic, String value) {
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(false);
        byte[] b = value.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);

        try {
            mqttHelper.mqttAndroidClient.publish(topic, msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void startMQTT() {
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + "***" + message.toString());
                if (topic.contains("mse14-group6/feeds/temperature")) {
                    double temperature = Double.parseDouble(message.toString());
                    txtTemp.setText(temperature + "℃");
                    if (temperature > 27) {
                        showNotification("Temperature Alert", "Temperature is above 35°C");
                    }
                } else if (topic.contains("mse14-group6/feeds/moisture")) {
                    txtHumi.setText(message.toString() + "%");
                } else if (topic.contains("mse14-group6/feeds/relay02")) {
                    if (message.toString().equals("1")) {
                        btn1.setOn(true);
                    } else {
                        btn1.setOn(false);
                    }
                } else if (topic.contains("mse14-group6/feeds/relay03")) {
                    if (message.toString().equals("1")) {
                        btn2.setOn(true);
                    } else {
                        btn2.setOn(false);
                    }
                } else if (topic.contains("mse14-group6/feeds/relay04")) {
                    if (message.toString().equals("1")) {
                        btn3.setOn(true);
                    } else {
                        btn3.setOn(false);
                    }
                }
                processSensorData(topic, message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
    private void processSensorData(String topic, MqttMessage message) {
        // Xử lý và trích xuất dữ liệu từ message của cảm biến 1 và cảm biến 2
        // Gán dữ liệu vào biến sensor1Data và sensor2Data
        // Ví dụ:
        if (topic.contains("mse14-group6/feeds/temperature")) {
            double temperature = Double.parseDouble(message.toString());
            // Gán dữ liệu từ cảm biến 1 vào biến sensor1Data
            sensor1Data = new double[] {temperature};
        } else if (topic.contains("mse14-group6/feeds/moisture")) {
            double moisture = Double.parseDouble(message.toString());
            // Gán dữ liệu từ cảm biến 2 vào biến sensor2Data
            sensor2Data = new double[] {moisture};
        }
    }

    private void showNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_iot,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuMain:
                Toast.makeText(this, "Main",Toast.LENGTH_LONG).show();
                break;
            case R.id.menuChart:
                Intent intent = new Intent(this, GraphActivity.class);
                startActivity(intent);
                Toast.makeText(this, "Chart",Toast.LENGTH_LONG).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
