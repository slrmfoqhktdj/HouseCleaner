package com.example.housecleaner02.model;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.housecleaner02.sharedpreferences.KeyControl;

public class MainViewModel extends ViewModel {
    private MutableLiveData<Boolean> IsRunning;    // 실행여부
    private MutableLiveData<DeviceModel> Cleanning_Running_Situation_1; //  방제 진행상황
    private MutableLiveData<Integer> Cleanning_Running_Situation_2; // 청소 진행상황
    private MutableLiveData<Integer> Repeat_Cycle;    // 반복주기
    private MutableLiveData<Integer> Repeat_PauseTime;  // 반복휴지시간

    private MutableLiveData<String> main_time_tv;     //메인 시간
    private MutableLiveData<Integer> House01_Temp1_1;  //하우스 1 - 온도
//    private MutableLiveData<Integer> House01_Temp1_2;  //하우스 1 - 온도
    private MutableLiveData<Integer> House01_Temp2_1;  //하우스 1 - 온도
//    private MutableLiveData<Integer> House01_Temp2_2;  //하우스 1 - 온도
    private MutableLiveData<Integer> House01_Temp3_1;  //하우스 1 - 온도
//    private MutableLiveData<Integer> House01_Temp3_2;  //하우스 1 - 온도
    private MutableLiveData<Integer> House01_Temp4_1;  //하우스 1 - 온도
//    private MutableLiveData<Integer> House01_Temp4_2;  //하우스 1 - 온도
    private MutableLiveData<Integer> House01_Temp5_1;  //하우스 1 - 온도
    private MutableLiveData<Integer> House01_Temp6_1;  //하우스 1 - 온도
    private MutableLiveData<Integer> House01_Temp7_1;  //하우스 1 - 온도
    private MutableLiveData<Integer> House01_Temp8_1;  //하우스 1 - 온도
    
    private MutableLiveData<Integer> House01_Humi1_1;  //하우스 1 - 습도
//    private MutableLiveData<Integer> House01_Humi1_2;  //하우스 1 - 습도
    private MutableLiveData<Integer> House01_Humi2_1;  //하우스 1 - 습도
//    private MutableLiveData<Integer> House01_Humi2_2;  //하우스 1 - 습도
    private MutableLiveData<Integer> House01_Humi3_1;  //하우스 1 - 습도
//    private MutableLiveData<Integer> House01_Humi3_2;  //하우스 1 - 습도
    private MutableLiveData<Integer> House01_Humi4_1;  //하우스 1 - 습도
//    private MutableLiveData<Integer> House01_Humi4_2;  //하우스 1 - 습도
    private MutableLiveData<Integer> House01_Humi5_1;  //하우스 1 - 습도
    private MutableLiveData<Integer> House01_Humi6_1;  //하우스 1 - 습도
    private MutableLiveData<Integer> House01_Humi7_1;  //하우스 1 - 습도
    private MutableLiveData<Integer> House01_Humi8_1;  //하우스 1 - 습도

    private MutableLiveData<Integer> House02_Temp1_1;  //하우스 2 - 온도
//    private MutableLiveData<Integer> House02_Temp1_2;  //하우스 2 - 온도
    private MutableLiveData<Integer> House02_Temp2_1;  //하우스 2 - 온도
//    private MutableLiveData<Integer> House02_Temp2_2;  //하우스 2 - 온도
    private MutableLiveData<Integer> House02_Temp3_1;  //하우스 2 - 온도
//    private MutableLiveData<Integer> House02_Temp3_2;  //하우스 2 - 온도
    private MutableLiveData<Integer> House02_Temp4_1;  //하우스 2 - 온도
//    private MutableLiveData<Integer> House02_Temp4_2;  //하우스 2 - 온도
    private MutableLiveData<Integer> House02_Temp5_1;  //하우스 2 - 온도
    private MutableLiveData<Integer> House02_Temp6_1;  //하우스 2 - 온도
    private MutableLiveData<Integer> House02_Temp7_1;  //하우스 2 - 온도
    private MutableLiveData<Integer> House02_Temp8_1;  //하우스 2 - 온도
    
    private MutableLiveData<Integer> House02_Humi1_1;  //하우스 2 - 습도
//    private MutableLiveData<Integer> House02_Humi1_2;  //하우스 2 - 습도
    private MutableLiveData<Integer> House02_Humi2_1;  //하우스 2 - 습도
//    private MutableLiveData<Integer> House02_Humi2_2;  //하우스 2 - 습도
    private MutableLiveData<Integer> House02_Humi3_1;  //하우스 2 - 습도
//    private MutableLiveData<Integer> House02_Humi3_2;  //하우스 2 - 습도
    private MutableLiveData<Integer> House02_Humi4_1;  //하우스 2 - 습도
//    private MutableLiveData<Integer> House02_Humi4_2;  //하우스 2 - 습도
    private MutableLiveData<Integer> House02_Humi5_1;  //하우스 2 - 습도
    private MutableLiveData<Integer> House02_Humi6_1;  //하우스 2 - 습도
    private MutableLiveData<Integer> House02_Humi7_1;  //하우스 2 - 습도
    private MutableLiveData<Integer> House02_Humi8_1;  //하우스 2 - 습도
    
