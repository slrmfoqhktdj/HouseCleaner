package com.example.housecleaner02.sharedpreferences;

import com.example.housecleaner02.sharedpreferences.model.KeyModel;

public class KeyControl {

    public static void setHouse01_Nozzle_Auto(boolean house01_nozzle_auto){PrefsHelper.write(String.valueOf(KeyModel.House01_Nozzle_Auto),house01_nozzle_auto);}
    public static Boolean getHouse01_Nozzle_Auto(){return PrefsHelper.read(String.valueOf(KeyModel.House01_Nozzle_Auto),true);}

    public static void setHouse02_Nozzle_Auto(boolean house02_nozzle_auto){PrefsHelper.write(String.valueOf(KeyModel.House02_Nozzle_Auto),house02_nozzle_auto);}
    public static Boolean getHouse02_Nozzle_Auto(){return PrefsHelper.read(String.valueOf(KeyModel.House02_Nozzle_Auto),true);}

    public static void setHouse03_Nozzle_Auto(boolean house03_nozzle_auto){PrefsHelper.write(String.valueOf(KeyModel.House03_Nozzle_Auto),house03_nozzle_auto);}
    public static Boolean getHouse03_Nozzle_Auto(){return PrefsHelper.read(String.valueOf(KeyModel.House03_Nozzle_Auto),true);}

    public static void setHouse04_Nozzle_Auto(boolean house04_nozzle_auto){PrefsHelper.write(String.valueOf(KeyModel.House04_Nozzle_Auto),house04_nozzle_auto);}
    public static Boolean getHouse04_Nozzle_Auto(){return PrefsHelper.read(String.valueOf(KeyModel.House04_Nozzle_Auto),true);}

    public static void setHouse01_Nozzle01(int house01_nozzle01){PrefsHelper.write(String.valueOf(KeyModel.House01_Nozzle01),house01_nozzle01);}
    public static Integer getHouse01_Nozzle01(){return PrefsHelper.read(String.valueOf(KeyModel.House01_Nozzle01),2);}

    public static void setHouse01_Nozzle02(int house01_nozzle02){PrefsHelper.write(String.valueOf(KeyModel.House01_Nozzle02),house01_nozzle02);}
    public static Integer getHouse01_Nozzle02(){return PrefsHelper.read(String.valueOf(KeyModel.House01_Nozzle02),2);}

    public static void setHouse01_Nozzle03(int house01_nozzle03){PrefsHelper.write(String.valueOf(KeyModel.House01_Nozzle03),house01_nozzle03);}
    public static Integer getHouse01_Nozzle03(){return PrefsHelper.read(String.valueOf(KeyModel.House01_Nozzle03),2);}

    public static void setHouse01_Nozzle04(int house01_nozzle04){PrefsHelper.write(String.valueOf(KeyModel.House01_Nozzle04),house01_nozzle04);}
    public static Integer getHouse01_Nozzle04(){return PrefsHelper.read(String.valueOf(KeyModel.House01_Nozzle04),2);}


    public static void setHouse02_Nozzle01(int house02_nozzle01){PrefsHelper.write(String.valueOf(KeyModel.House02_Nozzle01),house02_nozzle01);}
    public static Integer getHouse02_Nozzle01(){return PrefsHelper.read(String.valueOf(KeyModel.House02_Nozzle01),2);}

    public static void setHouse02_Nozzle02(int house02_nozzle02){PrefsHelper.write(String.valueOf(KeyModel.House02_Nozzle02),house02_nozzle02);}
    public static Integer getHouse02_Nozzle02(){return PrefsHelper.read(String.valueOf(KeyModel.House02_Nozzle02),2);}

    public static void setHouse02_Nozzle03(int house02_nozzle03){PrefsHelper.write(String.valueOf(KeyModel.House02_Nozzle03),house02_nozzle03);}
    public static Integer getHouse02_Nozzle03(){return PrefsHelper.read(String.valueOf(KeyModel.House02_Nozzle03),2);}

    public static void setHouse02_Nozzle04(int house02_nozzle04){PrefsHelper.write(String.valueOf(KeyModel.House02_Nozzle04),house02_nozzle04);}
    public static Integer getHouse02_Nozzle04(){return PrefsHelper.read(String.valueOf(KeyModel.House02_Nozzle04),2);}


    public static void setHouse03_Nozzle01(int house03_nozzle01){PrefsHelper.write(String.valueOf(KeyModel.House03_Nozzle01),house03_nozzle01);}
    public static Integer getHouse03_Nozzle01(){return PrefsHelper.read(String.valueOf(KeyModel.House03_Nozzle01),2);}

    public static void setHouse03_Nozzle02(int house03_nozzle02){PrefsHelper.write(String.valueOf(KeyModel.House03_Nozzle02),house03_nozzle02);}
    public static Integer getHouse03_Nozzle02(){return PrefsHelper.read(String.valueOf(KeyModel.House03_Nozzle02),2);}

