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
        //send data to client
        OutputStream out = socket.getOutputStream();
        byte[] data = new byte[1024];
        while(true){
            //get start
            if((in.read(data, 0, 2) == 2) && (((data[0] == 0x00) && data[1] == 0x01))){
                System.out.println("recv start");
                break;
            }
        }

        File file = new File("./src/sources/from/test.txt");
        if(!file.exists()){
            System.out.println("file not exists!");
        }
        InputStream is = new FileInputStream(file);
        int curLen = 0;
        while((curLen = is.read(data)) != -1){
            out.write(data,0,curLen);
        }
        is.close();
    }
}