    private MutableLiveData<Integer> House03_Temp1_1;  //하우스 3 - 온도
//    private MutableLiveData<Integer> House03_Temp1_2;  //하우스 3 - 온도
    private MutableLiveData<Integer> House03_Temp2_1;  //하우스 3 - 온도
//    private MutableLiveData<Integer> House03_Temp2_2;  //하우스 3 - 온도
    private MutableLiveData<Integer> House03_Temp3_1;  //하우스 3 - 온도
//    private MutableLiveData<Integer> House03_Temp3_2;  //하우스 3 - 온도
    private MutableLiveData<Integer> House03_Temp4_1;  //하우스 3 - 온도
//    private MutableLiveData<Integer> House03_Temp4_2;  //하우스 3 - 온도
    private MutableLiveData<Integer> House03_Temp5_1;  //하우스 3 - 온도
    private MutableLiveData<Integer> House03_Temp6_1;  //하우스 3 - 온도
    private MutableLiveData<Integer> House03_Temp7_1;  //하우스 3 - 온도
    private MutableLiveData<Integer> House03_Temp8_1;  //하우스 3 - 온도
    
    private MutableLiveData<Integer> House03_Humi1_1;  //하우스 3 - 습도
//    private MutableLiveData<Integer> House03_Humi1_2;  //하우스 3 - 습도
    private MutableLiveData<Integer> House03_Humi2_1;  //하우스 3 - 습도
//    private MutableLiveData<Integer> House03_Humi2_2;  //하우스 3 - 습도
    private MutableLiveData<Integer> House03_Humi3_1;  //하우스 3 - 습도
//    private MutableLiveData<Integer> House03_Humi3_2;  //하우스 3 - 습도
    private MutableLiveData<Integer> House03_Humi4_1;  //하우스 3 - 습도
//    private MutableLiveData<Integer> House03_Humi4_2;  //하우스 3 - 습도
    private MutableLiveData<Integer> House03_Humi5_1;  //하우스 3 - 습도
    private MutableLiveData<Integer> House03_Humi6_1;  //하우스 3 - 습도
    private MutableLiveData<Integer> House03_Humi7_1;  //하우스 3 - 습도
    private MutableLiveData<Integer> House03_Humi8_1;  //하우스 3 - 습도
    private MutableLiveData<Integer> House04_Temp1_1;  //하우스 4 - 온도
//    private MutableLiveData<Integer> House04_Temp1_2;  //하우스 4 - 온도
    private MutableLiveData<Integer> House04_Temp2_1;  //하우스 4 - 온도
//    private MutableLiveData<Integer> House04_Temp2_2;  //하우스 4 - 온도
    private MutableLiveData<Integer> House04_Temp3_1;  //하우스 4 - 온도
//    private MutableLiveData<Integer> House04_Temp3_2;  //하우스 4 - 온도
    private MutableLiveData<Integer> House04_Temp4_1;  //하우스 4 - 온도
//    private MutableLiveData<Integer> House04_Temp4_2;  //하우스 4 - 온도
    private MutableLiveData<Integer> House04_Temp5_1;  //하우스 4 - 온도
    private MutableLiveData<Integer> House04_Temp6_1;  //하우스 4 - 온도
    private MutableLiveData<Integer> House04_Temp7_1;  //하우스 4 - 온도
    private MutableLiveData<Integer> House04_Temp8_1;  //하우스 4 - 온도
    
    private MutableLiveData<Integer> House04_Humi1_1;  //하우스 4 - 습도
//    private MutableLiveData<Integer> House04_Humi1_2;  //하우스 4 - 습도
    private MutableLiveData<Integer> House04_Humi2_1;  //하우스 4 - 습도
//    private MutableLiveData<Integer> House04_Humi2_2;  //하우스 4 - 습도
    private MutableLiveData<Integer> House04_Humi3_1;  //하우스 4 - 습도
//    private MutableLiveData<Integer> House04_Humi3_2;  //하우스 4 - 습도
    private MutableLiveData<Integer> House04_Humi4_1;  //하우스 4 - 습도
//    private MutableLiveData<Integer> House04_Humi4_2;  //하우스 4 - 습도
    private MutableLiveData<Integer> House04_Humi5_1;  //하우스 4 - 습도
    private MutableLiveData<Integer> House04_Humi6_1;  //하우스 4 - 습도
    private MutableLiveData<Integer> House04_Humi7_1;  //하우스 4 - 습도
    private MutableLiveData<Integer> House04_Humi8_1;  //하우스 4 - 습도

    private MutableLiveData<Boolean> Water_Shortage;    // 물부족

    private MutableLiveData<Integer> AirCompressor01and02_RunningTime;   // 하우스 1 - 콤프동작시간
    private MutableLiveData<Integer> Pump01and02_RunningTime;  // 하우스 1 - 펌프동작시간
    private MutableLiveData<Integer> House01and02_Cleaning01_Time;   // 하우스 1 - 청소1 시간
    private MutableLiveData<Integer> House01and02_Cleaning02_Time;  // 하우스 1 - 청소2시간
    private MutableLiveData<Boolean> House01_Nozzle_Auto;  // 하우스 1 - 노즐자동
    private MutableLiveData<Boolean> House02_Nozzle_Auto;  // 하우스 2 - 노즐자동


    private MutableLiveData<Integer> AirCompressor03and04_RunningTime;   // 하우스 3 - 콤프동작시간
    private MutableLiveData<Integer> Pump03and04_RunningTime;  // 하우스 3 - 펌프동작시간
    private MutableLiveData<Integer> House03and04_Cleaning01_Time;   // 하우스 3 - 청소1 시간
    private MutableLiveData<Integer> House03and04_Cleaning02_Time;  // 하우스 3 - 청소2시간
    private MutableLiveData<Boolean> House03_Nozzle_Auto;  // 하우스 3 - 노즐자동
    private MutableLiveData<Boolean> House04_Nozzle_Auto;  // 하우스 4 - 노즐자동
    
