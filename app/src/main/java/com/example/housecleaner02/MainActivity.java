package com.example.housecleaner02;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.housecleaner02.activity.HouseNozzleDialog;
import com.example.housecleaner02.activity.HouseSettingDialog;
import com.example.housecleaner02.activity.HouseTHDialog;
import com.example.housecleaner02.activity.LoadingDialog;
import com.example.housecleaner02.activity.THSettingDialog;
import com.example.housecleaner02.activity.TimeSettingDialog;
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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

    private final int command_timer_time = 8; // 메시지 제한시간단위 (초)
    
    private Boolean CleannerType = false;   // 하우스 청소동작 1,2 여부 false = 1 , true = 2

    private Timer temphumi_timer = null;   // 온습도 타이머s

    private Timer start_time_timer = null;    // 확정 시작시간 설정 타이머
    private Timer end_time_timer = null;    // 확정 종료시간 설정 타이머

    private final int temp_humi_timer = 60;   // 온습도 요청시간단위(초)

    private final int temp_humi_logging_cnt = 30;   // 온습도 요청의 몇번 중 한번 로그로 기록을 하는가 (번)

    private int temp_humi_cnt = -1;
    
    private final int Comp_Pump_Cleanner_time = 60;    // 컴프,콤프,청소 시간단위(초)
    
    private Boolean first_info = true; // 처음 시작될때 정보물어보기, 아닐경우 시작

//    private Boolean Pause_End = false; // 처음 시작될때 정보물어보기, 아닐경우 시작

    private int TH_Step = 0;

    LoadingDialog loadingDialog ;

    private ViewPager2 mViewPager;

    private ConstraintLayout House_01_02,House_03_04;

    private ConstraintLayout House01_NozzleAuto,House02_NozzleAuto,House03_NozzleAuto,House04_NozzleAuto;       // 노즐

    private ConstraintLayout House01_TH,House02_TH,House03_TH,House04_TH;   // 온습도

    private TextView Main_Time,Main_water_shortage;
    private ImageView Main_TH_Setting;

