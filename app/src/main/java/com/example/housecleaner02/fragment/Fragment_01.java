package com.example.housecleaner02.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.housecleaner02.R;
import com.example.housecleaner02.serialport.SerialPortManager;
import com.example.housecleaner02.serialport.model.SerialModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Fragment_01#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_01 extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private Button start_btn;

    public Fragment_01() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment Fragment_01.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_01 newInstance(String param1) {
        Fragment_01 fragment = new Fragment_01();
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

        View view = inflater.inflate(R.layout.fragment_01, container, false);

        start_btn = view.findViewById(R.id.Cleaner_start_btn);

        start_btn.setOnClickListener(v -> {


            Intent intent = new Intent("Page");
            intent.putExtra("index",1);
            LocalBroadcastManager.getInstance(requireActivity()).sendBroadcast(intent);

//            SerialPortManager.getInstance().SendSerialCom(SerialModel.Com_House_01_02_Pump_Stop,null);
        });



        return view;
    }
}