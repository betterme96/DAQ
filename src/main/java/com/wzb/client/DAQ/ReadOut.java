package com.wzb.client.DAQ;
import com.wzb.client.helper.RingBuffer;

import java.io.*;
import java.net.Socket;


public class ReadOut implements Runnable{
    public static final char Ctype = 'r';

    public volatile boolean start = false;
    public volatile boolean exit = false;

    private RingBuffer curBuffer;
    private Socket socket;

    private Builder builder;
    public ReadOut(Socket socket, RingBuffer ringBuffer, Builder builder){
        this.socket = socket;
        this.curBuffer = ringBuffer;
        this.builder = builder;
    }

    public void run() {
        try{
            while (!start){
            }
            System.out.println("Readout module working......");
            InputStream in = socket.getInputStream();

            byte[] data = new byte[100];
            int length = 0;
            while((length = in.read(data)) != -1){
                //System.out.println("----readout write----");
                int write = curBuffer.write(data, 0,length);
                if(write == -1){
                    break;
                }
            }
            builder.exit = true;
            System.out.println("readout suc!");
            socket.shutdownInput();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}





