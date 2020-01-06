package com.serenegiant.rfid;

public interface OnCardCheckedListener {
    void onReadSuccess(CardInfo cardInfo, RFID.CardType cardType);
}
