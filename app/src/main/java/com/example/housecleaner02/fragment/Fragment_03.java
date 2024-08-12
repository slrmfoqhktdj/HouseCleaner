package com.example.housecleaner02.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.housecleaner02.R;
import com.example.housecleaner02.model.DeviceModel;
import com.example.housecleaner02.model.MainViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_03#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_03 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private MainViewModel viewModel;

    private ImageView Running_situation_1_1,Running_situation_1_2;
    private TextView Running_situation_2,Running_cycle_number,Running_pause_time;

    private Button pause_botton;

    public Fragment_03() {
        // Required empty public constructor
    }


    public static Fragment_03 newInstance(String param1) {
        Fragment_03 fragment = new Fragment_03();
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

        View view = inflater.inflate(R.layout.fragment_03, container, false);

        Running_situation_1_1 = view.findViewById(R.id.Running_situation_1_1);
        Running_situation_1_2 = view.findViewById(R.id.Running_situation_1_2);
        Running_situation_2 = view.findViewById(R.id.Running_situation_2);
        Running_cycle_number = view.findViewById(R.id.Running_cycle_number);
        Running_pause_time = view.findViewById(R.id.Running_pause_time);

        pause_botton = view.findViewById(R.id.pause_botton);

        pause_botton.setOnClickListener(v -> {
            viewModel.getIsRunning().postValue(false);

            Intent intent = new Intent("End");
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);
        });



        return view;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);



        final Observer<DeviceModel>  Running_situation_01_ob = b -> {
            switch (b){
                case House_01_02:
                    Running_situation_1_1.setImageResource(R.drawable.house_1);
                    Running_situation_1_2.setImageResource(R.drawable.house_2);
                    break;
                case House_03_04:
                    Running_situation_1_1.setImageResource(R.drawable.house_3);
                    Running_situation_1_2.setImageResource(R.drawable.house_4);
                    break;
                case House_Stop:
                    Running_situation_1_1.setImageResource(0);
                    Running_situation_1_2.setImageResource(0);
            }
        };

        final Observer<Integer>  Running_situation_02_ob = b -> {

            switch (b){
                case 0:
                    Running_situation_2.setText("방제중");
                    break;
                case 1:
                    Running_situation_2.setText("물청소");
                    break;
                case 2:
                    Running_situation_2.setText("에어청소");
                    break;
                case 3:
                    Running_situation_2.setText("휴지중");
                    break;

            }

        };

        final Observer<Integer>  Running_cycle_number_ob = b -> {
            Running_cycle_number.setText(b+"번");
        };

        final Observer<Integer>  Running_pause_time_ob = b -> {
            Running_pause_time.setText(b+"분");
        };


        viewModel.getCleanning_Running_Situation_1().observe(requireActivity(),Running_situation_01_ob);
        viewModel.getCleanning_Running_Situation_2().observe(requireActivity(),Running_situation_02_ob);
        viewModel.getRepeat_Cycle().observe(requireActivity(),Running_cycle_number_ob);
        viewModel.getRepeat_PauseTime().observe(requireActivity(),Running_pause_time_ob);

    }
}