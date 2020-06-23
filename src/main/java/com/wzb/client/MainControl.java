package com.wzb.client;

import com.sun.tools.internal.xjc.reader.Ring;
import com.wzb.client.DAQ.Builder;
import com.wzb.client.DAQ.ReadOut;
import com.wzb.client.DAQ.Store;
import com.wzb.client.helper.RingBuffer;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MainControl {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket commSocket = new Socket("127.0.0.1", 8000);
        Socket dataSocket = new Socket("127.0.0.1", 8001);

        RingBuffer[] ringBuffers = new RingBuffer[3];
        int time = 3;
        for(int i = 0; i < ringBuffers.length; ++i){
            ringBuffers[i] = new RingBuffer(1024, time);
            time *= 2;
        }
        Store store = new Store(ringBuffers[1]);
        Builder builder = new Builder(ringBuffers[0], ringBuffers[1], store);
        ReadOut rd = new ReadOut(dataSocket, ringBuffers[0], builder);

        Monitor monitor = new Monitor(commSocket, rd, builder, store);
        new Thread(monitor).start();
        new Thread(rd).start();
        new Thread(builder).start();
        new Thread(store).start();
    }
}
class Monitor implements Runnable{
    private Socket socket;
    private ReadOut rd;
    private Builder builder;
    private Store store;

    public Monitor(Socket socket, ReadOut rd, Builder builder, Store store){
        this.socket = socket;
        this.rd = rd;
        this.builder = builder;
        this.store = store;
    }
    public void run() {
        try{
            OutputStream out = socket.getOutputStream();
            Scanner scan = new Scanner(System.in);

            System.out.println("——————————————————");
            System.out.println("1.start");
            System.out.println("2.stop");

            while(scan.nextInt() != 1){
                System.out.println("input \"1\" to start！");
            }

            byte[] comm = new byte[2];
            comm[0] = 0x00;
            comm[1] = 0x01;
            out.write(comm);

            rd.start = true;
            Thread.sleep(1000);
            builder.start = true;
            Thread.sleep(1000);
            store.start = true;

            while (scan.nextInt() != 2){
            }
            comm[0] = 0x00;
            comm[1] = 0x11;
            out.write(comm);
            System.out.println("send stop!");

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


