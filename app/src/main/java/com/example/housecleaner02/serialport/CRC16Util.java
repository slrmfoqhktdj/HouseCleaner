package com.example.housecleaner02.serialport;

import androidx.annotation.NonNull;

import java.nio.ByteBuffer;

public class CRC16Util {

    private static final int POLYNOMIAL = 0x1021; // CRC-CCITT 표준 다항식
    private static final int CRC_Init = 0xFFFF;

    public static int calculateCRC16(byte[] data) {
        int crc = CRC_Init; // 초기 값

        for (byte b : data) {
            crc ^= (b & 0xFF) << 8;
            for (int i = 0; i < 8; i++) {
                if ((crc & 0x8000) != 0) {
                    crc = (crc << 1) ^ POLYNOMIAL;
                } else {
                    crc = crc << 1;
                }
            }
        }

        return crc & 0xFFFF;
    }

    public static byte[] sendData(byte[] data) {
        // CRC16 계산
        int crc = CRC16Util.calculateCRC16(data);

//        Log.d("CRCUtil", String.valueOf(crc));

        // 데이터와 CRC16 결합
        ByteBuffer buffer = ByteBuffer.allocate(data.length + 2);
        buffer.put(data);
        buffer.putShort((short) crc);

        return buffer.array();

        // 데이터 송신 (예: Bluetooth, Socket 등)
        // sendToDevice(dataWithCrc);
    }

//    public static boolean receiveData(byte[] receivedData) {
//        if (receivedData.length < 3) { // 최소 데이터 길이는 3 (데이터 1 + CRC16 2)
//            return false;
//        }
//
//        // 수신된 데이터에서 CRC16 분리
//        byte[] data = Arrays.copyOfRange(receivedData, 0, receivedData.length - 2);
//        byte[] crcBytes = Arrays.copyOfRange(receivedData, receivedData.length - 2, receivedData.length);
//        int receivedCrc = ByteBuffer.wrap(crcBytes).getShort() & 0xFFFF;
//
//        // CRC16 계산
//        int calculatedCrc = CRC16Util.calculateCRC16(data);
//
//        // CRC16 검증
//        return receivedCrc == calculatedCrc;
//    }


    public static boolean receiveData(@NonNull byte[] data , @NonNull  byte[] crc) {
        if (data.length < 1) { // 최소 데이터 길이는 3 (데이터 1 + CRC16 2)
            return false;
        }

        if (crc.length < 2) { // 최소 데이터 길이는 3 (데이터 1 + CRC16 2)
            return false;
        }

        int receivedCrc = ByteBuffer.wrap(crc).getShort() & CRC_Init;

        // CRC16 계산
        int calculatedCrc = CRC16Util.calculateCRC16(data);

        // CRC16 검증
        return receivedCrc == calculatedCrc;
    }
}
