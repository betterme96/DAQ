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
        switch (type){
            case 'r':
                ReadOut rd = (ReadOut) object;
                try{
                    if(op == 0){
                        rd.getData();
                    }else {
                        rd.analyzeData();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 's':
                Store store = (Store)object;
                try{
                    if(op == 0){
                        store.getData(ringBuf);
                    }else {
                        store.analyzeData();
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
