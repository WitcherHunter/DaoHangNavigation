package com.serenegiant.business.obd;

import com.kongqw.serialportlibrary.Device;
import com.kongqw.serialportlibrary.SerialPortFinder;
import com.kongqw.serialportlibrary.SerialPortManager;
import com.kongqw.serialportlibrary.listener.OnSerialPortDataListener;
import com.serenegiant.AppContext;

import java.util.ArrayList;
import java.util.List;

abstract class ObdService implements OnSerialPortDataListener {
    private SerialPortManager mSerialPortManager;
    protected List<Byte> tempDataList = new ArrayList<>();
    protected boolean startReceive = false;
    protected StringBuffer buffer = new StringBuffer();

    public boolean open(String name, int bandRate){
        mSerialPortManager = new SerialPortManager();
        SerialPortFinder finder = new SerialPortFinder();
        ArrayList<Device> devices = finder.getDevices();

        boolean isObdOpen = false;

        for (Device d : devices) {
            if (d.getName().equals(name)) {
                isObdOpen = mSerialPortManager.openSerialPort(d.getFile(), bandRate);
            }
        }

        mSerialPortManager.setOnSerialPortDataListener(this);
        return isObdOpen;
    }

    public void close(){
        mSerialPortManager.closeSerialPort();
    }

    @Override
    public void onDataReceived(byte[] bytes) {
        System.out.println("接收OBD消息");
        AppContext.lastCheckObdTime = System.currentTimeMillis();
    }

    @Override
    public void onDataSent(byte[] bytes) {

    }

    abstract void processData(String data);
}