    private MutableLiveData<Boolean> End_Time_Sw;   // 종료시간 설정여부

    private MutableLiveData<Integer> Start_Time_Picker_Hour;  // 시작시간 시각
    private MutableLiveData<Integer> Start_Time_Picker_Min;   // 시작시간 분
    private MutableLiveData<Integer> End_Time_Picker_Hour;  // 종료시간 시각
    private MutableLiveData<Integer> End_Time_Picker_Min;   // 종료시간 분




    public MutableLiveData<Boolean> getIsRunning()
    {
        if(IsRunning == null)
            IsRunning = new MutableLiveData<>(false);
        return IsRunning;
    }
    public MutableLiveData<DeviceModel> getCleanning_Running_Situation_1()
    {
        if(Cleanning_Running_Situation_1 == null)
            Cleanning_Running_Situation_1 = new MutableLiveData<>(DeviceModel.House_01_02);
        return Cleanning_Running_Situation_1;
    }
    public MutableLiveData<Integer> getCleanning_Running_Situation_2()
    {
        if(Cleanning_Running_Situation_2 == null)
            Cleanning_Running_Situation_2 = new MutableLiveData<>(0);
        return Cleanning_Running_Situation_2;
    }
    public MutableLiveData<Integer> getRepeat_Cycle()
    {
        if(Repeat_Cycle == null)
            Repeat_Cycle = new MutableLiveData<>(KeyControl.getRepeat_Cycle());
        return Repeat_Cycle;
    }
    public MutableLiveData<Integer> getRepeat_PauseTime()
    {
        if(Repeat_PauseTime == null)
            Repeat_PauseTime = new MutableLiveData<>(KeyControl.getRepeat_PauseTime());
        return Repeat_PauseTime;
    }
    public MutableLiveData<String> getMain_time_tv() {
        if(main_time_tv == null)
            main_time_tv = new MutableLiveData<>("00:00:00");
        return main_time_tv;
    }



    public MutableLiveData<Integer> getHouse01_Temp1_1()
    {
        if(House01_Temp1_1 == null)
            House01_Temp1_1 = new MutableLiveData<>(0);
        return House01_Temp1_1;
    }
//    public MutableLiveData<Integer> getHouse01_Temp1_2()
//    {
//        if(House01_Temp1_2 == null)
//            House01_Temp1_2 = new MutableLiveData<>(0);
//        return House01_Temp1_2;
//    }
    public MutableLiveData<Integer> getHouse01_Temp2_1()
    {
        if(House01_Temp2_1 == null)
            House01_Temp2_1 = new MutableLiveData<>(0);
        return House01_Temp2_1;
    }
//    public MutableLiveData<Integer> getHouse01_Temp2_2()
//    {
//        if(House01_Temp2_2 == null)
//            House01_Temp2_2 = new MutableLiveData<>(0);
//        return House01_Temp2_2;
//    }
    public MutableLiveData<Integer> getHouse01_Temp3_1()
    {
        if(House01_Temp3_1 == null)
            House01_Temp3_1 = new MutableLiveData<>(0);
        return House01_Temp3_1;
    }
//    public MutableLiveData<Integer> getHouse01_Temp3_2()
//    {
//        if(House01_Temp3_2 == null)
//            House01_Temp3_2 = new MutableLiveData<>(0);
//        return House01_Temp3_2;
//    }
    public MutableLiveData<Integer> getHouse01_Temp4_1()
    {
        if(House01_Temp4_1 == null)
            House01_Temp4_1 = new MutableLiveData<>(0);
        return House01_Temp4_1;
    }
//    public MutableLiveData<Integer> getHouse01_Temp4_2()
//    {
//        if(House01_Temp4_2 == null)
//            House01_Temp4_2 = new MutableLiveData<>(0);
//        return House01_Temp4_2;
//    }

    public MutableLiveData<Integer> getHouse01_Temp5_1()
    {
        if(House01_Temp5_1 == null)
            House01_Temp5_1 = new MutableLiveData<>(0);
        return House01_Temp5_1;
    }

    public MutableLiveData<Integer> getHouse01_Temp6_1()
    {
        if(House01_Temp6_1 == null)
            House01_Temp6_1 = new MutableLiveData<>(0);
        return House01_Temp6_1;
    }

    public MutableLiveData<Integer> getHouse01_Temp7_1()
    {
        if(House01_Temp7_1 == null)
            House01_Temp7_1 = new MutableLiveData<>(0);
        return House01_Temp7_1;
    }


    public MutableLiveData<Integer> getHouse01_Temp8_1()
    {
        if(House01_Temp8_1 == null)
            House01_Temp8_1 = new MutableLiveData<>(0);
        return House01_Temp8_1;
    }



