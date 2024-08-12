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
    



    private ImageButton Main_up_btn,Main_down_btn,Deviation_up_btn,Deviation_down_btn;
    private TextView th_1_step,th_2_step,th_3_step,th_4_step,th_5_step,th_deviation_tv;

    private Button positive_botton , negative_botton;

    private int Deviation = 0;
    private int th_main = 0;

    private final ArrayList<THSettingDialogCallback> callbacksList = new ArrayList<>();

    public THSettingDialog(Context context, int Deviation, int th_main) {
        super(context, R.style.Dialog);// 스타일 지정
        
        this.Deviation = Deviation;
        this.th_main = th_main;
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


        th_1_step = findViewById(R.id.th_1_step);
        th_2_step = findViewById(R.id.th_2_step);
        th_3_step = findViewById(R.id.th_3_step);
        th_4_step = findViewById(R.id.th_4_step);
        th_5_step = findViewById(R.id.th_5_step);
        th_deviation_tv = findViewById(R.id.th_deviation_tv);

        
        Main_up_btn = findViewById(R.id.Main_up_btn);
        Main_down_btn = findViewById(R.id.Main_down_btn);


        Deviation_up_btn = findViewById(R.id.Deviation_up_btn);
        Deviation_down_btn = findViewById(R.id.Deviation_down_btn);
    
        positive_botton = findViewById(R.id.positive_botton);
        negative_botton = findViewById(R.id.negative_botton);


        th_1_step.setText(String.valueOf(th_main-(Deviation*2)));
        th_2_step.setText(String.valueOf(th_main-(Deviation)));
        th_3_step.setText(String.valueOf(th_main));
        th_4_step.setText(String.valueOf(th_main+(Deviation)));
        th_5_step.setText(String.valueOf(th_main+(Deviation*2)));

        th_deviation_tv.setText(String.valueOf(Deviation));



        Main_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) th_5_step.getText())<50 && th_main<50){
                th_main = th_main+1;
                th_1_step.setText(String.valueOf(Integer.parseInt((String) th_1_step.getText())+1));
                th_2_step.setText(String.valueOf(Integer.parseInt((String) th_2_step.getText())+1));
                th_3_step.setText(String.valueOf(Integer.parseInt((String) th_3_step.getText())+1));
                th_4_step.setText(String.valueOf(Integer.parseInt((String) th_4_step.getText())+1));
                th_5_step.setText(String.valueOf(Integer.parseInt((String) th_5_step.getText())+1));
            }
        });

        Main_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) th_1_step.getText()) >0 && th_main>0){
                th_main = th_main-1;
                th_1_step.setText(String.valueOf(Integer.parseInt((String) th_1_step.getText())-1));
                th_2_step.setText(String.valueOf(Integer.parseInt((String) th_2_step.getText())-1));
                th_3_step.setText(String.valueOf(Integer.parseInt((String) th_3_step.getText())-1));
                th_4_step.setText(String.valueOf(Integer.parseInt((String) th_4_step.getText())-1));
                th_5_step.setText(String.valueOf(Integer.parseInt((String) th_5_step.getText())-1));
            }
        });

        Deviation_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) th_3_step.getText())+(Deviation*4)<50 && Deviation<10){
                Deviation = Deviation+1;
                th_deviation_tv.setText(String.valueOf(Deviation));
                th_1_step.setText(String.valueOf(th_main-(Deviation*4)));
                th_2_step.setText(String.valueOf(th_main-(Deviation*2)));
                th_4_step.setText(String.valueOf(th_main+(Deviation*2)));
                th_5_step.setText(String.valueOf(th_main+(Deviation*4)));
            }
        });

        Deviation_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) th_3_step.getText())-(Deviation*4) > 0 && Deviation>1){
                Deviation = Deviation-1;
                th_deviation_tv.setText(String.valueOf(Deviation));
                th_1_step.setText(String.valueOf(th_main-(Deviation*4)));
                th_2_step.setText(String.valueOf(th_main-(Deviation*2)));
                th_4_step.setText(String.valueOf(th_main+(Deviation*2)));
                th_5_step.setText(String.valueOf(th_main+(Deviation*4)));
            }
        });



        positive_botton.setOnClickListener(v -> {

            for (int i = 0; i < callbacksList.size(); i++) {
                THSettingDialogCallback callbackListener = callbacksList.get(i);
                if (null != callbackListener) {
                    callbackListener.onPositiveClick(Deviation,th_main);
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
