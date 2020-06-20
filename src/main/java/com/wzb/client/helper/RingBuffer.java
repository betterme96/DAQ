package com.wzb.client.helper;

public class RingBuffer{
    /*
    private final static int DEFAULT_SIZE  = 1024;
    private byte[] buffer;
    private int pRead = 0;
    private int pWrite = 1;
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
    public int canRead()
    {
        if(pRead < pWrite){
            return pWrite - pRead - 1;
        }

        return rbCapacity - (pRead - pWrite) -1;
    }
    public int canWrite()
    {
        return rbCapacity - canRead() - 2;
    }

    public int write( byte[]data, int srcpos, int count) throws InterruptedException
    {
        if(count > canWrite()){
            System.out.printf("no space to write:space:%d , count:%d; \n",canWrite(),count);
            int waitTime = 0;
            while ( count > canWrite())
            {
                Thread.sleep(1000);
                waitTime++;
                if(waitTime > time){
                    System.out.println("Write Over Time");
                    return -1;
                }
            }
            if(pRead < pWrite){
                int tailAddCap = rbCapacity - pWrite;
                if(count <= tailAddCap){
                    System.arraycopy(data, srcpos, buffer, pWrite, count);
                    pWrite += count;
                    if(count == bufferSize){
                        pWrite = 0;
                    }
                }else{
                    System.arraycopy(data, srcpos, buffer, pWrite, tailAddCap);
                    pWrite = 0;
                    System.arraycopy(data, srcpos+tailAddCap, buffer, pWrite, count - tailAddCap);
                    pWrite += count - tailAddCap;
                }
            }else{
                System.arraycopy(data, srcpos, buffer, pWrite, count);
                pWrite += count;
            }
        }
        return count;
    }


    public int read(byte[] data,int srcpos ,int count) throws InterruptedException {
        if(canRead() < count){
            System.out.printf("no data to read:dataSize:%d , count:%d; \n",canRead(),count);
            int waitTime = 0;
            while (canRead() < count){
                Thread.sleep(1000);
                waitTime++;
                if(waitTime > time){
                    System.out.println("read time over");
                    return -1;
                }
            }
        }

        if(pRead < pWrite){
            System.arraycopy(buffer, pRead+1, data, srcpos, count);
            pRead += count;
        }else{
            int tailReadCap = bufferSize - pRead - 1;
            if(count <= tailReadCap){
                System.arraycopy(buffer, pRead+1, data, srcpos, count);
                pRead += count;
                if (pRead == bufferSize){
                    pRead = -1;
                }
            }else{
                System.arraycopy(buffer, pRead+1, data, srcpos, tailReadCap);
                pRead = -1;
                System.arraycopy(buffer, pRead+1, data, srcpos+tailReadCap, count - tailReadCap);
                pRead += count - tailReadCap;
            }
        }
        return count;
    }

     */
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
        this.time=5;
    }
    public RingBuffer(int initSize,int time){
        this.bufferSize = initSize;
        this.buffer = new byte[bufferSize];
        this.time=time;
        rbCapacity = bufferSize;
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

    public int read(byte[] data,int srcpos ,int count) throws InterruptedException {
        if(count > canRead()) {
            System.out.printf("no data to read:dataSize:%d , count:%d; \n",canRead(),count);
            int waitTime = 0;
            while (count > canRead()) {
                Thread.sleep(1000);
                waitTime++;
                //sop("write----sleep:"+i);
                if(waitTime>time) {
                    System.out.print("Read Over Time ");
                    if(canRead() > 0){
                        int lastRead = canRead();
                        System.out.printf("Read Over Time and read the last %d bytes data; \n", lastRead);
                        System.arraycopy(buffer, pRead+1, data, srcpos, lastRead);
                        pRead += lastRead;
                        return  lastRead;
                    }else{
                        System.out.println("Read Over Time and no data to read");
                        return -1;
                    }
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
        return count;
    }
    public int write(byte[]data, int srcpos,int count) throws InterruptedException
    {
        int waitTime = 0;
        while(count > canWrite()){
            System.out.printf("no space to write:space:%d , count:%d; \n",canWrite(),count);
            Thread.sleep(1000);
            waitTime++;
            //sop("read----sleep:"+i);
            if(waitTime > time) {
                System.out.println("Write Over Time");
                return -1;
            }
        }

        if (pRead <pWrite) {
            int tailWriteCap = 0;
            tailWriteCap = rbCapacity - pWrite;
            if (count <= tailWriteCap) {
                System.arraycopy(data,srcpos,buffer ,pWrite, count);
                pWrite += count;
                if (pWrite == rbCapacity) {
                    pWrite = 0;
                }
            } else {
                System.arraycopy(data,srcpos,buffer ,pWrite, tailWriteCap);
                pWrite = 0;
                System.arraycopy(data,srcpos+tailWriteCap,buffer ,pWrite, count-tailWriteCap);
                pWrite += count - tailWriteCap;
            }
        } else {
            System.arraycopy(data,srcpos,buffer ,pWrite, count);
            pWrite += count;
            return count;
        }
        return count;
    }

}

