package com.wzb.client.DAQ;
import com.wzb.client.helper.DThread;
import com.wzb.client.helper.RingBuffer;

import java.io.*;
import java.net.Socket;


public class ReadOut {
    public static final char Ctype = 'r';

    private RingBuffer rdBuffer;
    private Socket socket;
    public RingBuffer buffer;

    public ReadOut(Socket socket){
        this.socket = socket;
        buffer = new RingBuffer();
        rdBuffer = new RingBuffer();
    }
    public ReadOut(int buffersize, int time){
        buffer = new RingBuffer(buffersize, time);
        rdBuffer = new RingBuffer(buffersize, time);
    }

    public void recvData() throws IOException, InterruptedException {
        System.out.println("start to recv");
        new Thread(new DThread(rdBuffer, this, Ctype, 0)).start();
        new Thread(new DThread(rdBuffer, this, Ctype, 1)).start();
    }

    public void getData() throws IOException, InterruptedException {
        InputStream in = socket.getInputStream();
        byte[] data = new byte[100];
        int length = 0;
        while((length = in.read(data)) != -1){
            int write = rdBuffer.write(data, 0,length);
            if(write == -1){
                break;
            }
        }
        System.out.println("data recv suc!");
        socket.shutdownInput();
    }

    //ReadOut模块的初步分析处理，并将处理后的数据放入下一级buffer
    public  void analyzeData() throws InterruptedException, IOException {
        /*
        byte[] data = new byte[100];
        int length = 0;
        while((length = rdBuffer.read(data,0,100) )!= -1){
            int write = buffer.write(data, 0, length);
        }
        System.out.println("Readout module analyze done!");
         */
        String filePath = "./src/sources/to/res.txt";
        File sFile = new File(filePath);
        if(!sFile.exists()){
            sFile.createNewFile();
        }
        FileOutputStream storeFile = new FileOutputStream(sFile);
        byte[] data = new byte[100];
        int length = 0;
        while((length = rdBuffer.read(data,0,100) )!= -1){
            storeFile.write(data, 0, length);
        }
        System.out.println("store suc!!");
        storeFile.flush();;
        storeFile.close();
    }

}



