package com.example.housecleaner02.filemanage;


import com.example.housecleaner02.sharedpreferences.PrefsHelper;

import java.io.File;
import java.io.FileWriter;

public class LogFile {

//    private static LogFile mInstance = null;

//    private Context mcontext;

//    public File file =  new File(PrefsHelper.log_path,"Log.txt");

//    private LogFile(Context context){
//        mcontext = context;
//    }

    public final File file = new File(PrefsHelper.log_path,"Log.txt");

//    public static LogFile getInstance(Context context){
//
//
//        if(mInstance == null){
//            mInstance = new LogFile(context);
//        }
//
//        return mInstance;
//    }


    public boolean writeFile(String data){

//        File file = new File(PrefsHelper.log_path,"Log.txt");

        if(data.equals("")){
            return false;
        }

        FileWriter fw = null ;

        try {
            // open file.
            fw = new FileWriter(file) ;
            
            // write file.
            fw.write(data) ;

        } catch (Exception e) {
            e.printStackTrace() ;
            return false;
        }

        // close file.
        if (fw != null) {
            // catch Exception here or throw.
            try {
                fw.close() ;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }else{
            return false;
        }
    }


//    public String loadLogFile(ZonedDateTime startime, ZonedDateTime stopTime){
//
//        boolean result = false;
//
//        ZonedDateTime defalutTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
//
//        LocalDateTime startLT;
//        LocalDateTime stopLT;
//
//        if(startime != null){
//            startLT = startime.toLocalDateTime();
//        }else{
//            if(stopTime != null){
//                startLT = stopTime.minusDays(3).toLocalDateTime();
//            }else{
//                startLT = defalutTime.minusDays(3).toLocalDateTime();
//            }
//        }
//
//        if(stopTime != null){
//            stopLT = stopTime.toLocalDateTime();
//        }else{
//            stopLT = defalutTime.toLocalDateTime();
//        }
//
//
//        LocalDate fileStartLT = startLT.toLocalDate();
//        LocalDate fileStopLT = stopLT.toLocalDate();
//
//        Log.d("Time", "receive Start = "+startLT+" receive Stop = "+ stopLT);
//        Log.d("Time", "Start = "+fileStartLT+" Stop = "+fileStopLT);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        DateTimeFormatter filenameformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//
//        StringBuilder log = new StringBuilder();
//
//        log.append("device id : ").append(DeviceModel.getDeviceid()).append("\n");
//        log.append("connector id : ").append(DeviceModel.getConnectId()).append("\n");
//
//        try {
////            Log.d("Logfile", String.valueOf(android.os.Process.myPid()));
//
////            "logcat --pid="+ android.os.Process.myPid()+" -d *:I "    --- 어플 로그만. 대신 프로세스가 바뀌어버려서 이전 프로세스로그는 안나옴
////            Process process = Runtime.getRuntime().exec("logcat *:I -v year -d ");
////            BufferedReader bufferedReader = new BufferedReader(
////                    new InputStreamReader(process.getInputStream()));
//
//
//            File f = new File(DeviceModel.getDeviceLogFilePath());
//            File[] files = f.listFiles(pathname -> {
////                return pathname.getName().endsWith(".txt"); //확장자
//                StringBuilder dateStringFinal = new StringBuilder();
//                dateStringFinal.append(pathname.getName().split("\\.")[0]);
//
//                LocalDate dateTime = LocalDate.parse(dateStringFinal, filenameformatter);
//
//                if ((dateTime.isAfter(fileStartLT) && dateTime.isBefore(fileStopLT))|| dateTime.equals(fileStartLT) || dateTime.equals(fileStopLT)) {
//
//
//                    return pathname.getName().endsWith(".txt"); //확장자
//                }
//
//                return false;
//            });
//
//            Arrays.sort(files);
//
//            Log.d("filelist", Arrays.toString(files));
//
//
//
//
//            for(int d = 0; d< Objects.requireNonNull(files).length ; d++){
//
//                BufferedReader logbufferedReader = new BufferedReader(new FileReader(files[d]));
//
//                String line;
//                while((line = logbufferedReader.readLine()) != null){
////                if(!line.contains("chatty  :")&&!line.contains("exec su failed:")&&!line.contains("exec failed:")&&!line.contains("exec testsu failed:")&&!line.contains("RockFFPlayer:")&&!line.contains("ZtlManager:")&&!line.contains("System.out:")){
//                    String[] dateString = line.split(" ");
//                    StringBuilder dateStringFinal = new StringBuilder();
//                    try {
//                        dateStringFinal.append(dateString[0]).append(" ").append(dateString[1]);
//                        LocalDateTime dateTime = LocalDateTime.parse(dateStringFinal, formatter);
////                        Log.d("Logfile",dateStringFinal.toString());
//
//                        if (dateTime.isAfter(startLT) && dateTime.isBefore(stopLT)) {
//                            Log.d("time","true time : "+dateTime);
//
//                            log.append(line).append("\n");
//                            result = true;  // 해당 하는 로그들이 있으면
//                        }else{
//                            if (dateTime.isAfter(stopLT)) {
//                                break;
//                            }
//                        }
//                    }catch (Exception ignore){
//                        ignore.printStackTrace();
//                        log.append(line).append("\n");
//                        result = true;  // 해당 하는 로그들이 있으면
//                    }
//
//
////                }
//
//                }
//
//            }
//
//
//
//
//
//
//
//
////            File logFile = new File(DeviceModel.getDeviceLogFilePath()+"/",  "2024-06-14.txt");
////            FileReader logfileReader = new FileReader(logFile);
////
////            BufferedReader logbufferedReader = new BufferedReader(logfileReader);
////
////            String line;
//////            while((line = bufferedReader.readLine()) != null){
////            while((line = logbufferedReader.readLine()) != null){
//////                if(!line.contains("chatty  :")&&!line.contains("exec su failed:")&&!line.contains("exec failed:")&&!line.contains("exec testsu failed:")&&!line.contains("RockFFPlayer:")&&!line.contains("ZtlManager:")&&!line.contains("System.out:")){
////                    String[] dateString = line.split(" ");
////                    StringBuilder dateStringFinal = new StringBuilder();
////                    try {
////                        dateStringFinal.append(dateString[0]).append(" ").append(dateString[1]);
////                        LocalDateTime dateTime = LocalDateTime.parse(dateStringFinal, formatter);
//////                        Log.d("Logfile",dateStringFinal.toString());
////
////                        if (dateTime.isAfter(startLT) && dateTime.isBefore(stopLT)) {
////
////
////                            log.append(line).append("\n");
////                            result = true;  // 해당 하는 로그들이 있으면
////                        }else{
////                            if (dateTime.isAfter(stopLT)) {
////                                break;
////                            }
////                        }
////                    }catch (Exception ignore){
////                        log.append(line).append("\n");
////                        result = true;  // 해당 하는 로그들이 있으면
////                    }
////
////
//////                }
////
////            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            Logger.e(String.valueOf(e));
//            return "";
//        }
//
//        if(result){
//            return log.toString();
//        }else{
//            return "";
//        }
//
//    }

    public boolean fileDelete(){

        return file.delete();
    }

}
