package com.serenegiant.net;

/*
 * Created by Hambobo on 2016-06-16.
 */
public class TransmissionState
{
    public final int WAIT_ACK = 0x0001;//0000 0000 0000 0001
    public final int SEND_ACK = 0x0002;//0000 0000 0000 0010
    public final int USING = 0x0004;//0000 0000 0000 0100
    public final int TIMEOUT_WAIT_ACK = 6000;//ms
    public final int TIMEOUT_USING = 300000;//发送数据连续占用的最大时间
    public static long waitACKStartTime;
    public static long usingStartTime;
    public int waitAckCmd;
    public int waitAckSubCmd;
    public int sendAckCmd;
    public int sendAckSubCmd;
    public int state;
//                                        8900      8101
    public synchronized void setState(int cmd, int subCmd, int mode)
    {
        switch (mode)
        {
            case WAIT_ACK:
                waitACKStartTime = System.currentTimeMillis();
                this.waitAckCmd = cmd;
                this.waitAckSubCmd = subCmd;
                this.state |= WAIT_ACK;
                break;
            case SEND_ACK:
                this.sendAckCmd = cmd;
                this.sendAckSubCmd = subCmd;
                this.state |= SEND_ACK;
                break;
            case USING:
                usingStartTime = System.currentTimeMillis();
                this.sendAckCmd = cmd;
                this.sendAckSubCmd = subCmd;
                this.state |= USING;
                break;
            default:
                break;
        }
    }

    public synchronized int getSendAckCmd()
    {
        return this.sendAckCmd;
    }
    public synchronized int getWaitAckCmd()
    {
        return this.waitAckCmd;
    }
    public synchronized int getSendAckSubCmd()
    {
        return this.sendAckSubCmd;
    }
    public synchronized int getWaitAckSubCmd()
    {
        return this.waitAckSubCmd;
    }
    public synchronized void clearState(int flag)
    {
        state &= ~flag;
    }

    public synchronized int  getState()
    {
        if ((this.state & WAIT_ACK) != 0)//state == WAIT_ACK
        {
            if (System.currentTimeMillis() - waitACKStartTime > TIMEOUT_WAIT_ACK)
            {
                state &= ~WAIT_ACK;
            }
            if (System.currentTimeMillis() - usingStartTime > TIMEOUT_USING)
            {
                state &= ~USING;
            }
        }
        return state;  //1
    }
}
