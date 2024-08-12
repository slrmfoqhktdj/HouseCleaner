package com.example.housecleaner02.serialport.model;

public enum SerialModel {

    Require_Info,   // 정보요청
    Require_House_01_TH,    //하우스 01 온습도
    Require_House_02_TH,    //하우스 02 온습도
    Require_House_03_TH,    //하우스 03 온습도
    Require_House_04_TH,    //하우스 04 온습도
    // 1,2 온도
    // 3,4 습도

    Com_House_01_02_Air_Pump_Start,  // house 01,02 콤 펌프 시작
    Com_House_01_02_Air_Stop,
    Com_House_01_02_Pump_Stop,

    Com_House_03_04_Air_Pump_Start,  // house 03,04 콤 펌프 시작
    Com_House_03_04_Air_Stop,
    Com_House_03_04_Pump_Stop,

    Com_House_01_02_Cleanner01_Start,   // house 01,02 에어청소 시작
    Com_House_01_02_Cleanner01_Air_Start,
    Com_House_01_02_Cleanner01_Air_Stop,
    Com_House_01_02_Cleanner01_Pump_Stop,
    Com_House_03_04_Cleanner01_Start,   // house 03,04 에어청소 시작
    Com_House_03_04_Cleanner01_Air_Start,
    Com_House_03_04_Cleanner01_Air_Stop,
    Com_House_03_04_Cleanner01_Pump_Stop,
    Com_House_01_02_Cleanner02_Start,   // house 01,02 물청소 시작
    Com_House_01_02_Cleanner02_Stop,   // house 01,02 물청소 멈춤
    Com_House_03_04_Cleanner02_Start,   // house 03,04 물청소 시작
    Com_House_03_04_Cleanner02_Stop,    // house 03,04 물청소 멈춤
    House_01_Nozzle_1,  // house 01 노즐 1단계
    House_01_Nozzle_2, // house 01 노즐 2단계
    House_01_Nozzle_3, // house 01 노즐 3단계
    House_01_Nozzle_4, // house 01 노즐 4단계

    House_02_Nozzle_1,  // house 02 노즐 1단계
    House_02_Nozzle_2, // house 02 노즐 2단계
    House_02_Nozzle_3, // house 02 노즐 3단계
    House_02_Nozzle_4, // house 02 노즐 4단계

    House_03_Nozzle_1,  // house 03 노즐 1단계
    House_03_Nozzle_2, // house 03 노즐 2단계
    House_03_Nozzle_3, // house 03 노즐 3단계
    House_03_Nozzle_4, // house 03 노즐 4단계

    House_04_Nozzle_1,  // house 04 노즐 1단계
    House_04_Nozzle_2, // house 04 노즐 2단계
    House_04_Nozzle_3, // house 04 노즐 3단계
    House_04_Nozzle_4, // house 04 노즐 4단계
    House_All_Stop,     // 전체 멈춤

    House_01_Nozzle_5, // house 01 노즐 5단계
    House_01_Nozzle_6, // house 01 노즐 6단계

    House_02_Nozzle_5, // house 02 노즐 3단계
    House_02_Nozzle_6, // house 02 노즐 4단계

    House_03_Nozzle_5, // house 03 노즐 3단계
    House_03_Nozzle_6, // house 03 노즐 4단계

    House_04_Nozzle_5, // house 04 노즐 3단계
    House_04_Nozzle_6; // house 04 노즐 4단계


    public final static byte InfoReceive_1 = 0x50;  //정보응답
    public final static byte InfoReceive_2 = 0x51;  //정보응답
    public final static byte THReceive = 0x52;  //정보응답

    public final static byte House01and02_Com_Aire_Pump_Send = 0x51;  // House01 02 콤 펌프 전송
    public final static byte House03and04_Com_Aire_Pump_Send = 0x52;  // House03 04 콤 펌프 전송

    public final static byte House01and02_Com_Aire_Pump_Receive = 0x53;  // House01 02 콤 펌프 응답
    public final static byte House03and04_Com_Aire_Pump_Receive = 0x54;  // House03 04 콤 펌프 응답

    public final static byte House01and02_Cleanner01_Send = 0x53;  // House01 02 에어청소 전송
    public final static byte House03and04_Cleanner01_Send = 0x54;  // House03 04 에어청소 전송

    public final static byte House01and02_Cleanner01_Receive = 0x55;  // House01 02 에어청소 응답
    public final static byte House03and04_Cleanner01_Receive = 0x56;  // House03 04 에어청소 응답

    public final static byte House01and02_Cleanner02_Send = 0x55;  // House01 02 물청소 전송
    public final static byte House03and04_Cleanner02_Send = 0x56;  // House03 04 물청소 전송

    public final static byte House01and02_Cleanner02_Receive = 0x57;  // House01 02 물청소 응답
    public final static byte House03and04_Cleanner02_Receive = 0x58;  // House03 04 물청소 응답
    
    public final static byte Stortage_Water = 0x69; // 물부족

    public final static byte House_All_Stop_Receive = (byte) 0x81;
}