    public MutableLiveData<Integer> getHouse01_Humi1_1()
    {
        if(House01_Humi1_1 == null)
            House01_Humi1_1 = new MutableLiveData<>(0);
        return House01_Humi1_1;
    }
//    public MutableLiveData<Integer> getHouse01_Humi1_2()
//    {
//        if(House01_Humi1_2 == null)
//            House01_Humi1_2 = new MutableLiveData<>(0);
//        return House01_Humi1_2;
//    }
    public MutableLiveData<Integer> getHouse01_Humi2_1()
    {
        if(House01_Humi2_1 == null)
            House01_Humi2_1 = new MutableLiveData<>(0);
        return House01_Humi2_1;
    }
//    public MutableLiveData<Integer> getHouse01_Humi2_2()
//    {
//        if(House01_Humi2_2 == null)
//            House01_Humi2_2 = new MutableLiveData<>(0);
//        return House01_Humi2_2;
//    }
    public MutableLiveData<Integer> getHouse01_Humi3_1()
    {
        if(House01_Humi3_1 == null)
            House01_Humi3_1 = new MutableLiveData<>(0);
        return House01_Humi3_1;
    }
//    public MutableLiveData<Integer> getHouse01_Humi3_2()
//    {
//        if(House01_Humi3_2 == null)
//            House01_Humi3_2 = new MutableLiveData<>(0);
//        return House01_Humi3_2;
//    }
    public MutableLiveData<Integer> getHouse01_Humi4_1()
    {
        if(House01_Humi4_1 == null)
            House01_Humi4_1 = new MutableLiveData<>(0);
        return House01_Humi4_1;
    }
//    public MutableLiveData<Integer> getHouse01_Humi4_2()
//    {
//        if(House01_Humi4_2 == null)
//            House01_Humi4_2 = new MutableLiveData<>(0);
//        return House01_Humi4_2;
//    }
    public MutableLiveData<Integer> getHouse01_Humi5_1()
    {
        if(House01_Humi5_1 == null)
            House01_Humi5_1 = new MutableLiveData<>(0);
        return House01_Humi5_1;
    }
    public MutableLiveData<Integer> getHouse01_Humi6_1()
    {
        if(House01_Humi6_1 == null)
            House01_Humi6_1 = new MutableLiveData<>(0);
        return House01_Humi6_1;
    }

    public MutableLiveData<Integer> getHouse01_Humi7_1()
    {
        if(House01_Humi7_1 == null)
            House01_Humi7_1 = new MutableLiveData<>(0);
        return House01_Humi7_1;
    }

    public MutableLiveData<Integer> getHouse01_Humi8_1()
    {
        if(House01_Humi8_1 == null)
            House01_Humi8_1 = new MutableLiveData<>(0);
        return House01_Humi8_1;
    }


    public MutableLiveData<Integer> getHouse02_Temp1_1()
    {
        if(House02_Temp1_1 == null)
            House02_Temp1_1 = new MutableLiveData<>(0);
        return House02_Temp1_1;
    }
//    public MutableLiveData<Integer> getHouse02_Temp1_2()
//    {
//        if(House02_Temp1_2 == null)
//            House02_Temp1_2 = new MutableLiveData<>(0);
//        return House02_Temp1_2;
//    }
    public MutableLiveData<Integer> getHouse02_Temp2_1()
    {
        if(House02_Temp2_1 == null)
            House02_Temp2_1 = new MutableLiveData<>(0);
        return House02_Temp2_1;
    }
//    public MutableLiveData<Integer> getHouse02_Temp2_2()
//    {
//        if(House02_Temp2_2 == null)
//            House02_Temp2_2 = new MutableLiveData<>(0);
//        return House02_Temp2_2;
//    }
    public MutableLiveData<Integer> getHouse02_Temp3_1()
    {
        if(House02_Temp3_1 == null)
            House02_Temp3_1 = new MutableLiveData<>(0);
        return House02_Temp3_1;
    }
//    public MutableLiveData<Integer> getHouse02_Temp3_2()
//    {
//        if(House02_Temp3_2 == null)
//            House02_Temp3_2 = new MutableLiveData<>(0);
//        return House02_Temp3_2;
//    }
    public MutableLiveData<Integer> getHouse02_Temp4_1()
    {
        if(House02_Temp4_1 == null)
            House02_Temp4_1 = new MutableLiveData<>(0);
        return House02_Temp4_1;
    }
//    public MutableLiveData<Integer> getHouse02_Temp4_2()
//    {
//        if(House02_Temp4_2 == null)
//            House02_Temp4_2 = new MutableLiveData<>(0);
//        return House02_Temp4_2;
//    }
    public MutableLiveData<Integer> getHouse02_Temp5_1()
    {   
        if(House02_Temp5_1 == null)
            House02_Temp5_1 = new MutableLiveData<>(0);
        return House02_Temp5_1;
    }

    public MutableLiveData<Integer> getHouse02_Temp6_1()
    {
        if(House02_Temp6_1 == null)
            House02_Temp6_1 = new MutableLiveData<>(0);
        return House02_Temp6_1;
    }

    public MutableLiveData<Integer> getHouse02_Temp7_1()
    {
        if(House02_Temp7_1 == null)
            House02_Temp7_1 = new MutableLiveData<>(0);
        return House02_Temp7_1;
    }

    public MutableLiveData<Integer> getHouse02_Temp8_1()
    {
        if(House02_Temp8_1 == null)
            House02_Temp8_1 = new MutableLiveData<>(0);
        return House02_Temp8_1;
    }

