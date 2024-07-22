package com.example.housecleaner02.model;

import android.os.Environment;

public enum DeviceModel {

    House_01,
    House_02,

    House_03,
    House_04,

    House_01_02,
    House_03_04,
    House_Stop;

    // tty,ttyFIQ0,ttyS0,ttyS1,ttyS5,ttyS8,ttyS9
    private static final String DeviceSerialPort = "/dev/ttyS9";
//    private static final String DeviceLogFilePath = Environment.getExternalStorageDirectory().getPath()+"/Documents/cleaner";

    private static final String DeviceLogFilePath = "/cleaner";

    public static String getDeviceSerialPort(){return DeviceSerialPort;}
    public static String getDeviceLogFilePath(){return DeviceLogFilePath;}
}
