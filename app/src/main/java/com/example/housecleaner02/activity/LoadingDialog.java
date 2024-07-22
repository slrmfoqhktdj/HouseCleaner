package com.example.housecleaner02.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.housecleaner02.R;

public class LoadingDialog extends AlertDialog{

    ProgressBar progressBar;
    TextView textView;

    public LoadingDialog(Context context) {
        super(context, R.style.Dialog);// 스타일 지정
    }


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_loading_dialog);
        setCancelable(true);

        progressBar = findViewById(R.id.loading_progress);
        textView = findViewById(R.id.loading_msg_tv);

    }

    public void setText(String msg){
        textView.setText(msg);
    }


}
