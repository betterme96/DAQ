package com.wzb.client;

import com.wzb.client.DAQ.ReadOut;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MainControl {
    public static void main(String[] args) throws IOException, InterruptedException {
        Socket socket = new Socket("127.0.0.1", 8001);
        OutputStream out = socket.getOutputStream();
        byte[] start = new byte[2];
        start[0] = 0x00;
        start[1] = 0x01;
        System.out.println("——————————————————");
        System.out.println("1.启动");
        Scanner scan = new Scanner(System.in);
        while(Integer.parseInt(scan.nextLine()) != 1){
            System.out.println("输入 1 启动！");
        }
        out.write(start);

        ReadOut rd = new ReadOut();
        String filePath = "./src/sources/to/res.txt";
        rd.recvData(socket, filePath);
    }
}
