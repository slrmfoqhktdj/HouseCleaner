package com.example.housecleaner02.filemanage;

import android.os.Environment;
import android.util.Log;

import com.example.housecleaner02.model.DeviceModel;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DiskLogStrategy implements LogStrategy {
    private File[] logpathfile;

    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat FileNamedateFormat;
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 1024; // 1GB

    private int File_cnt = 0;
    private String origin_filename = "";
    private ArrayList<String> erred_message = new ArrayList<>();


    public DiskLogStrategy(File[] file) throws Exception {
//        this.context = context;
        this.logpathfile = file;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        this.FileNamedateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//        File folder = new File(DeviceModel.getDeviceLogFilePath());

//        Log.d("DiskLogStragy","path = "+logpathfile[0]);
        File folder = new File(logpathfile[1]+DeviceModel.getDeviceLogFilePath()); // 외부저장소
        if(!folder.exists()){
            folder.mkdir();
        }

    }

    @Override
    public void log(int priority, String tag, String message) {

        if (isExternalStorageWritable()) {
            long now = System.currentTimeMillis();
            Date date = new Date(now);
            String dateform = dateFormat.format(date);
            String FileName = FileNamedateFormat.format(date);

            if(!origin_filename.equals(FileName)){
                origin_filename = FileName;
                File_cnt = 0;
            }
//            Log.d("DiskLogStragy","path = "+logpathfile[0]);
//            File logFile = new File(DeviceModel.getDeviceLogFilePath()+"/", FileName +".txt");
            File logFile = new File(logpathfile[1]+DeviceModel.getDeviceLogFilePath(),FileName +"_"+File_cnt+".txt");


            try (FileWriter writer = new FileWriter(logFile, true)) {

                if(erred_message.size()>0){
                    for(int i = 0; i< erred_message.size(); i++){
                        writer.append(dateform).append(" ").append(formatLogPriority(priority)).append(" : ").append(erred_message.get(i)).append("\n");
                    }
                }


                writer.append(dateform).append(" ").append(formatLogPriority(priority)).append(" : ").append(message).append("\n");
                writer.flush();

                erred_message.clear();

            }   catch (IOException ie){
                ie.printStackTrace();
                File_cnt = File_cnt +1;
                erred_message.add(message);

            }   catch (Exception e) {
                e.printStackTrace();

            }


        }
    }

    private boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    private String formatLogPriority(int priority) {
        String priorityString;
        switch (priority) {
            case android.util.Log.VERBOSE:
                priorityString = "VERBOSE";
                break;
            case android.util.Log.DEBUG:
                priorityString = "DEBUG";
                break;
            case android.util.Log.INFO:
                priorityString = "INFO";
                break;
            case android.util.Log.WARN:
                priorityString = "WARN";
                break;
            case android.util.Log.ERROR:
                priorityString = "ERROR";
                break;
            case android.util.Log.ASSERT:
                priorityString = "ASSERT";
                break;
            default:
                priorityString = "UNKNOWN";
                break;
        }
        return priorityString;
    }
}