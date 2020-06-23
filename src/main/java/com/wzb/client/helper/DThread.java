package com.wzb.client.helper;

import com.wzb.client.DAQ.ReadOut;
import com.wzb.client.DAQ.Store;

import java.net.Socket;

public class DThread<T> implements Runnable{
    private RingBuffer ringBuf;
    private T object;
    private char type;
    private int op;

    public DThread( RingBuffer ringBuf, T object, char type, int op){
        this.ringBuf = ringBuf;
        this.object = object;
        this.type = type;
        this.op = op;
    }
    public void run() {

    }
}
