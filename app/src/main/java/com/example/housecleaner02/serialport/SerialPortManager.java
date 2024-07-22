package com.example.housecleaner02.serialport;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.housecleaner02.listener.OnOpenSerialPortListener;
import com.example.housecleaner02.listener.OnSerialPortDataListener;
import com.example.housecleaner02.serialport.model.SerialModel;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android_serialport_api.SerialPort;

public class SerialPortManager extends SerialPort {

    private static final String TAG = SerialPortManager.class.getSimpleName();
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;
    private FileDescriptor mFd;

    private HandlerThread mSendingHandlerThread;
    private Handler mSendingHandler;
    private SerialPortReadThread mSerialPortReadThread;

    private final ArrayList<OnOpenSerialPortListener> mOpenSerialPortListenList = new ArrayList<>();
    private final ArrayList<OnSerialPortDataListener> mSerialPortDataListenList = new ArrayList<>();

    public static SerialPortManager instance = null;

    public static SerialPortManager getInstance() {
        if (instance == null) {
            synchronized (SerialPortManager.class) {
                instance = new SerialPortManager();
            }
        }
        return instance;
    }

    private SerialPortManager() {

    }

    /**
     * 打开串口
     *
     * @param device   串口设备
     * @param baudRate 波特率
     * @return 打开是否成功
     */
    public boolean openSerialPort(File device, int baudRate) {
        if (isSerialOpen()) {
            return true;
        }
        Log.i(TAG, "openSerialPort: " + String.format("직렬 포트 열기 %s  전송 속도 %s", device.getPath(), baudRate));

        // 校验串口权限
        if (!device.canRead() || !device.canWrite()) {
            boolean chmod777 = chmod777(device);
            if (!chmod777) {
                Log.i(TAG, "openSerialPort: 읽기 및 쓰기 권한이 없습니다");
                for (int i = 0; i < mOpenSerialPortListenList.size(); i++) {
                    OnOpenSerialPortListener onOpenSerialPortListener = mOpenSerialPortListenList.get(i);
                    if (null != onOpenSerialPortListener) {
                        onOpenSerialPortListener.onFail(device, OnOpenSerialPortListener.Status.NO_READ_WRITE_PERMISSION);
                    }
                }
                return false;
            }
        }

        try {
            mFd = open(device.getAbsolutePath(), baudRate, 0);
            mFileInputStream = new FileInputStream(mFd);
            mFileOutputStream = new FileOutputStream(mFd);
            Log.i(TAG, "openSerialPort: 직렬 포트가 열려 있습니다. " + mFd);
            for (int i = 0; i < mOpenSerialPortListenList.size(); i++) {
                OnOpenSerialPortListener onOpenSerialPortListener = mOpenSerialPortListenList.get(i);
                if (null != onOpenSerialPortListener) {
                    onOpenSerialPortListener.onSuccess(device);
                }
            }
            // 开启发送消息的线程
            startSendThread();
            // 开启接收消息的线程
            startReadThread();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            for (int i = 0; i < mOpenSerialPortListenList.size(); i++) {
                OnOpenSerialPortListener onOpenSerialPortListener = mOpenSerialPortListenList.get(i);
                if (null != onOpenSerialPortListener) {
                    onOpenSerialPortListener.onFail(device, OnOpenSerialPortListener.Status.OPEN_FAIL);
                }
            }
        }
        return false;
    }

    public boolean isSerialOpen() {
        return mFd != null;
    }


