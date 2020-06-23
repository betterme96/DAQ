package com.wzb.client.helper;

public class RingBuffer{

    private final static int DEFAULT_SIZE  = 1024;
    public byte[] buffer;
    private int pRead = 0;
    private int pWrite = 1;
    private int bufferSize;
    private int rbCapacity;
    private int time;

    public RingBuffer(){
        this.bufferSize = DEFAULT_SIZE;
        this.buffer = new byte[bufferSize];
        rbCapacity = bufferSize;
        this.time = 3;
    }
    public RingBuffer(int initSize,int time){
        this.bufferSize = initSize;
        this.buffer = new byte[bufferSize];
        this.time=time;
        this.rbCapacity = bufferSize;
    }

    public int canRead() {
        if (pRead < pWrite) {
            return pWrite - pRead-1;
        }
        return rbCapacity - (pRead - pWrite)-1;
    }
    public int canWrite() {
        return rbCapacity - canRead()-2;
    }

    public int write(byte[]data, int srcpos,int count) throws InterruptedException
    {
        int waitTime = 0;
        if(count > canWrite()){
            //System.out.printf("space can write: %d , space need write: %d \n",canWrite(),count);
        }
        while(count > canWrite()){
            Thread.sleep(1000);
            waitTime++;
            //sop("read----sleep:"+i);
            if(waitTime > time) {
                System.out.println("Write Over Time");
                return -1;
            }
        }

        if (pRead <pWrite) {
            int tailWriteCap = rbCapacity - pWrite;

            if (count <= tailWriteCap) {
                System.arraycopy(data, srcpos, buffer ,pWrite, count);
                pWrite += count;
                if (pWrite == rbCapacity) {
                    pWrite = 0;
                }
                return count;
            } else {
                System.arraycopy(data,srcpos,buffer ,pWrite, tailWriteCap);
                pWrite = 0;
                return tailWriteCap + write(data, srcpos + tailWriteCap, count - tailWriteCap);
            }
        } else {
            System.arraycopy(data,srcpos,buffer ,pWrite, count);
            pWrite += count;
            return count;
        }
    }

    public int read(byte[] data,int srcpos ,int count, String module) throws InterruptedException {
        try{
            if(count > canRead()) {
                //System.out.printf("space can read: %d , space need read: % d \n",canRead(),count);
            }
            int waitTime = 0;
            while (count > canRead()) {
                Thread.sleep(1000);
                waitTime++;
                //sop("write----sleep:"+i);
                if(waitTime>time) {
                    if(canRead() > 0){
                        int lastRead = canRead();
                        // System.out.printf("Read Over Time and read the last %d bytes data; \n", lastRead);
                        System.arraycopy(buffer, pRead+1, data, srcpos, lastRead);
                        pRead += lastRead;
                        return  lastRead;
                    }else{
                        System.out.println(module + ": Read Over Time and no data to read");
                        return -1;
                    }
                }
            }

            if (pRead < pWrite) {
                System.arraycopy(buffer, (pRead+1), data, srcpos, count);
                pRead += count;
            }else{
                int tailReadCap = 0;
                tailReadCap = rbCapacity-pRead-1;
                if (count <= tailReadCap) {
                    System.arraycopy(buffer, pRead+1, data, srcpos, count);
                    pRead += count;
                    if (pRead == rbCapacity) {
                        pRead= -1;
                    }
                }else {
                    System.arraycopy(buffer, pRead+1, data, srcpos, tailReadCap);
                    pRead= -1;
                    System.arraycopy(buffer, pRead+1, data, srcpos+tailReadCap, count-tailReadCap);
                    pRead += count-tailReadCap;
                }
            }
        }catch (Exception e){
            System.out.printf("pRead+1 : %d, srcpos: %d, count: %d" , pRead+1, srcpos, count);
            e.printStackTrace();
            return -1;
        }

        return count;
    }
}