//    private TextView House01_Temp_tv,House01_Humi_tv,House02_Temp_tv,House02_Humi_tv,House03_Temp_tv,House03_Humi_tv,House04_Temp_tv,House04_Humi_tv;
    private TextView AirCompressor01and02_RunningTime_tv,Pump01and02_RunningTime_tv,House01and02_Cleaning01_Time_tv,House01and02_Cleaning02_Time_tv;
    private TextView AirCompressor03and04_RunningTime_tv,Pump03and04_RunningTime_tv,House03and04_Cleaning01_Time_tv,House03and04_Cleaning02_Time_tv;
    private TextView House01_NozzleAuto_bool,House02_NozzleAuto_bool,House03_NozzleAuto_bool,House04_NozzleAuto_bool;

    private boolean isRealEndCom = false;

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

                        if(viewModel.getEnd_Time_Sw().getValue()){
                            Start_start_time();
                            Start_end_time();

                            mViewPager.setCurrentItem(index);
                            return;
                        }

                        Start_Running();

                    }


                    if(index == 0){ // 종료시

                        StopRunningThreadSetting();
                        StopTimeThread();
                        viewModel.getIsRunning().postValue(false);
                        Logger.i("Running End");
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
//                    Pause_End = true;
//
//                    DeviceModel s_1 = viewModel.getCleanning_Running_Situation_1().getValue();
//                    int s_2 = viewModel.getCleanning_Running_Situation_2().getValue();
//
//                    Log.d(TAG,"End : "+ s_1 + ", "+ s_2);
//
//                    if(s_1.equals(DeviceModel.House_01_02) && s_2 == 0){
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Stop,null);
//                    }else if(s_1.equals(DeviceModel.House_01_02) && s_2 == 1){
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Stop,null);
//                    }else if(s_1.equals(DeviceModel.House_01_02) && s_2 == 2){
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Stop,null);
//                    }else if(s_1.equals(DeviceModel.House_03_04) && s_2 == 0){
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Stop,null);
//                    }else if(s_1.equals(DeviceModel.House_03_04) && s_2 == 1){
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Stop,null);
//                    }else if(s_1.equals(DeviceModel.House_03_04) && s_2 == 2){
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Stop,null);
//                    }else{
//                        Pause_End = false;
//
//                        ToZeroPage();
//                    }

                    isRealEndCom = true;

                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);

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

        checkPermissions();

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
        sfilter01.addAction("End");
        LocalBroadcastManager.getInstance(this).registerReceiver(MainReceiver,sfilter01);



        mViewPager = findViewById(R.id.viewPager);

        Main_Time = findViewById(R.id.main_time);
        Main_water_shortage = findViewById(R.id.main_water_shortage);
        Main_TH_Setting = findViewById(R.id.main_TH_setting);

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

        Main_Time.setOnClickListener(v -> {

            if(Boolean.TRUE.equals(viewModel.getIsRunning().getValue())){

                Toast.makeText(getApplicationContext(),"작동중 수정이 불가능합니다. 기기를 멈춘 후 다시 시도해주세요.",Toast.LENGTH_LONG).show();
                return;
            }


            TimeSettingDialog thSettingDialog = new TimeSettingDialog(this);
            thSettingDialog.setTimeSettingDialogListener((start_hour,start_min,end_hour,end_min) -> {
                viewModel.setStart_Time_Picker_Hour(start_hour);
                viewModel.setStart_Time_Picker_Min(start_min);
                viewModel.setEnd_Time_Picker_Hour(end_hour);
                viewModel.setEnd_Time_Picker_Min(end_min);


                Log.i(TAG,"Time Setting Changed : Start = "+start_hour+" : "+ start_min+", End = "+end_hour+" : "+ end_min);

                Logger.i("Time Setting Changed : Start = "+start_hour+" : "+ start_min+", End = "+end_hour+" : "+ end_min);
            });
            thSettingDialog.show();
        });

        Main_TH_Setting.setOnClickListener(v -> {
            THSettingDialog thSettingDialog = new THSettingDialog(this,KeyControl.getDeviation(),KeyControl.getTh_main());
            thSettingDialog.setTHSettingDialogListener((Deviation,Th_main) -> {
                KeyControl.setDeviation(Deviation);
                KeyControl.setTh_main(Th_main);

                Logger.i("Auto Nozzle Setting Changed : Deviation = "+KeyControl.getDeviation()+", TH_Max = "+KeyControl.getTh_main());
            });
            thSettingDialog.show();
        });


        House_01_02.setOnClickListener(v -> {
            if(Boolean.FALSE.equals(viewModel.getIsRunning().getValue())){
                Log.d(TAG,"House01_02");
                HouseSettingDialog houseSettingDialog = new HouseSettingDialog(this,DeviceModel.House_01_02,viewModel.getAirCompressor01and02_RunningTime().getValue(),viewModel.getPump01and02_RunningTime().getValue(),viewModel.getHouse01and02_Cleaning01_Time().getValue(),viewModel.getHouse01and02_Cleaning02_Time().getValue());
                houseSettingDialog.setHouseSettingDialogListener((AirCompressor_RunningTime, Pump_RunningTime, House_Cleaning01_Time, House_Cleaning02_Time) -> {
                    boolean change_setting = false;

                    if(!viewModel.getAirCompressor01and02_RunningTime().getValue().equals(AirCompressor_RunningTime)){
                        viewModel.getAirCompressor01and02_RunningTime().postValue(AirCompressor_RunningTime);
                        KeyControl.setAirCompressor01and02_RunningTime(AirCompressor_RunningTime);
                        change_setting = true;
                    }

                    if(!viewModel.getPump01and02_RunningTime().getValue().equals(Pump_RunningTime)){
                        viewModel.getPump01and02_RunningTime().postValue(Pump_RunningTime);
                        KeyControl.setPump01and02_RunningTime(Pump_RunningTime);
                        change_setting = true;
                    }

                    if(!viewModel.getHouse01and02_Cleaning01_Time().getValue().equals(House_Cleaning01_Time)){
                        viewModel.getHouse01and02_Cleaning01_Time().postValue(House_Cleaning01_Time);
                        KeyControl.setHouse01and02_Cleaning01(House_Cleaning01_Time);
                        change_setting = true;
                    }

                    if(!viewModel.getHouse01and02_Cleaning02_Time().getValue().equals(House_Cleaning02_Time)){
                        viewModel.getHouse01and02_Cleaning02_Time().postValue(House_Cleaning02_Time);
                        KeyControl.setHouse01and02_Cleaning02(House_Cleaning02_Time);
                        change_setting = true;
                    }

                    if(change_setting){
                        StringBuilder insertSetting = new StringBuilder();
                        insertSetting.append("House_01_02 Time Settings Changed : AirComp = ")
                                .append(AirCompressor_RunningTime)
                                .append("Pump = ")
                                .append(Pump_RunningTime)
                                .append("Cleaning01 = ")
                                .append(House_Cleaning01_Time)
                                .append("Cleaning02 = ")
                                .append(House_Cleaning02_Time);
                        Logger.i(String.valueOf(change_setting));
                    }

                });
                houseSettingDialog.show();
            }else{
                Toast.makeText(getApplicationContext(),"작동중 수정이 불가능합니다. 기기를 멈춘 후 다시 시도해주세요.",Toast.LENGTH_LONG).show();
            }

        });


        House_03_04.setOnClickListener(v -> {
//            if(Boolean.FALSE.equals(viewModel.getIsRunning().getValue())){
//                Log.d(TAG,"House03_04");
//                HouseSettingDialog houseSettingDialog = new HouseSettingDialog(this,DeviceModel.House_03_04,viewModel.getAirCompressor03and04_RunningTime().getValue(),viewModel.getPump03and04_RunningTime().getValue(),viewModel.getHouse03and04_Cleaning01_Time().getValue(),viewModel.getHouse03and04_Cleaning02_Time().getValue());
//                houseSettingDialog.setHouseSettingDialogListener((AirCompressor_RunningTime, Pump_RunningTime, House_Cleaning01_Time, House_Cleaning02_Time) -> {
//                    boolean change_setting = false;
//
//                    if(!viewModel.getAirCompressor03and04_RunningTime().getValue().equals(AirCompressor_RunningTime)){
//                        viewModel.getAirCompressor03and04_RunningTime().postValue(AirCompressor_RunningTime);
//                        KeyControl.setAirCompressor03and04_RunningTime(AirCompressor_RunningTime);
//                        change_setting = true;
//                    }
//
//                    if(!viewModel.getPump03and04_RunningTime().getValue().equals(Pump_RunningTime)){
//                        viewModel.getPump03and04_RunningTime().postValue(Pump_RunningTime);
//                        KeyControl.setPump03and04_RunningTime(Pump_RunningTime);
//                        change_setting = true;
//                    }
//
//                    if(!viewModel.getHouse03and04_Cleaning01_Time().getValue().equals(House_Cleaning01_Time)){
//                        viewModel.getHouse03and04_Cleaning01_Time().postValue(House_Cleaning01_Time);
//                        KeyControl.setHouse03and04_Cleaning01(House_Cleaning01_Time);
//                        change_setting = true;
//                    }
//
//                    if(!viewModel.getHouse03and04_Cleaning02_Time().getValue().equals(House_Cleaning02_Time)){
//                        viewModel.getHouse03and04_Cleaning02_Time().postValue(House_Cleaning02_Time);
//                        KeyControl.setHouse03and04_Cleaning02(House_Cleaning02_Time);
//                        change_setting = true;
//                    }
//
//                    if(change_setting){
//                        StringBuilder insertSetting = new StringBuilder();
//                        insertSetting.append("House_03_04 Time Settings Changed : AirComp = ")
//                                .append(AirCompressor_RunningTime)
//                                .append("Pump = ")
//                                .append(Pump_RunningTime)
//                                .append("Cleaning01 = ")
//                                .append(House_Cleaning01_Time)
//                                .append("Cleaning02 = ")
//                                .append(House_Cleaning02_Time);
//                        Logger.i(String.valueOf(change_setting));
//                    }
//                });
//                houseSettingDialog.show();
//            }else{
//                Toast.makeText(getApplicationContext(),"작동중 수정이 불가능합니다. 기기를 멈춘 후 다시 시도해주세요.",Toast.LENGTH_LONG).show();
//            }

        });



        House01_NozzleAuto.setOnClickListener(v -> {

            Log.d(TAG,"노즐 = "+KeyControl.getHouse01_Nozzle01()+","+KeyControl.getHouse01_Nozzle02()+","+KeyControl.getHouse01_Nozzle03()+","+KeyControl.getHouse01_Nozzle04()+","+KeyControl.getHouse01_Nozzle05()+","+KeyControl.getHouse01_Nozzle06()+","+KeyControl.getHouse01_Nozzle07()+","+KeyControl.getHouse01_Nozzle08());

            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_01, Boolean.TRUE.equals(viewModel.getHouse01_Nozzle_Auto().getValue()),KeyControl.getHouse01_Nozzle01(),KeyControl.getHouse01_Nozzle02(),KeyControl.getHouse01_Nozzle03(),KeyControl.getHouse01_Nozzle04(),KeyControl.getHouse01_Nozzle05(),KeyControl.getHouse01_Nozzle06(),KeyControl.getHouse01_Nozzle07(),KeyControl.getHouse01_Nozzle08());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04,Nozzle_05,Nozzle_06,Nozzle_07,Nozzle_08) -> {
                boolean isRunning = Boolean.TRUE.equals(viewModel.getIsRunning().getValue());
                boolean situation = Objects.equals(viewModel.getCleanning_Running_Situation_1().getValue(), DeviceModel.House_01_02)  && viewModel.getCleanning_Running_Situation_2().getValue() == 0;

                if(!auto) {

                    if (!KeyControl.getHouse01_Nozzle01().equals(Nozzle_01) && Nozzle_01 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_1, (byte) Nozzle_01);
                        KeyControl.setHouse01_Nozzle01(Nozzle_01);
                    }

                    if (!KeyControl.getHouse01_Nozzle02().equals(Nozzle_02) && Nozzle_02 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_2, (byte) Nozzle_02);
                        KeyControl.setHouse01_Nozzle02(Nozzle_02);
                    }

                    if (!KeyControl.getHouse01_Nozzle03().equals(Nozzle_03) && Nozzle_03 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_3, (byte) Nozzle_03);
                        KeyControl.setHouse01_Nozzle03(Nozzle_03);
                    }

                    if (!KeyControl.getHouse01_Nozzle04().equals(Nozzle_04) && Nozzle_04 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_4, (byte) Nozzle_04);
                        KeyControl.setHouse01_Nozzle04(Nozzle_04);
                    }

                    if (!KeyControl.getHouse01_Nozzle05().equals(Nozzle_05) && Nozzle_05 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_5, (byte) Nozzle_05);
                        KeyControl.setHouse01_Nozzle05(Nozzle_05);
                    }

                    if (!KeyControl.getHouse01_Nozzle06().equals(Nozzle_06) && Nozzle_06 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_6, (byte) Nozzle_06);
                        KeyControl.setHouse01_Nozzle06(Nozzle_06);
                    }
                    
                    if (!KeyControl.getHouse01_Nozzle07().equals(Nozzle_07) && Nozzle_07 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_7, (byte) Nozzle_07);
                        KeyControl.setHouse01_Nozzle07(Nozzle_07);
                    }

                    if (!KeyControl.getHouse01_Nozzle08().equals(Nozzle_08) && Nozzle_08 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_01_Nozzle_8, (byte) Nozzle_08);
                        KeyControl.setHouse01_Nozzle08(Nozzle_08);
                    }
                    
                }else{
                    int Deviation = KeyControl.getDeviation();
                    int Th_main = KeyControl.getTh_main();

                    if(Nozzle_01 != -1) KeyControl.setHouse01_Nozzle01(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp1_1().getValue(),SerialModel.House_01_Nozzle_1));
                    if(Nozzle_02 != -1) KeyControl.setHouse01_Nozzle02(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp2_1().getValue(),SerialModel.House_01_Nozzle_2));
                    if(Nozzle_03 != -1) KeyControl.setHouse01_Nozzle03(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp3_1().getValue(),SerialModel.House_01_Nozzle_3));
                    if(Nozzle_04 != -1) KeyControl.setHouse01_Nozzle04(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp4_1().getValue(),SerialModel.House_01_Nozzle_4));
                    if(Nozzle_05 != -1) KeyControl.setHouse01_Nozzle05(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp5_1().getValue(),SerialModel.House_01_Nozzle_5));
                    if(Nozzle_06 != -1) KeyControl.setHouse01_Nozzle06(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp6_1().getValue(),SerialModel.House_01_Nozzle_6));
                    if(Nozzle_07 != -1) KeyControl.setHouse01_Nozzle07(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp7_1().getValue(),SerialModel.House_01_Nozzle_7));
                    if(Nozzle_08 != -1) KeyControl.setHouse01_Nozzle08(TH_Set_Command(Th_main,Deviation,viewModel.getHouse01_Temp8_1().getValue(),SerialModel.House_01_Nozzle_8));
                }

                viewModel.setHouse01_Nozzle_Auto(auto);

            });
            houseNozzleDialog.show();
        });

        House02_NozzleAuto.setOnClickListener(v -> {
            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_02, Boolean.TRUE.equals(viewModel.getHouse02_Nozzle_Auto().getValue()),KeyControl.getHouse02_Nozzle01(),KeyControl.getHouse02_Nozzle02(),KeyControl.getHouse02_Nozzle03(),KeyControl.getHouse02_Nozzle04(),KeyControl.getHouse02_Nozzle05(),KeyControl.getHouse02_Nozzle06(),KeyControl.getHouse02_Nozzle07(),KeyControl.getHouse02_Nozzle08());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04,Nozzle_05,Nozzle_06,Nozzle_07,Nozzle_08) -> {
                boolean isRunning = Boolean.TRUE.equals(viewModel.getIsRunning().getValue());
                boolean situation = Objects.equals(viewModel.getCleanning_Running_Situation_1().getValue(), DeviceModel.House_01_02)  && viewModel.getCleanning_Running_Situation_2().getValue() == 0;

                if(!auto) {

                    if (!KeyControl.getHouse02_Nozzle01().equals(Nozzle_01) && Nozzle_01 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_1, (byte) Nozzle_01);
                        KeyControl.setHouse02_Nozzle01(Nozzle_01);
                    }

                    if (!KeyControl.getHouse02_Nozzle02().equals(Nozzle_02) && Nozzle_02 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_2, (byte) Nozzle_02);
                        KeyControl.setHouse02_Nozzle02(Nozzle_02);
                    }

                    if (!KeyControl.getHouse02_Nozzle03().equals(Nozzle_03) && Nozzle_03 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_3, (byte) Nozzle_03);
                        KeyControl.setHouse02_Nozzle03(Nozzle_03);
                    }

                    if (!KeyControl.getHouse02_Nozzle04().equals(Nozzle_04) && Nozzle_04 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_4, (byte) Nozzle_04);
                        KeyControl.setHouse02_Nozzle04(Nozzle_04);
                    }

                    if (!KeyControl.getHouse02_Nozzle05().equals(Nozzle_05) && Nozzle_05 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_5, (byte) Nozzle_05);
                        KeyControl.setHouse02_Nozzle05(Nozzle_05);
                    }

                    if (!KeyControl.getHouse02_Nozzle06().equals(Nozzle_06) && Nozzle_06 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_6, (byte) Nozzle_06);
                        KeyControl.setHouse02_Nozzle06(Nozzle_06);
                    }

                    if (!KeyControl.getHouse02_Nozzle07().equals(Nozzle_07) && Nozzle_07 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_7, (byte) Nozzle_07);
                        KeyControl.setHouse02_Nozzle07(Nozzle_07);
                    }

                    if (!KeyControl.getHouse02_Nozzle08().equals(Nozzle_08) && Nozzle_08 != -1) {
                        if(isRunning&& situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_02_Nozzle_8, (byte) Nozzle_08);
                        KeyControl.setHouse02_Nozzle08(Nozzle_08);
                    }
                }else{
                    int Deviation = KeyControl.getDeviation();
                    int Th_main = KeyControl.getTh_main();

                    if(Nozzle_01 != -1) KeyControl.setHouse02_Nozzle01(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp1_1().getValue(),SerialModel.House_02_Nozzle_1));
                    if(Nozzle_02 != -1) KeyControl.setHouse02_Nozzle02(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp2_1().getValue(),SerialModel.House_02_Nozzle_2));
                    if(Nozzle_03 != -1) KeyControl.setHouse02_Nozzle03(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp3_1().getValue(),SerialModel.House_02_Nozzle_3));
                    if(Nozzle_04 != -1) KeyControl.setHouse02_Nozzle04(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp4_1().getValue(),SerialModel.House_02_Nozzle_4));
                    if(Nozzle_05 != -1) KeyControl.setHouse02_Nozzle05(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp5_1().getValue(),SerialModel.House_02_Nozzle_5));
                    if(Nozzle_06 != -1) KeyControl.setHouse02_Nozzle06(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp6_1().getValue(),SerialModel.House_02_Nozzle_6));
                    if(Nozzle_07 != -1) KeyControl.setHouse02_Nozzle07(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp7_1().getValue(),SerialModel.House_02_Nozzle_7));
                    if(Nozzle_08 != -1) KeyControl.setHouse02_Nozzle08(TH_Set_Command(Th_main,Deviation,viewModel.getHouse02_Temp8_1().getValue(),SerialModel.House_02_Nozzle_8));
                    
                }

                viewModel.setHouse02_Nozzle_Auto(auto);


            });
            houseNozzleDialog.show();
        });

        House03_NozzleAuto.setOnClickListener(v -> {
            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_03, Boolean.TRUE.equals(viewModel.getHouse03_Nozzle_Auto().getValue()),KeyControl.getHouse03_Nozzle01(),KeyControl.getHouse03_Nozzle02(),KeyControl.getHouse03_Nozzle03(),KeyControl.getHouse03_Nozzle04(),KeyControl.getHouse03_Nozzle05(),KeyControl.getHouse03_Nozzle06(),KeyControl.getHouse03_Nozzle07(),KeyControl.getHouse03_Nozzle08());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04,Nozzle_05,Nozzle_06,Nozzle_07,Nozzle_08) -> {
                boolean isRunning = Boolean.TRUE.equals(viewModel.getIsRunning().getValue());
                boolean situation = Objects.equals(viewModel.getCleanning_Running_Situation_1().getValue(), DeviceModel.House_03_04)  && viewModel.getCleanning_Running_Situation_2().getValue() == 0;
                if(!auto) {

                    if (!KeyControl.getHouse03_Nozzle01().equals(Nozzle_01) && Nozzle_01 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_1, (byte) Nozzle_01);
                        KeyControl.setHouse03_Nozzle01(Nozzle_01);
                    }

                    if (!KeyControl.getHouse03_Nozzle02().equals(Nozzle_02) && Nozzle_02 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_2, (byte) Nozzle_02);
                        KeyControl.setHouse03_Nozzle02(Nozzle_02);
                    }

                    if (!KeyControl.getHouse03_Nozzle03().equals(Nozzle_03) && Nozzle_03 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_3, (byte) Nozzle_03);
                        KeyControl.setHouse03_Nozzle03(Nozzle_03);
                    }

                    if (!KeyControl.getHouse03_Nozzle04().equals(Nozzle_04) && Nozzle_04 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_4, (byte) Nozzle_04);
                        KeyControl.setHouse03_Nozzle04(Nozzle_04);
                    }

                    if (!KeyControl.getHouse03_Nozzle05().equals(Nozzle_05) && Nozzle_05 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_5, (byte) Nozzle_05);
                        KeyControl.setHouse03_Nozzle05(Nozzle_05);
                    }

                    if (!KeyControl.getHouse03_Nozzle06().equals(Nozzle_06) && Nozzle_06 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_6, (byte) Nozzle_06);
                        KeyControl.setHouse03_Nozzle06(Nozzle_06);
                    }

                    if (!KeyControl.getHouse03_Nozzle07().equals(Nozzle_07) && Nozzle_07 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_7, (byte) Nozzle_07);
                        KeyControl.setHouse03_Nozzle07(Nozzle_07);
                    }

                    if (!KeyControl.getHouse03_Nozzle08().equals(Nozzle_08) && Nozzle_08 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_03_Nozzle_8, (byte) Nozzle_08);
                        KeyControl.setHouse03_Nozzle08(Nozzle_08);
                    }
                }else{
                    int Deviation = KeyControl.getDeviation();
                    int Th_main = KeyControl.getTh_main();

                    if(Nozzle_01 != -1) KeyControl.setHouse03_Nozzle01(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp1_1().getValue(),SerialModel.House_03_Nozzle_1));
                    if(Nozzle_02 != -1) KeyControl.setHouse03_Nozzle02(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp2_1().getValue(),SerialModel.House_03_Nozzle_2));
                    if(Nozzle_03 != -1) KeyControl.setHouse03_Nozzle03(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp3_1().getValue(),SerialModel.House_03_Nozzle_3));
                    if(Nozzle_04 != -1) KeyControl.setHouse03_Nozzle04(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp4_1().getValue(),SerialModel.House_03_Nozzle_4));
                    if(Nozzle_05 != -1) KeyControl.setHouse03_Nozzle05(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp5_1().getValue(),SerialModel.House_03_Nozzle_5));
                    if(Nozzle_06 != -1) KeyControl.setHouse03_Nozzle06(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp6_1().getValue(),SerialModel.House_03_Nozzle_6));
                    if(Nozzle_07 != -1) KeyControl.setHouse03_Nozzle07(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp7_1().getValue(),SerialModel.House_03_Nozzle_7));
                    if(Nozzle_08 != -1) KeyControl.setHouse03_Nozzle08(TH_Set_Command(Th_main,Deviation,viewModel.getHouse03_Temp8_1().getValue(),SerialModel.House_03_Nozzle_8));
                }

                viewModel.setHouse03_Nozzle_Auto(auto);

            });
            houseNozzleDialog.show();
        });

        House04_NozzleAuto.setOnClickListener(v -> {
            HouseNozzleDialog houseNozzleDialog = new HouseNozzleDialog(this,DeviceModel.House_04, Boolean.TRUE.equals(viewModel.getHouse04_Nozzle_Auto().getValue()),KeyControl.getHouse04_Nozzle01(),KeyControl.getHouse04_Nozzle02(),KeyControl.getHouse04_Nozzle03(),KeyControl.getHouse04_Nozzle04(),KeyControl.getHouse04_Nozzle05(),KeyControl.getHouse04_Nozzle06(),KeyControl.getHouse04_Nozzle07(),KeyControl.getHouse04_Nozzle08());
            houseNozzleDialog.setHouseNozzleDialogListener((auto,Nozzle_01,Nozzle_02,Nozzle_03,Nozzle_04,Nozzle_05,Nozzle_06,Nozzle_07,Nozzle_08) -> {
                boolean isRunning = Boolean.TRUE.equals(viewModel.getIsRunning().getValue());
                boolean situation = Objects.equals(viewModel.getCleanning_Running_Situation_1().getValue(), DeviceModel.House_03_04)  && viewModel.getCleanning_Running_Situation_2().getValue() == 0;

                if(!auto) {
                    if (!KeyControl.getHouse04_Nozzle01().equals(Nozzle_01) && Nozzle_01 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_1, (byte) Nozzle_01);
                        KeyControl.setHouse04_Nozzle01(Nozzle_01);
                    }

                    if (!KeyControl.getHouse04_Nozzle02().equals(Nozzle_02) && Nozzle_02 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_2, (byte) Nozzle_02);
                        KeyControl.setHouse04_Nozzle02(Nozzle_02);
                    }

                    if (!KeyControl.getHouse04_Nozzle03().equals(Nozzle_03) && Nozzle_03 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_3, (byte) Nozzle_03);
                        KeyControl.setHouse04_Nozzle03(Nozzle_03);
                    }

                    if (!KeyControl.getHouse04_Nozzle04().equals(Nozzle_04) && Nozzle_04 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_4, (byte) Nozzle_04);
                        KeyControl.setHouse04_Nozzle04(Nozzle_04);
                    }

                    if (!KeyControl.getHouse04_Nozzle05().equals(Nozzle_05) && Nozzle_05 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_5, (byte) Nozzle_05);
                        KeyControl.setHouse04_Nozzle05(Nozzle_05);
                    }

                    if (!KeyControl.getHouse04_Nozzle06().equals(Nozzle_06) && Nozzle_06 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_6, (byte) Nozzle_06);
                        KeyControl.setHouse04_Nozzle06(Nozzle_06);
                    }

                    if (!KeyControl.getHouse04_Nozzle07().equals(Nozzle_07) && Nozzle_07 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_7, (byte) Nozzle_07);
                        KeyControl.setHouse04_Nozzle07(Nozzle_07);
                    }

                    if (!KeyControl.getHouse04_Nozzle08().equals(Nozzle_08) && Nozzle_08 != -1) {
                        if(isRunning&&situation) SerialPortManager.getInstance().SendSerialCom(SerialModel.House_04_Nozzle_8, (byte) Nozzle_08);
                        KeyControl.setHouse04_Nozzle08(Nozzle_08);
                    }

                }else{
                    int Deviation = KeyControl.getDeviation();
                    int Th_main = KeyControl.getTh_main();

                    if(Nozzle_01 != -1) KeyControl.setHouse04_Nozzle01(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp1_1().getValue(),SerialModel.House_04_Nozzle_1));
                    if(Nozzle_02 != -1) KeyControl.setHouse04_Nozzle02(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp2_1().getValue(),SerialModel.House_04_Nozzle_2));
                    if(Nozzle_03 != -1) KeyControl.setHouse04_Nozzle03(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp3_1().getValue(),SerialModel.House_04_Nozzle_3));
                    if(Nozzle_04 != -1) KeyControl.setHouse04_Nozzle04(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp4_1().getValue(),SerialModel.House_04_Nozzle_4));
                    if(Nozzle_05 != -1) KeyControl.setHouse04_Nozzle05(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp5_1().getValue(),SerialModel.House_04_Nozzle_5));
                    if(Nozzle_06 != -1) KeyControl.setHouse04_Nozzle06(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp6_1().getValue(),SerialModel.House_04_Nozzle_6));
                    if(Nozzle_07 != -1) KeyControl.setHouse04_Nozzle07(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp7_1().getValue(),SerialModel.House_04_Nozzle_7));
                    if(Nozzle_08 != -1) KeyControl.setHouse04_Nozzle08(TH_Set_Command(Th_main,Deviation,viewModel.getHouse04_Temp8_1().getValue(),SerialModel.House_04_Nozzle_8));
                }

                viewModel.setHouse04_Nozzle_Auto(auto);
            });
            houseNozzleDialog.show();
        });



