package com.wzb.client.helper;

public class Config {
    public int sendConfig() throws InterruptedException {
        System.out.println("Sending config......");
        Thread.sleep(3000);
        System.out.println("Sending config suc!");
        return 1;
    }
}
