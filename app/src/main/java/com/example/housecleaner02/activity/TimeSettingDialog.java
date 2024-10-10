package com.example.housecleaner02.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.housecleaner02.R;
import com.example.housecleaner02.callback.HouseSettingDialogCallback;
import com.example.housecleaner02.callback.TimeSettingDialogCallback;
import com.example.housecleaner02.model.DeviceModel;
import com.example.housecleaner02.sharedpreferences.KeyControl;

import java.util.ArrayList;
import java.util.Calendar;


public class TimeSettingDialog extends AlertDialog{


    private TimePicker start_time_picker;
    private TimePicker end_time_picker;



    private Button positive_botton;
    private Button negative_botton;



    private final ArrayList<TimeSettingDialogCallback> callbacksList = new ArrayList<>();

    public TimeSettingDialog(Context context) {
        super(context, R.style.Dialog);// 스타일 지정

    }

    public TimeSettingDialog setTimeSettingDialogListener(TimeSettingDialogCallback callback){
        if (!callbacksList.contains(callback)) {
            callbacksList.add(callback);
        }
        return this;
    }



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("Dialog","onCreate Dialog");
        setContentView(R.layout.activity_time_setting_dialog);
        setCancelable(true);

        start_time_picker = findViewById(R.id.start_time_picker);
        end_time_picker = findViewById(R.id.end_time_picker);


        if(KeyControl.getStart_Time_Picker_Hour() ==-1){

            Calendar calendar = Calendar.getInstance();

            start_time_picker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            start_time_picker.setMinute(calendar.get(Calendar.MINUTE));
            KeyControl.setStart_Time_Picker_Hour(calendar.get(Calendar.HOUR_OF_DAY));
            KeyControl.setStart_Time_Picker_Min(calendar.get(Calendar.MINUTE));

            calendar.add(Calendar.MINUTE,1);

            end_time_picker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            end_time_picker.setMinute(calendar.get(Calendar.MINUTE));

            KeyControl.setEnd_Time_Picker_Hour(calendar.get(Calendar.HOUR_OF_DAY));
            KeyControl.setEnd_Time_Picker_Min(calendar.get(Calendar.MINUTE));
        }else{
            start_time_picker.setHour(KeyControl.getStart_Time_Picker_Hour());
            start_time_picker.setMinute(KeyControl.getStart_Time_Picker_Min());

            end_time_picker.setHour(KeyControl.getEnd_Time_Picker_Hour());
            end_time_picker.setMinute(KeyControl.getEnd_Time_Picker_Min());
        }




        positive_botton = findViewById(R.id.positive_botton);
        negative_botton = findViewById(R.id.negative_botton);



        positive_botton.setOnClickListener(v -> {

            if(start_time_picker.getHour() == end_time_picker.getHour() && start_time_picker.getMinute() == end_time_picker.getMinute()){
                Toast.makeText(getContext(),"시작과 종료시간이 같습니다. 다시 설정해주세요.",Toast.LENGTH_LONG).show();
                return;
            }


            for (int i = 0; i < callbacksList.size(); i++) {
                TimeSettingDialogCallback callbackListener = callbacksList.get(i);
                if (null != callbackListener) {
                    callbackListener.onPositiveClick(start_time_picker.getHour(),start_time_picker.getMinute(),end_time_picker.getHour(),end_time_picker.getMinute());
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