//        House01_TH.setOnClickListener(v->{
//            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_01,viewModel.getHouse01_Temp1_1().getValue(),viewModel.getHouse01_Temp1_2().getValue(),viewModel.getHouse01_Temp2_1().getValue(),viewModel.getHouse01_Temp2_2().getValue(),viewModel.getHouse01_Temp3_1().getValue(),viewModel.getHouse01_Temp3_2().getValue(),viewModel.getHouse01_Temp4_1().getValue(),viewModel.getHouse01_Temp4_2().getValue(),viewModel.getHouse01_Humi1_1().getValue(),viewModel.getHouse01_Humi1_2().getValue(),viewModel.getHouse01_Humi2_1().getValue(),viewModel.getHouse01_Humi2_2().getValue(),viewModel.getHouse01_Humi3_1().getValue(),viewModel.getHouse01_Humi3_2().getValue(),viewModel.getHouse01_Humi4_1().getValue(),viewModel.getHouse01_Humi4_2().getValue());
//            houseTHDialog.show();
//        });
//
//        House02_TH.setOnClickListener(v->{
//            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_02,viewModel.getHouse02_Temp1_1().getValue(),viewModel.getHouse02_Temp1_2().getValue(),viewModel.getHouse02_Temp2_1().getValue(),viewModel.getHouse02_Temp2_2().getValue(),viewModel.getHouse02_Temp3_1().getValue(),viewModel.getHouse02_Temp3_2().getValue(),viewModel.getHouse02_Temp4_1().getValue(),viewModel.getHouse02_Temp4_2().getValue(),viewModel.getHouse02_Humi1_1().getValue(),viewModel.getHouse02_Humi1_2().getValue(),viewModel.getHouse02_Humi2_1().getValue(),viewModel.getHouse02_Humi2_2().getValue(),viewModel.getHouse02_Humi3_1().getValue(),viewModel.getHouse02_Humi3_2().getValue(),viewModel.getHouse02_Humi4_1().getValue(),viewModel.getHouse02_Humi4_2().getValue());
//            houseTHDialog.show();
//        });
//
//        House03_TH.setOnClickListener(v->{
//            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_03,viewModel.getHouse03_Temp1_1().getValue(),viewModel.getHouse03_Temp1_2().getValue(),viewModel.getHouse03_Temp2_1().getValue(),viewModel.getHouse03_Temp2_2().getValue(),viewModel.getHouse03_Temp3_1().getValue(),viewModel.getHouse03_Temp3_2().getValue(),viewModel.getHouse03_Temp4_1().getValue(),viewModel.getHouse03_Temp4_2().getValue(),viewModel.getHouse03_Humi1_1().getValue(),viewModel.getHouse03_Humi1_2().getValue(),viewModel.getHouse03_Humi2_1().getValue(),viewModel.getHouse03_Humi2_2().getValue(),viewModel.getHouse03_Humi3_1().getValue(),viewModel.getHouse03_Humi3_2().getValue(),viewModel.getHouse03_Humi4_1().getValue(),viewModel.getHouse03_Humi4_2().getValue());
//            houseTHDialog.show();
//        });
//
//        House04_TH.setOnClickListener(v->{
//            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_04,viewModel.getHouse04_Temp1_1().getValue(),viewModel.getHouse04_Temp1_2().getValue(),viewModel.getHouse04_Temp2_1().getValue(),viewModel.getHouse04_Temp2_2().getValue(),viewModel.getHouse04_Temp3_1().getValue(),viewModel.getHouse04_Temp3_2().getValue(),viewModel.getHouse04_Temp4_1().getValue(),viewModel.getHouse04_Temp4_2().getValue(),viewModel.getHouse04_Humi1_1().getValue(),viewModel.getHouse04_Humi1_2().getValue(),viewModel.getHouse04_Humi2_1().getValue(),viewModel.getHouse04_Humi2_2().getValue(),viewModel.getHouse04_Humi3_1().getValue(),viewModel.getHouse04_Humi3_2().getValue(),viewModel.getHouse04_Humi4_1().getValue(),viewModel.getHouse04_Humi4_2().getValue());
//            houseTHDialog.show();
//        });

        House01_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_01,viewModel.getHouse01_Temp1_1().getValue(),viewModel.getHouse01_Temp2_1().getValue(),viewModel.getHouse01_Temp3_1().getValue(),viewModel.getHouse01_Temp4_1().getValue(),viewModel.getHouse01_Temp5_1().getValue(),viewModel.getHouse01_Temp6_1().getValue(),viewModel.getHouse01_Temp7_1().getValue(),viewModel.getHouse01_Temp8_1().getValue(),viewModel.getHouse01_Humi1_1().getValue(),viewModel.getHouse01_Humi2_1().getValue(),viewModel.getHouse01_Humi3_1().getValue(),viewModel.getHouse01_Humi4_1().getValue(),viewModel.getHouse01_Humi5_1().getValue(),viewModel.getHouse01_Humi6_1().getValue(),viewModel.getHouse01_Humi7_1().getValue(),viewModel.getHouse01_Humi8_1().getValue());
            houseTHDialog.show();
        });

        House02_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_02,viewModel.getHouse02_Temp1_1().getValue(),viewModel.getHouse02_Temp2_1().getValue(),viewModel.getHouse02_Temp3_1().getValue(),viewModel.getHouse02_Temp4_1().getValue(),viewModel.getHouse02_Temp5_1().getValue(),viewModel.getHouse02_Temp6_1().getValue(),viewModel.getHouse02_Temp7_1().getValue(),viewModel.getHouse02_Temp8_1().getValue(),viewModel.getHouse02_Humi1_1().getValue(),viewModel.getHouse02_Humi2_1().getValue(),viewModel.getHouse02_Humi3_1().getValue(),viewModel.getHouse02_Humi4_1().getValue(),viewModel.getHouse02_Humi5_1().getValue(),viewModel.getHouse02_Humi6_1().getValue(),viewModel.getHouse02_Humi7_1().getValue(),viewModel.getHouse02_Humi8_1().getValue());
            houseTHDialog.show();
        });

        House03_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_03,viewModel.getHouse03_Temp1_1().getValue(),viewModel.getHouse03_Temp2_1().getValue(),viewModel.getHouse03_Temp3_1().getValue(),viewModel.getHouse03_Temp4_1().getValue(),viewModel.getHouse03_Temp5_1().getValue(),viewModel.getHouse03_Temp6_1().getValue(),viewModel.getHouse03_Temp7_1().getValue(),viewModel.getHouse03_Temp8_1().getValue(),viewModel.getHouse03_Humi1_1().getValue(),viewModel.getHouse03_Humi2_1().getValue(),viewModel.getHouse03_Humi3_1().getValue(),viewModel.getHouse03_Humi4_1().getValue(),viewModel.getHouse03_Humi5_1().getValue(),viewModel.getHouse03_Humi6_1().getValue(),viewModel.getHouse03_Humi7_1().getValue(),viewModel.getHouse03_Humi8_1().getValue());
            houseTHDialog.show();
        });

        House04_TH.setOnClickListener(v->{
            HouseTHDialog houseTHDialog = new HouseTHDialog(this,DeviceModel.House_04,viewModel.getHouse04_Temp1_1().getValue(),viewModel.getHouse04_Temp2_1().getValue(),viewModel.getHouse04_Temp3_1().getValue(),viewModel.getHouse04_Temp4_1().getValue(),viewModel.getHouse04_Temp5_1().getValue(),viewModel.getHouse04_Temp6_1().getValue(),viewModel.getHouse04_Temp7_1().getValue(),viewModel.getHouse04_Temp8_1().getValue(),viewModel.getHouse04_Humi1_1().getValue(),viewModel.getHouse04_Humi2_1().getValue(),viewModel.getHouse04_Humi3_1().getValue(),viewModel.getHouse04_Humi4_1().getValue(),viewModel.getHouse04_Humi5_1().getValue(),viewModel.getHouse04_Humi6_1().getValue(),viewModel.getHouse04_Humi7_1().getValue(),viewModel.getHouse04_Humi8_1().getValue());
            houseTHDialog.show();
        });




        SerialPortManager.getInstance().setOnSerialPortDataListener(new OnSerialPortDataListener() {
            @Override
            public void onDataReceived(byte[] bytes) {  // 수신시
//                Log.d(TAG, "직렬 포트 데이터 수신：" + Arrays.toString(bytes));

                Log.d(TAG, "직렬 포트 데이터 수신：" + byteArrayToHex(bytes));


                try{
                    if (bytes[1]==SerialModel.InfoReceive_Receive){  //정보응답

                        Log.d(TAG, "종류：" + (0xff & SerialModel.InfoReceive_Receive));

                        
                        Stop_Command_time();
                        
                        try {
                            byte[] data = Arrays.copyOfRange(bytes,2,67);
                            byte[] crc = new byte[]{bytes[67],bytes[68]};


                            if(CRC16Util.receiveData(data,crc)){
                                Log.d(TAG, "crc true：" + (0xff & SerialModel.InfoReceive_Receive));
                                byte[] house01_data = Arrays.copyOfRange(bytes,2,18);
                                byte[] house02_data = Arrays.copyOfRange(bytes,18,34);
                                byte[] house03_data = Arrays.copyOfRange(bytes,34,50);
                                byte[] house04_data = Arrays.copyOfRange(bytes,50,66);

                                byte shortage = bytes[66];


                                viewModel.getWater_Shortage().postValue(shortage != 0x00);

                                if(first_info){         // 처음 기기를 시작할때

                                    Log.d(TAG,"house01_data : "+(0xff & house01_data[0])+" "+house01_data[2]+" "+house01_data[3]+" "+house01_data[5]+" "+house01_data[7]+" "+house01_data[9]+" "+house01_data[11]);

                                    KeyControl.setHouse01_Nozzle01(0xff & house01_data[0]);
                                    KeyControl.setHouse01_Nozzle02(0xff & house01_data[2]);
                                    KeyControl.setHouse01_Nozzle03(0xff & house01_data[3]);
                                    KeyControl.setHouse01_Nozzle04(0xff & house01_data[5]);
                                    KeyControl.setHouse01_Nozzle05(0xff & house01_data[7]);
                                    KeyControl.setHouse01_Nozzle06(0xff & house01_data[9]);
                                    KeyControl.setHouse01_Nozzle07(0xff & house01_data[11]);
                                    KeyControl.setHouse01_Nozzle08(0xff & house01_data[13]);


                                    KeyControl.setHouse02_Nozzle01(0xff & house02_data[0]);
                                    KeyControl.setHouse02_Nozzle02(0xff & house02_data[2]);
                                    KeyControl.setHouse02_Nozzle03(0xff & house02_data[3]);
                                    KeyControl.setHouse02_Nozzle04(0xff & house02_data[5]);
                                    KeyControl.setHouse02_Nozzle05(0xff & house02_data[7]);
                                    KeyControl.setHouse02_Nozzle06(0xff & house02_data[9]);
                                    KeyControl.setHouse02_Nozzle07(0xff & house02_data[11]);
                                    KeyControl.setHouse02_Nozzle08(0xff & house02_data[13]);


                                    KeyControl.setHouse03_Nozzle01(0xff & house03_data[0]);
                                    KeyControl.setHouse03_Nozzle02(0xff & house03_data[2]);
                                    KeyControl.setHouse03_Nozzle03(0xff & house03_data[3]);
                                    KeyControl.setHouse03_Nozzle04(0xff & house03_data[5]);
                                    KeyControl.setHouse03_Nozzle05(0xff & house03_data[7]);
                                    KeyControl.setHouse03_Nozzle06(0xff & house03_data[9]);
                                    KeyControl.setHouse03_Nozzle07(0xff & house03_data[11]);
                                    KeyControl.setHouse03_Nozzle08(0xff & house03_data[13]);


                                    KeyControl.setHouse04_Nozzle01(0xff & house04_data[0]);
                                    KeyControl.setHouse04_Nozzle02(0xff & house04_data[2]);
                                    KeyControl.setHouse04_Nozzle03(0xff & house04_data[3]);
                                    KeyControl.setHouse04_Nozzle04(0xff & house04_data[5]);
                                    KeyControl.setHouse04_Nozzle05(0xff & house04_data[7]);
                                    KeyControl.setHouse04_Nozzle06(0xff & house04_data[9]);
                                    KeyControl.setHouse04_Nozzle07(0xff & house04_data[11]);
                                    KeyControl.setHouse04_Nozzle08(0xff & house04_data[13]);

                                    first_info = false;
                                }else{
                                    // 시작 버튼을 누를때
                                    if(shortage != 0x00){      // 물이 부족할때
                                        AlarmDialogShow("물부족","물이 부족합니다. 적정량의 물이 존재하는지 확인해주세요.");
                                        Logger.i("Warnning : Water Shartage");
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
                                    }else{


                                        mViewPager.setCurrentItem(1);
                                    }
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else if(bytes[1]==SerialModel.THReceive_Receive){  //온습도응답
                        try {
                            Log.d(TAG, "종류：" + "SerialModel.THReceive");

                            byte[] data = new byte[]{bytes[3],bytes[4],bytes[5],bytes[6],bytes[7],bytes[8],bytes[9],bytes[10],bytes[11],bytes[12],bytes[13],bytes[14],bytes[15],bytes[16],bytes[17],bytes[18]};
                            byte[] crc = new byte[]{bytes[19],bytes[20]};

                            if(CRC16Util.receiveData(data,crc)){

                                int Deviation = KeyControl.getDeviation();
                                int Th_main = KeyControl.getTh_main();
                                
                                int temp1 = (0xff & bytes[3]);
                                int humi1 = (0xff & bytes[4]);
                                int temp2 = (0xff & bytes[5]);
                                int humi2 = (0xff & bytes[6]);
                                int temp3 = (0xff & bytes[7]);
                                int humi3 = (0xff & bytes[8]);
                                int temp4 = (0xff & bytes[9]);
                                int humi4 = (0xff & bytes[10]);
                                int temp5 = (0xff & bytes[11]);
                                int humi5 = (0xff & bytes[12]);
                                int temp6 = (0xff & bytes[13]);
                                int humi6 = (0xff & bytes[14]);
                                int temp7 = (0xff & bytes[15]);
                                int humi7 = (0xff & bytes[16]);
                                int temp8 = (0xff & bytes[17]);
                                int humi8 = (0xff & bytes[18]);
                                
                                if(TH_Step==0){
                                    TH_Step = 1;

                                    viewModel.getHouse01_Temp1_1().postValue(temp1);
                                    viewModel.getHouse01_Humi1_1().postValue(humi1);
                                    viewModel.getHouse01_Temp2_1().postValue(temp2);
                                    viewModel.getHouse01_Humi2_1().postValue(humi2);
                                    viewModel.getHouse01_Temp3_1().postValue(temp3);
                                    viewModel.getHouse01_Humi3_1().postValue(humi3);
                                    viewModel.getHouse01_Temp4_1().postValue(temp4);
                                    viewModel.getHouse01_Humi4_1().postValue(humi4);
                                    viewModel.getHouse01_Temp5_1().postValue(temp5);
                                    viewModel.getHouse01_Humi5_1().postValue(humi5);
                                    viewModel.getHouse01_Temp6_1().postValue(temp6);
                                    viewModel.getHouse01_Humi6_1().postValue(humi6);
                                    viewModel.getHouse01_Temp7_1().postValue(temp7);
                                    viewModel.getHouse01_Humi7_1().postValue(humi7);
                                    viewModel.getHouse01_Temp8_1().postValue(temp8);
                                    viewModel.getHouse01_Humi8_1().postValue(humi8);

                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_House_02_TH,null);

                                    if(Boolean.TRUE.equals(viewModel.getHouse01_Nozzle_Auto().getValue())){

                                        KeyControl.setHouse01_Nozzle01(TH_Set_Command(Th_main,Deviation,temp1,SerialModel.House_01_Nozzle_1));
                                        KeyControl.setHouse01_Nozzle02(TH_Set_Command(Th_main,Deviation,temp2,SerialModel.House_01_Nozzle_2));
                                        KeyControl.setHouse01_Nozzle03(TH_Set_Command(Th_main,Deviation,temp3,SerialModel.House_01_Nozzle_3));
                                        KeyControl.setHouse01_Nozzle04(TH_Set_Command(Th_main,Deviation,temp4,SerialModel.House_01_Nozzle_4));
                                        KeyControl.setHouse01_Nozzle05(TH_Set_Command(Th_main,Deviation,temp5,SerialModel.House_01_Nozzle_5));
                                        KeyControl.setHouse01_Nozzle06(TH_Set_Command(Th_main,Deviation,temp6,SerialModel.House_01_Nozzle_6));
                                        KeyControl.setHouse01_Nozzle07(TH_Set_Command(Th_main,Deviation,temp7,SerialModel.House_01_Nozzle_7));
                                        KeyControl.setHouse01_Nozzle08(TH_Set_Command(Th_main,Deviation,temp8,SerialModel.House_01_Nozzle_8));
                                        

                                    }


                                    if(temp_humi_cnt == -1 || temp_humi_cnt == temp_humi_logging_cnt){

                                        Logger.i(String.format(getString(R.string.TH_log),temp1,humi1,KeyControl.getHouse01_Nozzle01(),
                                                temp2,humi2,KeyControl.getHouse01_Nozzle02(),
                                                temp3,humi3,KeyControl.getHouse01_Nozzle03(),
                                                temp4,humi4,KeyControl.getHouse01_Nozzle04(),
                                                temp5,humi5,KeyControl.getHouse01_Nozzle05(),
                                                temp6,humi6,KeyControl.getHouse01_Nozzle06(),
                                                temp7,humi7,KeyControl.getHouse01_Nozzle07(),
                                                temp8,humi8,KeyControl.getHouse01_Nozzle08(),"House01"));
                                    }

                                }else if (TH_Step ==1){
                                    TH_Step = 2;

                                    viewModel.getHouse02_Temp1_1().postValue(temp1);
                                    viewModel.getHouse02_Humi1_1().postValue(humi1);
                                    viewModel.getHouse02_Temp2_1().postValue(temp2);
                                    viewModel.getHouse02_Humi2_1().postValue(humi2);
                                    viewModel.getHouse02_Temp3_1().postValue(temp3);
                                    viewModel.getHouse02_Humi3_1().postValue(humi3);
                                    viewModel.getHouse02_Temp4_1().postValue(temp4);
                                    viewModel.getHouse02_Humi4_1().postValue(humi4);
                                    viewModel.getHouse02_Temp5_1().postValue(temp5);
                                    viewModel.getHouse02_Humi5_1().postValue(humi5);
                                    viewModel.getHouse02_Temp6_1().postValue(temp6);
                                    viewModel.getHouse02_Humi6_1().postValue(humi6);
                                    viewModel.getHouse02_Temp7_1().postValue(temp7);
                                    viewModel.getHouse02_Humi7_1().postValue(humi7);
                                    viewModel.getHouse02_Temp8_1().postValue(temp8);
                                    viewModel.getHouse02_Humi8_1().postValue(humi8);

                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_House_03_TH,null);

                                    if(Boolean.TRUE.equals(viewModel.getHouse02_Nozzle_Auto().getValue())){
                                        KeyControl.setHouse02_Nozzle01(TH_Set_Command(Th_main,Deviation,temp1,SerialModel.House_02_Nozzle_1));
                                        KeyControl.setHouse02_Nozzle02(TH_Set_Command(Th_main,Deviation,temp2,SerialModel.House_02_Nozzle_2));
                                        KeyControl.setHouse02_Nozzle03(TH_Set_Command(Th_main,Deviation,temp3,SerialModel.House_02_Nozzle_3));
                                        KeyControl.setHouse02_Nozzle04(TH_Set_Command(Th_main,Deviation,temp4,SerialModel.House_02_Nozzle_4));
                                        KeyControl.setHouse02_Nozzle05(TH_Set_Command(Th_main,Deviation,temp5,SerialModel.House_02_Nozzle_5));
                                        KeyControl.setHouse02_Nozzle06(TH_Set_Command(Th_main,Deviation,temp6,SerialModel.House_02_Nozzle_6));
                                        KeyControl.setHouse02_Nozzle07(TH_Set_Command(Th_main,Deviation,temp7,SerialModel.House_02_Nozzle_7));
                                        KeyControl.setHouse02_Nozzle08(TH_Set_Command(Th_main,Deviation,temp8,SerialModel.House_02_Nozzle_8));
                                    }

                                    if(temp_humi_cnt == -1 || temp_humi_cnt == temp_humi_logging_cnt){
                                        Logger.i(String.format(getString(R.string.TH_log),temp1,humi1,KeyControl.getHouse02_Nozzle01(),
                                                temp2,humi2,KeyControl.getHouse02_Nozzle02(),
                                                temp3,humi3,KeyControl.getHouse02_Nozzle03(),
                                                temp4,humi4,KeyControl.getHouse02_Nozzle04(),
                                                temp5,humi5,KeyControl.getHouse02_Nozzle05(),
                                                temp6,humi6,KeyControl.getHouse02_Nozzle06(),
                                                temp7,humi7,KeyControl.getHouse02_Nozzle07(),
                                                temp8,humi8,KeyControl.getHouse02_Nozzle08(),"House02"));
                                    }
                                }else if (TH_Step ==2){
                                    TH_Step = 3;

                                    viewModel.getHouse03_Temp1_1().postValue(temp1);
                                    viewModel.getHouse03_Humi1_1().postValue(humi1);
                                    viewModel.getHouse03_Temp2_1().postValue(temp2);
                                    viewModel.getHouse03_Humi2_1().postValue(humi2);
                                    viewModel.getHouse03_Temp3_1().postValue(temp3);
                                    viewModel.getHouse03_Humi3_1().postValue(humi3);
                                    viewModel.getHouse03_Temp4_1().postValue(temp4);
                                    viewModel.getHouse03_Humi4_1().postValue(humi4);
                                    viewModel.getHouse03_Temp5_1().postValue(temp5);
                                    viewModel.getHouse03_Humi5_1().postValue(humi5);
                                    viewModel.getHouse03_Temp6_1().postValue(temp6);
                                    viewModel.getHouse03_Humi6_1().postValue(humi6);
                                    viewModel.getHouse03_Temp7_1().postValue(temp7);
                                    viewModel.getHouse03_Humi7_1().postValue(humi7);
                                    viewModel.getHouse03_Temp8_1().postValue(temp8);
                                    viewModel.getHouse03_Humi8_1().postValue(humi8);

                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_House_04_TH,null);

                                    if(Boolean.TRUE.equals(viewModel.getHouse03_Nozzle_Auto().getValue())){
                                        KeyControl.setHouse03_Nozzle01(TH_Set_Command(Th_main,Deviation,temp1,SerialModel.House_03_Nozzle_1));
                                        KeyControl.setHouse03_Nozzle02(TH_Set_Command(Th_main,Deviation,temp2,SerialModel.House_03_Nozzle_2));
                                        KeyControl.setHouse03_Nozzle03(TH_Set_Command(Th_main,Deviation,temp3,SerialModel.House_03_Nozzle_3));
                                        KeyControl.setHouse03_Nozzle04(TH_Set_Command(Th_main,Deviation,temp4,SerialModel.House_03_Nozzle_4));
                                        KeyControl.setHouse03_Nozzle05(TH_Set_Command(Th_main,Deviation,temp5,SerialModel.House_03_Nozzle_5));
                                        KeyControl.setHouse03_Nozzle06(TH_Set_Command(Th_main,Deviation,temp6,SerialModel.House_03_Nozzle_6));
                                        KeyControl.setHouse03_Nozzle07(TH_Set_Command(Th_main,Deviation,temp7,SerialModel.House_03_Nozzle_7));
                                        KeyControl.setHouse03_Nozzle08(TH_Set_Command(Th_main,Deviation,temp8,SerialModel.House_03_Nozzle_8));
                                    }

                                    if(temp_humi_cnt == -1 || temp_humi_cnt == temp_humi_logging_cnt){
                                        Logger.i(String.format(getString(R.string.TH_log),temp1,humi1,KeyControl.getHouse03_Nozzle01(),
                                                temp2,humi2,KeyControl.getHouse03_Nozzle02(),
                                                temp3,humi3,KeyControl.getHouse03_Nozzle03(),
                                                temp4,humi4,KeyControl.getHouse03_Nozzle04(),
                                                temp5,humi5,KeyControl.getHouse03_Nozzle05(),
                                                temp6,humi6,KeyControl.getHouse03_Nozzle06(),
                                                temp7,humi7,KeyControl.getHouse03_Nozzle07(),
                                                temp8,humi8,KeyControl.getHouse03_Nozzle08(),"House03"));
                                    }
                                }else if (TH_Step ==3){
                                    TH_Step = 0;

                                    viewModel.getHouse04_Temp1_1().postValue(temp1);
                                    viewModel.getHouse04_Humi1_1().postValue(humi1);
                                    viewModel.getHouse04_Temp2_1().postValue(temp2);
                                    viewModel.getHouse04_Humi2_1().postValue(humi2);
                                    viewModel.getHouse04_Temp3_1().postValue(temp3);
                                    viewModel.getHouse04_Humi3_1().postValue(humi3);
                                    viewModel.getHouse04_Temp4_1().postValue(temp4);
                                    viewModel.getHouse04_Humi4_1().postValue(humi4);
                                    viewModel.getHouse04_Temp5_1().postValue(temp5);
                                    viewModel.getHouse04_Humi5_1().postValue(humi5);
                                    viewModel.getHouse04_Temp6_1().postValue(temp6);
                                    viewModel.getHouse04_Humi6_1().postValue(humi6);
                                    viewModel.getHouse04_Temp7_1().postValue(temp7);
                                    viewModel.getHouse04_Humi7_1().postValue(humi7);
                                    viewModel.getHouse04_Temp8_1().postValue(temp8);
                                    viewModel.getHouse04_Humi8_1().postValue(humi8);

                                    if(Boolean.TRUE.equals(viewModel.getHouse04_Nozzle_Auto().getValue())){
                                        KeyControl.setHouse04_Nozzle01(TH_Set_Command(Th_main,Deviation,temp1,SerialModel.House_04_Nozzle_1));
                                        KeyControl.setHouse04_Nozzle02(TH_Set_Command(Th_main,Deviation,temp2,SerialModel.House_04_Nozzle_2));
                                        KeyControl.setHouse04_Nozzle03(TH_Set_Command(Th_main,Deviation,temp3,SerialModel.House_04_Nozzle_3));
                                        KeyControl.setHouse04_Nozzle04(TH_Set_Command(Th_main,Deviation,temp4,SerialModel.House_04_Nozzle_4));
                                        KeyControl.setHouse04_Nozzle05(TH_Set_Command(Th_main,Deviation,temp5,SerialModel.House_04_Nozzle_5));
                                        KeyControl.setHouse04_Nozzle06(TH_Set_Command(Th_main,Deviation,temp6,SerialModel.House_04_Nozzle_6));
                                        KeyControl.setHouse04_Nozzle07(TH_Set_Command(Th_main,Deviation,temp7,SerialModel.House_04_Nozzle_7));
                                        KeyControl.setHouse04_Nozzle08(TH_Set_Command(Th_main,Deviation,temp8,SerialModel.House_04_Nozzle_8));
                                    }

                                    if(temp_humi_cnt == -1 || temp_humi_cnt == temp_humi_logging_cnt){
                                        Logger.i(String.format(getString(R.string.TH_log),temp1,humi1,KeyControl.getHouse04_Nozzle01(),
                                                temp2,humi2,KeyControl.getHouse04_Nozzle02(),
                                                temp3,humi3,KeyControl.getHouse04_Nozzle03(),
                                                temp4,humi4,KeyControl.getHouse04_Nozzle04(),
                                                temp5,humi5,KeyControl.getHouse04_Nozzle05(),
                                                temp6,humi6,KeyControl.getHouse04_Nozzle06(),
                                                temp7,humi7,KeyControl.getHouse04_Nozzle07(),
                                                temp8,humi8,KeyControl.getHouse04_Nozzle08(),"House04"));
                                    }



                                    if(temp_humi_cnt >= temp_humi_logging_cnt){
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

                                        // 다음시퀀스시간이 0일경우
                                        if(viewModel.getAirCompressor03and04_RunningTime().getValue()==0){

                                                // 현재 상태가 Houst_Stop의 식을 실행 - air comp만 실행
                                                viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_Stop);
                                                viewModel.getCleanning_Running_Situation_2().postValue(3);


                                            try {
                                                Thread.sleep(1000);
                                            }catch (Exception e){
                                                e.printStackTrace();
                                            }

                                            if(KeyControl.getRepeat_PauseTime()>0){     // 정시 시간이 0보다 크면 실행. 아니면 바로 HOUSE01방제 실행
                                                Log.d(TAG, "종류：" + "SerialModel.House01and02_Com_Aire_Pump_Receive case 1");
                                                Start_AirCompressor_time();
                                            }else{
                                                Log.d(TAG, "종류：" + "SerialModel.House01and02_Com_Aire_Pump_Receive case 2");


                                                if(viewModel.getRepeat_Cycle().getValue()>1){

                                                    viewModel.getRepeat_Cycle().postValue(viewModel.getRepeat_Cycle().getValue()-1);
                                                    viewModel.getRepeat_PauseTime().postValue(KeyControl.getRepeat_PauseTime());
                                                    viewModel.getAirCompressor01and02_RunningTime().postValue(KeyControl.getAirCompressor01and02_RunningTime());
                                                    viewModel.getPump01and02_RunningTime().postValue(KeyControl.getPump01and02_RunningTime());
                                                    viewModel.getAirCompressor03and04_RunningTime().postValue(KeyControl.getAirCompressor03and04_RunningTime());
                                                    viewModel.getPump03and04_RunningTime().postValue(KeyControl.getPump03and04_RunningTime());


                                                    if(KeyControl.getAirCompressor03and04_RunningTime()>0){
                                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Pump_Start,null);
                                                    }else if(KeyControl.getAirCompressor01and02_RunningTime()>0){
                                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Pump_Start,null);
                                                    }

                                                }else{
                                                    // 다음시퀀스시간이 0일경우
                                                    if(viewModel.getHouse01and02_Cleaning01_Time().getValue()!=0){
                                                        if(viewModel.getHouse01and02_Cleaning01_Time().getValue()>2){
                                                            // 콤프 펌프 둘다 0x01
                                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Start,null);
                                                        }else{
                                                            // 콤프만 0x01 펌프는 0x00
                                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Start,null);
                                                        }
                                                    }else if(viewModel.getHouse03and04_Cleaning01_Time().getValue()!=0){
                                                        if(viewModel.getHouse03and04_Cleaning01_Time().getValue()>2){
                                                            // 콤프 펌프 둘다 0x01
                                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Start,null);
                                                        }else{
                                                            // 콤프만 0x01 펌프는 0x00
                                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Start,null);
                                                        }
                                                    }else if(viewModel.getHouse01and02_Cleaning02_Time().getValue()!=0){
                                                        CleannerType = true;
                                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
                                                    }else if(viewModel.getHouse03and04_Cleaning02_Time().getValue()!=0){
                                                        CleannerType = true;
                                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                                    }else{
                                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
                                                    }
                                                }


                                            }

                                        }else{
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Pump_Start,null);
                                        }



                                }else if(bytes[3]==0x01 && bytes[4]== 0x01){    // 시작
                                    viewModel.getCleanning_Running_Situation_2().postValue(0);
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);

                                    try {
                                        Thread.sleep(1000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    if( viewModel.getPump01and02_RunningTime().getValue()>0){
                                        Start_Pump_time();
                                    }else{
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Pump_Stop,null);
                                    }

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

                                        // 현재 상태가 Houst_Stop의 식을 실행 - air comp만 실행
                                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_Stop);
                                        viewModel.getCleanning_Running_Situation_2().postValue(3);

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



                                        if(viewModel.getRepeat_Cycle().getValue()>1){


                                            viewModel.getRepeat_PauseTime().postValue(KeyControl.getRepeat_PauseTime());
                                            viewModel.getRepeat_Cycle().postValue(viewModel.getRepeat_Cycle().getValue()-1);
                                            viewModel.getAirCompressor01and02_RunningTime().postValue(KeyControl.getAirCompressor01and02_RunningTime());
                                            viewModel.getPump01and02_RunningTime().postValue(KeyControl.getPump01and02_RunningTime());
                                            viewModel.getAirCompressor03and04_RunningTime().postValue(KeyControl.getAirCompressor03and04_RunningTime());
                                            viewModel.getPump03and04_RunningTime().postValue(KeyControl.getPump03and04_RunningTime());


                                            if(KeyControl.getAirCompressor01and02_RunningTime()>0){
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Pump_Start,null);
                                            }else if(KeyControl.getAirCompressor03and04_RunningTime()>0){
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Pump_Start,null);
                                            }

                                        }else{
                                            // 다음시퀀스시간이 0일경우


                                            if(viewModel.getHouse01and02_Cleaning01_Time().getValue()!=0){
                                                if(viewModel.getHouse01and02_Cleaning01_Time().getValue()>2){
                                                    // 콤프 펌프 둘다 0x01
                                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Start,null);
                                                }else{
                                                    // 콤프만 0x01 펌프는 0x00
                                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Start,null);
                                                }
                                            }else if(viewModel.getHouse03and04_Cleaning01_Time().getValue()!=0){
                                                if(viewModel.getHouse03and04_Cleaning01_Time().getValue()>2){
                                                    // 콤프 펌프 둘다 0x01
                                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Start,null);
                                                }else{
                                                    // 콤프만 0x01 펌프는 0x00
                                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Start,null);
                                                }
                                            }else if(viewModel.getHouse01and02_Cleaning02_Time().getValue()!=0){
                                                CleannerType = true;
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
                                            }else if(viewModel.getHouse03and04_Cleaning02_Time().getValue()!=0){
                                                CleannerType = true;
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                            }else{
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
                                            }
                                        }

                                    }
                                }else if(bytes[3]==0x01 && bytes[4]== 0x01){    // 시작
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);

                                    try {
                                        Thread.sleep(1000);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }

                                    if( viewModel.getPump03and04_RunningTime().getValue()>0){
                                        Start_Pump_time();
                                    }else{
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Pump_Stop,null);
                                    }
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

                                    Log.d("isRealEndCom","isRealEndCom : "+ isRealEndCom);

                                    if(!isRealEndCom){
                                        return;
                                    }

