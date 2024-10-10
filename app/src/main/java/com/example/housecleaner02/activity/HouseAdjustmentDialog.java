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

import androidx.core.content.ContextCompat;

import com.example.housecleaner02.R;
import com.example.housecleaner02.callback.HouseSettingDialogCallback;
import com.example.housecleaner02.model.DeviceModel;
import com.example.housecleaner02.serialport.SerialPortManager;
import com.example.housecleaner02.sharedpreferences.KeyControl;

import java.util.ArrayList;


public class HouseAdjustmentDialog extends AlertDialog{


    private DeviceModel name;
    private ImageView house_name;
    private TextView nozzle_1_name,nozzle_2_name,nozzle_3_name,nozzle_4_name,nozzle_5_name,nozzle_6_name,nozzle_7_name,nozzle_8_name,nozzle_9_name,nozzle_10_name,nozzle_11_name,nozzle_12_name,nozzle_13_name,nozzle_14_name,nozzle_15_name,nozzle_16_name;
    private ImageButton nozzle_1_back_btn,nozzle_1_stop_btn,nozzle_1_go_btn;
    private ImageButton nozzle_2_back_btn,nozzle_2_stop_btn,nozzle_2_go_btn;
    private ImageButton nozzle_3_back_btn,nozzle_3_stop_btn,nozzle_3_go_btn;
    private ImageButton nozzle_4_back_btn,nozzle_4_stop_btn,nozzle_4_go_btn;
    private ImageButton nozzle_5_back_btn,nozzle_5_stop_btn,nozzle_5_go_btn;
    private ImageButton nozzle_6_back_btn,nozzle_6_stop_btn,nozzle_6_go_btn;
    private ImageButton nozzle_7_back_btn,nozzle_7_stop_btn,nozzle_7_go_btn;
    private ImageButton nozzle_8_back_btn,nozzle_8_stop_btn,nozzle_8_go_btn;
    private ImageButton nozzle_9_back_btn,nozzle_9_stop_btn,nozzle_9_go_btn;
    private ImageButton nozzle_10_back_btn,nozzle_10_stop_btn,nozzle_10_go_btn;
    private ImageButton nozzle_11_back_btn,nozzle_11_stop_btn,nozzle_11_go_btn;
    private ImageButton nozzle_12_back_btn,nozzle_12_stop_btn,nozzle_12_go_btn;
    private ImageButton nozzle_13_back_btn,nozzle_13_stop_btn,nozzle_13_go_btn;
    private ImageButton nozzle_14_back_btn,nozzle_14_stop_btn,nozzle_14_go_btn;
    private ImageButton nozzle_15_back_btn,nozzle_15_stop_btn,nozzle_15_go_btn;
    private ImageButton nozzle_16_back_btn,nozzle_16_stop_btn,nozzle_16_go_btn;
    private Button positive_botton;

