package cn.happyloves.java.agent;

import java.lang.instrument.Instrumentation;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author zc
 * @date 2021/5/26 19:25
 */
public class MyAgent {
    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("this is an perform monitor agent.");

        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                JvmMonitor.printMemoryInfo();
                JvmMonitor.printGCInfo();
            }
        }, 0, 5000, TimeUnit.MILLISECONDS);
    }

    static class JvmMonitor {
        private static final long MB = 1048576L;

        public static void printMemoryInfo() {
            MemoryMXBean memory = ManagementFactory.getMemoryMXBean();
            MemoryUsage headMemory = memory.getHeapMemoryUsage();

            String info = String.format("\ninit: %s\t max: %s\t used: %s\t committed: %s\t use rate: %s\n",
                    headMemory.getInit() / MB + "MB",
                    headMemory.getMax() / MB + "MB", headMemory.getUsed() / MB + "MB",
                    headMemory.getCommitted() / MB + "MB",
                    headMemory.getUsed() * 100 / headMemory.getCommitted() + "%"

            );

            System.out.print(info);

            MemoryUsage nonheadMemory = memory.getNonHeapMemoryUsage();

            info = String.format("init: %s\t max: %s\t used: %s\t committed: %s\t use rate: %s\n",
                    nonheadMemory.getInit() / MB + "MB",
                    nonheadMemory.getMax() / MB + "MB", nonheadMemory.getUsed() / MB + "MB",
                    nonheadMemory.getCommitted() / MB + "MB",
                    nonheadMemory.getUsed() * 100 / nonheadMemory.getCommitted() + "%"

            );
            System.out.println(info);
        }

        public static void printGCInfo() {
            List<GarbageCollectorMXBean> garbages = ManagementFactory.getGarbageCollectorMXBeans();
            for (GarbageCollectorMXBean garbage : garbages) {
                String info = String.format("name: %s\t count:%s\t took:%s\t pool name:%s",
                        garbage.getName(),
                        garbage.getCollectionCount(),
                        garbage.getCollectionTime(),
                        Arrays.deepToString(garbage.getMemoryPoolNames()));
                System.out.println(info);
            }
        }
    }
}
