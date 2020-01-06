package com.serenegiant.rfid;

import com.serenegiant.AppContext;

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
            byte type;
            byte[] id = null;
            CardInfo cardInfo = null;
            RFID.CardType cardType = RFID.CardType.UnKnown;

            AppContext.Stopped = false;
            while (!AppContext.Stopped) {
                if (mRfid.checkCard()){
                    type = mRfid.readCardType();
                    //身份证
                    if (type == (byte) 4 && mRfid.readIdFromCard() != null){
                        id = mRfid.readIdFromCard();
//                    mListener.onReadIdentificationCardSuccess(mRfid.readIdFromCard());
                        AppContext.Stopped = true;
                        break;
                    }
                    //其他类型卡
                    else {
                        cardInfo = mRfid.getCardInfo();
                        cardType = cardInfo.getCardType();

                        if (cardType != RFID.CardType.UnKnown && cardType != null && mListener != null) {
                            AppContext.Stopped = true;
                            break;
                        }
                    }

                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (cardInfo != null && cardType != RFID.CardType.UnKnown && mListener != null)
                mListener.onReadSuccess(cardInfo, cardType);
        }
    }
}
