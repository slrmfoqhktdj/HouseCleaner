package com.example.housecleaner02.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.housecleaner02.R;
import com.example.housecleaner02.callback.HouseSettingDialogCallback;
import com.example.housecleaner02.model.DeviceModel;

import java.util.ArrayList;


public class HouseSettingDialog extends AlertDialog{


    private DeviceModel name;
    private int AirCompressor_RunningTime, Pump_RunningTime,House_Cleaning01_Time,House_Cleaning02_Time;
    private ImageView house_name_01,house_name_02;
    private TextView AirCompressor_RunningTime_tv,Pump_RunningTime_tv,House_Cleaning01_Time_tv,House_Cleaning02_Time_tv;
    private ImageButton AirCompressor_RunningTime_up_btn,AirCompressor_RunningTime_down_btn,Pump_RunningTime_up_btn,Pump_RunningTime_down_btn,House_Cleaning01_Time_up_btn,House_Cleaning01_Time_down_btn,House_Cleaning02_Time_up_btn,House_Cleaning02_Time_down_btn;
    private Button positive_botton;
    private Button negative_botton;

    private int AirCompressor_RunningTime_Int;
    private int Pump_RunningTime_Int;

    private final ArrayList<HouseSettingDialogCallback> callbacksList = new ArrayList<>();

    public HouseSettingDialog(Context context,DeviceModel name, int AirCompressor_RunningTime, int Pump_RunningTime, int House_Cleaning01_Time, int House_Cleaning02_Time) {
        super(context, R.style.Dialog);// 스타일 지정

        this.name = name;
        this.AirCompressor_RunningTime = AirCompressor_RunningTime;
        this.Pump_RunningTime = Pump_RunningTime;
        this.House_Cleaning01_Time = House_Cleaning01_Time;
        this.House_Cleaning02_Time = House_Cleaning02_Time;

    }

