package com.wzb.client.DAQ;
import com.wzb.client.helper.Config;
import com.wzb.client.helper.RingBuffer;

import java.io.*;
import java.net.Socket;


public class ReadOut implements Runnable{
    public volatile boolean start = false;
    public volatile boolean config = false;

    private String ip;
    private String port;
    private RingBuffer curBuffer;

    private Builder builder;
    public ReadOut(String ip, String port, RingBuffer ringBuffer, Builder builder){
        this.ip = ip;
        this.port = port;
        this.curBuffer = ringBuffer;
        this.builder = builder;
    }

    public void run() {
        try{
            Socket dataSocket = new Socket(ip, Integer.parseInt(port));

            while (!start){
            }
            System.out.println("Readout module working......");
            InputStream in = dataSocket.getInputStream();

            byte[] data = new byte[100];
            int length = 0;
            while((length = in.read(data)) != -1){
                //System.out.println("----readout write----");
                int write = curBuffer.write(data, 0,length, "ReadOut");
                if(write == -1){
                    break;
                }
            }
            System.out.println("readout suc!");
            dataSocket.shutdownInput();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}




