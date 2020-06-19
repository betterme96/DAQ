package com.wzb.client.DAQ;
import com.wzb.client.helper.RingBuffer;

import java.io.*;
import java.net.Socket;

public class ReadOut {
    public void getData(Socket socket, RingBuffer ringbuf) throws IOException, InterruptedException {
        InputStream in = socket.getInputStream();
        byte[] data = new byte[100];
        int length = 0;
        while((length = in.read(data)) != -1){
            int write = ringbuf.write(data, 0,length);
            if(write == -1){
                break;
            }
        }
        socket.shutdownInput();
    }

    public void recvData(Socket socket, String filePath) throws IOException, InterruptedException {
        /*
        InputStream in = socket.getInputStream();
        File file = new File(filePath);
        FileOutputStream storeFile = new FileOutputStream(file);

        byte[] data = new byte[1024];
        int length = 0;
        int total = 0;
        while ((length = in.read(data)) != -1){
            storeFile.write(data, total, length);
            total += length;
        }
        System.out.println("recv suc!!");
        storeFile.flush();
        storeFile.close();
         */

        Store store = new Store();
        RingBuffer ringbuf = new RingBuffer();
        new Thread(new DThread(socket, ringbuf, this, store, true, filePath)).start();
        new Thread(new DThread(socket, ringbuf, this,store,false,filePath)).start();
    }
}

class DThread implements Runnable{
    private RingBuffer ringBuf;
    private boolean flag;
    private ReadOut rd;
    private Store store;
    private Socket socket;
    private String filePath;

    public DThread(Socket socket, RingBuffer ringBuf, ReadOut rd, Store store, boolean flag, String filePath){
        this.socket = socket;
        this.ringBuf = ringBuf;
        this.rd = rd;
        this.store = store;
        this.flag = flag;
        this.filePath = filePath;
    }
    public void run() {
        //write
        if(flag){
            try {
                rd.getData(socket, ringBuf);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            try {
                store.storeData(ringBuf,filePath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}


