package com.wzb.client.helper;

import java.util.Arrays;

public class RingBuffer {

    private final static int DEFAULT_SIZE  = 1024;
    public byte[] buffer;
    private int rHead = 0;
    private int wTail = 1;
    private int bufferSize;
    private int rbCapacity;
    private int time;

    public RingBuffer(){
        this.bufferSize = DEFAULT_SIZE;
        this.buffer = new byte[bufferSize];
        rbCapacity = bufferSize;
        this.time=5;
    }
    public RingBuffer(int initSize,int time){
        this.bufferSize = initSize;
        this.buffer = new byte[bufferSize];
        this.time=time;
        rbCapacity = bufferSize;
    }
    public void clear(){
        Arrays.fill(buffer,(byte)0xff);
        this.rHead = 0;
        this.wTail = 1;
    }
    public int canRead()
    {
        if (rHead < wTail)
        {
            return wTail - rHead-1;
        }
        return rbCapacity - (rHead - wTail)-1;
    }
    public int canWrite()
    {
        return rbCapacity - canRead()-2;
    }
    int fla=1;
    public int read(byte[] rdata,int srcpos ,int count)
    {
        try {
            int copySz = 0;  int tailAvailSz = 0;
            if(count<0) {
                throw new RuntimeException("count must more than zero!");
            }
            int i = 0;
            if(count > canRead())
                System.out.printf("no data to read:dataSize:%d , count:%d; \n",canRead(),count);
            while (count > canRead())
            {

                Thread.sleep(1);
                i++;
                //sop("write----sleep:"+i);
                if(i>time) {
                    sop("Read Over Time ");
                    return -1;
                }
            }

            if (rHead < wTail)
            {
                copySz = count;
                //memcpy(data, rHead, copySz);
                System.arraycopy(buffer, (rHead+1), rdata, srcpos, count);
                rHead += copySz;
                return copySz;
            }
            else
            {
                tailAvailSz = rbCapacity-rHead-1;
                if (count <= tailAvailSz)
                {
                    //memcpy(data, rHead, count);
                    //System.out.printf("rHead+1:%d,  srcpos:%d, count:%d\n",rHead+1,  srcpos, count);
                    System.arraycopy(buffer, rHead+1, rdata, srcpos, count);
                    rHead += count;
                    if (rHead == rbCapacity)
                    {
                        rHead= -1;
                    }
                    return count;
                }
                else
                {
                    //memcpy(data, rHead, tailAvailSz);
                    System.arraycopy(buffer, rHead+1, rdata, srcpos, tailAvailSz);
                    rHead= -1;
                    return tailAvailSz+read(rdata, srcpos+tailAvailSz, count-tailAvailSz);
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            System.out.printf("rHead+1:%d,  srcpos:%d, count:%d\n",rHead+1,  srcpos, count);
            e.printStackTrace();
            return -1;
        }

    }
    public int write(byte[]data, int srcpos,int count) throws InterruptedException
    {
        int tailAvailSz = 0;

        int i=0;
        if(count > canWrite())
            System.out.printf("no space to write:space:%d , count:%d; \n",canWrite(),count);
        while ( count > canWrite() )
        {
            Thread.sleep(1);
            i++;
            //sop("read----sleep:"+i);
            if(i>time){
                sop("Write Over Time");
                return -1;
            }
        }
        if (rHead <wTail)
        {
            tailAvailSz = rbCapacity - wTail;
            if (count <= tailAvailSz)
            {
                //memcpy(wTail, data, count);
                System.arraycopy(data,srcpos,buffer ,wTail, count);
                wTail += count;
                if (wTail == rbCapacity)
                {
                    wTail = 0;
                }
                return count;
            }
            else
            {
                // memcpy(wTail, data, tailAvailSz);
                System.arraycopy(data,srcpos,buffer ,wTail, tailAvailSz);
                wTail = 0;

                return tailAvailSz + write(data,tailAvailSz+srcpos ,count-tailAvailSz);
            }
        }
        else
        {
            // memcpy(wTail, data, count);
            System.arraycopy(data,srcpos,buffer ,wTail, count);
            wTail += count;
            return count;
        }
    }
    public static void sop(Object obj) {
        System.out.println(obj);
    }
}

