package com.example.housecleaner02;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housecleaner02.activity.HouseNozzleDialog;
import com.example.housecleaner02.activity.HouseSettingDialog;
import com.example.housecleaner02.activity.HouseTHDialog;
import com.example.housecleaner02.activity.LoadingDialog;
import com.example.housecleaner02.adapter.ViewPagerAdapter;
import com.example.housecleaner02.filemanage.CustomFormatStrategy;
import com.example.housecleaner02.filemanage.DiskLogStrategy;
import com.example.housecleaner02.fragment.Fragment_01;
import com.example.housecleaner02.fragment.Fragment_02;
import com.example.housecleaner02.fragment.Fragment_03;
import com.example.housecleaner02.listener.OnOpenSerialPortListener;
import com.example.housecleaner02.listener.OnSerialPortDataListener;
import com.example.housecleaner02.model.DeviceModel;
import com.example.housecleaner02.model.MainViewModel;
import com.example.housecleaner02.serialport.CRC16Util;
import com.example.housecleaner02.serialport.SerialPortManager;
import com.example.housecleaner02.serialport.model.SerialModel;
import com.example.housecleaner02.sharedpreferences.KeyControl;
import com.example.housecleaner02.sharedpreferences.PrefsHelper;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    private MainViewModel viewModel;

    private Timer center_timer = null;

    private Timer AirCompressor_timer = null;
    private Timer Pump_timer = null;
    private Timer Cleanner_timer = null;

    private Timer command_timer = null; // 메시지 제한시간

    private int command_timer_time = 8; // 메시지 제한시간단위 (초)
    
    private Boolean CleannerType = false;   // 하우스 청소동작 1,2 여부 false = 1 , true = 2

    private Timer temphumi_timer = null;   // 온습도 타이머
    private final int temp_humi_timer = 60;   // 온습도 요청시간단위(초)

    private int temp_humi_cnt = -1;
    
    private final int Comp_Pump_Cleanner_time = 60;    // 컴프,콤프,청소 시간단위(초)
    
    private Boolean first_info = true; // 처음 시작될때 정보물어보기, 아닐경우 시작

    private Boolean Pause_End = false; // 처음 시작될때 정보물어보기, 아닐경우 시작

    private int TH_Step = 0;

    LoadingDialog loadingDialog ;

    private ViewPager2 mViewPager;

    private ConstraintLayout House_01_02,House_03_04;

    private ConstraintLayout House01_NozzleAuto,House02_NozzleAuto,House03_NozzleAuto,House04_NozzleAuto;       // 노즐

    private ConstraintLayout House01_TH,House02_TH,House03_TH,House04_TH;   // 온습도

    private TextView Main_Time,Main_water_shortage;

//    private TextView House01_Temp_tv,House01_Humi_tv,House02_Temp_tv,House02_Humi_tv,House03_Temp_tv,House03_Humi_tv,House04_Temp_tv,House04_Humi_tv;
    private TextView AirCompressor01and02_RunningTime_tv,Pump01and02_RunningTime_tv,House01and02_Cleaning01_Time_tv,House01and02_Cleaning02_Time_tv;
    private TextView AirCompressor03and04_RunningTime_tv,Pump03and04_RunningTime_tv,House03and04_Cleaning01_Time_tv,House03and04_Cleaning02_Time_tv;
    private TextView House01_NozzleAuto_bool,House02_NozzleAuto_bool,House03_NozzleAuto_bool,House04_NozzleAuto_bool;

    private final BroadcastReceiver MainReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {


            switch (Objects.requireNonNull(intent.getAction())) {
                case "Page":

                    int index = intent.getIntExtra("index",0);
                    

                    if(index == 2){     // 시작시


//                        byte[] data = new byte[1];
//                        data[0] = (byte) 0x01;


//                        SerialPortManager.getInstance().sendBytes(data);

                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Pump_Start,null);

//                        Logger.i("Start Cleanner Plan");
                    }


                    if(index == 0){ // 종료시

                        Stop_AirCompressor_time();
                        Stop_Pump_time();
                        Stop_Cleanner_time();

                        EndCleaning();
                    }

                    if(index ==1){
                        Start_Command_time("시작 전 기기상태 확인 중입니다.");

                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_Info,null);
                        return;
                    }



                    mViewPager.setCurrentItem(index);
                    
                    break;

                case "End":

//
                    Pause_End = true;

                    DeviceModel s_1 = viewModel.getCleanning_Running_Situation_1().getValue();
                    int s_2 = viewModel.getCleanning_Running_Situation_2().getValue();

                    if(s_1.equals(DeviceModel.House_01_02) && s_2 == 0){
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Stop,null);
                    }else if(s_1.equals(DeviceModel.House_01_02) && s_2 == 1){
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Stop,null);
                    }else if(s_1.equals(DeviceModel.House_01_02) && s_2 == 2){
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Stop,null);
                    }else if(s_1.equals(DeviceModel.House_03_04) && s_2 == 0){
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Stop,null);
                    }else if(s_1.equals(DeviceModel.House_03_04) && s_2 == 1){
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Stop,null);
                    }else if(s_1.equals(DeviceModel.House_03_04) && s_2 == 2){
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Stop,null);
                    }else{
                        Pause_End = false;

                        Intent intent2 = new Intent("Page");
                        intent2.putExtra("index",0);
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);

                    }

                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);


        //SharedPreferences 등록
        PrefsHelper.init(this);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        loadingDialog = new LoadingDialog(this);

        // 로그파일 등록
        try {
            FormatStrategy formatStrategy = CustomFormatStrategy.newBuilder()
//                .showThreadInfo(false)  // (Optional) Whether to show thread info or not. Default true
//                .methodCount(0)         // (Optional) How many method line to show. Default 2
//                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                    .logStrategy(new DiskLogStrategy(getExternalFilesDirs(null)))
                    .tag("MyCustomTag")     // (Optional) Global tag for every log. Default PRETTY_LOGGER
                    .build();

            Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
        }catch (Exception e){
            e.printStackTrace();
            AlarmDialogShow("로그기록 세팅 실패","저장할 저장소의 위치를 특정하지 못했습니다. 온도와 센서값을 저장하지 못합니다.");
        }


//        Logger.i("Start Program");


        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(Fragment_01.newInstance(String.valueOf(0)));
        fragments.add(Fragment_02.newInstance(String.valueOf(1)));
        fragments.add(Fragment_03.newInstance(String.valueOf(2)));


        IntentFilter sfilter01 = new IntentFilter();
        sfilter01.addAction("Page");
        LocalBroadcastManager.getInstance(this).registerReceiver(MainReceiver,sfilter01);



        mViewPager = findViewById(R.id.viewPager);

        Main_Time = findViewById(R.id.main_time);
        Main_water_shortage = findViewById(R.id.main_water_shortage);

