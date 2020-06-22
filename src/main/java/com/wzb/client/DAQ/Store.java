package com.wzb.client.DAQ;

import com.wzb.client.helper.DThread;
import com.wzb.client.helper.RingBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Store {
    public static final char Ctype = 's';
    private RingBuffer sbuffer;

    public Store(){
        sbuffer = new RingBuffer();
    }
    public Store(int buffersize, int time){
        sbuffer = new RingBuffer(buffersize, time);
    }

    public  void getData(RingBuffer buffer) throws InterruptedException {
        new Thread(new DThread(sbuffer, Store.class,'s',1)).start();
        byte[] data = new byte[100];
        int length = 0;
        while ((length = buffer.read(data, 0, data.length)) != -1){
            int wLen = sbuffer.write(data, 0, length);
        }

    }
    public void analyzeData() throws InterruptedException, IOException {
        String filePath = "./src/sources/res.txt";
        File sFile = new File(filePath);
        if(!sFile.exists()){
            sFile.createNewFile();
        }
        FileOutputStream storeFile = new FileOutputStream(sFile);
        byte[] data = new byte[100];
        int length = 0;
        while((length = sbuffer.read(data,0,100) )!= -1){
            storeFile.write(data, 0, length);
        }
        System.out.println("store suc!!");
        storeFile.flush();;
        storeFile.close();
    }
}
