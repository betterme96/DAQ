package com.wzb.client.DAQ;

import com.wzb.client.helper.RingBuffer;

import java.io.File;
import java.io.FileOutputStream;

public class Store implements Runnable{
    public static final char Ctype = 's';
    private RingBuffer curBuffer;

    public volatile boolean start = false;
    public volatile int status = 1;

    public Store(RingBuffer curBuffer, int status){
        this.curBuffer = curBuffer;
        this.status = status;
    }

    public void run() {
        try {

            while (!start){

            }
            System.out.println("Store module working......");
            String filePath = "./src/sources/to/store.txt";
            File sFile = new File(filePath);
            if(!sFile.exists()){
                sFile.createNewFile();
            }
            FileOutputStream storeFile = new FileOutputStream(sFile);
            byte[] data = new byte[100];
            int length = 0;
            while((length = curBuffer.read(data,0,100, "store") )!= -1){
                storeFile.write(data, 0, length);
            }
            System.out.println("store suc!!");
            storeFile.flush();;
            storeFile.close();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