    public MutableLiveData<Integer> getHouse02_Humi1_1()
    {
        if(House02_Humi1_1 == null)
            House02_Humi1_1 = new MutableLiveData<>(0);
        return House02_Humi1_1;
    }
//    public MutableLiveData<Integer> getHouse02_Humi1_2()
//    {
//        if(House02_Humi1_2 == null)
//            House02_Humi1_2 = new MutableLiveData<>(0);
//        return House02_Humi1_2;
//    }
    public MutableLiveData<Integer> getHouse02_Humi2_1()
    {
        if(House02_Humi2_1 == null)
            House02_Humi2_1 = new MutableLiveData<>(0);
        return House02_Humi2_1;
    }
//    public MutableLiveData<Integer> getHouse02_Humi2_2()
//    {
//        if(House02_Humi2_2 == null)
//            House02_Humi2_2 = new MutableLiveData<>(0);
//        return House02_Humi2_2;
//    }
    public MutableLiveData<Integer> getHouse02_Humi3_1()
    {
        if(House02_Humi3_1 == null)
            House02_Humi3_1 = new MutableLiveData<>(0);
        return House02_Humi3_1;
    }
//    public MutableLiveData<Integer> getHouse02_Humi3_2()
//    {
//        if(House02_Humi3_2 == null)
//            House02_Humi3_2 = new MutableLiveData<>(0);
//        return House02_Humi3_2;
//    }
    public MutableLiveData<Integer> getHouse02_Humi4_1()
    {
        if(House02_Humi4_1 == null)
            House02_Humi4_1 = new MutableLiveData<>(0);
        return House02_Humi4_1;
    }
//    public MutableLiveData<Integer> getHouse02_Humi4_2()
//    {
//        if(House02_Humi4_2 == null)
//            House02_Humi4_2 = new MutableLiveData<>(0);
//        return House02_Humi4_2;
//    }
    public MutableLiveData<Integer> getHouse02_Humi5_1()
    {
        if(House02_Humi5_1 == null)
            House02_Humi5_1 = new MutableLiveData<>(0);
        return House02_Humi5_1;
    }
    public MutableLiveData<Integer> getHouse02_Humi6_1()
    {
        if(House02_Humi6_1 == null)
            House02_Humi6_1 = new MutableLiveData<>(0);
        return House02_Humi6_1;
    }

    public MutableLiveData<Integer> getHouse02_Humi7_1()
    {
        if(House02_Humi7_1 == null)
            House02_Humi7_1 = new MutableLiveData<>(0);
        return House02_Humi7_1;
    }

    public MutableLiveData<Integer> getHouse02_Humi8_1()
    {
        if(House02_Humi8_1 == null)
            House02_Humi8_1 = new MutableLiveData<>(0);
        return House02_Humi8_1;
    }


    public MutableLiveData<Integer> getHouse03_Temp1_1()
    {
        if(House03_Temp1_1 == null)
            House03_Temp1_1 = new MutableLiveData<>(0);
        return House03_Temp1_1;
    }
//    public MutableLiveData<Integer> getHouse03_Temp1_2()
//    {
//        if(House03_Temp1_2 == null)
//            House03_Temp1_2 = new MutableLiveData<>(0);
//        return House03_Temp1_2;
//    }
    public MutableLiveData<Integer> getHouse03_Temp2_1()
    {
        if(House03_Temp2_1 == null)
            House03_Temp2_1 = new MutableLiveData<>(0);
        return House03_Temp2_1;
    }
//    public MutableLiveData<Integer> getHouse03_Temp2_2()
//    {
//        if(House03_Temp2_2 == null)
//            House03_Temp2_2 = new MutableLiveData<>(0);
//        return House03_Temp2_2;
//    }
    public MutableLiveData<Integer> getHouse03_Temp3_1()
    {
        if(House03_Temp3_1 == null)
            House03_Temp3_1 = new MutableLiveData<>(0);
        return House03_Temp3_1;
    }
//    public MutableLiveData<Integer> getHouse03_Temp3_2()
//    {
//        if(House03_Temp3_2 == null)
//            House03_Temp3_2 = new MutableLiveData<>(0);
//        return House03_Temp3_2;
//    }
    public MutableLiveData<Integer> getHouse03_Temp4_1()
    {
        if(House03_Temp4_1 == null)
            House03_Temp4_1 = new MutableLiveData<>(0);
        return House03_Temp4_1;
    }
//    public MutableLiveData<Integer> getHouse03_Temp4_2()
//    {
//        if(House03_Temp4_2 == null)
//            House03_Temp4_2 = new MutableLiveData<>(0);
//        return House03_Temp4_2;
//    }
    public MutableLiveData<Integer> getHouse03_Temp5_1()
    {
        if(House03_Temp5_1 == null)
            House03_Temp5_1 = new MutableLiveData<>(0);
        return House03_Temp5_1;
    }

    public MutableLiveData<Integer> getHouse03_Temp6_1()
    {
        if(House03_Temp6_1 == null)
            House03_Temp6_1 = new MutableLiveData<>(0);
        return House03_Temp6_1;
    }

    public MutableLiveData<Integer> getHouse03_Temp7_1()
    {
        if(House03_Temp7_1 == null)
            House03_Temp7_1 = new MutableLiveData<>(0);
        return House03_Temp7_1;
    }

    public MutableLiveData<Integer> getHouse03_Temp8_1()
    {
        if(House03_Temp8_1 == null)
            House03_Temp8_1 = new MutableLiveData<>(0);
        return House03_Temp8_1;
    }


