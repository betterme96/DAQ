package com.wzb.client;

import com.wzb.client.DAQ.ReadOut;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MainControl {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 8001);

        RecvData rd = new RecvData(socket);
        new Thread(new Monitor(socket, rd)).start();
        rd.run();
    }
}

class RecvData implements Runnable{
    private Socket socket;
    public volatile boolean start = false;

    public RecvData(Socket socket){
        this.socket = socket;
    }
    public void run() {
        System.out.println("start:  " + start);
        while (!start){

        }
        System.out.println("start:  " + start);
        ReadOut rd = new ReadOut(socket);
        try {
            rd.recvData();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class Monitor implements Runnable{
    private Socket socket;
    private RecvData rd;
    public Monitor(Socket socket, RecvData rd){
        this.socket = socket;
        this.rd = rd;
    }
    public void run() {
        try{OutputStream out = socket.getOutputStream();
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
