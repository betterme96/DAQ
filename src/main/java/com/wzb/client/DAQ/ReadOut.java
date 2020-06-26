package com.wzb.client.DAQ;
import com.wzb.client.helper.Config;
import com.wzb.client.helper.RingBuffer;

import java.io.*;
import java.net.Socket;


public class ReadOut implements Runnable{
    public volatile boolean start = false;
    public volatile boolean config = false;
    public volatile int status = 1;

    private String ip;
    private String port;
    private RingBuffer curBuffer;

    private Builder builder;
    public ReadOut(String ip, String port, RingBuffer ringBuffer, Builder builder, int status){
        this.ip = ip;
        this.port = port;
        this.curBuffer = ringBuffer;
        this.builder = builder;
        this.status = status;
    }

    public void run() {
        try{
            String[] ports = port.split(",");
            Socket commSocket = new Socket(ip, Integer.parseInt(ports[0]));
            Socket dataSocket = new Socket(ip, Integer.parseInt(ports[1]));

            while (!config){

            }
            Config config = new Config();
            config.sendConfig();

            while (!start){
            }
            this.status = 4;
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