    public MutableLiveData<Integer> getHouse03_Humi1_1()
    {
        if(House03_Humi1_1 == null)
            House03_Humi1_1 = new MutableLiveData<>(0);
        return House03_Humi1_1;
    }
//    public MutableLiveData<Integer> getHouse03_Humi1_2()
//    {
//        if(House03_Humi1_2 == null)
//            House03_Humi1_2 = new MutableLiveData<>(0);
//        return House03_Humi1_2;
//    }
    public MutableLiveData<Integer> getHouse03_Humi2_1()
    {
        if(House03_Humi2_1 == null)
            House03_Humi2_1 = new MutableLiveData<>(0);
        return House03_Humi2_1;
    }
//    public MutableLiveData<Integer> getHouse03_Humi2_2()
//    {
//        if(House03_Humi2_2 == null)
//            House03_Humi2_2 = new MutableLiveData<>(0);
//        return House03_Humi2_2;
//    }
    public MutableLiveData<Integer> getHouse03_Humi3_1()
    {
        if(House03_Humi3_1 == null)
            House03_Humi3_1 = new MutableLiveData<>(0);
        return House03_Humi3_1;
    }
//    public MutableLiveData<Integer> getHouse03_Humi3_2()
//    {
//        if(House03_Humi3_2 == null)
//            House03_Humi3_2 = new MutableLiveData<>(0);
//        return House03_Humi3_2;
//    }
    public MutableLiveData<Integer> getHouse03_Humi4_1()
    {
        if(House03_Humi4_1 == null)
            House03_Humi4_1 = new MutableLiveData<>(0);
        return House03_Humi4_1;
    }
//    public MutableLiveData<Integer> getHouse03_Humi4_2()
//    {
//        if(House03_Humi4_2 == null)
//            House03_Humi4_2 = new MutableLiveData<>(0);
//        return House03_Humi4_2;
//    }
    public MutableLiveData<Integer> getHouse03_Humi5_1()
    {
        if(House03_Humi5_1 == null)
            House03_Humi5_1 = new MutableLiveData<>(0);
        return House03_Humi5_1;
    }
    public MutableLiveData<Integer> getHouse03_Humi6_1()
    {
        if(House03_Humi6_1 == null)
            House03_Humi6_1 = new MutableLiveData<>(0);
        return House03_Humi6_1;
    }
    public MutableLiveData<Integer> getHouse03_Humi7_1()
    {
        if(House03_Humi7_1 == null)
            House03_Humi7_1 = new MutableLiveData<>(0);
        return House03_Humi7_1;
    }

    public MutableLiveData<Integer> getHouse03_Humi8_1()
    {
        if(House03_Humi8_1 == null)
            House03_Humi8_1 = new MutableLiveData<>(0);
        return House03_Humi8_1;
    }
    


    public MutableLiveData<Integer> getHouse04_Temp1_1()
    {
        if(House04_Temp1_1 == null)
            House04_Temp1_1 = new MutableLiveData<>(0);
        return House04_Temp1_1;
    }
//    public MutableLiveData<Integer> getHouse04_Temp1_2()
//    {
//        if(House04_Temp1_2 == null)
//            House04_Temp1_2 = new MutableLiveData<>(0);
//        return House04_Temp1_2;
//    }
    public MutableLiveData<Integer> getHouse04_Temp2_1()
    {
        if(House04_Temp2_1 == null)
            House04_Temp2_1 = new MutableLiveData<>(0);
        return House04_Temp2_1;
    }
//    public MutableLiveData<Integer> getHouse04_Temp2_2()
//    {
//        if(House04_Temp2_2 == null)
//            House04_Temp2_2 = new MutableLiveData<>(0);
//        return House04_Temp2_2;
//    }
    public MutableLiveData<Integer> getHouse04_Temp3_1()
    {
        if(House04_Temp3_1 == null)
            House04_Temp3_1 = new MutableLiveData<>(0);
        return House04_Temp3_1;
    }
//    public MutableLiveData<Integer> getHouse04_Temp3_2()
//    {
//        if(House04_Temp3_2 == null)
//            House04_Temp3_2 = new MutableLiveData<>(0);
//        return House04_Temp3_2;
//    }
    public MutableLiveData<Integer> getHouse04_Temp4_1()
    {
        if(House04_Temp4_1 == null)
            House04_Temp4_1 = new MutableLiveData<>(0);
        return House04_Temp4_1;
    }
//    public MutableLiveData<Integer> getHouse04_Temp4_2()
//    {
//        if(House04_Temp4_2 == null)
//            House04_Temp4_2 = new MutableLiveData<>(0);
//        return House04_Temp4_2;
//    }
    public MutableLiveData<Integer> getHouse04_Temp5_1()
    {
        if(House04_Temp5_1 == null)
            House04_Temp5_1 = new MutableLiveData<>(0);
        return House04_Temp5_1;
    }

    public MutableLiveData<Integer> getHouse04_Temp6_1()
    {
        if(House04_Temp6_1 == null)
            House04_Temp6_1 = new MutableLiveData<>(0);
        return House04_Temp6_1;
    }

    public MutableLiveData<Integer> getHouse04_Temp7_1()
    {
        if(House04_Temp7_1 == null)
            House04_Temp7_1 = new MutableLiveData<>(0);
        return House04_Temp7_1;
    }

    public MutableLiveData<Integer> getHouse04_Temp8_1()
    {
        if(House04_Temp8_1 == null)
            House04_Temp8_1 = new MutableLiveData<>(0);
        return House04_Temp8_1;
    }