//        House01_Temp_tv = findViewById(R.id.House01_Temp);
//        House01_Humi_tv = findViewById(R.id.House01_Humi);
//        House02_Temp_tv = findViewById(R.id.House02_Temp);
//        House02_Humi_tv = findViewById(R.id.House02_Humi);
//        House03_Temp_tv = findViewById(R.id.House03_Temp);
//        House03_Humi_tv = findViewById(R.id.House03_Humi);
//        House04_Temp_tv = findViewById(R.id.House04_Temp);
//        House04_Humi_tv = findViewById(R.id.House04_Humi);
//        Main_Temp = findViewById(R.id.main_temp);
//        Main_Humi = findViewById(R.id.main_humi);

        House_01_02 = findViewById(R.id.House01and02_constraintLayout);
        House_03_04 = findViewById(R.id.House03and04_constraintLayout);


        House01_TH = findViewById(R.id.House01_TH);
        House02_TH = findViewById(R.id.House02_TH);
        House03_TH = findViewById(R.id.House03_TH);
        House04_TH = findViewById(R.id.House04_TH);

        AirCompressor01and02_RunningTime_tv = findViewById(R.id.AirCompressor01and02_RunningTime_tv);
        Pump01and02_RunningTime_tv = findViewById(R.id.Pump01and02_RunningTime_tv);
        House01and02_Cleaning01_Time_tv = findViewById(R.id.House01and02_Cleaning01_Time_tv);
        House01and02_Cleaning02_Time_tv = findViewById(R.id.House01and02_Cleaning02_Time_tv);

        House01_NozzleAuto = findViewById(R.id.House01_NozzleAuto);
        House01_NozzleAuto_bool = findViewById(R.id.House01_NozzleAuto_bool);
        House02_NozzleAuto = findViewById(R.id.House02_NozzleAuto);
        House02_NozzleAuto_bool = findViewById(R.id.House02_NozzleAuto_bool);

        AirCompressor03and04_RunningTime_tv = findViewById(R.id.AirCompressor03and04_RunningTime_tv);
        Pump03and04_RunningTime_tv = findViewById(R.id.Pump03and04_RunningTime_tv);
        House03and04_Cleaning01_Time_tv = findViewById(R.id.House03and04_Cleaning01_Time_tv);
        House03and04_Cleaning02_Time_tv = findViewById(R.id.House03and04_Cleaning02_Time_tv);

        House03_NozzleAuto = findViewById(R.id.House03_NozzleAuto);
        House03_NozzleAuto_bool = findViewById(R.id.House03_NozzleAuto_bool);
        House04_NozzleAuto = findViewById(R.id.House04_NozzleAuto);
        House04_NozzleAuto_bool = findViewById(R.id.House04_NozzleAuto_bool);


        ViewPagerAdapter viewPager2Adapter = new ViewPagerAdapter(this, fragments);
        mViewPager.setAdapter(viewPager2Adapter);
        mViewPager.setUserInputEnabled(false);  // 스와이프 막기



        final Observer<String> main_time_tv_ob = b -> {
            Main_Time.setText(b);
        };

        final Observer<Boolean>  water_shortage_ob = b -> {

            if(b){
                Main_water_shortage.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.water_shartage_alarm));
                Main_water_shortage.setShadowLayer(3,0,2,ContextCompat.getColor(getApplicationContext(),R.color.water_shartage_alarm_shadow));
            }else{
                Main_water_shortage.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.water_shartage));
                Main_water_shortage.setShadowLayer(3,0,2,ContextCompat.getColor(getApplicationContext(),R.color.water_shartage_shadow));
            }

        };


        final Observer<Integer> AirCompressor01and02_RunningTime_tv_ob = b -> {
            AirCompressor01and02_RunningTime_tv.setText(String.valueOf(b));
        };

        final Observer<Integer> AirCompressor03and04_RunningTime_tv_ob = b -> {
            AirCompressor03and04_RunningTime_tv.setText(String.valueOf(b));
        };


        final Observer<Integer> Pump01and02_RunningTime_tv_ob = b -> {
            Pump01and02_RunningTime_tv.setText(String.valueOf(b));
        };
        final Observer<Integer> Pump03and04_RunningTime_tv_ob = b -> {
            Pump03and04_RunningTime_tv.setText(String.valueOf(b));
        };


        final Observer<Integer> House01and02_Cleaning01_Time_tv_ob = b -> {
            House01and02_Cleaning01_Time_tv.setText(String.valueOf(b));
        };
        final Observer<Integer> House03and04_Cleaning01_Time_tv_ob = b -> {
            House03and04_Cleaning01_Time_tv.setText(String.valueOf(b));
        };


        final Observer<Integer> House01and02_Cleaning02_Time_tv_ob = b -> {
            House01and02_Cleaning02_Time_tv.setText(String.valueOf(b));
        };
        final Observer<Integer> House03and04_Cleaning02_Time_tv_ob = b -> {
            House03and04_Cleaning02_Time_tv.setText(String.valueOf(b));
        };


        final Observer<Boolean> House01_NozzleAuto_sw_ob = b -> {
            Log.d(TAG,"House01_NozzleAuto : "+ b);
            if(b){
                House01_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.auto));
                House01_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.auto_shadow));
            }else{
                House01_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.manual));
                House01_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.manual_shadow));
            }

        };
        final Observer<Boolean> House02_NozzleAuto_sw_ob = b -> {
            Log.d(TAG,"House02_NozzleAuto : "+ b);
            if(b){
                House02_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.auto));
                House02_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.auto_shadow));
            }else{
                House02_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.manual));
                House02_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.manual_shadow));
            }
        };
        final Observer<Boolean> House03_NozzleAuto_sw_ob = b -> {
            Log.d(TAG,"House03_NozzleAuto : "+ b);
            if(b){
                House03_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.auto));
                House03_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.auto_shadow));
            }else{
                House03_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.manual));
                House03_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.manual_shadow));
            }
        };
        final Observer<Boolean> House04_NozzleAuto_sw_ob = b -> {
            Log.d(TAG,"House04_NozzleAuto : "+ b);
            if(b){
                House04_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.auto));
                House04_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.auto_shadow));
            }else{
                House04_NozzleAuto_bool.setTextColor(ContextCompat.getColor(getApplicationContext(),R.color.manual));
                House04_NozzleAuto_bool.setShadowLayer(1,0,1,ContextCompat.getColor(getApplicationContext(),R.color.manual_shadow));
            }
        };

        viewModel.getMain_time_tv().observe(this,main_time_tv_ob);

        viewModel.getWater_Shortage().observe(this,water_shortage_ob);

