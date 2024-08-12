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
import com.example.housecleaner02.callback.HouseNozzleDialogCallback;
import com.example.housecleaner02.model.DeviceModel;

import java.util.ArrayList;

public class HouseNozzleDialog extends AlertDialog {

    private DeviceModel name;
    private boolean switch_bool;

    private int Nozzle01 = -1,Nozzle02 = -1,Nozzle03 = -1,Nozzle04 = -1,Nozzle05 = -1,Nozzle06 = -1;
    private Switch Nozzle_sw;
    private ImageView house_name;
    private TextView Nozzle01_tv,Nozzle02_tv,Nozzle03_tv,Nozzle04_tv,Nozzle05_tv,Nozzle06_tv;
    private ImageButton Nozzle01_up_btn,Nozzle01_down_btn,Nozzle02_up_btn,Nozzle02_down_btn,Nozzle03_up_btn,Nozzle03_down_btn,Nozzle04_up_btn,Nozzle04_down_btn,Nozzle05_up_btn,Nozzle05_down_btn,Nozzle06_up_btn,Nozzle06_down_btn;
    private Button positive_botton;
    private Button negative_botton;

    private int max_level = 5;

    private final ArrayList<HouseNozzleDialogCallback> callbacksList = new ArrayList<>();

    public HouseNozzleDialog(Context context, DeviceModel name, boolean switch_bool, int nozzle01, int nozzle02, int nozzle03, int nozzle04, int nozzle05, int nozzle06) {
        super(context, R.style.Dialog);// 스타일 지정

        this.name = name;
        this.switch_bool = switch_bool;
        if(nozzle01 != 150){
            this.Nozzle01 = nozzle01;    
        }
        if(nozzle02 != 150) {
            this.Nozzle02 = nozzle02;
        }
        if(nozzle03 != 150) {
            this.Nozzle03 = nozzle03;
        }
        if(nozzle04 != 150) {
            this.Nozzle04 = nozzle04;
        }
        if(nozzle05 != 150) {
            this.Nozzle05 = nozzle05;
        }
        if(nozzle06 != 150) {
            this.Nozzle06 = nozzle06;
        }
    }

    public HouseNozzleDialog setHouseNozzleDialogListener(HouseNozzleDialogCallback callback){
        if (!callbacksList.contains(callback)) {
            callbacksList.add(callback);
        }
        return this;
    }

    public void initSettingNozzle(){
        if(Nozzle01 == -1){
            Nozzle01_tv.setText("N");
            Nozzle01_up_btn.setEnabled(false);
            Nozzle01_down_btn.setEnabled(false);
            Nozzle01_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle01_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle02 == -1){
            Nozzle02_tv.setText("N");
            Nozzle02_up_btn.setEnabled(false);
            Nozzle02_down_btn.setEnabled(false);
            Nozzle02_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle02_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle03 == -1){
            Nozzle03_tv.setText("N");
            Nozzle03_up_btn.setEnabled(false);
            Nozzle03_down_btn.setEnabled(false);
            Nozzle03_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle03_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle04 == -1){
            Nozzle04_tv.setText("N");
            Nozzle04_up_btn.setEnabled(false);
            Nozzle04_down_btn.setEnabled(false);
            Nozzle04_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle04_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle05 == -1){
            Nozzle05_tv.setText("N");
            Nozzle05_up_btn.setEnabled(false);
            Nozzle05_down_btn.setEnabled(false);
            Nozzle05_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle05_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle06 == -1){
            Nozzle06_tv.setText("N");
            Nozzle06_up_btn.setEnabled(false);
            Nozzle06_down_btn.setEnabled(false);
            Nozzle06_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle06_down_btn.setImageResource(R.drawable.chevron_down_un);
        }

    }