    /**
     * 关闭串口
     */
    public void closeSerialPort() {

        if (null != mFd) {
            close();
            mFd = null;
        }
        // 停止发送消息的线程
        stopSendThread();
        // 停止接收消息的线程
        stopReadThread();

        if (null != mFileInputStream) {
            try {
                mFileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileInputStream = null;
        }

        if (null != mFileOutputStream) {
            try {
                mFileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mFileOutputStream = null;
        }

        mOpenSerialPortListenList.clear();
        mSerialPortDataListenList.clear();
    }

    /**
     * 添加打开串口监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public SerialPortManager setOnOpenSerialPortListener(OnOpenSerialPortListener listener) {
        if (listener != null) {
            if (!mOpenSerialPortListenList.contains(listener)) {
                mOpenSerialPortListenList.add(listener);
            }
        }
        return this;
    }

    /**
     * 移除打开串口监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public void removeOpenSerialPortListener(OnOpenSerialPortListener listener) {
        if (listener != null) {
            if (mOpenSerialPortListenList.contains(listener)) {
                mOpenSerialPortListenList.remove(listener);
            }
        }
    }

    /**
     * 添加数据通信监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public SerialPortManager setOnSerialPortDataListener(OnSerialPortDataListener listener) {
        if (listener != null) {
            if (!mSerialPortDataListenList.contains(listener)) {
                mSerialPortDataListenList.add(listener);
            }
        }
        return this;
    }

    /**
     * 移除数据通信监听
     *
     * @param listener listener
     * @return SerialPortManager
     */
    public SerialPortManager removeSerialPortDataListener(OnSerialPortDataListener listener) {
        if (listener != null) {
            if (!mSerialPortDataListenList.contains(listener)) {
                mSerialPortDataListenList.remove(listener);
            }
        }
        return this;
    }

    /**
     * 开启发送消息的线程
     */
    private void startSendThread() {
        // 开启发送消息的线程
        mSendingHandlerThread = new HandlerThread("mSendingHandlerThread");
        mSendingHandlerThread.start();
        // Handler
        mSendingHandler = new Handler(mSendingHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                byte[] sendBytes = (byte[]) msg.obj;

                if (null != mFileOutputStream && null != sendBytes && 0 < sendBytes.length) {
                    try {
                        mFileOutputStream.write(sendBytes);
                        for (int i = 0; i < mSerialPortDataListenList.size(); i++) {
                            OnSerialPortDataListener onSerialPortDataListener = mSerialPortDataListenList.get(i);
                            if (null != onSerialPortDataListener) {
                                onSerialPortDataListener.onDataSent(sendBytes);
                            }
                        }
                    } catch (IOException e) {
                        for (int i = 0; i < mSerialPortDataListenList.size(); i++) {
                            OnSerialPortDataListener onSerialPortDataListener = mSerialPortDataListenList.get(i);
                            if (null != onSerialPortDataListener) {
                                onSerialPortDataListener.onDataSent(null);
                            }
                        }
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    /**
     * 停止发送消息线程
     */
    private void stopSendThread() {
        mSendingHandler = null;
        if (null != mSendingHandlerThread) {
            mSendingHandlerThread.interrupt();
            mSendingHandlerThread.quit();
            mSendingHandlerThread = null;
        }
    }

    /**
     * 开启接收消息的线程
     */
    private void startReadThread() {
        mSerialPortReadThread = new SerialPortReadThread(mFileInputStream) {
            @Override
            public void onDataReceived(byte[] bytes) {
                for (int i = 0; i < mSerialPortDataListenList.size(); i++) {
                    OnSerialPortDataListener onSerialPortDataListener = mSerialPortDataListenList.get(i);
                    if (null != onSerialPortDataListener) {
                        onSerialPortDataListener.onDataReceived(bytes);
                    }
                }
            }
        };
        mSerialPortReadThread.start();
    }

    /**
     * 停止接收消息的线程
     */
    private void stopReadThread() {
        if (null != mSerialPortReadThread) {
            mSerialPortReadThread.release();
        }
    }

    /**
     * 发送数据
     *
     * @param sendBytes 发送数据
     * @return 发送是否成功
     */
    public boolean sendBytes(byte[] sendBytes) {
        if (null != mFd && null != mFileInputStream && null != mFileOutputStream) {
            if (null != mSendingHandler) {
                Message message = Message.obtain();
                message.obj = sendBytes;
                return mSendingHandler.sendMessage(message);
            }
        }
        return false;
    }


    public void SendSerialCom(SerialModel type, @Nullable Byte NozzleStep){

        Log.d(TAG,"SendSerialCom : "+ type);

        byte[] senddata;
        byte[] crcdata;

        switch (type){
            case   Require_Info:   // 정보요청
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x50,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                 break;
            case   Require_House_01_TH:    //하우스 01 온습도
                crcdata = CRC16Util.sendData(new byte[]{0x02,0x00});
                senddata = new byte[]{0x02,0x50,0x01,0x02,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case   Require_House_02_TH:    //하우스 02 온습도
                crcdata = CRC16Util.sendData(new byte[]{0x03,0x00});
                senddata = new byte[]{0x02,0x50,0x01,0x03,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case   Require_House_03_TH:    //하우스 03 온습도
                crcdata = CRC16Util.sendData(new byte[]{0x04,0x00});
                senddata = new byte[]{0x02,0x50,0x01,0x04,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case   Require_House_04_TH:    //하우스 04 온습도
                crcdata = CRC16Util.sendData(new byte[]{0x05,0x00});
                senddata = new byte[]{0x02,0x50,0x01,0x05,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;     // 1,2 온도
                    // 3,4 습도

            case     Com_House_01_02_Air_Pump_Start:  // house 01,02 콤 펌프 시작
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x01});
                senddata = new byte[]{0x02,0x51,0x01,0x01,0x01,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case      Com_House_01_02_Air_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x00,0x00});
                senddata = new byte[]{0x02,0x51,0x01,0x00,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case      Com_House_01_02_Pump_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x51,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;

            case      Com_House_03_04_Air_Pump_Start:  // house 03,04 콤 펌프 시작
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x01});
                senddata = new byte[]{0x02,0x52,0x01,0x01,0x01,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case      Com_House_03_04_Air_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x00,0x00});
                senddata = new byte[]{0x02,0x52,0x01,0x00,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case      Com_House_03_04_Pump_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x52,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;

            case        Com_House_01_02_Cleanner01_Start:   // house 01,02 에어청소 시작
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x01});
                senddata = new byte[]{0x02,0x53,0x01,0x01,0x01,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        Com_House_01_02_Cleanner01_Air_Start:
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x53,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        Com_House_01_02_Cleanner01_Air_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x00,0x00});
                senddata = new byte[]{0x02,0x53,0x01,0x00,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        Com_House_01_02_Cleanner01_Pump_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x53,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       Com_House_03_04_Cleanner01_Start:   // house 03,04 에어청소 시작
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x01});
                senddata = new byte[]{0x02,0x54,0x01,0x01,0x01,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        Com_House_03_04_Cleanner01_Air_Start:
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x54,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        Com_House_03_04_Cleanner01_Air_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x00,0x00});
                senddata = new byte[]{0x02,0x54,0x01,0x00,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        Com_House_03_04_Cleanner01_Pump_Stop:
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x54,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       Com_House_01_02_Cleanner02_Start:   // house 01,02 물청소 시작
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x55,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       Com_House_01_02_Cleanner02_Stop:   // house 01,02 물청소 시작
                crcdata = CRC16Util.sendData(new byte[]{0x00,0x00});
                senddata = new byte[]{0x02,0x55,0x01,0x00,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       Com_House_03_04_Cleanner02_Start:   // house 03,04 물청소 시작
                crcdata = CRC16Util.sendData(new byte[]{0x01,0x00});
                senddata = new byte[]{0x02,0x56,0x01,0x01,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       Com_House_03_04_Cleanner02_Stop:   // house 03,04 물청소 시작
                crcdata = CRC16Util.sendData(new byte[]{0x00,0x00});
                senddata = new byte[]{0x02,0x56,0x01,0x00,0x00,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;

            case        House_01_Nozzle_1:  // house 01 노즐 1단계
                crcdata = CRC16Util.sendData(new byte[]{0x01,NozzleStep});
                senddata = new byte[]{0x02,0x57,0x01,0x01,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       House_01_Nozzle_2: // house 01 노즐 2단계
                crcdata = CRC16Util.sendData(new byte[]{0x02,NozzleStep});
                senddata = new byte[]{0x02,0x57,0x01,0x02,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       House_01_Nozzle_3: // house 01 노즐 3단계
                crcdata = CRC16Util.sendData(new byte[]{0x03,NozzleStep});
                senddata = new byte[]{0x02,0x57,0x01,0x03,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       House_01_Nozzle_4: // house 01 노즐 4단계
                crcdata = CRC16Util.sendData(new byte[]{0x04,NozzleStep});
                senddata = new byte[]{0x02,0x57,0x01,0x04,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;

            case       House_02_Nozzle_1:  // house 02 노즐 1단계
                crcdata = CRC16Util.sendData(new byte[]{0x01,NozzleStep});
                senddata = new byte[]{0x02,0x58,0x01,0x01,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       House_02_Nozzle_2: // house 02 노즐 2단계
                crcdata = CRC16Util.sendData(new byte[]{0x02,NozzleStep});
                senddata = new byte[]{0x02,0x58,0x01,0x02,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       House_02_Nozzle_3: // house 02 노즐 3단계
                crcdata = CRC16Util.sendData(new byte[]{0x03,NozzleStep});
                senddata = new byte[]{0x02,0x58,0x01,0x03,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case       House_02_Nozzle_4: // house 02 노즐 4단계
                crcdata = CRC16Util.sendData(new byte[]{0x04,NozzleStep});
                senddata = new byte[]{0x02,0x58,0x01,0x04,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;

            case       House_03_Nozzle_1:  // house 03 노즐 1단계
                crcdata = CRC16Util.sendData(new byte[]{0x01,NozzleStep});
                senddata = new byte[]{0x02,0x59,0x01,0x01,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        House_03_Nozzle_2: // house 03 노즐 2단계
                crcdata = CRC16Util.sendData(new byte[]{0x02,NozzleStep});
                senddata = new byte[]{0x02,0x59,0x01,0x02,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        House_03_Nozzle_3: // house 03 노즐 3단계
                crcdata = CRC16Util.sendData(new byte[]{0x03,NozzleStep});
                senddata = new byte[]{0x02,0x59,0x01,0x03,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        House_03_Nozzle_4: // house 03 노즐 4단계
                crcdata = CRC16Util.sendData(new byte[]{0x04,NozzleStep});
                senddata = new byte[]{0x02,0x59,0x01,0x04,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;

            case        House_04_Nozzle_1:  // house 04 노즐 1단계
                crcdata = CRC16Util.sendData(new byte[]{0x01,NozzleStep});
                senddata = new byte[]{0x02,0x5A,0x01,0x01,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        House_04_Nozzle_2: // house 04 노즐 2단계
                crcdata = CRC16Util.sendData(new byte[]{0x02,NozzleStep});
                senddata = new byte[]{0x02,0x5A,0x01,0x02,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        House_04_Nozzle_3: // house 04 노즐 3단계
                crcdata = CRC16Util.sendData(new byte[]{0x03,NozzleStep});
                senddata = new byte[]{0x02,0x5A,0x01,0x03,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
            case        House_04_Nozzle_4: // house 04 노즐 4단계
                crcdata = CRC16Util.sendData(new byte[]{0x04,NozzleStep});
                senddata = new byte[]{0x02,0x5A,0x01,0x04,NozzleStep,crcdata[1],crcdata[2],0x03};
                sendBytes(senddata);
                break;
        }

    }

    /**
     * 发送数据
     *
     * @param sendBytes 发送数据
     * @return 发送是否成功
     */
    public boolean sendBytes(byte[] sendBytes, long delayMillis) {
        if (null != mFd && null != mFileInputStream && null != mFileOutputStream) {
            if (null != mSendingHandler) {
                Message message = Message.obtain();
                message.obj = sendBytes;
                return mSendingHandler.sendMessageDelayed(message, delayMillis);
            }
        }
        return false;
    }
}
