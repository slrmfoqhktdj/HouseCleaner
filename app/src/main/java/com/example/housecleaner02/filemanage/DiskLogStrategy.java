package com.example.housecleaner02.filemanage;

import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.housecleaner02.model.DeviceModel;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiskLogStrategy implements LogStrategy {
    private File[] logpathfile;

    private final SimpleDateFormat dateFormat;
    private final SimpleDateFormat FileNamedateFormat;
    private static final long MAX_FILE_SIZE = 1024 * 1024 * 1024; // 1GB

    public DiskLogStrategy(File[] file) throws Exception {
//        this.context = context;
        this.logpathfile = file;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
        this.FileNamedateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
//        File folder = new File(DeviceModel.getDeviceLogFilePath());

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


//            File logFile = new File(DeviceModel.getDeviceLogFilePath()+"/", FileName +".txt");
            File logFile = new File(logpathfile[1]+DeviceModel.getDeviceLogFilePath(),FileName +".txt");

            if(!logFile.exists()){
                logFile.mkdir();
            }

            try (FileWriter writer = new FileWriter(logFile, true)) {
                writer.append(dateform).append(" ").append(formatLogPriority(priority)).append(" : ").append(message).append("\n");
                writer.flush();
            } catch (IOException e) {
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