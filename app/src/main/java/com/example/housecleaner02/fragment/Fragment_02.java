package com.example.housecleaner02.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.housecleaner02.R;
import com.example.housecleaner02.model.MainViewModel;
import com.example.housecleaner02.sharedpreferences.KeyControl;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_02#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_02 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private MainViewModel viewModel;
    private TextView Repeat_number_tv,Pause_Time_tv;
    private ImageButton Repeat_number_up_btn,Repeat_number_down_btn,Pause_Time_up_btn,Pause_Time_down_btn;

    private Button positive_botton,negative_botton;

    private Switch end_time_sw;


    public Fragment_02() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Fragment_02.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_02 newInstance(String param1) {
        Fragment_02 fragment = new Fragment_02();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_02, container, false);

        end_time_sw = view.findViewById(R.id.end_time_sw);

        Repeat_number_tv = view.findViewById(R.id.Repeat_number);
        Pause_Time_tv = view.findViewById(R.id.Pause_Time);

        Repeat_number_up_btn = view.findViewById(R.id.Repeat_number_up_btn);
        Repeat_number_down_btn = view.findViewById(R.id.Repeat_number_down_btn);
        Pause_Time_up_btn = view.findViewById(R.id.Pause_Time_up_btn);
        Pause_Time_down_btn = view.findViewById(R.id.Pause_Time_down_btn);

        positive_botton = view.findViewById(R.id.positive_botton);
        negative_botton = view.findViewById(R.id.negative_botton);

        end_time_sw.setChecked(KeyControl.getEnd_Time_Sw());




        Repeat_number_up_btn.setOnClickListener(v -> {
            int num = Integer.parseInt((String) Repeat_number_tv.getText());

            if(num == 1){
                Pause_Time_up_btn.setEnabled(true);
                Pause_Time_down_btn.setEnabled(true);
                Pause_Time_up_btn.setImageResource(R.drawable.chevron_up);
                Pause_Time_down_btn.setImageResource(R.drawable.chevron_down);
            }

            Repeat_number_tv.setText(String.valueOf(num+1));
        });

        Repeat_number_down_btn.setOnClickListener(v -> {
            int num = Integer.parseInt((String) Repeat_number_tv.getText());

            if(num>1){
                Repeat_number_tv.setText(String.valueOf(num-1));
            }

            if(Integer.parseInt((String) Repeat_number_tv.getText()) <= 1){
                Pause_Time_up_btn.setEnabled(false);
                Pause_Time_down_btn.setEnabled(false);
                Pause_Time_up_btn.setImageResource(R.drawable.chevron_up_un);
                Pause_Time_down_btn.setImageResource(R.drawable.chevron_down_un);
                Pause_Time_tv.setText(String.valueOf(0));
            }
        });

        Pause_Time_up_btn.setOnClickListener(v -> {

            if(Integer.parseInt((String) Repeat_number_tv.getText())> 1){
                Pause_Time_tv.setText(String.valueOf(Integer.parseInt((String) Pause_Time_tv.getText())+1));
            }
        });

        Pause_Time_down_btn.setOnClickListener(v -> {
            if(Integer.parseInt((String) Pause_Time_tv.getText())>0){
                Pause_Time_tv.setText(String.valueOf(Integer.parseInt((String) Pause_Time_tv.getText())-1));
            }
        });

        positive_botton.setOnClickListener(v -> {

            int Repeat_Cycle = Integer.parseInt(String.valueOf(Repeat_number_tv.getText()));
            int Repeat_PauseTime = Integer.parseInt(String.valueOf(Pause_Time_tv.getText()));

            viewModel.getRepeat_Cycle().postValue(Repeat_Cycle);
            viewModel.getRepeat_PauseTime().postValue(Repeat_PauseTime);

            KeyControl.setRepeat_Cycle(Repeat_Cycle);
            KeyControl.setRepeat_PauseTime(Repeat_PauseTime);


            viewModel.setEnd_Time_Sw(end_time_sw.isChecked());

            Intent intent = new Intent("Page");
            intent.putExtra("index",2);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        });

        negative_botton.setOnClickListener(v -> {
            Intent intent = new Intent("Page");
            intent.putExtra("index",0);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        });

        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        final Observer<Integer>  repeat_cycle_number_ob = b -> {
            Repeat_number_tv.setText(String.valueOf(b));
            if(b<=1){
                Pause_Time_up_btn.setImageResource(R.drawable.chevron_up_un);
                Pause_Time_down_btn.setImageResource(R.drawable.chevron_down_un);
            }else{
                Pause_Time_up_btn.setImageResource(R.drawable.chevron_up);
                Pause_Time_down_btn.setImageResource(R.drawable.chevron_down);
            }

        };

        final Observer<Integer>  repeat_pause_time_ob = b -> {
            Pause_Time_tv.setText(String.valueOf(b));
        };


        viewModel.getRepeat_Cycle().observe(requireActivity(),repeat_cycle_number_ob);
        viewModel.getRepeat_PauseTime().observe(requireActivity(), repeat_pause_time_ob);


    }
}