    private byte house_step;
    
    
    public HouseAdjustmentDialog(Context context, DeviceModel name) {
        super(context, R.style.Dialog);// 스타일 지정

        this.name = name;

    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Dialog","onCreate Dialog");
        setContentView(R.layout.activity_house_adjustment_dialog);
        setCancelable(true);

        house_name = findViewById(R.id.housename_tv);

        nozzle_1_name = findViewById(R.id.nozzle_1_name);
        nozzle_2_name = findViewById(R.id.nozzle_2_name);
        nozzle_3_name = findViewById(R.id.nozzle_3_name);
        nozzle_4_name = findViewById(R.id.nozzle_4_name);
        nozzle_5_name = findViewById(R.id.nozzle_5_name);
        nozzle_6_name = findViewById(R.id.nozzle_6_name);
        nozzle_7_name = findViewById(R.id.nozzle_7_name);
        nozzle_8_name = findViewById(R.id.nozzle_8_name);
        nozzle_9_name = findViewById(R.id.nozzle_9_name);
        nozzle_10_name = findViewById(R.id.nozzle_10_name);
        nozzle_11_name = findViewById(R.id.nozzle_11_name);
        nozzle_12_name = findViewById(R.id.nozzle_12_name);
        nozzle_13_name = findViewById(R.id.nozzle_13_name);
        nozzle_14_name = findViewById(R.id.nozzle_14_name);
        nozzle_15_name = findViewById(R.id.nozzle_15_name);
        nozzle_16_name = findViewById(R.id.nozzle_16_name);

        nozzle_1_back_btn = findViewById(R.id.nozzle_1_back_btn);
        nozzle_1_stop_btn = findViewById(R.id.nozzle_1_stop_btn);
        nozzle_1_go_btn = findViewById(R.id.nozzle_1_go_btn);
        nozzle_2_back_btn = findViewById(R.id.nozzle_2_back_btn);
        nozzle_2_stop_btn = findViewById(R.id.nozzle_2_stop_btn);
        nozzle_2_go_btn = findViewById(R.id.nozzle_2_go_btn);
        nozzle_3_back_btn = findViewById(R.id.nozzle_3_back_btn);
        nozzle_3_stop_btn = findViewById(R.id.nozzle_3_stop_btn);
        nozzle_3_go_btn = findViewById(R.id.nozzle_3_go_btn);
        nozzle_4_back_btn = findViewById(R.id.nozzle_4_back_btn);
        nozzle_4_stop_btn = findViewById(R.id.nozzle_4_stop_btn);
        nozzle_4_go_btn = findViewById(R.id.nozzle_4_go_btn);
        nozzle_5_back_btn = findViewById(R.id.nozzle_5_back_btn);
        nozzle_5_stop_btn = findViewById(R.id.nozzle_5_stop_btn);
        nozzle_5_go_btn = findViewById(R.id.nozzle_5_go_btn);
        nozzle_6_back_btn = findViewById(R.id.nozzle_6_back_btn);
        nozzle_6_stop_btn = findViewById(R.id.nozzle_6_stop_btn);
        nozzle_6_go_btn = findViewById(R.id.nozzle_6_go_btn);
        nozzle_7_back_btn = findViewById(R.id.nozzle_7_back_btn);
        nozzle_7_stop_btn = findViewById(R.id.nozzle_7_stop_btn);
        nozzle_7_go_btn = findViewById(R.id.nozzle_7_go_btn);
        nozzle_8_back_btn = findViewById(R.id.nozzle_8_back_btn);
        nozzle_8_stop_btn = findViewById(R.id.nozzle_8_stop_btn);
        nozzle_8_go_btn = findViewById(R.id.nozzle_8_go_btn);
        nozzle_9_back_btn = findViewById(R.id.nozzle_9_back_btn);
        nozzle_9_stop_btn = findViewById(R.id.nozzle_9_stop_btn);
        nozzle_9_go_btn = findViewById(R.id.nozzle_9_go_btn);
        nozzle_10_back_btn = findViewById(R.id.nozzle_10_back_btn);
        nozzle_10_stop_btn = findViewById(R.id.nozzle_10_stop_btn);
        nozzle_10_go_btn = findViewById(R.id.nozzle_10_go_btn);
        nozzle_11_back_btn = findViewById(R.id.nozzle_11_back_btn);
        nozzle_11_stop_btn = findViewById(R.id.nozzle_11_stop_btn);
        nozzle_11_go_btn = findViewById(R.id.nozzle_11_go_btn);
        nozzle_12_back_btn = findViewById(R.id.nozzle_12_back_btn);
        nozzle_12_stop_btn = findViewById(R.id.nozzle_12_stop_btn);
        nozzle_12_go_btn = findViewById(R.id.nozzle_12_go_btn);
        nozzle_13_back_btn = findViewById(R.id.nozzle_13_back_btn);
        nozzle_13_stop_btn = findViewById(R.id.nozzle_13_stop_btn);
        nozzle_13_go_btn = findViewById(R.id.nozzle_13_go_btn);
        nozzle_14_back_btn = findViewById(R.id.nozzle_14_back_btn);
        nozzle_14_stop_btn = findViewById(R.id.nozzle_14_stop_btn);
        nozzle_14_go_btn = findViewById(R.id.nozzle_14_go_btn);
        nozzle_15_back_btn = findViewById(R.id.nozzle_15_back_btn);
        nozzle_15_stop_btn = findViewById(R.id.nozzle_15_stop_btn);
        nozzle_15_go_btn = findViewById(R.id.nozzle_15_go_btn);
        nozzle_16_back_btn = findViewById(R.id.nozzle_16_back_btn);
        nozzle_16_stop_btn = findViewById(R.id.nozzle_16_stop_btn);
        nozzle_16_go_btn = findViewById(R.id.nozzle_16_go_btn);
        
        positive_botton = findViewById(R.id.positive_botton);
      


        switch (name){
            case House_01:
                house_name.setImageResource(R.drawable.house_1);
                house_step = 0x57;
                break;
            case House_02:
                house_name.setImageResource(R.drawable.house_2);
                house_step = 0x58;
                break;
            case House_03:
                house_name.setImageResource(R.drawable.house_3);
                house_step = 0x59;
                break;
            case House_04:
                house_name.setImageResource(R.drawable.house_4);
                house_step = 0x5A;
                break;
        }






        nozzle_1_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x01, (byte) 0x06);});
        nozzle_1_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x01, (byte) 0x08);});
        nozzle_1_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x01, (byte) 0x07);});
        nozzle_2_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte) 0x02, (byte) 0x06);});
        nozzle_2_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte) 0x02, (byte) 0x08);});
        nozzle_2_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x02, (byte) 0x07);});
        nozzle_3_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x03, (byte) 0x06);});
        nozzle_3_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x03, (byte) 0x08);});
        nozzle_3_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x03, (byte) 0x07);});
        nozzle_4_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x04, (byte) 0x06);});
        nozzle_4_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x04, (byte) 0x08);});
        nozzle_4_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x04, (byte) 0x07);});
        nozzle_5_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x05, (byte) 0x06);});
        nozzle_5_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x05, (byte) 0x08);});
        nozzle_5_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x05, (byte) 0x07);});
        nozzle_6_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x06, (byte) 0x06);});
        nozzle_6_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x06, (byte) 0x08);});
        nozzle_6_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x06, (byte) 0x07);});
        nozzle_7_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x07, (byte) 0x06);});
        nozzle_7_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x07, (byte) 0x08);});
        nozzle_7_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x07, (byte) 0x07);});
        nozzle_8_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x08, (byte) 0x06);});
        nozzle_8_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x08, (byte) 0x08);});
        nozzle_8_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x08, (byte) 0x07);});
        nozzle_9_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x09, (byte) 0x06);});
        nozzle_9_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x09, (byte) 0x08);});
        nozzle_9_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x09, (byte) 0x07);});
        nozzle_10_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0A, (byte) 0x06);});
        nozzle_10_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0A, (byte) 0x08);});
        nozzle_10_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0A, (byte) 0x07);});
        nozzle_11_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0B, (byte) 0x06);});
        nozzle_11_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0B, (byte) 0x08);});
        nozzle_11_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0B, (byte) 0x07);});
        nozzle_12_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0C, (byte) 0x06);});
        nozzle_12_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0C, (byte) 0x08);});
        nozzle_12_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0C, (byte) 0x07);});
        nozzle_13_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0D, (byte) 0x06);});
        nozzle_13_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0D, (byte) 0x08);});
        nozzle_13_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0D, (byte) 0x07);});
        nozzle_14_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0E, (byte) 0x06);});
        nozzle_14_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0E, (byte) 0x08);});
        nozzle_14_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0E, (byte) 0x07);});
        nozzle_15_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0F, (byte) 0x06);});
        nozzle_15_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0F, (byte) 0x08);});
        nozzle_15_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x0F, (byte) 0x07);});
        nozzle_16_back_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x10, (byte) 0x06);});
        nozzle_16_stop_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x10, (byte) 0x08);});
        nozzle_16_go_btn.setOnClickListener(v -> {SerialPortManager.getInstance().SendSerialCom_Adjustment(house_step, (byte)0x10, (byte) 0x07);});

        

        positive_botton.setOnClickListener(v -> {

            dismiss();
        });



    }





}