    public MutableLiveData<Integer> getHouse04_Humi1_1()
    {
        if(House04_Humi1_1 == null)
            House04_Humi1_1 = new MutableLiveData<>(0);
        return House04_Humi1_1;
    }
//    public MutableLiveData<Integer> getHouse04_Humi1_2()
//    {
//        if(House04_Humi1_2 == null)
//            House04_Humi1_2 = new MutableLiveData<>(0);
//        return House04_Humi1_2;
//    }
    public MutableLiveData<Integer> getHouse04_Humi2_1()
    {
        if(House04_Humi2_1 == null)
            House04_Humi2_1 = new MutableLiveData<>(0);
        return House04_Humi2_1;
    }
//    public MutableLiveData<Integer> getHouse04_Humi2_2()
//    {
//        if(House04_Humi2_2 == null)
//            House04_Humi2_2 = new MutableLiveData<>(0);
//        return House04_Humi2_2;
//    }
    public MutableLiveData<Integer> getHouse04_Humi3_1()
    {
        if(House04_Humi3_1 == null)
            House04_Humi3_1 = new MutableLiveData<>(0);
        return House04_Humi3_1;
    }
//    public MutableLiveData<Integer> getHouse04_Humi3_2()
//    {
//        if(House04_Humi3_2 == null)
//            House04_Humi3_2 = new MutableLiveData<>(0);
//        return House04_Humi3_2;
//    }
    public MutableLiveData<Integer> getHouse04_Humi4_1()
    {
        if(House04_Humi4_1 == null)
            House04_Humi4_1 = new MutableLiveData<>(0);
        return House04_Humi4_1;
    }
//    public MutableLiveData<Integer> getHouse04_Humi4_2()
//    {
//        if(House04_Humi4_2 == null)
//            House04_Humi4_2 = new MutableLiveData<>(0);
//        return House04_Humi4_2;
//    }
    public MutableLiveData<Integer> getHouse04_Humi5_1()
    {
        if(House04_Humi5_1 == null)
            House04_Humi5_1 = new MutableLiveData<>(0);
        return House04_Humi5_1;
    }
    public MutableLiveData<Integer> getHouse04_Humi6_1()
    {
        if(House04_Humi6_1 == null)
            House04_Humi6_1 = new MutableLiveData<>(0);
        return House04_Humi6_1;
    }

    public MutableLiveData<Integer> getHouse04_Humi7_1()
    {
        if(House04_Humi7_1 == null)
            House04_Humi7_1 = new MutableLiveData<>(0);
        return House04_Humi7_1;
    }

    public MutableLiveData<Integer> getHouse04_Humi8_1()
    {
        if(House04_Humi8_1 == null)
            House04_Humi8_1 = new MutableLiveData<>(0);
        return House04_Humi8_1;
    }


    public MutableLiveData<Boolean> getWater_Shortage()
    {
        if(Water_Shortage == null)
            Water_Shortage = new MutableLiveData<>(false);
        return Water_Shortage;
    }


    public MutableLiveData<Integer> getAirCompressor01and02_RunningTime()
    {
        if(AirCompressor01and02_RunningTime == null)
            AirCompressor01and02_RunningTime = new MutableLiveData<>(KeyControl.getAirCompressor01and02_RunningTime());
        return AirCompressor01and02_RunningTime;
    }
    public MutableLiveData<Integer> getPump01and02_RunningTime()
    {
        if(Pump01and02_RunningTime == null)
            Pump01and02_RunningTime = new MutableLiveData<>(KeyControl.getPump01and02_RunningTime());
        return Pump01and02_RunningTime;
    }
    public MutableLiveData<Integer> getHouse01and02_Cleaning01_Time()
    {
        if(House01and02_Cleaning01_Time == null)
            House01and02_Cleaning01_Time = new MutableLiveData<>(KeyControl.getHouse01and02_Cleaning01());
        return House01and02_Cleaning01_Time;
    }
    public MutableLiveData<Integer> getHouse01and02_Cleaning02_Time()
    {
        if(House01and02_Cleaning02_Time == null)
            House01and02_Cleaning02_Time = new MutableLiveData<>(KeyControl.getHouse01and02_Cleaning02());
        return House01and02_Cleaning02_Time;
    }
    public MutableLiveData<Boolean> getHouse01_Nozzle_Auto()
    {
        if(House01_Nozzle_Auto == null)
            House01_Nozzle_Auto = new MutableLiveData<>(KeyControl.getHouse01_Nozzle_Auto());
        return House01_Nozzle_Auto;
    }
    public MutableLiveData<Boolean> getHouse02_Nozzle_Auto()
    {
        if(House02_Nozzle_Auto == null)
            House02_Nozzle_Auto = new MutableLiveData<>(KeyControl.getHouse02_Nozzle_Auto());
        return House02_Nozzle_Auto;
    }


    public MutableLiveData<Integer> getAirCompressor03and04_RunningTime()
    {
        if(AirCompressor03and04_RunningTime == null)
            AirCompressor03and04_RunningTime = new MutableLiveData<>(KeyControl.getAirCompressor03and04_RunningTime());
        return AirCompressor03and04_RunningTime;
    }

    public MutableLiveData<Integer> getPump03and04_RunningTime()
    {
        if(Pump03and04_RunningTime == null)
            Pump03and04_RunningTime = new MutableLiveData<>(KeyControl.getPump03and04_RunningTime());
        return Pump03and04_RunningTime;
    }

    public MutableLiveData<Integer> getHouse03and04_Cleaning01_Time()
    {
        if(House03and04_Cleaning01_Time == null)
            House03and04_Cleaning01_Time = new MutableLiveData<>(KeyControl.getHouse03and04_Cleaning01());
        return House03and04_Cleaning01_Time;
    }

    public MutableLiveData<Integer> getHouse03and04_Cleaning02_Time()
    {
        if(House03and04_Cleaning02_Time == null)
            House03and04_Cleaning02_Time = new MutableLiveData<>(KeyControl.getHouse03and04_Cleaning02());
        return House03and04_Cleaning02_Time;
    }

