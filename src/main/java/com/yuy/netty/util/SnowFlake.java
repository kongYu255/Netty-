package com.yuy.netty.util;

public class SnowFlake {

    // 开始使用的时间戳
    private final static long START_TIME_STAMP = 1600396658670L;

    private final static long SEQUENCE_BIT = 12;
    private final static long MACHINE_BIT = 5;
    private final static long DATACENTER_BIT = 5;

    private final static long SEQUENCE_MAX_VALUE = ~(-1L << SEQUENCE_BIT);
    private final static long MACHINE_MAX_VALUE = ~(-1L << MACHINE_BIT);
    private final static long DATACENTER_MAX_VALUE = ~(-1L << DATACENTER_BIT);

    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = MACHINE_LEFT + MACHINE_BIT;
    private final static long STARTTIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long dataCenterId;
    private long machineId;
    private long sequenceId;

    private long lastTimestamp = -1L;

    public SnowFlake(long dataCenterId, long machineId) {
        if (dataCenterId > DATACENTER_MAX_VALUE || dataCenterId < 0) {
            throw new IllegalArgumentException("参数异常");
        }
        if (machineId > MACHINE_MAX_VALUE || machineId < 0) {
            throw new IllegalArgumentException("参数异常");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long currTime = getNow();
        if (currTime < lastTimestamp) {
            throw new RuntimeException("时钟异常") ;
        }

        if (currTime == lastTimestamp) {
            sequenceId = (sequenceId + 1) & SEQUENCE_MAX_VALUE;
            if (sequenceId == 0) {
                currTime = getNextMill();
            }
        }
        else {
            sequenceId = 0;
        }

        lastTimestamp = currTime;

        return ((currTime - START_TIME_STAMP) << STARTTIMESTAMP_LEFT)
                | (dataCenterId << DATACENTER_LEFT)
                | (machineId << MACHINE_LEFT)
                | (sequenceId);
    }

    private long getNextMill() {
        long mill = getNow();
        while (mill <= lastTimestamp) {
            mill = getNow();
        }
        return mill;
    }

    private long getNow() {
        return System.currentTimeMillis();
    }

}