//                                    isRealEndCom = false;



                                         if(viewModel.getHouse03and04_Cleaning01_Time().getValue()!=0){
                                            if(viewModel.getHouse03and04_Cleaning01_Time().getValue()>2){
                                                // 콤프 펌프 둘다 0x01
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Start,null);
                                            }else{
                                                // 콤프만 0x01 펌프는 0x00
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Start,null);
                                            }
                                        }else if(viewModel.getHouse01and02_Cleaning02_Time().getValue()!=0){
                                             CleannerType = true;
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
                                        }else if(viewModel.getHouse03and04_Cleaning02_Time().getValue()!=0){
                                             CleannerType = true;
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                        }else{
                                             SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
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

                                    Log.d("isRealEndCom","isRealEndCom : "+ isRealEndCom);

                                    if(!isRealEndCom){
                                        return;
                                    }

//                                    isRealEndCom = false;


                                        if(viewModel.getHouse01and02_Cleaning02_Time().getValue()>0){
                                            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
                                        }else{
                                            if(viewModel.getHouse03and04_Cleaning02_Time().getValue()>0){
                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                            }else{

                                                SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
                                            }
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

                                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                                        viewModel.getCleanning_Running_Situation_2().postValue(2);

                                        try {
                                            Thread.sleep(1000);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        Start_Cleanner_time();

                                }else{  // 정지시

                                    Log.d("isRealEndCom","isRealEndCom : "+ isRealEndCom);

                                    if(!isRealEndCom){
                                        return;
                                    }

//                                    isRealEndCom = false;

                                    if(viewModel.getHouse03and04_Cleaning02_Time().getValue() == 0){
                                        viewModel.getCleanning_Running_Situation_2().postValue(0);
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
                                        return;
                                    }

                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);

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

                                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                                        viewModel.getCleanning_Running_Situation_2().postValue(2);

                                        try {
                                            Thread.sleep(1000);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        Start_Cleanner_time();

                                }else{  // 정지시

                                    Log.d("isRealEndCom","isRealEndCom : "+ isRealEndCom);

                                    if(!isRealEndCom){
                                        return;
                                    }

//                                    isRealEndCom = false;

                                    viewModel.getCleanning_Running_Situation_2().postValue(0);
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
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
                    }else if(bytes[1]==SerialModel.House_All_Stop_Receive){     // 중단 멈춤
                        try {
                            byte[] data = new byte[]{bytes[3]};
                            byte[] crc = new byte[]{bytes[4],bytes[5]};

                            if(CRC16Util.receiveData(data,crc)){

                                Log.d("isRealEndCom","isRealEndCom : "+ isRealEndCom);

                                if(!isRealEndCom){
                                    return;
                                }

                                isRealEndCom = false;
                                viewModel.getIsRunning().postValue(false);
                                ToZeroPage();

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


                // 상태정보 변환
                try {
                    if (bytes[1] == SerialModel.House01and02_Com_Aire_Pump_Send) {
                        if(bytes[3]==0x01 && bytes[4]== 0x00){
                            return;
                        }
                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                        viewModel.getCleanning_Running_Situation_2().postValue(0);
                    }else if(bytes[1] == SerialModel.House03and04_Com_Aire_Pump_Send){
                        if(bytes[3]==0x01 && bytes[4]== 0x00){
                            return;
                        }
                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                        viewModel.getCleanning_Running_Situation_2().postValue(0);
                    }else if(bytes[1] == SerialModel.House01and02_Cleanner01_Send){

                        if(bytes[3]==0x01 && bytes[4]== 0x00){
                            return;
                        }
                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                        viewModel.getCleanning_Running_Situation_2().postValue(1);
                    }else if(bytes[1] == SerialModel.House03and04_Cleanner01_Send){
                        if(bytes[3]==0x01 && bytes[4]== 0x00){
                            return;
                        }
                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                        viewModel.getCleanning_Running_Situation_2().postValue(1);
                    }else if(bytes[1] == SerialModel.House01and02_Cleanner02_Send){
                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                        viewModel.getCleanning_Running_Situation_2().postValue(2);
                    }else if(bytes[1] == SerialModel.House03and04_Cleanner02_Send){
                        viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                        viewModel.getCleanning_Running_Situation_2().postValue(2);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }





//                switch (bytes[1]){
//
//                }

//                if(bytes[1] == 0x50){
//                    if(bytes[3] == 0x01){
//
//                    }else if(bytes[3] == 0x02){
//
//                    }else if(bytes[3] == 0x03){
//
//                    }else if(bytes[3] == 0x04){
//
//                    }else if(bytes[3] == 0x05){
//
//                    }
//
//                }
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

        SerialPortManager.getInstance().SendSerialCom(SerialModel.Require_Info,null);

        Logger.i("Device Start");





//        KeyControl.setHouse01_Nozzle01(2);
//        KeyControl.setHouse01_Nozzle02(2);
//        KeyControl.setHouse01_Nozzle03(2);
//        KeyControl.setHouse01_Nozzle04(2);
//        KeyControl.setHouse01_Nozzle05(2);
//        KeyControl.setHouse01_Nozzle06(150);

    }
    public static final int MULTIPLE_PERMISSIONS = 1801;
    private String[] permissions = {Manifest.permission.SYSTEM_ALERT_WINDOW};


    private boolean checkPermissions() {
        int result;
        List<String> permissionList = new ArrayList<>();
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(this, pm);

            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }

        if (!permissionList.isEmpty()) {



//            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);

            ActivityCompat.requestPermissions(this, permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
            return false;

        }
        return true;
    }

    /**
     * checkPermission 메소드를 실행한 후 콜백 메소드
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                boolean isDeny = false;
                if (grantResults.length > 0) {
                    Log.d(TAG,"grantResults = "+ grantResults.length);
                    for (int i = 0; i < permissions.length; i++) {
                        Log.d(TAG,"permissions = "+ permissions.length);
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            //permission denyed
                            isDeny = true;
                            Log.d(TAG,"permissions is deny");
                        }
                    }
                }

                if (isDeny) {
//                    showNoPermissionToastAndFinish();
                }
            }
        }
    }

    /**
     * 권한 거부 시 실행 메소드
     */
    private void showNoPermissionToastAndFinish() {

        Toast toast = Toast.makeText(this, "권한 요청에 동의 해주셔야 이용 가능합니다. 설정에서 권한 허용 하시기 바랍니다.", Toast.LENGTH_SHORT);
        toast.show();
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
        StopTimeThread();
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

                            if(viewModel.getRepeat_Cycle().getValue()>1){
                                // 시리얼 중지 보내기
//                                SerialPortManager.getInstance().sendBytes(new byte[]{0x07});

                                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Stop,null);
                            }else{
                                // 청소동작으로 넘어가야하는데..
                                viewModel.getRepeat_Cycle().postValue(0);
                                
                                // 청소시간이 0일경우
                                if(viewModel.getHouse01and02_Cleaning01_Time().getValue()!=0) {
                                    if(viewModel.getHouse01and02_Cleaning01_Time().getValue()>2){
                                        // 콤프 펌프 둘다 0x01
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Start,null);
                                    }else{
                                        // 콤프만 0x01 펌프는 0x00
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Start,null);
                                    }
                                }else  if(viewModel.getHouse03and04_Cleaning01_Time().getValue()!=0){
                                    if(viewModel.getHouse03and04_Cleaning01_Time().getValue()>2){
                                        // 콤프 펌프 둘다 0x01
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Start,null);
                                    }else{
                                        // 콤프만 0x01 펌프는 0x00
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Start,null);
                                    }
                                }else if(viewModel.getHouse01and02_Cleaning02_Time().getValue()!=0){
                                    CleannerType = true;
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
                                }else if(viewModel.getHouse03and04_Cleaning02_Time().getValue()!=0){
                                    CleannerType = true;
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                }else{
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
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


                            viewModel.getRepeat_Cycle().postValue(viewModel.getRepeat_Cycle().getValue()-1);

                            Log.d(TAG,"Check 1 : "+ (viewModel.getRepeat_Cycle().getValue()));
                            // 반복이 남이있을경우
                            if(viewModel.getRepeat_Cycle().getValue()>1){
                                if(KeyControl.getAirCompressor01and02_RunningTime()!=0){
                                    Log.d(TAG,"Check 2 : ");
                                    viewModel.getAirCompressor01and02_RunningTime().postValue(KeyControl.getAirCompressor01and02_RunningTime());
                                    viewModel.getPump01and02_RunningTime().postValue(KeyControl.getPump01and02_RunningTime());
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                                    viewModel.getCleanning_Running_Situation_2().postValue(0);
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Pump_Start,null);
                                }else if(KeyControl.getAirCompressor03and04_RunningTime()!=0){
                                    viewModel.getAirCompressor03and04_RunningTime().postValue(KeyControl.getAirCompressor03and04_RunningTime());
                                    viewModel.getPump03and04_RunningTime().postValue(KeyControl.getPump03and04_RunningTime());
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                                    viewModel.getCleanning_Running_Situation_2().postValue(0);
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Pump_Start,null);
                                }
                            }else{  // 반복이 없을경우
                                if(viewModel.getHouse01and02_Cleaning01_Time().getValue()!=0){
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                                    viewModel.getCleanning_Running_Situation_2().postValue(1);
                                    if(viewModel.getHouse01and02_Cleaning01_Time().getValue()>2){
                                        // 콤프 펌프 둘다 0x01
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Start,null);
                                    }else{
                                        // 콤프만 0x01 펌프는 0x00
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Start,null);
                                    }
                                }else if(viewModel.getHouse03and04_Cleaning01_Time().getValue()!=0){
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                                    viewModel.getCleanning_Running_Situation_2().postValue(1);
                                    if(viewModel.getHouse03and04_Cleaning01_Time().getValue()>2){
                                        // 콤프 펌프 둘다 0x01
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Start,null);
                                    }else{
                                        // 콤프만 0x01 펌프는 0x00
                                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Start,null);
                                    }
                                }else if(viewModel.getHouse01and02_Cleaning02_Time().getValue()!=0){
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
                                    viewModel.getCleanning_Running_Situation_2().postValue(2);
                                    CleannerType = true;
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
                                }else if(viewModel.getHouse03and04_Cleaning02_Time().getValue()!=0){
                                    viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
                                    viewModel.getCleanning_Running_Situation_2().postValue(2);
                                    CleannerType = true;
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
                                }else{
                                    SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
                                }
                            }


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
        if(Pump_timer != null ){
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


    private void Start_start_time(){

        if(start_time_timer != null){
            return;
        }

        start_time_timer = new Timer();

        if(viewModel.getStart_Time_Picker_Hour().getValue() == -1 || viewModel.getStart_Time_Picker_Min().getValue() == -1){
            return;
        }
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {

                int start_Hour = viewModel.getStart_Time_Picker_Hour().getValue();
                int start_min = viewModel.getStart_Time_Picker_Min().getValue();
                int end_Hour = viewModel.getEnd_Time_Picker_Hour().getValue();
                int end_min = viewModel.getEnd_Time_Picker_Min().getValue();

                Log.d("StartTimer","Start_start_time_Timer = "+start_Hour+":"+start_min+", end_time_Timer = "+end_Hour +":"+end_min+" is running : "+ viewModel.getIsRunning().getValue());

                Calendar calendar = Calendar.getInstance();

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    LocalDateTime startDateTime = LocalDateTime.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),start_Hour,start_min);
                    LocalDateTime nowDateTime = LocalDateTime.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE));
                    LocalDateTime endDateTime = LocalDateTime.of(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH),end_Hour,end_min);
                    Log.d("StartTimer","now time = "+nowDateTime);
                    Log.d("StartTimer","startDateTime = "+startDateTime);
                    Log.d("StartTimer","endDateTime = "+endDateTime);


                    if(nowDateTime.isBefore(startDateTime)){
                        Log.d("StartTimer","startDateTime.isAfter(nowDateTime)");
                        return;
                    }

                    if(endDateTime.isBefore(nowDateTime)||endDateTime.isEqual(nowDateTime)){
                        Log.d("StartTimer","endDateTime.isBefore(nowDateTime)||endDateTime.isEqual(nowDateTime)");
                        if(startDateTime.isBefore(endDateTime)||startDateTime.isEqual(endDateTime)){
                            Log.d("StartTimer","startDateTime.isBefore(endDateTime)||startDateTime.isEqual(endDateTime)");
                            return;
                        }
                    }

                }

                if(!viewModel.getIsRunning().getValue()){

                    viewModel.getIsRunning().postValue(true);
                    Start_Running();
                }

            }
        };

        start_time_timer.schedule(right_timerTask,0,20*1000);
    }


    private void Stop_start_time(){
        Log.d(TAG,"Stop_start_time");
        if(start_time_timer != null){
            start_time_timer.cancel();
            start_time_timer = null;
        }
    }

    private void Start_end_time(){

        if(end_time_timer != null){
            return;
        }

        end_time_timer = new Timer();



        if(viewModel.getEnd_Time_Picker_Hour().getValue() == -1 || viewModel.getEnd_Time_Picker_Min().getValue() == -1){
            return;
        }
        TimerTask right_timerTask = new TimerTask() {
            @Override
            public void run() {

                int end_Hour = viewModel.getEnd_Time_Picker_Hour().getValue();
                int end_min = viewModel.getEnd_Time_Picker_Min().getValue();

                Log.d(TAG,"Start_end_time_Timer = "+end_Hour+":"+end_min+", is running : "+ viewModel.getIsRunning().getValue());

                String[] t = new SimpleDateFormat("HH:mm").format(new Date()).split(":");
                int h = Integer.parseInt(t[0]);
                int m = Integer.parseInt(t[1]);

                if(h != end_Hour){
                    return;
                }

                if(m== end_min-1){
                    if(viewModel.getIsRunning().getValue()){
                        StopRunningThreadSetting();

                        SerialPortManager.getInstance().House_01_02_Nozzle_Close();
                        SerialPortManager.getInstance().House_03_04_Nozzle_Close();

//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Stop,null);
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Stop,null);
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Stop,null);
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Stop,null);
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Pump_Stop,null);
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Pump_Stop,null);
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Pump_Stop,null);
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Pump_Stop,null);
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Stop,null);
//                        SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Stop,null);


                    }
                }

                if(m == end_min){

                    if(viewModel.getIsRunning().getValue()){
                        SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);

                    }
                }

            }
        };

        end_time_timer.schedule(right_timerTask,0,20*1000);
    }


    private void Stop_end_time(){
        Log.d(TAG,"Stop_end_time");
        if(end_time_timer != null){
            end_time_timer.cancel();
            end_time_timer = null;
        }
    }

    //전체 프로세스 시작
    private void Start_Running(){
        // 다음시퀀스시간이 0일경우
        if(viewModel.getAirCompressor01and02_RunningTime().getValue()!=0){
            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Air_Pump_Start,null);
        }else if(viewModel.getAirCompressor03and04_RunningTime().getValue()!=0){
            viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
            viewModel.getCleanning_Running_Situation_2().postValue(0);
            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Air_Pump_Start,null);
        }else if(viewModel.getHouse01and02_Cleaning01_Time().getValue()!=0){
            viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
            viewModel.getCleanning_Running_Situation_2().postValue(1);
            if(viewModel.getHouse01and02_Cleaning01_Time().getValue()>2){
                // 콤프 펌프 둘다 0x01
                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Start,null);
            }else{
                // 콤프만 0x01 펌프는 0x00
                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner01_Air_Start,null);
            }
        }else if(viewModel.getHouse03and04_Cleaning01_Time().getValue()!=0){
            viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
            viewModel.getCleanning_Running_Situation_2().postValue(1);
            if(viewModel.getHouse03and04_Cleaning01_Time().getValue()>2){
                // 콤프 펌프 둘다 0x01
                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Start,null);
            }else{
                // 콤프만 0x01 펌프는 0x00
                SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner01_Air_Start,null);
            }
        }else if(viewModel.getHouse01and02_Cleaning02_Time().getValue()!=0){
            viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_01_02);
            viewModel.getCleanning_Running_Situation_2().postValue(2);
            CleannerType = true;
            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Cleanner02_Start,null);
        }else if(viewModel.getHouse03and04_Cleaning02_Time().getValue()!=0){
            viewModel.getCleanning_Running_Situation_1().postValue(DeviceModel.House_03_04);
            viewModel.getCleanning_Running_Situation_2().postValue(2);
            CleannerType = true;
            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_03_04_Cleanner02_Start,null);
        }else{
            SerialPortManager.getInstance().SendSerialCom(SerialModel.House_All_Stop,null);
        }

        Logger.i("Running Start");
    }


    //부분 쓰레드 종료
    private void StopRunningThreadSetting(){
        Stop_AirCompressor_time();
        Stop_Pump_time();
        Stop_Cleanner_time();
        EndCleaning();
    }

    private void StopTimeThread(){
        Stop_start_time();
        Stop_end_time();
    }


    private void ToZeroPage(){
        Intent intent = new Intent("Page");
        intent.putExtra("index",0);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    // 설정 변수 초기화
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
    
    private int TH_Set_Command(int Th_main,int Deviation,int temp ,SerialModel nozzle){

        boolean situation_01_02 = Objects.equals(viewModel.getCleanning_Running_Situation_1().getValue(), DeviceModel.House_01_02) && viewModel.getCleanning_Running_Situation_2().getValue() == 0;
        boolean situation_03_04 = Objects.equals(viewModel.getCleanning_Running_Situation_1().getValue(), DeviceModel.House_03_04) && viewModel.getCleanning_Running_Situation_2().getValue() == 0;

        int result = 150;

        if(temp == 150){
            return result;
        }

        if(temp< Th_main - (Deviation*3)){
            result = 1;
        }else if((temp >= Th_main - (Deviation*3))&&(temp<Th_main - Deviation)){
            result = 2;
        }else if((temp >= Th_main - Deviation)&&(temp< Th_main + Deviation)){
            result = 3;
        }else if((temp >= Th_main + Deviation)&&(temp<Th_main + (Deviation*3))){
            result = 4;
        }else if(temp >= Th_main + (Deviation*3)){
            result = 5;
        }

        if(Boolean.TRUE.equals(viewModel.getIsRunning().getValue())) {
            if (nozzle.equals(SerialModel.House_01_Nozzle_1) || nozzle.equals(SerialModel.House_01_Nozzle_2) || nozzle.equals(SerialModel.House_01_Nozzle_3) || nozzle.equals(SerialModel.House_01_Nozzle_4) || nozzle.equals(SerialModel.House_01_Nozzle_5) || nozzle.equals(SerialModel.House_01_Nozzle_6) || nozzle.equals(SerialModel.House_01_Nozzle_7) || nozzle.equals(SerialModel.House_01_Nozzle_8) || nozzle.equals(SerialModel.House_02_Nozzle_1) || nozzle.equals(SerialModel.House_02_Nozzle_2) || nozzle.equals(SerialModel.House_02_Nozzle_3) || nozzle.equals(SerialModel.House_02_Nozzle_4) || nozzle.equals(SerialModel.House_02_Nozzle_5) || nozzle.equals(SerialModel.House_02_Nozzle_6) || nozzle.equals(SerialModel.House_02_Nozzle_7) || nozzle.equals(SerialModel.House_02_Nozzle_8)) {
                if(situation_01_02){
                    SerialPortManager.getInstance().SendSerialCom(nozzle, (byte) result);
                }
            }else if (nozzle.equals(SerialModel.House_03_Nozzle_1) || nozzle.equals(SerialModel.House_03_Nozzle_2) || nozzle.equals(SerialModel.House_03_Nozzle_3) || nozzle.equals(SerialModel.House_03_Nozzle_4) || nozzle.equals(SerialModel.House_03_Nozzle_5) || nozzle.equals(SerialModel.House_03_Nozzle_6) || nozzle.equals(SerialModel.House_03_Nozzle_7) || nozzle.equals(SerialModel.House_03_Nozzle_8) || nozzle.equals(SerialModel.House_04_Nozzle_1) || nozzle.equals(SerialModel.House_04_Nozzle_2) || nozzle.equals(SerialModel.House_04_Nozzle_3) || nozzle.equals(SerialModel.House_04_Nozzle_4) || nozzle.equals(SerialModel.House_04_Nozzle_5) || nozzle.equals(SerialModel.House_04_Nozzle_6) || nozzle.equals(SerialModel.House_04_Nozzle_7) || nozzle.equals(SerialModel.House_04_Nozzle_8)) {
                if(situation_03_04){
                    SerialPortManager.getInstance().SendSerialCom(nozzle, (byte) result);
                }
            }
        }
        return result;


    }
}