    public void enabledNozzle(){
        if(Nozzle01 != -1){
            Nozzle01_up_btn.setEnabled(false);
            Nozzle01_down_btn.setEnabled(false);
            Nozzle01_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle01_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle02 != -1){
            Nozzle02_up_btn.setEnabled(false);
            Nozzle02_down_btn.setEnabled(false);
            Nozzle02_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle02_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle03 != -1){
            Nozzle03_up_btn.setEnabled(false);
            Nozzle03_down_btn.setEnabled(false);
            Nozzle03_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle03_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle04 != -1){
            Nozzle04_up_btn.setEnabled(false);
            Nozzle04_down_btn.setEnabled(false);
            Nozzle04_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle04_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle05 != -1){
            Nozzle05_up_btn.setEnabled(false);
            Nozzle05_down_btn.setEnabled(false);
            Nozzle05_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle05_down_btn.setImageResource(R.drawable.chevron_down_un);
        }
        if(Nozzle06 != -1){
            Nozzle06_up_btn.setEnabled(false);
            Nozzle06_down_btn.setEnabled(false);
            Nozzle06_up_btn.setImageResource(R.drawable.chevron_up_un);
            Nozzle06_down_btn.setImageResource(R.drawable.chevron_down_un);
        }

    }

    public void abledNozzle(){
        if(Nozzle01 != -1){
            Nozzle01_up_btn.setEnabled(true);
            Nozzle01_down_btn.setEnabled(true);
            Nozzle01_up_btn.setImageResource(R.drawable.chevron_up);
            Nozzle01_down_btn.setImageResource(R.drawable.chevron_down);
        }
        if(Nozzle02 != -1){
            Nozzle02_up_btn.setEnabled(true);
            Nozzle02_down_btn.setEnabled(true);
            Nozzle02_up_btn.setImageResource(R.drawable.chevron_up);
            Nozzle02_down_btn.setImageResource(R.drawable.chevron_down);
        }
        if(Nozzle03 != -1){
            Nozzle03_up_btn.setEnabled(true);
            Nozzle03_down_btn.setEnabled(true);
            Nozzle03_up_btn.setImageResource(R.drawable.chevron_up);
            Nozzle03_down_btn.setImageResource(R.drawable.chevron_down);
        }
        if(Nozzle04 != -1){
            Nozzle04_up_btn.setEnabled(true);
            Nozzle04_down_btn.setEnabled(true);
            Nozzle04_up_btn.setImageResource(R.drawable.chevron_up);
            Nozzle04_down_btn.setImageResource(R.drawable.chevron_down);
        }
        if(Nozzle05 != -1){
            Nozzle05_up_btn.setEnabled(true);
            Nozzle05_down_btn.setEnabled(true);
            Nozzle05_up_btn.setImageResource(R.drawable.chevron_up);
            Nozzle05_down_btn.setImageResource(R.drawable.chevron_down);
        }
        if(Nozzle06 != -1){
            Nozzle06_up_btn.setEnabled(true);
            Nozzle06_down_btn.setEnabled(true);
            Nozzle06_up_btn.setImageResource(R.drawable.chevron_up);
            Nozzle06_down_btn.setImageResource(R.drawable.chevron_down);
        }
        
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Dialog", "onCreate Dialog");
        setContentView(R.layout.activity_housenozzle_dialog);
        setCancelable(true);


        house_name = findViewById(R.id.housename_tv);
        Nozzle_sw = findViewById(R.id.nozzle_sw);
        Nozzle01_tv = findViewById(R.id.Nozzle01_tv);
        Nozzle02_tv = findViewById(R.id.Nozzle02_tv);
        Nozzle03_tv = findViewById(R.id.Nozzle03_tv);
        Nozzle04_tv = findViewById(R.id.Nozzle04_tv);
        Nozzle05_tv = findViewById(R.id.Nozzle05_tv);
        Nozzle06_tv = findViewById(R.id.Nozzle06_tv);
        Nozzle01_up_btn = findViewById(R.id.Nozzle01_up_btn);
        Nozzle01_down_btn = findViewById(R.id.Nozzle01_down_btn);
        Nozzle02_up_btn = findViewById(R.id.Nozzle02_up_btn);
        Nozzle02_down_btn = findViewById(R.id.Nozzle02_down_btn);
        Nozzle03_up_btn = findViewById(R.id.Nozzle03_up_btn);
        Nozzle03_down_btn = findViewById(R.id.Nozzle03_down_btn);
        Nozzle04_up_btn = findViewById(R.id.Nozzle04_up_btn);
        Nozzle04_down_btn = findViewById(R.id.Nozzle04_down_btn);
        Nozzle05_up_btn = findViewById(R.id.Nozzle05_up_btn);
        Nozzle05_down_btn = findViewById(R.id.Nozzle05_down_btn);
        Nozzle06_up_btn = findViewById(R.id.Nozzle06_up_btn);
        Nozzle06_down_btn = findViewById(R.id.Nozzle06_down_btn);
        positive_botton = findViewById(R.id.positive_botton);
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

        Nozzle_sw.setChecked(switch_bool);

        Nozzle01_tv.setText(String.valueOf(Nozzle01));
        Nozzle02_tv.setText(String.valueOf(Nozzle02));
        Nozzle03_tv.setText(String.valueOf(Nozzle03));
        Nozzle04_tv.setText(String.valueOf(Nozzle04));
        Nozzle05_tv.setText(String.valueOf(Nozzle05));
        Nozzle06_tv.setText(String.valueOf(Nozzle06));


        initSettingNozzle();

        if(switch_bool){
            enabledNozzle();
        }

        Nozzle_sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                enabledNozzle();
            }else {
                abledNozzle();
            }
        });


        Nozzle01_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle01_tv.getText())<max_level){
                Nozzle01_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle01_tv.getText())+1));
            }
        });

        Nozzle01_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle01_tv.getText()) >0){
                Nozzle01_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle01_tv.getText())-1));
            }
        });

        Nozzle02_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle02_tv.getText())<max_level){
                Nozzle02_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle02_tv.getText())+1));
            }
        });

        Nozzle02_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle02_tv.getText()) >0){
                Nozzle02_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle02_tv.getText())-1));
            }
        });

        Nozzle03_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle03_tv.getText())<max_level){
                Nozzle03_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle03_tv.getText())+1));
            }
        });

        Nozzle03_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle03_tv.getText()) >0){
                Nozzle03_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle03_tv.getText())-1));
            }
        });
        Nozzle04_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle04_tv.getText())<max_level){
                Nozzle04_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle04_tv.getText())+1));
            }
        });

        Nozzle04_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle04_tv.getText()) >0){
                Nozzle04_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle04_tv.getText())-1));
            }
        });
        Nozzle05_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle05_tv.getText())<max_level){
                Nozzle05_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle05_tv.getText())+1));
            }
        });

        Nozzle05_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle05_tv.getText()) >0){
                Nozzle05_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle05_tv.getText())-1));
            }
        });
        Nozzle06_up_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle06_tv.getText())<max_level){
                Nozzle06_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle06_tv.getText())+1));
            }
        });

        Nozzle06_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Nozzle06_tv.getText()) >0){
                Nozzle06_tv.setText(String.valueOf(Integer.parseInt((String) Nozzle06_tv.getText())-1));
            }
        });


        positive_botton.setOnClickListener(v -> {

            for (int i = 0; i < callbacksList.size(); i++) {
                HouseNozzleDialogCallback callbackListener = callbacksList.get(i);
                if (null != callbackListener) {
                    callbackListener.onPositiveClick(Nozzle_sw.isChecked(),Integer.parseInt((String)Nozzle01_tv.getText()),Integer.parseInt((String)Nozzle02_tv.getText()),Integer.parseInt((String)Nozzle03_tv.getText()),Integer.parseInt((String)Nozzle04_tv.getText()),Integer.parseInt((String)Nozzle05_tv.getText()),Integer.parseInt((String)Nozzle06_tv.getText()));
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