    public HouseSettingDialog setHouseSettingDialogListener(HouseSettingDialogCallback callback){
        if (!callbacksList.contains(callback)) {
            callbacksList.add(callback);
        }
        return this;
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Dialog","onCreate Dialog");
        setContentView(R.layout.activity_housesetting_dialog);
        setCancelable(true);

        house_name_01 = findViewById(R.id.housename_01_tv);
        house_name_02 = findViewById(R.id.housename_02_tv);
        AirCompressor_RunningTime_tv= findViewById(R.id.AirCompressor_RunningTime_tv);
        Pump_RunningTime_tv = findViewById(R.id.Pump_RunningTime_tv);
        House_Cleaning01_Time_tv = findViewById(R.id.House_Cleaning01_Time_tv);
        House_Cleaning02_Time_tv = findViewById(R.id.House_Cleaning02_Time_tv);
        AirCompressor_RunningTime_up_btn = findViewById(R.id.AirCompressor_RunningTime_up_btn);
        AirCompressor_RunningTime_down_btn = findViewById(R.id.AirCompressor_RunningTime_down_btn);
        Pump_RunningTime_up_btn = findViewById(R.id.Pump_RunningTime_up_btn);
        Pump_RunningTime_down_btn = findViewById(R.id.Pump_RunningTime_down_btn);
        House_Cleaning01_Time_up_btn = findViewById(R.id.House_Cleaning01_Time_up_btn);
        House_Cleaning01_Time_down_btn = findViewById(R.id.House_Cleaning01_Time_down_btn);
        House_Cleaning02_Time_up_btn = findViewById(R.id.House_Cleaning02_Time_up_btn);
        House_Cleaning02_Time_down_btn = findViewById(R.id.House_Cleaning02_Time_down_btn);

        positive_botton = findViewById(R.id.positive_botton);
        negative_botton = findViewById(R.id.negative_botton);


        switch (name){
            case House_01_02:
                house_name_01.setImageResource(R.drawable.house_1);
                house_name_02.setImageResource(R.drawable.house_2);
                break;
            case House_03_04:
                house_name_01.setImageResource(R.drawable.house_3);
                house_name_02.setImageResource(R.drawable.house_4);
                break;
        }

        AirCompressor_RunningTime_tv.setText(String.valueOf(AirCompressor_RunningTime));
        Pump_RunningTime_tv.setText(String.valueOf(Pump_RunningTime));
        House_Cleaning01_Time_tv.setText(String.valueOf(House_Cleaning01_Time));
        House_Cleaning02_Time_tv.setText(String.valueOf(House_Cleaning02_Time));

        AirCompressor_RunningTime_Int = Integer.parseInt((String) AirCompressor_RunningTime_tv.getText());
        Pump_RunningTime_Int = Integer.parseInt((String) Pump_RunningTime_tv.getText());


        AirCompressor_RunningTime_up_btn.setOnClickListener(v -> {
            AirCompressor_RunningTime_Int = AirCompressor_RunningTime_Int+1;
            AirCompressor_RunningTime_tv.setText(String.valueOf(AirCompressor_RunningTime_Int));
        });

        AirCompressor_RunningTime_down_btn.setOnClickListener(v -> {
            if(AirCompressor_RunningTime_Int >0){

                if(AirCompressor_RunningTime_Int > Pump_RunningTime_Int){
                    AirCompressor_RunningTime_Int = AirCompressor_RunningTime_Int-1;
                    AirCompressor_RunningTime_tv.setText(String.valueOf(AirCompressor_RunningTime_Int));
                }else{
                    Toast.makeText(getContext(),"펌프동작시간 이하의 시간을 설정 할 수 없습니다.",Toast.LENGTH_LONG).show();
                }

            }
        });

        Pump_RunningTime_up_btn.setOnClickListener(v -> {

            if(AirCompressor_RunningTime_Int > Pump_RunningTime_Int){
                Pump_RunningTime_Int = Pump_RunningTime_Int+1;
                Pump_RunningTime_tv.setText(String.valueOf(Pump_RunningTime_Int));
            }else{
                Toast.makeText(getContext(),"콤프동작시간 이상의 시간을 설정 할 수 없습니다.",Toast.LENGTH_LONG).show();
            }


        });


        Pump_RunningTime_down_btn.setOnClickListener(v -> {
            if(Pump_RunningTime_Int >0){
                Pump_RunningTime_Int = Pump_RunningTime_Int-1;
                Pump_RunningTime_tv.setText(String.valueOf(Pump_RunningTime_Int));
            }
        });

        House_Cleaning01_Time_up_btn.setOnClickListener(v -> {
            House_Cleaning01_Time_tv.setText(String.valueOf(Integer.parseInt((String) House_Cleaning01_Time_tv.getText())+1));
        });

        House_Cleaning01_Time_down_btn.setOnClickListener(v -> {
            int Time = Integer.parseInt((String) House_Cleaning01_Time_tv.getText());
            if(Time >0){
                House_Cleaning01_Time_tv.setText(String.valueOf(Time-1));
            }
        });

        House_Cleaning02_Time_up_btn.setOnClickListener(v -> {
            House_Cleaning02_Time_tv.setText(String.valueOf(Integer.parseInt((String) House_Cleaning02_Time_tv.getText())+1));
        });

        House_Cleaning02_Time_down_btn.setOnClickListener(v -> {
            int Time = Integer.parseInt((String) House_Cleaning02_Time_tv.getText());
            if(Time >0){
                House_Cleaning02_Time_tv.setText(String.valueOf(Time-1));
            }
        });

        positive_botton.setOnClickListener(v -> {





            for (int i = 0; i < callbacksList.size(); i++) {
                HouseSettingDialogCallback callbackListener = callbacksList.get(i);
                if (null != callbackListener) {
                    callbackListener.onPositiveClick(Integer.parseInt((String)AirCompressor_RunningTime_tv.getText()),Integer.parseInt((String)Pump_RunningTime_tv.getText()),Integer.parseInt((String)House_Cleaning01_Time_tv.getText()),Integer.parseInt((String)House_Cleaning02_Time_tv.getText()));
                    callbacksList.clear();
                }
            }

            dismiss();
        });

        negative_botton.setOnClickListener(v -> {
            dismiss();
        });


    }





}
