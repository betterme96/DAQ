package com.wzb.client;

import com.sun.tools.internal.xjc.reader.Ring;
import com.wzb.client.DAQ.Builder;
import com.wzb.client.DAQ.ReadOut;
import com.wzb.client.DAQ.Store;
import com.wzb.client.helper.Config;
import com.wzb.client.helper.RingBuffer;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MainControl {
    /* DAQ state machine
     * 1: waiting
     * 2: initialized
     * 3: configed
     * 4: runing
     */
    public static int status;

    public static void main(String[] args) throws IOException {
        status = 1;
        new Thread(new Monitor(status)).start();
    }

}
class Monitor implements Runnable{
    private Socket commSocket;
    private ReadOut rd;
    private Builder builder;
    private Store store;
    private int status;

    public Monitor(int status){
        this.status = status;
    }
    /*
    public Monitor(Socket socket, ReadOut rd, Builder builder, Store store, int status){
        this.socket = socket;
        this.rd = rd;
        this.builder = builder;
        this.store = store;
        this.status = status;
    }

     */
    public void run() {
        try{
            Scanner scan = new Scanner(System.in);

            while (true){
                System.out.println("——————————————————");
                System.out.println("1.init");
                System.out.println("2.config");
                System.out.println("3.start");
                System.out.println("4.stop");
                System.out.println("5.unconfig");

                //init
                while (status < 2){
                    while (scan.nextInt() != 1){
                        System.out.println("input \"1\" to init！");
                    }if(status != 1){
                        return;
                    }

                    //init command socket
                    commSocket = new Socket("127.0.0.1", 8000);

                    //init ringbuffer
                    RingBuffer[] ringBuffers = new RingBuffer[3];
                    int time = 3;
                    for(int i = 0; i < ringBuffers.length; ++i){
                        ringBuffers[i] = new RingBuffer(1024*3, time);
                        time *= 2;
                    }

                    //init module
                    store = new Store(ringBuffers[1]);
                    builder = new Builder(ringBuffers[0], ringBuffers[1], store);
                    rd = new ReadOut("127.0.0.1", "8001", ringBuffers[0], builder);

                    //start thread
                    new Thread(rd).start();
                    new Thread(builder).start();
                    new Thread(store).start();

                    //init suc
                    status = 2;
                }
                System.out.println("init sunc!!");

                OutputStream out = commSocket.getOutputStream();
                //config
                while (status < 3){
                    while (scan.nextInt() != 2){
                        System.out.println("input \"2\" to config！");
                    }

                    //send config
                    Config config = new Config();
                    config.sendConfig();
                    status = 3;
                }

                System.out.println("config sunc!!");
                //start running
                while(scan.nextInt() != 3){
                    System.out.println("input \"3\" to start！");
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
                status = 4;

                //stop running
                while (scan.nextInt() != 4){
                }
                comm[0] = 0x00;
                comm[1] = 0x11;
                out.write(comm);
                System.out.println("send stop!");
                Thread.sleep(3);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}