    public MutableLiveData<Boolean> getHouse03_Nozzle_Auto()
    {
        if(House03_Nozzle_Auto == null)
            House03_Nozzle_Auto = new MutableLiveData<>(KeyControl.getHouse03_Nozzle_Auto());
        return House03_Nozzle_Auto;
    }
    public MutableLiveData<Boolean> getHouse04_Nozzle_Auto()
    {
        if(House04_Nozzle_Auto == null)
            House04_Nozzle_Auto = new MutableLiveData<>(KeyControl.getHouse04_Nozzle_Auto());
        return House04_Nozzle_Auto;
    }

    public MutableLiveData<Boolean> getEnd_Time_Sw()
    {
        if(End_Time_Sw == null)
            End_Time_Sw = new MutableLiveData<>(KeyControl.getEnd_Time_Sw());
        return End_Time_Sw;
    }

    public MutableLiveData<Integer> getStart_Time_Picker_Hour()
    {
        if(Start_Time_Picker_Hour == null)
            Start_Time_Picker_Hour = new MutableLiveData<>(KeyControl.getStart_Time_Picker_Hour());
        return Start_Time_Picker_Hour;
    }

    public MutableLiveData<Integer> getStart_Time_Picker_Min()
    {
        if(Start_Time_Picker_Min == null)
            Start_Time_Picker_Min = new MutableLiveData<>(KeyControl.getStart_Time_Picker_Min());
        return Start_Time_Picker_Min;
    }
    
    public MutableLiveData<Integer> getEnd_Time_Picker_Hour()
    {
        if(End_Time_Picker_Hour == null)
            End_Time_Picker_Hour = new MutableLiveData<>(KeyControl.getEnd_Time_Picker_Hour());
        return End_Time_Picker_Hour;
    }

    public MutableLiveData<Integer> getEnd_Time_Picker_Min()
    {
        if(End_Time_Picker_Min == null)
            End_Time_Picker_Min = new MutableLiveData<>(KeyControl.getEnd_Time_Picker_Min());
        return End_Time_Picker_Min;
    }




    public void setHouse01_Nozzle_Auto(Boolean house01_nozzle_auto) {
        KeyControl.setHouse01_Nozzle_Auto(house01_nozzle_auto);
        if(this.House01_Nozzle_Auto == null)
            this.House01_Nozzle_Auto = new MutableLiveData<>(house01_nozzle_auto);
        this.House01_Nozzle_Auto.setValue(house01_nozzle_auto);
    }

    public void setHouse02_Nozzle_Auto(Boolean house02_nozzle_auto) {
        KeyControl.setHouse02_Nozzle_Auto(house02_nozzle_auto);
        if(this.House02_Nozzle_Auto == null)
            this.House02_Nozzle_Auto = new MutableLiveData<>(house02_nozzle_auto);
        this.House02_Nozzle_Auto.setValue(house02_nozzle_auto);
    }

    public void setHouse03_Nozzle_Auto(Boolean house03_nozzle_auto) {
        KeyControl.setHouse03_Nozzle_Auto(house03_nozzle_auto);
        if(this.House03_Nozzle_Auto == null)
            this.House03_Nozzle_Auto = new MutableLiveData<>(house03_nozzle_auto);
        this.House03_Nozzle_Auto.setValue(house03_nozzle_auto);
    }

    public void setHouse04_Nozzle_Auto(Boolean house04_nozzle_auto) {
        KeyControl.setHouse04_Nozzle_Auto(house04_nozzle_auto);
        if(this.House04_Nozzle_Auto == null)
            this.House04_Nozzle_Auto = new MutableLiveData<>(house04_nozzle_auto);
        this.House04_Nozzle_Auto.setValue(house04_nozzle_auto);
    }


    public void setEnd_Time_Sw(Boolean endTimeSw) {
        KeyControl.setEnd_Time_Sw(endTimeSw);
        if(this.End_Time_Sw == null)
            this.End_Time_Sw = new MutableLiveData<>(endTimeSw);
        this.End_Time_Sw.setValue(endTimeSw);
    }

    public void setStart_Time_Picker_Hour(int StartTimePickerHour) {
        KeyControl.setStart_Time_Picker_Hour(StartTimePickerHour);
        if(this.Start_Time_Picker_Hour == null)
            this.Start_Time_Picker_Hour = new MutableLiveData<>(StartTimePickerHour);
        this.Start_Time_Picker_Hour.setValue(StartTimePickerHour);
    }

    public void setStart_Time_Picker_Min(int StartTimePickerMin) {
        KeyControl.setStart_Time_Picker_Min(StartTimePickerMin);
        if(this.Start_Time_Picker_Min == null)
            this.Start_Time_Picker_Min = new MutableLiveData<>(StartTimePickerMin);
        this.Start_Time_Picker_Min.setValue(StartTimePickerMin);
    }

    public void setEnd_Time_Picker_Hour(int endTimePickerHour) {
        KeyControl.setEnd_Time_Picker_Hour(endTimePickerHour);
        if(this.End_Time_Picker_Hour == null)
            this.End_Time_Picker_Hour = new MutableLiveData<>(endTimePickerHour);
        this.End_Time_Picker_Hour.setValue(endTimePickerHour);
    }

    public void setEnd_Time_Picker_Min(int endTimePickerMin) {
        KeyControl.setEnd_Time_Picker_Min(endTimePickerMin);
        if(this.End_Time_Picker_Min == null)
            this.End_Time_Picker_Min = new MutableLiveData<>(endTimePickerMin);
        this.End_Time_Picker_Min.setValue(endTimePickerMin);
    }

}
