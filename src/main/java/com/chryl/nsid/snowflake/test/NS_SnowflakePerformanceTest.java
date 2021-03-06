package com.chryl.nsid.snowflake.test;

import com.chryl.nsid.snowflake.NS_Snowflake;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class NS_SnowflakePerformanceTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testSingleThread() {
        int n1 = 1000000; // 1百万次
        long[] r1 = runC1N(n1);
        showReport(1, n1, r1);

        int n2 = 10000000; // 1千万次
        long[] r2 = runC1N(n2);
        showReport(1, n2, r2);
    }

    @Test
    public void testC10N10w() throws Exception {
        ConcurrentTestFramework ctf = new ConcurrentTestFramework("C10N10w", true);
        final NS_Snowflake NSSnowflake = new NS_Snowflake(2, 5);
        ConcurrentTestFramework.SummaryReport report = ctf.test(10, 100000, new Runnable() {

            @Override
            public void run() {
                NSSnowflake.nextId();
            }
        });
        report.setAttachment(String.format("wait: %d", NSSnowflake.getWaitCount()));
        System.out.println("C10N10w Report: " + report);
    }

    @Test
    public void testC100N1w() throws Exception {
        ConcurrentTestFramework ctf = new ConcurrentTestFramework("C100N1w", false);
        final NS_Snowflake NSSnowflake = new NS_Snowflake(2, 5);
        ConcurrentTestFramework.SummaryReport report = ctf.test(100, 10000, new Runnable() {

            @Override
            public void run() {
                NSSnowflake.nextId();
            }
        });
        report.setAttachment(String.format("wait: %d", NSSnowflake.getWaitCount()));
        System.out.println("C100N1w Report: " + report);
    }

    @Test
    public void testC50N100w() throws Exception {
        ConcurrentTestFramework ctf = new ConcurrentTestFramework("C50N100w", false);
        final NS_Snowflake NSSnowflake = new NS_Snowflake(2, 5);
        ConcurrentTestFramework.SummaryReport report = ctf.test(50, 1000000, new Runnable() {

            @Override
            public void run() {
                NSSnowflake.nextId();
            }
        });
        report.setAttachment(String.format("wait: %d", NSSnowflake.getWaitCount()));
        System.out.println("C50N100w Report: " + report);
    }

    /**
     * @return time cost in MS, wait count
     */
    private long[] runC1N(int n) {
        NS_Snowflake NSSnowflake = new NS_Snowflake(2, 5);
        long btm = System.currentTimeMillis();
        for (int i = 0; i < n; i++) {
            NSSnowflake.nextId();
        }
        long etm = System.currentTimeMillis();
        long[] r = new long[2];
        r[0] = etm - btm;
        r[1] = NSSnowflake.getWaitCount();
        return r;
    }

    private void showReport(int c, int n, long[] r) {
        long costMS = r[0];
        long qps = (long) (n / (costMS / 1000.0));
        long qpms = n / costMS;
        System.out
                .println(String.format("C%dN%d: costMS=%d, QPS=%d, QPMS:=%d, wait=%d", c, n, costMS, qps, qpms, r[1]));
    }

}
