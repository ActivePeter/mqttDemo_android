package com.example.mqttdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.multidex.MultiDex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements IGetMessageCallBack{

    private MyServiceConnection serviceConnection;
    private MqttService mqttService;
    TextView humiText;
    TextView tempText;
    TextView lightText;
    TextView connectText;
    Switch fanSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        humiText=findViewById(R.id.mihoText);
        tempText=findViewById(R.id.tempText);
        lightText=findViewById(R.id.lightText);
        fanSwitch=findViewById(R.id.fanSwitch);
        connectText=findViewById(R.id.connectText);

        fanSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    MqttService.publish("fan","1");
                    //选中时 do some thing
                } else {
                    MqttService.publish("fan","0");
                    //非选中时 do some thing
                }
            }
        });

        serviceConnection = new MyServiceConnection();
        serviceConnection.setIGetMessageCallBack(MainActivity.this);

        Intent intent = new Intent(this, MqttService.class);

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override//重写接口的回调函数
    public void setMessage(String topic,String message) {
        mqttService = serviceConnection.getMqttService();
        mqttService.toCreateNotification(message);
        if(topic.equals("connect")){
            if(message.equals("0")){
                connectText.setText("未连接到服务器");
            }else{
                connectText.setText("已连接到服务器");
            }
        }
        if(topic.equals("temp")){
            tempText.setText("温度："+message);
        }
        if(topic.equals("light")){
            lightText.setText("光照："+message);
        }
        if(topic.equals("humi")){
            humiText.setText("湿度："+message);
        }
        if(topic.equals("fan")){
            if(message.equals("0")){
                fanSwitch.setChecked(false);
            }else{
                fanSwitch.setChecked(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
