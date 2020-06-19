package com.wzb.client.DAQ;

import com.wzb.client.helper.RingBuffer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Store {
    public void storeData(RingBuffer ringBuf, String filePath) throws InterruptedException, IOException {
        File sFile = new File(filePath);
        if(!sFile.exists()){
            sFile.createNewFile();
        }
        FileOutputStream storeFile = new FileOutputStream(sFile);
        byte[] data = new byte[100];
        int length = 0;
        while((length = ringBuf.read(data,0,100) )!= -1){
            storeFile.write(data, 0, length);
        }
        System.out.println("store suc!!");
        storeFile.flush();;
        storeFile.close();
    }
}
