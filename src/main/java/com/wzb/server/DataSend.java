package com.wzb.server;

import com.sun.corba.se.spi.activation.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class DataSend {

    public static void main(String[] args) throws IOException {
        ServerSocket commServerSocket = new ServerSocket(8000);
        ServerSocket dataServerSocket = new ServerSocket(8001);
        System.out.println("server started, waiting for connect...");

        Socket commSocket = commServerSocket.accept();
        Socket dataSocket = dataServerSocket.accept();
        //recv data from client

        SendData sd = new SendData(dataSocket);
        Monitor monitor = new Monitor(commSocket, sd);
        new Thread(monitor).start();
        new Thread(sd).start();
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
            while(true){
                //get start
                if((in.read(comm, 0, 2) == 2) && (((comm[0] == 0x00) && comm[1] == 0x01))){
                    System.out.println("recv start");
                    Thread.sleep(1000);
                    sd.start = true;
                    break;
                }
            }
            in.read(comm);
            if(comm[0] == 0x00 && comm[1] == 0x11){
                System.out.println("recv stop command");
                sd.exit = true;
                System.out.println("sd.exit:  "+ sd.exit);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


class SendData implements Runnable{
    public volatile boolean start = false;
    public volatile boolean exit = false;

    private Socket socket;

    public  SendData(Socket socket){
        this.socket = socket;
    }

    public void run() {
        System.out.println("send data start");
        try{

            while(!start){
                Thread.sleep(100);
            }
            //send data to client
            OutputStream out = socket.getOutputStream();
            int i = 1;
            String filePath = "./src/sources/from/" + i + ".txt";
            File file = new File(filePath);
            System.out.println("file len:  " + file.length());
            if(!file.exists()){
                System.out.println("file not exists!");
            }
            InputStream fileIn = new FileInputStream(file);
            byte[] data = new byte[100];
            int total = 0;
            int curLen = 0;
            System.out.println("exit:  "+exit);
            while (!exit){
                curLen = fileIn.read(data);
                if(curLen != -1){
                    out.write(data, 0, curLen);
                    Thread.sleep(100);
                    total += curLen;
                }else{
                    i++;
                    if(i < 4){
                        fileIn.close();
                        filePath = "./src/sources/from/" + i + ".txt";
                        fileIn = new FileInputStream(new File(filePath));
                    }
                }
            }
            /*
            while ( (curLen = fileIn.read(data)) != -1){
                if(curLen != -1) {
                    out.write(data, 0, curLen);
                    total += curLen;
                }

            }

             */
            fileIn.close();
            out.close();
            System.out.println("bytes send:  " + total);
            System.out.println("exit:  "+exit);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

