package com.example.mqttdemo;

public interface IGetMessageCallBack {
    public void setMessage(String topic,String message);
}