    public static void setHouse03_Nozzle03(int house03_nozzle03){PrefsHelper.write(String.valueOf(KeyModel.House03_Nozzle03),house03_nozzle03);}
    public static Integer getHouse03_Nozzle03(){return PrefsHelper.read(String.valueOf(KeyModel.House03_Nozzle03),2);}

    public static void setHouse03_Nozzle04(int house03_nozzle04){PrefsHelper.write(String.valueOf(KeyModel.House03_Nozzle04),house03_nozzle04);}
    public static Integer getHouse03_Nozzle04(){return PrefsHelper.read(String.valueOf(KeyModel.House03_Nozzle04),2);}


    public static void setHouse04_Nozzle01(int house04_nozzle01){PrefsHelper.write(String.valueOf(KeyModel.House04_Nozzle01),house04_nozzle01);}
    public static Integer getHouse04_Nozzle01(){return PrefsHelper.read(String.valueOf(KeyModel.House04_Nozzle01),2);}

    public static void setHouse04_Nozzle02(int house04_nozzle02){PrefsHelper.write(String.valueOf(KeyModel.House04_Nozzle02),house04_nozzle02);}
    public static Integer getHouse04_Nozzle02(){return PrefsHelper.read(String.valueOf(KeyModel.House04_Nozzle02),2);}

    public static void setHouse04_Nozzle03(int house04_nozzle03){PrefsHelper.write(String.valueOf(KeyModel.House04_Nozzle03),house04_nozzle03);}
    public static Integer getHouse04_Nozzle03(){return PrefsHelper.read(String.valueOf(KeyModel.House04_Nozzle03),2);}

    public static void setHouse04_Nozzle04(int house04_nozzle04){PrefsHelper.write(String.valueOf(KeyModel.House04_Nozzle04),house04_nozzle04);}
    public static Integer getHouse04_Nozzle04(){return PrefsHelper.read(String.valueOf(KeyModel.House04_Nozzle04),2);}




    public static void setRepeat_Cycle(int i){PrefsHelper.write(String.valueOf(KeyModel.Repeat_Cycle),i);}
    public static Integer getRepeat_Cycle(){return PrefsHelper.read(String.valueOf(KeyModel.Repeat_Cycle),0);}

    public static void setRepeat_PauseTime(int i){PrefsHelper.write(String.valueOf(KeyModel.Repeat_PauseTime),i);}
    public static Integer getRepeat_PauseTime(){return PrefsHelper.read(String.valueOf(KeyModel.Repeat_PauseTime),0);}


    public static void setAirCompressor01and02_RunningTime(int i){PrefsHelper.write(String.valueOf(KeyModel.AirCompressor01and02_RunningTime),i);}
    public static Integer getAirCompressor01and02_RunningTime(){return PrefsHelper.read(String.valueOf(KeyModel.AirCompressor01and02_RunningTime),1);}

    public static void setAirCompressor03and04_RunningTime(int i){PrefsHelper.write(String.valueOf(KeyModel.AirCompressor03and04_RunningTime),i);}
    public static Integer getAirCompressor03and04_RunningTime(){return PrefsHelper.read(String.valueOf(KeyModel.AirCompressor03and04_RunningTime),1);}


    public static void setPump01and02_RunningTime(int i){PrefsHelper.write(String.valueOf(KeyModel.Pump01and02_RunningTime),i);}
    public static Integer getPump01and02_RunningTime(){return PrefsHelper.read(String.valueOf(KeyModel.Pump01and02_RunningTime),1);}

    public static void setPump03and04_RunningTime(int i){PrefsHelper.write(String.valueOf(KeyModel.Pump03and04_RunningTime),i);}
    public static Integer getPump03and04_RunningTime(){return PrefsHelper.read(String.valueOf(KeyModel.Pump03and04_RunningTime),1);}


    public static void setHouse01and02_Cleaning01(int i){PrefsHelper.write(String.valueOf(KeyModel.House01and02_Cleaning01),i);}
    public static Integer getHouse01and02_Cleaning01(){return PrefsHelper.read(String.valueOf(KeyModel.House01and02_Cleaning01),1);}

    public static void setHouse03and04_Cleaning01(int i){PrefsHelper.write(String.valueOf(KeyModel.House03and04_Cleaning01),i);}
    public static Integer getHouse03and04_Cleaning01(){return PrefsHelper.read(String.valueOf(KeyModel.House03and04_Cleaning01),1);}


    public static void setHouse01and02_Cleaning02(int i){PrefsHelper.write(String.valueOf(KeyModel.House01and02_Cleaning02),i);}
    public static Integer getHouse01and02_Cleaning02(){return PrefsHelper.read(String.valueOf(KeyModel.House01and02_Cleaning02),1);}

    public static void setHouse03and04_Cleaning02(int i){PrefsHelper.write(String.valueOf(KeyModel.House03and04_Cleaning02),i);}
    public static Integer getHouse03and04_Cleaning02(){return PrefsHelper.read(String.valueOf(KeyModel.House03and04_Cleaning02),1);}


}
