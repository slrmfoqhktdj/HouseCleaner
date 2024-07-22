package com.example.housecleaner02.activity;



import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.housecleaner02.R;
import com.example.housecleaner02.model.DeviceModel;


public class HouseTHDialog extends AlertDialog{


    private DeviceModel name;
//    private int AirCompressor_RunningTime, Pump_RunningTime,House_Cleaning01_Time,House_Cleaning02_Time;

    private String Temp_1_1,Temp_1_2,Temp_2_1,Temp_2_2,Temp_3_1,Temp_3_2,Temp_4_1,Temp_4_2,Humi_1_1,Humi_1_2,Humi_2_1,Humi_2_2,Humi_3_1,Humi_3_2,Humi_4_1,Humi_4_2;

    private TextView Temp_1_1_tv,Temp_1_2_tv,Temp_2_1_tv,Temp_2_2_tv,Temp_3_1_tv,Temp_3_2_tv,Temp_4_1_tv,Temp_4_2_tv,Humi_1_1_tv,Humi_1_2_tv,Humi_2_1_tv,Humi_2_2_tv,Humi_3_1_tv,Humi_3_2_tv,Humi_4_1_tv,Humi_4_2_tv;
    private ImageView house_name;
    private Button negative_botton;


    public HouseTHDialog(Context context, DeviceModel name, float Temp_1_1, float Temp_1_2, float Temp_2_1, float Temp_2_2, float Temp_3_1, float Temp_3_2, float Temp_4_1, float Temp_4_2, float Humi_1_1, float Humi_1_2, float Humi_2_1, float Humi_2_2, float Humi_3_1, float Humi_3_2, float Humi_4_1, float Humi_4_2) {
        super(context, R.style.Dialog);// 스타일 지정
        this.name = name;
        this.Temp_1_1 = String.format(context.getString(R.string.Tempvalue),Temp_1_1);
        this.Temp_1_2 = String.format(context.getString(R.string.Tempvalue),Temp_1_2);
        this.Temp_2_1 = String.format(context.getString(R.string.Tempvalue),Temp_2_1);
        this.Temp_2_2 = String.format(context.getString(R.string.Tempvalue),Temp_2_2);
        this.Temp_3_1 = String.format(context.getString(R.string.Tempvalue),Temp_3_1);
        this.Temp_3_2 = String.format(context.getString(R.string.Tempvalue),Temp_3_2);
        this.Temp_4_1 = String.format(context.getString(R.string.Tempvalue),Temp_4_1);
        this.Temp_4_2 = String.format(context.getString(R.string.Tempvalue),Temp_4_2);
        this.Humi_1_1 = String.format(context.getString(R.string.Humivalue),Humi_1_1);
        this.Humi_1_2 = String.format(context.getString(R.string.Humivalue),Humi_1_2);
        this.Humi_2_1 = String.format(context.getString(R.string.Humivalue),Humi_2_1);
        this.Humi_2_2 = String.format(context.getString(R.string.Humivalue),Humi_2_2);
        this.Humi_3_1 = String.format(context.getString(R.string.Humivalue),Humi_3_1);
        this.Humi_3_2 = String.format(context.getString(R.string.Humivalue),Humi_3_2);
        this.Humi_4_1 = String.format(context.getString(R.string.Humivalue),Humi_4_1);
        this.Humi_4_2 = String.format(context.getString(R.string.Humivalue),Humi_4_2);

    }




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Dialog","onCreate Dialog");
        setContentView(R.layout.activity_houseth_dialog);
        setCancelable(true);

        house_name = findViewById(R.id.housename_tv);


        Temp_1_1_tv = findViewById(R.id.Temp_1_1);
        Temp_1_2_tv = findViewById(R.id.Temp_1_2);
        Temp_2_1_tv = findViewById(R.id.Temp_2_1);
        Temp_2_2_tv = findViewById(R.id.Temp_2_2);
        Temp_3_1_tv = findViewById(R.id.Temp_3_1);
        Temp_3_2_tv = findViewById(R.id.Temp_3_2);
        Temp_4_1_tv = findViewById(R.id.Temp_4_1);
        Temp_4_2_tv = findViewById(R.id.Temp_4_2);

        Humi_1_1_tv = findViewById(R.id.Humi_1_1);
        Humi_1_2_tv = findViewById(R.id.Humi_1_2);
        Humi_2_1_tv = findViewById(R.id.Humi_2_1);
        Humi_2_2_tv = findViewById(R.id.Humi_2_2);
        Humi_3_1_tv = findViewById(R.id.Humi_3_1);
        Humi_3_2_tv = findViewById(R.id.Humi_3_2);
        Humi_4_1_tv = findViewById(R.id.Humi_4_1);
        Humi_4_2_tv = findViewById(R.id.Humi_4_2);




        Temp_1_1_tv.setText(Temp_1_1);
        Temp_1_2_tv.setText(Temp_1_2);
        Temp_2_1_tv.setText(Temp_2_1);
        Temp_2_2_tv.setText(Temp_2_2);
        Temp_3_1_tv.setText(Temp_3_1);
        Temp_3_2_tv.setText(Temp_3_2);
        Temp_4_1_tv.setText(Temp_4_1);
        Temp_4_2_tv.setText(Temp_4_2);

        Humi_1_1_tv.setText(Humi_1_1);
        Humi_1_2_tv.setText(Humi_1_2);
        Humi_2_1_tv.setText(Humi_2_1);
        Humi_2_2_tv.setText(Humi_2_2);
        Humi_3_1_tv.setText(Humi_3_1);
        Humi_3_2_tv.setText(Humi_3_2);
        Humi_4_1_tv.setText(Humi_4_1);
        Humi_4_2_tv.setText(Humi_4_2);


        negative_botton = findViewById(R.id.negative_botton);

        switch (name){
            case House_01:
                house_name.setImageResource(R.drawable.house_1);
                break;
            case House_02:
                house_name.setImageResource(R.drawable.house_2);
                break;
            case House_03:
                house_name.setImageResource(R.drawable.house_3);
                break;
            case House_04:
                house_name.setImageResource(R.drawable.house_4);
                break;
        }

        negative_botton.setOnClickListener(v -> {
            dismiss();
        });


    }





}
