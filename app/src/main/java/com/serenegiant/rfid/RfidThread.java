package com.serenegiant.rfid;

import com.serenegiant.AppContext;
import com.serenegiant.utils.HexUtil;

public class RfidThread extends Thread {
    private OnCardCheckedListener mListener;
    private RFID mRfid;

    public RfidThread(RFID rfid, OnCardCheckedListener mListener) {
        this.mListener = mListener;
        this.mRfid = rfid;
    }

    @Override
    public void run() {
        AppContext.Stopped = false;
        while (!AppContext.Stopped) {
            byte[] id = new byte[4];

            AppContext.Stopped = false;
            while (!AppContext.Stopped) {
                if (mRfid.checkCard()){
                    byte[] result  = mRfid.readIdFromCard();
                    if (result.length > 4) {
                        System.arraycopy(result, 0, id, 0, id.length);
                        AppContext.Stopped = true;

                        mListener.onReadSuccess(HexUtil.convertUidToLong(id));
                    }
                }
            }
        }
    }
}
