package com.wzb.client.DAQ;

import com.wzb.client.helper.RingBuffer;

import java.io.File;
import java.io.FileOutputStream;

public class Builder implements Runnable{
    private RingBuffer curBuffer;
    private RingBuffer nextBuffer;
    private Store store;

    public volatile boolean start = false;

    public Builder(RingBuffer curBuffer, RingBuffer nextBuffer, Store store){
        this.curBuffer = curBuffer;
        this.nextBuffer = nextBuffer;
        this.store = store;
    }

    public void run() {
        try{

            while (!start){

            }
            System.out.println("Builder module working......");

            byte[] data = new byte[100];
            int length = 0;
            while ((length = curBuffer.read(data, 0, 100,"builder")) != -1){
                //System.out.println("----builder read----");
                int write = handleData(data,length);
                if(write == -1) {
                    break;
                }
            }
            System.out.println("build data suc!!");
        }catch (Exception e){

        }

    }

    private int handleData(byte[] data, int length) throws InterruptedException {
        /*
        check data
         */
        //if data meet the conditions, write to next buffer
        return nextBuffer.write(data,0,length, "Builder module");
        //System.out.println("----builder write----");

    }
}