//        viewModel.getHouse01_Temp().observe(this,House01_Temp_tv_ob);
//        viewModel.getHouse01_Humi().observe(this,House01_Humi_tv_ob);
//        viewModel.getHouse02_Temp().observe(this,House02_Temp_tv_ob);
//        viewModel.getHouse02_Humi().observe(this,House02_Humi_tv_ob);
//        viewModel.getHouse03_Temp().observe(this,House03_Temp_tv_ob);
//        viewModel.getHouse03_Humi().observe(this,House03_Humi_tv_ob);
//        viewModel.getHouse04_Temp().observe(this,House04_Temp_tv_ob);
//        viewModel.getHouse04_Humi().observe(this,House04_Humi_tv_ob);

        viewModel.getAirCompressor01and02_RunningTime().observe(this,AirCompressor01and02_RunningTime_tv_ob);
        viewModel.getAirCompressor03and04_RunningTime().observe(this,AirCompressor03and04_RunningTime_tv_ob);

        viewModel.getPump01and02_RunningTime().observe(this,Pump01and02_RunningTime_tv_ob);
        viewModel.getPump03and04_RunningTime().observe(this,Pump03and04_RunningTime_tv_ob);

        viewModel.getHouse01and02_Cleaning01_Time().observe(this,House01and02_Cleaning01_Time_tv_ob);
        viewModel.getHouse03and04_Cleaning01_Time().observe(this,House03and04_Cleaning01_Time_tv_ob);

        viewModel.getHouse01and02_Cleaning02_Time().observe(this,House01and02_Cleaning02_Time_tv_ob);
        viewModel.getHouse03and04_Cleaning02_Time().observe(this,House03and04_Cleaning02_Time_tv_ob);


        viewModel.getHouse01_Nozzle_Auto().observe(this,House01_NozzleAuto_sw_ob);
        viewModel.getHouse02_Nozzle_Auto().observe(this,House02_NozzleAuto_sw_ob);
        viewModel.getHouse03_Nozzle_Auto().observe(this,House03_NozzleAuto_sw_ob);
        viewModel.getHouse04_Nozzle_Auto().observe(this,House04_NozzleAuto_sw_ob);


        House_01_02.setOnClickListener(v -> {
            if(Boolean.FALSE.equals(viewModel.getIsRunning().getValue())){
                Log.d(TAG,"House01_02");
                HouseSettingDialog houseSettingDialog = new HouseSettingDialog(this,DeviceModel.House_01_02,viewModel.getAirCompressor01and02_RunningTime().getValue(),viewModel.getPump01and02_RunningTime().getValue(),viewModel.getHouse01and02_Cleaning01_Time().getValue(),viewModel.getHouse01and02_Cleaning02_Time().getValue());
                houseSettingDialog.setHouseSettingDialogListener((AirCompressor_RunningTime, Pump_RunningTime, House_Cleaning01_Time, House_Cleaning02_Time) -> {
                    viewModel.getAirCompressor01and02_RunningTime().postValue(AirCompressor_RunningTime);
                    viewModel.getPump01and02_RunningTime().postValue(Pump_RunningTime);
                    viewModel.getHouse01and02_Cleaning01_Time().postValue(House_Cleaning01_Time);
                    viewModel.getHouse01and02_Cleaning02_Time().postValue(House_Cleaning02_Time);

                    KeyControl.setAirCompressor01and02_RunningTime(AirCompressor_RunningTime);
                    KeyControl.setPump01and02_RunningTime(Pump_RunningTime);
                    KeyControl.setHouse01and02_Cleaning01(House_Cleaning01_Time);
                    KeyControl.setHouse01and02_Cleaning02(House_Cleaning02_Time);
                });
                houseSettingDialog.show();
            }else{
                Toast.makeText(getApplicationContext(),"작동중 수정이 불가능합니다. 기기를 멈춘후 다시 시도해주세요.",Toast.LENGTH_LONG).show();
            }

        });


        House_03_04.setOnClickListener(v -> {
            if(Boolean.FALSE.equals(viewModel.getIsRunning().getValue())){
                Log.d(TAG,"House03_04");
                HouseSettingDialog houseSettingDialog = new HouseSettingDialog(this,DeviceModel.House_03_04,viewModel.getAirCompressor03and04_RunningTime().getValue(),viewModel.getPump03and04_RunningTime().getValue(),viewModel.getHouse03and04_Cleaning01_Time().getValue(),viewModel.getHouse03and04_Cleaning02_Time().getValue());
                houseSettingDialog.setHouseSettingDialogListener((AirCompressor_RunningTime, Pump_RunningTime, House_Cleaning01_Time, House_Cleaning02_Time) -> {


                    viewModel.getAirCompressor03and04_RunningTime().postValue(AirCompressor_RunningTime);
                    viewModel.getPump03and04_RunningTime().postValue(Pump_RunningTime);
                    viewModel.getHouse03and04_Cleaning01_Time().postValue(House_Cleaning01_Time);
                    viewModel.getHouse03and04_Cleaning02_Time().postValue(House_Cleaning02_Time);

                    KeyControl.setAirCompressor03and04_RunningTime(AirCompressor_RunningTime);
                    KeyControl.setPump03and04_RunningTime(Pump_RunningTime);
                    KeyControl.setHouse03and04_Cleaning01(House_Cleaning01_Time);
                    KeyControl.setHouse03and04_Cleaning02(House_Cleaning02_Time);
                });
                houseSettingDialog.show();
            }else{
                Toast.makeText(getApplicationContext(),"작동중 수정이 불가능합니다. 기기를 멈춘후 다시 시도해주세요.",Toast.LENGTH_LONG).show();
            }

        });



        House01_NozzleAuto.setOnClickListener(v -> {
            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_01, Boolean.TRUE.equals(viewModel.getHouse01_Nozzle_Auto().getValue()),KeyControl.getHouse01_Nozzle01(),KeyControl.getHouse01_Nozzle02(),KeyControl.getHouse01_Nozzle03(),KeyControl.getHouse01_Nozzle04());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04) -> {

                if(!KeyControl.getHouse01_Nozzle01().equals(Nozzle_01)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_1, (byte) Nozzle_01);
                    KeyControl.setHouse01_Nozzle01(Nozzle_01);
                }

                if(!KeyControl.getHouse01_Nozzle02().equals(Nozzle_02)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_2, (byte) Nozzle_02);
                    KeyControl.setHouse01_Nozzle02(Nozzle_02);
                }

                if(!KeyControl.getHouse01_Nozzle03().equals(Nozzle_03)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_3, (byte) Nozzle_03);
                    KeyControl.setHouse01_Nozzle03(Nozzle_03);
                }

                if(!KeyControl.getHouse01_Nozzle04().equals(Nozzle_04)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_4, (byte) Nozzle_04);
                    KeyControl.setHouse01_Nozzle04(Nozzle_04);
                }

                viewModel.setHouse01_Nozzle_Auto(auto);

            });
            houseNozzleDialog.show();
        });

        House02_NozzleAuto.setOnClickListener(v -> {
            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_02, Boolean.TRUE.equals(viewModel.getHouse02_Nozzle_Auto().getValue()),KeyControl.getHouse02_Nozzle01(),KeyControl.getHouse02_Nozzle02(),KeyControl.getHouse02_Nozzle03(),KeyControl.getHouse02_Nozzle04());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04) -> {

                if(!KeyControl.getHouse02_Nozzle01().equals(Nozzle_01)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_1, (byte) Nozzle_01);
                    KeyControl.setHouse02_Nozzle01(Nozzle_01);
                }

                if(!KeyControl.getHouse02_Nozzle02().equals(Nozzle_02)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_2, (byte) Nozzle_02);
                    KeyControl.setHouse02_Nozzle02(Nozzle_02);
                }

                if(!KeyControl.getHouse02_Nozzle03().equals(Nozzle_03)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_3, (byte) Nozzle_03);
                    KeyControl.setHouse02_Nozzle03(Nozzle_03);
                }

                if(!KeyControl.getHouse02_Nozzle04().equals(Nozzle_04)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_4, (byte) Nozzle_04);
                    KeyControl.setHouse02_Nozzle04(Nozzle_04);
                }



                viewModel.setHouse02_Nozzle_Auto(auto);


            });
            houseNozzleDialog.show();
        });

        House03_NozzleAuto.setOnClickListener(v -> {
            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_03, Boolean.TRUE.equals(viewModel.getHouse03_Nozzle_Auto().getValue()),KeyControl.getHouse03_Nozzle01(),KeyControl.getHouse03_Nozzle02(),KeyControl.getHouse03_Nozzle03(),KeyControl.getHouse03_Nozzle04());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04) -> {

                if(!KeyControl.getHouse03_Nozzle01().equals(Nozzle_01)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_1, (byte) Nozzle_01);
                    KeyControl.setHouse03_Nozzle01(Nozzle_01);
                }

                if(!KeyControl.getHouse03_Nozzle02().equals(Nozzle_02)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_2, (byte) Nozzle_02);
                    KeyControl.setHouse03_Nozzle02(Nozzle_02);
                }

                if(!KeyControl.getHouse03_Nozzle03().equals(Nozzle_03)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_3, (byte) Nozzle_03);
                    KeyControl.setHouse03_Nozzle03(Nozzle_03);
                }

                if(!KeyControl.getHouse03_Nozzle04().equals(Nozzle_04)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_4, (byte) Nozzle_04);
                    KeyControl.setHouse03_Nozzle04(Nozzle_04);
                }

                viewModel.setHouse03_Nozzle_Auto(auto);

            });
            houseNozzleDialog.show();
        });

        House04_NozzleAuto.setOnClickListener(v -> {
            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_04, Boolean.TRUE.equals(viewModel.getHouse04_Nozzle_Auto().getValue()),KeyControl.getHouse04_Nozzle01(),KeyControl.getHouse04_Nozzle02(),KeyControl.getHouse04_Nozzle03(),KeyControl.getHouse04_Nozzle04());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04) -> {

                if(!KeyControl.getHouse04_Nozzle01().equals(Nozzle_01)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_1, (byte) Nozzle_01);
                    KeyControl.setHouse04_Nozzle01(Nozzle_01);
                }

                if(!KeyControl.getHouse04_Nozzle02().equals(Nozzle_02)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_2, (byte) Nozzle_02);
                    KeyControl.setHouse04_Nozzle02(Nozzle_02);
                }

                if(!KeyControl.getHouse04_Nozzle03().equals(Nozzle_03)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_3, (byte) Nozzle_03);
                    KeyControl.setHouse04_Nozzle03(Nozzle_03);
                }

                if(!KeyControl.getHouse04_Nozzle04().equals(Nozzle_04)){
                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_4, (byte) Nozzle_04);
                    KeyControl.setHouse04_Nozzle04(Nozzle_04);
                }

                viewModel.setHouse04_Nozzle_Auto(auto);
            });
            houseNozzleDialog.show();
        });




        House01_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_01,viewModel.getHouse01_Temp1_1().getValue(),viewModel.getHouse01_Temp1_2().getValue(),viewModel.getHouse01_Temp2_1().getValue(),viewModel.getHouse01_Temp2_2().getValue(),viewModel.getHouse01_Temp3_1().getValue(),viewModel.getHouse01_Temp3_2().getValue(),viewModel.getHouse01_Temp4_1().getValue(),viewModel.getHouse01_Temp4_2().getValue(),viewModel.getHouse01_Humi1_1().getValue(),viewModel.getHouse01_Humi1_2().getValue(),viewModel.getHouse01_Humi2_1().getValue(),viewModel.getHouse01_Humi2_2().getValue(),viewModel.getHouse01_Humi3_1().getValue(),viewModel.getHouse01_Humi3_2().getValue(),viewModel.getHouse01_Humi4_1().getValue(),viewModel.getHouse01_Humi4_2().getValue());
            houseTHDialog.show();
        });

        House02_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_02,viewModel.getHouse02_Temp1_1().getValue(),viewModel.getHouse02_Temp1_2().getValue(),viewModel.getHouse02_Temp2_1().getValue(),viewModel.getHouse02_Temp2_2().getValue(),viewModel.getHouse02_Temp3_1().getValue(),viewModel.getHouse02_Temp3_2().getValue(),viewModel.getHouse02_Temp4_1().getValue(),viewModel.getHouse02_Temp4_2().getValue(),viewModel.getHouse02_Humi1_1().getValue(),viewModel.getHouse02_Humi1_2().getValue(),viewModel.getHouse02_Humi2_1().getValue(),viewModel.getHouse02_Humi2_2().getValue(),viewModel.getHouse02_Humi3_1().getValue(),viewModel.getHouse02_Humi3_2().getValue(),viewModel.getHouse02_Humi4_1().getValue(),viewModel.getHouse02_Humi4_2().getValue());
            houseTHDialog.show();
        });

        House03_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_03,viewModel.getHouse03_Temp1_1().getValue(),viewModel.getHouse03_Temp1_2().getValue(),viewModel.getHouse03_Temp2_1().getValue(),viewModel.getHouse03_Temp2_2().getValue(),viewModel.getHouse03_Temp3_1().getValue(),viewModel.getHouse03_Temp3_2().getValue(),viewModel.getHouse03_Temp4_1().getValue(),viewModel.getHouse03_Temp4_2().getValue(),viewModel.getHouse03_Humi1_1().getValue(),viewModel.getHouse03_Humi1_2().getValue(),viewModel.getHouse03_Humi2_1().getValue(),viewModel.getHouse03_Humi2_2().getValue(),viewModel.getHouse03_Humi3_1().getValue(),viewModel.getHouse03_Humi3_2().getValue(),viewModel.getHouse03_Humi4_1().getValue(),viewModel.getHouse03_Humi4_2().getValue());
            houseTHDialog.show();
        });

        House04_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_04,viewModel.getHouse04_Temp1_1().getValue(),viewModel.getHouse04_Temp1_2().getValue(),viewModel.getHouse04_Temp2_1().getValue(),viewModel.getHouse04_Temp2_2().getValue(),viewModel.getHouse04_Temp3_1().getValue(),viewModel.getHouse04_Temp3_2().getValue(),viewModel.getHouse04_Temp4_1().getValue(),viewModel.getHouse04_Temp4_2().getValue(),viewModel.getHouse04_Humi1_1().getValue(),viewModel.getHouse04_Humi1_2().getValue(),viewModel.getHouse04_Humi2_1().getValue(),viewModel.getHouse04_Humi2_2().getValue(),viewModel.getHouse04_Humi3_1().getValue(),viewModel.getHouse04_Humi3_2().getValue(),viewModel.getHouse04_Humi4_1().getValue(),viewModel.getHouse04_Humi4_2().getValue());
            houseTHDialog.show();
        });





        SerialPortManager.getInstance().openSerialPort(new File(DeviceModel.getDeviceSerialPort()), 115200);

        SerialPortManager.getInstance().setOnOpenSerialPortListener(new OnOpenSerialPortListener() {
            @Override
            public void onSuccess(File device) {
                Log.d(TAG, "직렬 포트 연결 성공");
            }

            @Override
            public void onFail(File device, Status status) {
                Log.d(TAG, "직렬 포트 연결 실패");
            }
        });
        SerialPortManager.getInstance().setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {  // 수신시
//                Log.d(TAG, "직렬 포트 데이터 수신：" + Arrays.toString(bytes));

                Log.d(TAG, "직렬 포트 데이터 수신：" + byteArrayToHex(bytes));


                try{
                    if (bytes[1]==SerialModel.InfoReceive){  //정보응답

                        Log.d(TAG, "종류：" + SerialModel.InfoReceive);

                        
                        Stop_Command_time();
                        
                        try {
                            byte[] data = new byte[]{bytes[3],bytes[4],bytes[5],bytes[6],bytes[7],bytes[8],bytes[9],bytes[10],bytes[11],bytes[12],bytes[13],bytes[14],bytes[15],bytes[16],bytes[17],bytes[18],bytes[19]};
                            byte[] crc = new byte[]{bytes[20],bytes[21]};

                            if(CRC16Util.receiveData(data,crc)){
                                KeyControl.setHouse01_Nozzle01(0xff & bytes[3]);
                                KeyControl.setHouse01_Nozzle02(0xff & bytes[4]);
                                KeyControl.setHouse01_Nozzle03(0xff & bytes[5]);
                                KeyControl.setHouse01_Nozzle04(0xff & bytes[6]);

                                KeyControl.setHouse02_Nozzle01(0xff & bytes[7]);
                                KeyControl.setHouse02_Nozzle02(0xff & bytes[8]);
                                KeyControl.setHouse02_Nozzle03(0xff & bytes[9]);
                                KeyControl.setHouse02_Nozzle04(0xff & bytes[10]);

                                KeyControl.setHouse03_Nozzle01(0xff & bytes[11]);
                                KeyControl.setHouse03_Nozzle02(0xff & bytes[12]);
                                KeyControl.setHouse03_Nozzle03(0xff & bytes[13]);
                                KeyControl.setHouse03_Nozzle04(0xff & bytes[14]);

                                KeyControl.setHouse04_Nozzle01(0xff & bytes[15]);
                                KeyControl.setHouse04_Nozzle02(0xff & bytes[16]);
                                KeyControl.setHouse04_Nozzle03(0xff & bytes[17]);
                                KeyControl.setHouse04_Nozzle04(0xff & bytes[18]);

                                viewModel.getWater_Shortage().postValue(bytes[19] != 0x00);

                                if(first_info){         // 처음 기기를 시작할때
                                    first_info = false;
                                }else{                  // 시작 버튼을 누를때
                                    if(bytes[19] != 0x00){      // 물이 부족할때
                                        AlarmDialogShow("물부족","물이 부족합니다. 적정량의 물이 존재하는지 확인해주세요.");
                                        Intent intent = new Intent("Page");
                                        intent.putExtra("index",0);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                    }else{
                                        mViewPager.setCurrentItem(1);
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(bytes[1]==SerialModel.THReceive){  //온습도응답
                        try {
                            Log.d(TAG, "종류：" + "SerialModel.THReceive");

                            byte[] data = new byte[]{bytes[3],bytes[4],bytes[5],bytes[6],bytes[7],bytes[8],bytes[9],bytes[10],bytes[11],bytes[12],bytes[13],bytes[14],bytes[15],bytes[16],bytes[17],bytes[18]};
                            byte[] crc = new byte[]{bytes[19],bytes[20]};

                            if(CRC16Util.receiveData(data,crc)){


                                if(TH_Step==0){
                                    TH_Step = 1;

                                    viewModel.getHouse01_Temp1_1().postValue(0xff & bytes[3]);
                                    viewModel.getHouse01_Temp1_2().postValue(0xff & bytes[4]);
                                    viewModel.getHouse01_Humi1_1().postValue(0xff & bytes[5]);
                                    viewModel.getHouse01_Humi1_2().postValue(0xff & bytes[6]);

                                    viewModel.getHouse01_Temp2_1().postValue(0xff & bytes[7]);
                                    viewModel.getHouse01_Temp2_2().postValue(0xff & bytes[8]);
                                    viewModel.getHouse01_Humi2_1().postValue(0xff & bytes[9]);
                                    viewModel.getHouse01_Humi2_2().postValue(0xff & bytes[10]);

                                    viewModel.getHouse01_Temp3_1().postValue(0xff & bytes[11]);
                                    viewModel.getHouse01_Temp3_2().postValue(0xff & bytes[12]);
                                    viewModel.getHouse01_Humi3_1().postValue(0xff & bytes[13]);
                                    viewModel.getHouse01_Humi3_2().postValue(0xff & bytes[14]);

                                    viewModel.getHouse01_Temp4_1().postValue(0xff & bytes[15]);
                                    viewModel.getHouse01_Temp4_2().postValue(0xff & bytes[16]);
                                    viewModel.getHouse01_Humi4_1().postValue(0xff & bytes[17]);
                                    viewModel.getHouse01_Humi4_2().postValue(0xff & bytes[18]);

                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_House_02_TH,null);

                                    if(Boolean.TRUE.equals(viewModel.getHouse01_Nozzle_Auto().getValue())){

                                        if((0xff & bytes[3])< 28 || (0xff & bytes[4])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_1,(byte)1);
                                        }else if((((0xff & bytes[3])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[4])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_1,(byte)2);
                                        }else if((0xff & bytes[3])>= 32 || (0xff & bytes[4])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_1,(byte)3);
                                        }


                                        if((0xff & bytes[7])< 28 || (0xff & bytes[8])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_2,(byte)1);
                                        }else if((((0xff & bytes[7])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[8])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_2,(byte)2);
                                        }else if((0xff & bytes[7])>= 32 || (0xff & bytes[8])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_2,(byte)3);
                                        }

                                        if((0xff & bytes[11])< 28 || (0xff & bytes[12])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_3,(byte)1);
                                        }else if((((0xff & bytes[11])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[12])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_3,(byte)2);
                                        }else if((0xff & bytes[11])>= 32 || (0xff & bytes[12])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_3,(byte)3);
                                        }

                                        if((0xff & bytes[15])< 28 || (0xff & bytes[16])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_4,(byte)1);
                                        }else if((((0xff & bytes[15])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[16])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_4,(byte)2);
                                        }else if((0xff & bytes[15])>= 32 || (0xff & bytes[16])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_4,(byte)3);
                                        }

                                    }


                                    if(temp_humi_cnt == -1 || temp_humi_cnt == 60){
                                        Logger.i("House01 = 1("+(0xff & bytes[3])+"/"+(0xff & bytes[4])+","+KeyControl.getHouse01_Nozzle01()+
                                                ")  2("+(0xff & bytes[7])+"/"+(0xff & bytes[8])+","+KeyControl.getHouse01_Nozzle02()+
                                                ")  3("+(0xff & bytes[11])+"/"+(0xff & bytes[12])+","+KeyControl.getHouse01_Nozzle03()+
                                                ")  4("+(0xff & bytes[15])+"/"+(0xff & bytes[16])+","+KeyControl.getHouse01_Nozzle04()+")");
                                    }

                                }else if (TH_Step ==1){
                                    TH_Step = 2;

                                    viewModel.getHouse02_Temp1_1().postValue(0xff & bytes[3]);
                                    viewModel.getHouse02_Temp1_2().postValue(0xff & bytes[4]);
                                    viewModel.getHouse02_Humi1_1().postValue(0xff & bytes[5]);
                                    viewModel.getHouse02_Humi1_2().postValue(0xff & bytes[6]);

                                    viewModel.getHouse02_Temp2_1().postValue(0xff & bytes[7]);
                                    viewModel.getHouse02_Temp2_2().postValue(0xff & bytes[8]);
                                    viewModel.getHouse02_Humi2_1().postValue(0xff & bytes[9]);
                                    viewModel.getHouse02_Humi2_2().postValue(0xff & bytes[10]);

                                    viewModel.getHouse02_Temp3_1().postValue(0xff & bytes[11]);
                                    viewModel.getHouse02_Temp3_2().postValue(0xff & bytes[12]);
                                    viewModel.getHouse02_Humi3_1().postValue(0xff & bytes[13]);
                                    viewModel.getHouse02_Humi3_2().postValue(0xff & bytes[14]);

                                    viewModel.getHouse02_Temp4_1().postValue(0xff & bytes[15]);
                                    viewModel.getHouse02_Temp4_2().postValue(0xff & bytes[16]);
                                    viewModel.getHouse02_Humi4_1().postValue(0xff & bytes[17]);
                                    viewModel.getHouse02_Humi4_2().postValue(0xff & bytes[18]);

                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_House_03_TH,null);

                                    if(Boolean.TRUE.equals(viewModel.getHouse02_Nozzle_Auto().getValue())){
                                        if((0xff & bytes[3])< 28 || (0xff & bytes[4])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_1,(byte)1);
                                        }else if((((0xff & bytes[3])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[4])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_1,(byte)2);
                                        }else if((0xff & bytes[3])>= 32 || (0xff & bytes[4])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_1,(byte)3);
                                        }


                                        if((0xff & bytes[7])< 28 || (0xff & bytes[8])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_2,(byte)1);
                                        }else if((((0xff & bytes[7])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[8])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_2,(byte)2);
                                        }else if((0xff & bytes[7])>= 32 || (0xff & bytes[8])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_2,(byte)3);
                                        }

                                        if((0xff & bytes[11])< 28 || (0xff & bytes[12])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_3,(byte)1);
                                        }else if((((0xff & bytes[11])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[12])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_3,(byte)2);
                                        }else if((0xff & bytes[11])>= 32 || (0xff & bytes[12])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_3,(byte)3);
                                        }

                                        if((0xff & bytes[15])< 28 || (0xff & bytes[16])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_4,(byte)1);
                                        }else if((((0xff & bytes[15])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[16])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_4,(byte)2);
                                        }else if((0xff & bytes[15])>= 32 || (0xff & bytes[16])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_4,(byte)3);
                                        }
                                    }

                                    if(temp_humi_cnt == -1 || temp_humi_cnt == 60){
                                        Logger.i("House02 = 1("+(0xff & bytes[3])+"/"+(0xff & bytes[4])+","+KeyControl.getHouse02_Nozzle01()+
                                                ")  2("+(0xff & bytes[7])+"/"+(0xff & bytes[8])+","+KeyControl.getHouse02_Nozzle02()+
                                                ")  3("+(0xff & bytes[11])+"/"+(0xff & bytes[12])+","+KeyControl.getHouse02_Nozzle03()+
                                                ")  4("+(0xff & bytes[15])+"/"+(0xff & bytes[16])+","+KeyControl.getHouse02_Nozzle04()+")");
                                    }
                                }else if (TH_Step ==2){
                                    TH_Step = 3;

                                    viewModel.getHouse03_Temp1_1().postValue(0xff & bytes[3]);
                                    viewModel.getHouse03_Temp1_2().postValue(0xff & bytes[4]);
                                    viewModel.getHouse03_Humi1_1().postValue(0xff & bytes[5]);
                                    viewModel.getHouse03_Humi1_2().postValue(0xff & bytes[6]);

                                    viewModel.getHouse03_Temp2_1().postValue(0xff & bytes[7]);
                                    viewModel.getHouse03_Temp2_2().postValue(0xff & bytes[8]);
                                    viewModel.getHouse03_Humi2_1().postValue(0xff & bytes[9]);
                                    viewModel.getHouse03_Humi2_2().postValue(0xff & bytes[10]);

                                    viewModel.getHouse03_Temp3_1().postValue(0xff & bytes[11]);
                                    viewModel.getHouse03_Temp3_2().postValue(0xff & bytes[12]);
                                    viewModel.getHouse03_Humi3_1().postValue(0xff & bytes[13]);
                                    viewModel.getHouse03_Humi3_2().postValue(0xff & bytes[14]);

                                    viewModel.getHouse03_Temp4_1().postValue(0xff & bytes[15]);
                                    viewModel.getHouse03_Temp4_2().postValue(0xff & bytes[16]);
                                    viewModel.getHouse03_Humi4_1().postValue(0xff & bytes[17]);
                                    viewModel.getHouse03_Humi4_2().postValue(0xff & bytes[18]);

                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_House_04_TH,null);

                                    if(Boolean.TRUE.equals(viewModel.getHouse03_Nozzle_Auto().getValue())){
                                        if((0xff & bytes[3])< 28 || (0xff & bytes[4])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_1,(byte)1);
                                        }else if((((0xff & bytes[3])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[4])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_1,(byte)2);
                                        }else if((0xff & bytes[3])>= 32 || (0xff & bytes[4])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_1,(byte)3);
                                        }


                                        if((0xff & bytes[7])< 28 || (0xff & bytes[8])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_2,(byte)1);
                                        }else if((((0xff & bytes[7])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[8])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_2,(byte)2);
                                        }else if((0xff & bytes[7])>= 32 || (0xff & bytes[8])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_2,(byte)3);
                                        }

                                        if((0xff & bytes[11])< 28 || (0xff & bytes[12])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_3,(byte)1);
                                        }else if((((0xff & bytes[11])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[12])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_3,(byte)2);
                                        }else if((0xff & bytes[11])>= 32 || (0xff & bytes[12])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_3,(byte)3);
                                        }

                                        if((0xff & bytes[15])< 28 || (0xff & bytes[16])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_4,(byte)1);
                                        }else if((((0xff & bytes[15])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[16])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_4,(byte)2);
                                        }else if((0xff & bytes[15])>= 32 || (0xff & bytes[16])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_4,(byte)3);
                                        }
                                    }

                                    if(temp_humi_cnt == -1 || temp_humi_cnt == 60){
                                        Logger.i("House03 = 1("+(0xff & bytes[3])+"/"+(0xff & bytes[4])+","+KeyControl.getHouse03_Nozzle01()+
                                                ")  2("+(0xff & bytes[7])+"/"+(0xff & bytes[8])+","+KeyControl.getHouse03_Nozzle02()+
                                                ")  3("+(0xff & bytes[11])+"/"+(0xff & bytes[12])+","+KeyControl.getHouse03_Nozzle03()+
                                                ")  4("+(0xff & bytes[15])+"/"+(0xff & bytes[16])+","+KeyControl.getHouse03_Nozzle04()+")");
                                    }
                                }else if (TH_Step ==3){
                                    TH_Step = 0;

                                    viewModel.getHouse04_Temp1_1().postValue(0xff & bytes[3]);
                                    viewModel.getHouse04_Temp1_2().postValue(0xff & bytes[4]);
                                    viewModel.getHouse04_Humi1_1().postValue(0xff & bytes[5]);
                                    viewModel.getHouse04_Humi1_2().postValue(0xff & bytes[6]);

                                    viewModel.getHouse04_Temp2_1().postValue(0xff & bytes[7]);
                                    viewModel.getHouse04_Temp2_2().postValue(0xff & bytes[8]);
                                    viewModel.getHouse04_Humi2_1().postValue(0xff & bytes[9]);
                                    viewModel.getHouse04_Humi2_2().postValue(0xff & bytes[10]);

                                    viewModel.getHouse04_Temp3_1().postValue(0xff & bytes[11]);
                                    viewModel.getHouse04_Temp3_2().postValue(0xff & bytes[12]);
                                    viewModel.getHouse04_Humi3_1().postValue(0xff & bytes[13]);
                                    viewModel.getHouse04_Humi3_2().postValue(0xff & bytes[14]);

                                    viewModel.getHouse04_Temp4_1().postValue(0xff & bytes[15]);
                                    viewModel.getHouse04_Temp4_2().postValue(0xff & bytes[16]);
                                    viewModel.getHouse04_Humi4_1().postValue(0xff & bytes[17]);
                                    viewModel.getHouse04_Humi4_2().postValue(0xff & bytes[18]);

                                    if(Boolean.TRUE.equals(viewModel.getHouse04_Nozzle_Auto().getValue())){
                                        if((0xff & bytes[3])< 28 || (0xff & bytes[4])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_1,(byte)1);
                                        }else if((((0xff & bytes[3])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[4])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_1,(byte)2);
                                        }else if((0xff & bytes[3])>= 32 || (0xff & bytes[4])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_1,(byte)3);
                                        }


                                        if((0xff & bytes[7])< 28 || (0xff & bytes[8])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_2,(byte)1);
                                        }else if((((0xff & bytes[7])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[8])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_2,(byte)2);
                                        }else if((0xff & bytes[7])>= 32 || (0xff & bytes[8])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_2,(byte)3);
                                        }

                                        if((0xff & bytes[11])< 28 || (0xff & bytes[12])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_3,(byte)1);
                                        }else if((((0xff & bytes[11])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[12])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_3,(byte)2);
                                        }else if((0xff & bytes[11])>= 32 || (0xff & bytes[12])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_3,(byte)3);
                                        }

                                        if((0xff & bytes[15])< 28 || (0xff & bytes[16])< 28){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_4,(byte)1);
                                        }else if((((0xff & bytes[15])>= 28)&&((0xff & bytes[3])<32)) || (((0xff & bytes[16])>= 28)&&((0xff & bytes[4])<32))){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_4,(byte)2);
                                        }else if((0xff & bytes[15])>= 32 || (0xff & bytes[16])>= 32){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_4,(byte)3);
                                        }
                                    }

                                    if(temp_humi_cnt == -1 || temp_humi_cnt == 60){
                                        Logger.i("House04 = 1("+(0xff & bytes[3])+"/"+(0xff & bytes[4])+","+KeyControl.getHouse04_Nozzle01()+
                                                ")  2("+(0xff & bytes[7])+"/"+(0xff & bytes[8])+","+KeyControl.getHouse04_Nozzle02()+
                                                ")  3("+(0xff & bytes[11])+"/"+(0xff & bytes[12])+","+KeyControl.getHouse04_Nozzle03()+
                                                ")  4("+(0xff & bytes[15])+"/"+(0xff & bytes[16])+","+KeyControl.getHouse04_Nozzle04()+")");
                                    }



                                    if(temp_humi_cnt >= 60){
                                        temp_humi_cnt = 0;
                                    }else{
                                        temp_humi_cnt= temp_humi_cnt+1;
                                    }

                                }



                            }




                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(bytes[1]==SerialModel.House01and02_Com_Aire_Pump_Receive){  // House01 02 콤 펌프 응답

                        Log.d(TAG, "종류：" + "SerialModel.House01and02_Com_Aire_Pump_Receive");

                        try {
                            byte[] data = new byte[]{bytes[3],bytes[4]};
                            byte[] crc = new byte[]{bytes[5],bytes[6]};

                            if(CRC16Util.receiveData(data,crc)){
                                if(bytes[3]==0x00 && bytes[4]== 0x00){      // 중지

                                    if(!Pause_End){
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Pump_Start,null);
                                    }else{
                                        Pause_End = false;
                                        Intent intent2 = new Intent("Page");
                                        intent2.putExtra("index",0);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
                                    }


                                }else if(bytes[3]==0x01 && bytes[4]== 0x01){    // 시작
                                    viewModel.getCleanning_Running_Situation_2().postValue(0);
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);

                                    try {
                                        Thread.sleep(1000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    Start_Pump_time();
                                    Start_AirCompressor_time();
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(bytes[1]==SerialModel.House03and04_Com_Aire_Pump_Receive){  // House03 04 콤 펌프 응답

                        Log.d(TAG, "종류：" + "SerialModel.House03and04_Com_Aire_Pump_Receive");

                        try {
                            byte[] data = new byte[]{bytes[3],bytes[4]};
                            byte[] crc = new byte[]{bytes[5],bytes[6]};

                            if(CRC16Util.receiveData(data,crc)){
                                if(bytes[3]==0x00 && bytes[4]== 0x00){      // 중지

                                    if(!Pause_End){
                                        // 현재 상태가 Houst_Stop의 식을 실행 - air comp만 실행
                                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_Stop);
                                        viewModel.getCleanning_Running_Situation_2().postValue(3);
                                    }else{
                                        Pause_End = false;
                                        Intent intent2 = new Intent("Page");
                                        intent2.putExtra("index",0);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
                                    }

                                    try {
                                        Thread.sleep(1000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    if(KeyControl.getRepeat_PauseTime()>0){     // 정시 시간이 0보다 크면 실행. 아니면 바로 HOUSE01방제 실행
                                        Log.d(TAG, "종류：" + "SerialModel.House03and04_Com_Aire_Pump_Receive case 1");
                                        Start_AirCompressor_time();
                                    }else{
                                        Log.d(TAG, "종류：" + "SerialModel.House03and04_Com_Aire_Pump_Receive case 2");

                                        viewModel.getRepeat_PauseTime().postValue(KeyControl.getRepeat_PauseTime());
                                        viewModel.getRepeat_Cycle().postValue(viewModel.getRepeat_Cycle().getValue()-1);
                                        viewModel.getAirCompressor01and02_RunningTime().postValue(KeyControl.getAirCompressor01and02_RunningTime());
                                        viewModel.getPump01and02_RunningTime().postValue(KeyControl.getPump01and02_RunningTime());
                                        viewModel.getAirCompressor03and04_RunningTime().postValue(KeyControl.getAirCompressor03and04_RunningTime());
                                        viewModel.getPump03and04_RunningTime().postValue(KeyControl.getPump03and04_RunningTime());

                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Pump_Start,null);
                                    }
                                }else if(bytes[3]==0x01 && bytes[4]== 0x01){    // 시작
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);

                                    try {
                                        Thread.sleep(1000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    Start_Pump_time();
                                    Start_AirCompressor_time();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(bytes[1]==SerialModel.House01and02_Cleanner01_Receive){  // House01 02 에어청소 응답

                        Log.d(TAG, "종류：" + "SerialModel.House01and02_Cleanner01_Receive");

                        try {
                            byte[] data = new byte[]{bytes[3],bytes[4]};
                            byte[] crc = new byte[]{bytes[5],bytes[6]};

                            if(CRC16Util.receiveData(data,crc)){
                                if(bytes[3]==0x00){      // 중지

                                    if(!Pause_End){
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Start,null);
                                    }else{
                                        Pause_End = false;
                                        Intent intent2 = new Intent("Page");
                                        intent2.putExtra("index",0);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
                                    }

                                }else if(bytes[3]==0x01){    // 시작
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                                    viewModel.getCleanning_Running_Situation_2().postValue(1);

                                    try {
                                        Thread.sleep(1000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    viewModel.getRepeat_PauseTime().postValue(0);       // 휴지시간 0설정
                                    Start_Cleanner_time();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(bytes[1]==SerialModel.House03and04_Cleanner01_Receive){  // House03 04 에어청소 응답

                        Log.d(TAG, "종류：" + "SerialModel.House03and04_Cleanner01_Receive");

                        try {
                            byte[] data = new byte[]{bytes[3],bytes[4]};
                            byte[] crc = new byte[]{bytes[5],bytes[6]};

                            if(CRC16Util.receiveData(data,crc)){
                                if(bytes[3]==0x00){      // 중지

                                    if(!Pause_End){
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
                                    }else{
                                        Pause_End = false;
                                        Intent intent2 = new Intent("Page");
                                        intent2.putExtra("index",0);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
                                    }
                                }else if(bytes[3]==0x01){    // 시작

                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                                    viewModel.getCleanning_Running_Situation_2().postValue(1);

                                    try {
                                        Thread.sleep(1000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    Start_Cleanner_time();
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(bytes[1]==SerialModel.House01and02_Cleanner02_Receive){  // House01 02 물청소 응답

                        Log.d(TAG, "종류：" + "SerialModel.House01and02_Cleanner02_Receive");

                        try {
                            byte[] data = new byte[]{bytes[3]};
                            byte[] crc = new byte[]{bytes[4],bytes[5]};

                            if(CRC16Util.receiveData(data,crc)){
                                if(bytes[3]==0x01){ // 시작시
                                    if(viewModel.getHouse01and02_Cleaning02_Time().getValue()==0){
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                    }else{
                                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                                        viewModel.getCleanning_Running_Situation_2().postValue(2);

                                        try {
                                            Thread.sleep(1000);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        Start_Cleanner_time();
                                    }
                                }else{  // 정지시

                                    if(!Pause_End){
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                    }else{
                                        Pause_End = false;
                                        Intent intent2 = new Intent("Page");
                                        intent2.putExtra("index",0);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
                                    }

                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if(bytes[1]==SerialModel.House03and04_Cleanner02_Receive){  // House03 04 물청소 응답

                        Log.d(TAG, "종류：" + "SerialModel.House03and04_Cleanner02_Receive");

                        try {
                            byte[] data = new byte[]{bytes[3]};
                            byte[] crc = new byte[]{bytes[4],bytes[5]};

                            if(CRC16Util.receiveData(data,crc)){
                                if(bytes[3]==0x01){ // 시작시
                                    if(viewModel.getHouse03and04_Cleaning02_Time().getValue()==0){

                                        Intent intent = new Intent("Page");
                                        intent.putExtra("index",0);
                                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                    }else{
                                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                                        viewModel.getCleanning_Running_Situation_2().postValue(2);

                                        try {
                                            Thread.sleep(1000);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        Start_Cleanner_time();
                                    }
                                }else{  // 정지시

                                    if(Pause_End){
                                        Pause_End = false;
                                    }

                                    viewModel.getCleanning_Running_Situation_2().postValue(0);
                                    Intent intent = new Intent("Page");
                                    intent.putExtra("index",0);
                                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if(bytes[1]==SerialModel.Stortage_Water) { // 물부족

                        Log.d(TAG, "종류：" + "SerialModel.Stortage_Water");

                        try {
                            byte[] data = new byte[]{bytes[3]};
                            byte[] crc = new byte[]{bytes[4],bytes[5]};

                            if(CRC16Util.receiveData(data,crc)){
                                viewModel.getWater_Shortage().postValue(true);

                                AlarmDialogShow("물부족","진행 도중 물이 부족하여 상태가 초기화됩니다.");

                                Log.d(TAG,"물부족");


                                Intent intent = new Intent("End");
                                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }




            }

            @Override
            public void onDataSent(byte[] bytes) {  // 전송시
                Log.d(TAG, "직렬 데이터 전송：" + byteArrayToHex(bytes));


                switch (bytes[0]){

                    // 시작전 상태 물어보기
//                    case 0x27:
//                        Log.d(TAG,"data type : 27");
//
//                        AlarmDialogShow("물부족","물이 부족하여 시작이 불가능합니다.");
//
//                        break;



                        // 하우스1-2 시작
//                    case 0x01:
//
//                        Log.d(TAG,"data type : 1");
//                        viewModel.getCleanning_Running_Situation_2().postValue(0);
//                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
//
//                        try {
//                            Thread.sleep(1000);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                        Start_Pump_time();
//                        Start_AirCompressor_time();
//                        break;

                        // 하우스3-4 시작
//                    case 0x03:
//                        Log.d(TAG,"data type : 3");
//
//                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
//
//                        try {
//                            Thread.sleep(1000);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                        Start_Pump_time();
//                        Start_AirCompressor_time();
//                        break;


                    // 하우스1-2 중지
//                    case 0x05:
//                        Log.d(TAG,"data type : 5");
//                        SerialPortManager.getInstance().sendBytes(new byte[]{0x03});
//                        break;

                    // 하우스3-4 중지
//                    case 0x07:
//                        Log.d(TAG,"data type : 7");
//
//                        // 현재 상태가 Houst_Stop의 식을 실행 - air comp만 실행
//                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_Stop);
//                        viewModel.getCleanning_Running_Situation_2().postValue(3);
//
//                        try {
//                            Thread.sleep(1000);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                        if(KeyControl.getRepeat_PauseTime()>0){     // 정시 시간이 0보다 크면 실행. 아니면 바로 HOUSE01방제 실행
//                            Start_AirCompressor_time();
//                        }else{
//                            SerialPortManager.getInstance().sendBytes(new byte[]{0x01});
//                        }
//
//                        break;


                    // 하우스1-2 청소01
//                    case 0x09:
//                        Log.d(TAG,"data type : 9");
//                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
//                        viewModel.getCleanning_Running_Situation_2().postValue(1);
//
//                        try {
//                            Thread.sleep(1000);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//                        viewModel.getRepeat_PauseTime().postValue(0);       // 휴지시간 0설정
//                        Start_Cleanner_time();
//                        break;
                    // 하우스1-2 청소02
//                    case 0x10:
//                        Log.d(TAG,"data type : 10");
//
//                        if(viewModel.getHouse01and02_Cleaning02_Time().getValue()==0){
//                            SerialPortManager.getInstance().sendBytes(new byte[]{0x14});
//                        }else{
//                            viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
//                            viewModel.getCleanning_Running_Situation_2().postValue(2);
//
//                            try {
//                                Thread.sleep(1000);
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                            Start_Cleanner_time();
//                        }
//
//
//                        break;

                    // 하우스3-4 청소01
//                    case 0x13:
//                        Log.d(TAG,"data type : 13");
//
//
//                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
//                        viewModel.getCleanning_Running_Situation_2().postValue(1);
//
//                        try {
//                            Thread.sleep(1000);
//                        }catch (Exception e){
//                            e.printStackTrace();
//                        }
//
//                        Start_Cleanner_time();
//                        break;
                    // 하우스3-4 청소02
//                    case 0x14:
//
//
//                        if(viewModel.getHouse03and04_Cleaning02_Time().getValue()==0){
//
//                            Intent intent = new Intent("Page");
//                            intent.putExtra("index",0);
//                            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                        }else{
//                            Log.d(TAG,"data type : 14");
//                            viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
//                            viewModel.getCleanning_Running_Situation_2().postValue(2);
//
//                            try {
//                                Thread.sleep(1000);
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                            Start_Cleanner_time();
//                        }
//
//
//                        break;



                    // 하우스1-2 청소01 중지
//                    case 0x17:
//                        Log.d(TAG,"data type : 17");
//                        SerialPortManager.getInstance().sendBytes(new byte[]{0x13});
//                        break;
                    // 하우스1-2 청소02 중지
//                    case 0x18:
//                        Log.d(TAG,"data type : 18");
//                        SerialPortManager.getInstance().sendBytes(new byte[]{0x14});
//                        break;

                    // 하우스3-4 청소01 중지
//                    case 0x21:
//                        Log.d(TAG,"data type : 21");
//                        SerialPortManager.getInstance().sendBytes(new byte[]{0x10});
//                        break;
                    // 하우스3-4 청소02 중지
//                    case 0x22:
//                        Log.d(TAG,"data type : 22");
//                        viewModel.getCleanning_Running_Situation_2().postValue(0);
//                        Intent intent = new Intent("Page");
//                        intent.putExtra("index",0);
//                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
//                        break;


//                    // 온습도 가져오기
//                    case 0x25:
//                        Log.d(TAG,"data type : 25");
//                        Log.d(TAG,"get Temp, Humi");
//
//                        if(Boolean.TRUE.equals(viewModel.getHouse01_Nozzle_Auto().getValue())){
//
//                        }
//
//                        if(Boolean.TRUE.equals(viewModel.getHouse02_Nozzle_Auto().getValue())){
//
//                        }
//
//                        if(Boolean.TRUE.equals(viewModel.getHouse03_Nozzle_Auto().getValue())){
//
//                        }
//
//                        if(Boolean.TRUE.equals(viewModel.getHouse04_Nozzle_Auto().getValue())){
//
//                        }
//
//                        break;

                    // 물부족
//                    case 0x26:
//
//                        viewModel.getWater_Shortage().postValue(true);
//
//
//                        AlarmDialogShow("물부족","진행 도중 물이 부족하여 상태가 초기화됩니다.");
//
//
//                        Log.d(TAG,"data type : 26");
//                        Log.d(TAG,"물부족");
//                        Intent intent2 = new Intent("Page");
//                        intent2.putExtra("index",0);
//                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent2);
//                        break;


                }
            }
        });

//        byte[] data = new byte[10];
//        data[0] = (byte) 0xA5;
//        data[1] = 0x5A;
//        data[2] = 0x01;
//        data[3] = 0x01;

        //  or

//        String Message = "hi";
//        byte[] data2 = Message.getBytes();


//        SerialPortManager.getInstance().sendBytes(data);


        // 중앙 시간 - 1초
        Start_Center_time();

        // 온습도 읽어오기
        Start_TempHumi_time();


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SerialPortManager.getInstance().closeSerialPort();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(MainReceiver);
        Stop_Center_time();
        Stop_TempHumi_time();

        Stop_AirCompressor_time();
        Stop_Pump_time();
        Stop_Cleanner_time();

        DismissLoadingDialog();
        Stop_Command_time();
    }

    private void Start_Center_time(){
        center_timer = new Timer();
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                viewModel.getMain_time_tv().postValue(returnTime());

            }
        };

        center_timer.schedule(right_timerTask,0,30000);
    }

    private void Stop_Center_time(){
        if(center_timer != null){
            center_timer.cancel();
            center_timer = null;
        }
    }


    private void Start_TempHumi_time(){
        if(temphumi_timer != null){
            return;
        }

        temphumi_timer = new Timer();
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_House_01_TH,null);
            }
        };

        temphumi_timer.schedule(right_timerTask,1000,temp_humi_timer*1000L);
    }

    private void Stop_TempHumi_time(){
        if(temphumi_timer != null){
            temphumi_timer.cancel();
            temphumi_timer = null;
        }
    }


    private String returnTime(){
//        long now = System.currentTimeMillis();
//        Date date = new Date(now);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
        ZonedDateTime zonedDateTime ;
        DateTimeFormatter formatter;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            zonedDateTime = ZonedDateTime.now(java.time.ZoneId.of("Asia/Seoul"));
            // 날짜 형식 지정
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm",Locale.KOREAN);

            return zonedDateTime.format(formatter);
        }else{
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.KOREAN);
            return sdf.format(date);
        }




    }

    private void Start_AirCompressor_time(){
        if(AirCompressor_timer != null){
            return;
        }

        AirCompressor_timer = new Timer();
        TimerTask right_timerTask = null;

        Log.d(TAG,"AirCompressor situation : "+viewModel.getCleanning_Running_Situation_1().getValue());

        switch (Objects.requireNonNull(viewModel.getCleanning_Running_Situation_1().getValue())){
            case House_01_02:

                right_timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        int time = viewModel.getAirCompressor01and02_RunningTime().getValue();


                        viewModel.getAirCompressor01and02_RunningTime().postValue(time-1);

                        Log.d(TAG,"House01_02 AirCompressor "+ time);

                        if(time<=1){
                            Stop_AirCompressor_time();

                            viewModel.getAirCompressor01and02_RunningTime().postValue(0);
//                            viewModel.getAirCompressor01_RunningTime().postValue(KeyControl.getAirCompressor01_RunningTime());
//                            viewModel.getPump01_RunningTime().postValue(KeyControl.getPump01_RunningTime());
                            
                            // 시리얼 중지 보내기   -- 중지 완료 하면 다시 재실행. house_stop일 경우 반복횟수 및 휴지시간 체크후 House_01로 변경후 다시 실행
//                            SerialPortManager.getInstance().sendBytes(new byte[]{0x05});
                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Stop,null);
                        }
                    }
                };
                break;

            case House_03_04:
                right_timerTask = new TimerTask() {
                    @Override
                    public void run() {

                        int time = viewModel.getAirCompressor03and04_RunningTime().getValue();

                        viewModel.getAirCompressor03and04_RunningTime().postValue(time-1);

                        Log.d(TAG,"House03_04 AirCompressor "+ time);

                        if(time<=1){
                            Stop_AirCompressor_time();
                            viewModel.getAirCompressor03and04_RunningTime().postValue(0);
//                            viewModel.getAirCompressor03_RunningTime().postValue(KeyControl.getAirCompressor03_RunningTime());
//                            viewModel.getPump03_RunningTime().postValue(KeyControl.getPump03_RunningTime());


                            Log.d(TAG,"남은 반복횟수 "+ viewModel.getRepeat_Cycle().getValue());

                            if(viewModel.getRepeat_Cycle().getValue()>0){
                                // 시리얼 중지 보내기
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x07});

                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Stop,null);
                            }else{
                                // 청소동작으로 넘어가야하는데..
                                viewModel.getRepeat_Cycle().postValue(0);

                                if(viewModel.getHouse01and02_Cleaning01_Time().getValue()>2){
                                    // 콤프 펌프 둘다 0x01
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Start,null);
                                }else{
                                    // 콤프만 0x01 펌프는 0x00
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Start,null);
                                }
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x09});
                            }

                        }
                    }
                };
                break;

            case House_Stop:
                right_timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG,"House_Stop AirCompressor");

                        int time = viewModel.getRepeat_PauseTime().getValue();

                        viewModel.getRepeat_PauseTime().postValue(time-1);

                        if(time<=1){
                            Stop_AirCompressor_time();


                            viewModel.getRepeat_PauseTime().postValue(KeyControl.getRepeat_PauseTime());


                            // 반복 횟수에 따른 하우스 01부터 다시 시작

//                            if(viewModel.getRepeat_Cycle().getValue()>1){
//                                viewModel.getRepeat_Cycle().postValue(viewModel.getRepeat_Cycle().getValue()-1);
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x01});
//                            }else{
//                                // 청소동작으로 넘어가야하는데..
//                                viewModel.getRepeat_Cycle().postValue(KeyControl.getRepeat_Cycle());
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x09});
//                            }

                            viewModel.getRepeat_Cycle().postValue(viewModel.getRepeat_Cycle().getValue()-1);
//                            SerialPortManager.getInstance().sendBytes(new byte[]{0x01});

                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Pump_Start,null);


                        }else{

                            viewModel.getAirCompressor01and02_RunningTime().postValue(KeyControl.getAirCompressor01and02_RunningTime());
                            viewModel.getPump01and02_RunningTime().postValue(KeyControl.getPump01and02_RunningTime());
                            viewModel.getAirCompressor03and04_RunningTime().postValue(KeyControl.getAirCompressor03and04_RunningTime());
                            viewModel.getPump03and04_RunningTime().postValue(KeyControl.getPump03and04_RunningTime());

                        }
                    }
                };
                break;
        }


        AirCompressor_timer.schedule(right_timerTask,Comp_Pump_Cleanner_time*1000L,Comp_Pump_Cleanner_time*1000L);
    }

    private void Stop_AirCompressor_time(){
        if(AirCompressor_timer != null){
            Log.d(TAG,"AirCompressor");
            AirCompressor_timer.cancel();
            AirCompressor_timer = null;
        }
    }

    private void Start_Pump_time(){
        if(Pump_timer != null){
            return;
        }

        Pump_timer = new Timer();
        TimerTask right_timerTask = null;

        Log.d(TAG,"Pump situation : "+viewModel.getCleanning_Running_Situation_1().getValue());

        switch (Objects.requireNonNull(viewModel.getCleanning_Running_Situation_1().getValue())){
            case House_01_02:
                right_timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG,"House01_02 Pump");

                        int time = viewModel.getPump01and02_RunningTime().getValue();

                        if(time<=1){
                            viewModel.getPump01and02_RunningTime().postValue(0);
//                            viewModel.getPump01_RunningTime().postValue(KeyControl.getPump01_RunningTime());
                            Stop_Pump_time();
                            if(!KeyControl.getAirCompressor01and02_RunningTime().equals(KeyControl.getPump01and02_RunningTime())){
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Pump_Stop,null);
                            }
                        }
                        else{
                            viewModel.getPump01and02_RunningTime().postValue(time-1);
                        }
                    }
                };
                break;

            case House_03_04:
                right_timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG,"House03_04 Pump");

                        int time = viewModel.getPump03and04_RunningTime().getValue();



                        if(time<=1){
                            viewModel.getPump03and04_RunningTime().postValue(0);
//                            viewModel.getPump03_RunningTime().postValue(KeyControl.getPump03_RunningTime());
                            Stop_Pump_time();

                            if(!KeyControl.getAirCompressor03and04_RunningTime().equals(KeyControl.getPump03and04_RunningTime())){
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Pump_Stop,null);
                            }

                        }
                        else{
                            viewModel.getPump03and04_RunningTime().postValue(time-1);
                        }
                    }
                };
                break;


        }

        Pump_timer.schedule(right_timerTask,Comp_Pump_Cleanner_time*1000L,Comp_Pump_Cleanner_time*1000L);
    }

    private void Stop_Pump_time(){
        if(Pump_timer != null){
            Log.d(TAG,"Pump_Stop");
            Pump_timer.cancel();
            Pump_timer = null;
        }
    }


    private void Start_Cleanner_time(){
        if(Cleanner_timer != null) {
            return;
        }

        Cleanner_timer = new Timer();
        TimerTask right_timerTask = null;

        switch (Objects.requireNonNull(viewModel.getCleanning_Running_Situation_1().getValue())){
            case House_01_02:
                right_timerTask = new TimerTask() {
                    @Override
                    public void run() {


                        if(!CleannerType){

                            Log.d(TAG,"House01_02 Cleaning01");
                            int time = viewModel.getHouse01and02_Cleaning01_Time().getValue();



                            if(time<=3 && viewModel.getHouse01and02_Cleaning01_Time().getValue()>=3){
                                //펌프 끄기
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Pump_Stop,null);
                            }


                            if(time<=1){
                                Stop_Cleanner_time();
                                viewModel.getHouse01and02_Cleaning01_Time().postValue(0);
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x17});
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Stop,null);
                            }else{
                                viewModel.getHouse01and02_Cleaning01_Time().postValue(time-1);
                            }
                        }else{

                            Log.d(TAG,"House01_02 Cleaning02");
                            int time = viewModel.getHouse01and02_Cleaning02_Time().getValue();



                            if(time <= 1){

                                Stop_Cleanner_time();
                                viewModel.getHouse01and02_Cleaning02_Time().postValue(0);
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x18});
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Stop,null);
                            }else{
                                viewModel.getHouse01and02_Cleaning02_Time().postValue(time-1);
                            }
                        }




                    }
                };
                break;

            case House_03_04:
                right_timerTask = new TimerTask() {
                    @Override
                    public void run() {


                        if(!CleannerType){

                            Log.d(TAG,"House03_04 Cleaning01");
                            int time = viewModel.getHouse03and04_Cleaning01_Time().getValue();

                            if(time<=3 && viewModel.getHouse03and04_Cleaning01_Time().getValue()>=3){
                                //펌프 끄기
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Pump_Stop,null);
                            }

                            if(time<=1){
                                Stop_Cleanner_time();

                                CleannerType = true;
                                viewModel.getHouse03and04_Cleaning01_Time().postValue(0);
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x21});
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Stop,null);
                            }else{
                                viewModel.getHouse03and04_Cleaning01_Time().postValue(time-1);
                            }

                        }else{

                            Log.d(TAG,"House03_04 Cleaning02");
                            int time = viewModel.getHouse03and04_Cleaning02_Time().getValue();

                            if(time <= 1){
                                Stop_Cleanner_time();

//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x22});
                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Stop,null);
                            }else{
                                viewModel.getHouse03and04_Cleaning02_Time().postValue(time-1);
                            }
                        }
                    }
                };
                break;


        }


        Cleanner_timer.schedule(right_timerTask,Comp_Pump_Cleanner_time*1000L,Comp_Pump_Cleanner_time*1000L);
    }

    private void Stop_Cleanner_time(){
        if(Cleanner_timer != null){
            Cleanner_timer.cancel();
            Cleanner_timer = null;
        }
    }

    private void Start_Command_time(String msg){
        
        if(command_timer != null){
            return;
        }

        ShowLoadingDialog(msg);
        command_timer = new Timer();
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {
                DismissLoadingDialog();
                Handler mHandler = new Handler(Looper.getMainLooper());
                mHandler.postDelayed(() -> {
                    // 사용하고자 하는 코드
                    AlarmDialogShow("기기 무응답","메시지에 응답이 없습니다. 기기 연결 상태를 확인해주세요.");
                }, 0);

                Stop_Command_time();
            }
        };

        command_timer.schedule(right_timerTask,command_timer_time*1000L,command_timer_time*1000L);
    }


    private void Stop_Command_time(){
        DismissLoadingDialog();
        
        if(command_timer != null){
            command_timer.cancel();
            command_timer = null;
        }
    }

    private void EndCleaning(){
        CleannerType = false;

        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
        viewModel.getCleanning_Running_Situation_2().postValue(0);

        viewModel.getRepeat_Cycle().postValue(KeyControl.getRepeat_Cycle());
        viewModel.getRepeat_PauseTime().postValue(KeyControl.getRepeat_PauseTime());

        viewModel.getAirCompressor01and02_RunningTime().postValue(KeyControl.getAirCompressor01and02_RunningTime());
        viewModel.getAirCompressor03and04_RunningTime().postValue(KeyControl.getAirCompressor03and04_RunningTime());

        viewModel.getPump01and02_RunningTime().postValue(KeyControl.getPump01and02_RunningTime());
        viewModel.getPump03and04_RunningTime().postValue(KeyControl.getPump03and04_RunningTime());

        viewModel.getHouse01and02_Cleaning01_Time().postValue(KeyControl.getHouse01and02_Cleaning01());
        viewModel.getHouse03and04_Cleaning01_Time().postValue(KeyControl.getHouse03and04_Cleaning01());

        viewModel.getHouse01and02_Cleaning02_Time().postValue(KeyControl.getHouse01and02_Cleaning02());
        viewModel.getHouse03and04_Cleaning02_Time().postValue(KeyControl.getHouse03and04_Cleaning02());

    }

    public void AlarmDialogShow(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title).setMessage(Message);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.dismiss();
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void DismissLoadingDialog(){
        if(loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    private void ShowLoadingDialog(String msg){
        if(!loadingDialog.isShowing()){
            loadingDialog.show();
        }

        loadingDialog.setText(msg);

    }

    public String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for(final byte b: a)
            sb.append(String.format("%02x ", b&0xff));
        return sb.toString();
    }


}