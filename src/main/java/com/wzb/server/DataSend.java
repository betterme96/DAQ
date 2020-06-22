package com.wzb.server;

import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class DataSend {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSock = new ServerSocket(8001);
        System.out.println("server started, wating for connect...");
        Socket socket = serverSock.accept();
        //recv data from client
        InputStream in = socket.getInputStream();
        byte[] comm = new byte[2];
        while(true){
            //get start
            if((in.read(comm, 0, 2) == 2) && (((comm[0] == 0x00) && comm[1] == 0x01))){
                System.out.println("recv start");
                break;
            }
        }

        SendData sd = new SendData(socket);
        new Thread(new Monitor(socket, sd)).start();
        sd.run();
    }
}

class SendData implements Runnable{
    public volatile boolean exit = false;

    private Socket socket;

    public  SendData(Socket socket){
        this.socket = socket;
    }

    public void run() {
        System.out.println("senddata start");
        try{
            //send data to client
            OutputStream out = socket.getOutputStream();
            File file = new File("./src/sources/from/test.txt");
            System.out.println("file len:  " + file.length());
            if(!file.exists()){
                System.out.println("file not exists!");
            }
            InputStream fileIn = new FileInputStream(file);
            byte[] data = new byte[100];
            int total = 0;
            int curLen = 0;
            System.out.println("exit:  "+exit);
            while (!exit ){
                /*
                if(curLen == -1){
                    fileIn.close();
                    fileIn = new FileInputStream(file);
                    curLen = fileIn.read(data);
                }

                 */
                while((curLen = fileIn.read(data)) != -1 && !exit) {
                    out.write(data, 0, curLen);
                    total += curLen;
                }
                fileIn.close();;
                fileIn = new FileInputStream(file);
            }
            System.out.println("bytes send:  " + total);
            System.out.println("exit:  "+exit);
            out.write("\nReceived the stop command".getBytes());
            fileIn.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

class Monitor implements Runnable{
    private Socket socket;
    private SendData sd;
    public Monitor(Socket socket, SendData sd){
        this.socket = socket;
        this.sd = sd;
    }

    public void run() {
        System.out.println("monitor start");
        try {
            InputStream in = socket.getInputStream();
            byte[] comm = new byte[2];
            while(in.read(comm) == -1){
                Thread.sleep(100);
            }
            if(comm[0] == 0x00 && comm[1] == 0x11){
                System.out.println("recv stop command");
                sd.exit = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
