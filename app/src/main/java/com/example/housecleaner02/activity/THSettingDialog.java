package com.example.housecleaner02.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.housecleaner02.R;
import com.example.housecleaner02.callback.THSettingDialogCallback;
import com.example.housecleaner02.model.DeviceModel;

import java.util.ArrayList;

public class THSettingDialog extends AlertDialog {
    

    private int TH_Min,TH_Max;

    private TextView TH_Min_tv,TH_Max_tv;
    private ImageButton Min_up_btn,Min_down_btn,Max_up_btn,Max_down_btn;
    private Button positive_botton;
    private Button negative_botton;

    private final ArrayList<THSettingDialogCallback> callbacksList = new ArrayList<>();

    public THSettingDialog(Context context, int th_min, int th_max) {
        super(context, R.style.Dialog);// 스타일 지정
        
        this.TH_Min = th_min;
        this.TH_Max = th_max;
    }

    public THSettingDialog setTHSettingDialogListener(THSettingDialogCallback callback){
        if (!callbacksList.contains(callback)) {
            callbacksList.add(callback);
        }
        return this;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Dialog", "onCreate Dialog");
        setContentView(R.layout.activity_th_setting_dialog);
        setCancelable(true);

        
        TH_Min_tv = findViewById(R.id.Min_number);
        TH_Max_tv = findViewById(R.id.Max_number);
        
        Min_up_btn = findViewById(R.id.Min_up_btn);
        Min_down_btn = findViewById(R.id.Min_down_btn);
        Max_up_btn = findViewById(R.id.Max_up_btn);
        Max_down_btn = findViewById(R.id.Max_down_btn);
    
        positive_botton = findViewById(R.id.positive_botton);
        negative_botton = findViewById(R.id.negative_botton);

        

        TH_Min_tv.setText(String.valueOf(TH_Min));
        TH_Max_tv.setText(String.valueOf(TH_Max));


        

        Min_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) TH_Min_tv.getText())<Integer.parseInt((String) TH_Max_tv.getText())-1){
                TH_Min_tv.setText(String.valueOf(Integer.parseInt((String) TH_Min_tv.getText())+1));
            }
        });

        Min_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) TH_Min_tv.getText()) >0){
                TH_Min_tv.setText(String.valueOf(Integer.parseInt((String) TH_Min_tv.getText())-1));
            }
        });

        Max_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) TH_Max_tv.getText())<50){
                TH_Max_tv.setText(String.valueOf(Integer.parseInt((String) TH_Max_tv.getText())+1));
            }
        });

        Max_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) TH_Max_tv.getText()) > Integer.parseInt((String) TH_Min_tv.getText())+1){
                TH_Max_tv.setText(String.valueOf(Integer.parseInt((String) TH_Max_tv.getText())-1));
            }
        });

        

        positive_botton.setOnClickListener(v -> {

            for (int i = 0; i < callbacksList.size(); i++) {
                THSettingDialogCallback callbackListener = callbacksList.get(i);
                if (null != callbackListener) {
                    callbackListener.onPositiveClick(Integer.parseInt((String)TH_Min_tv.getText()),Integer.parseInt((String)TH_Max_tv.getText()));
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
