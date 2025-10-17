package nz.ac.massey.se159251.assign251_2;

import org.apache.log4j.*;
import org.junit.jupiter.api.*;
import org.apache.log4j.spi.LoggingEvent;

import java.io.IOException;
import java.util.*;

public class StressTest {
    //number of log entries for each config
    private static final int NumLogs = 10_000;

    //list of maxsize values to test
    private static final int[] MaxSizes = {10,100,1_000,10_000};

    @BeforeAll
    static void setup(){
        System.out.println("Start stress testing...");
    }

    @Test
    void compareAppendersAndLayouts()throws IOException, InterruptedException{
        for (int maxsize:MaxSizes){
            runTestConfiguration("ArrayList", new ArrayList<>(), maxsize);
            runTestConfiguration("LinkedList", new LinkedList<>(),maxsize);
        }
        System.out.println("Attach VisualVM now...");

        Thread.sleep(30_000);

    }
    //runs a test config for a list and maxsize
    private void runTestConfiguration(String listTYpe, List<LoggingEvent> list, int maxsize) throws IOException, InterruptedException {
        Layout velLayout = new VelocityLayout("[$p] $c $d: $m$n");
        Layout patLayout = new PatternLayout("[%p] %c %d: %m%n" );

        Thread.sleep(5000);// 5s pause between tests

        //create memory appender with dependency injection
        MemAppender memAppender = MemAppender.getInstance(list, velLayout);
        memAppender.setMaxSize(maxsize);

        Logger velocityLogger = Logger.getLogger("velocityLogger-"+ listTYpe + "-" + maxsize);
        Logger patternLogger = Logger.getLogger("patternlogger-" +listTYpe + "-" +maxsize);

        velocityLogger.removeAllAppenders();
        patternLogger.removeAllAppenders();

        velocityLogger.addAppender(memAppender);
        patternLogger.addAppender(new ConsoleAppender(patLayout));

        //run performance tests
        System.out.printf("%n-- Testing %s with maxsize %d --%n ", listTYpe, maxsize);
        measurePerformance("MemAppender + VelocityLayout", velocityLogger);
        measurePerformance("ConsoleAppender + PatternLayout", patternLogger);

        // FileAppender
        FileAppender fileAppender = new FileAppender(patLayout, "stress_test.log", false);
        Logger fileLogger = Logger.getLogger("fileLogger-" + listTYpe + "-" + maxsize);
        fileLogger.removeAllAppenders();
        fileLogger.addAppender(fileAppender);

        // measure performance
        measurePerformance("FileAppender + PatternLayout", fileLogger);

        fileAppender.close();


        memAppender.close();
    }
//measures time and memory for one logging run
    private void measurePerformance(String descript, Logger logger) {
        Runtime runtime = Runtime.getRuntime();
        System.gc();
        long beforeUsedMem = runtime.totalMemory() - runtime.freeMemory();
        long startTime = System.nanoTime();

        for (int i = 0; i < NumLogs; i++){
            logger.info("log msg num" + i);
        }
        long elapsed = (System.nanoTime() - startTime)/ 1_000_000;//ms
        long afterUsedMem = runtime.totalMemory() - runtime.freeMemory();
        long usedMem = (afterUsedMem - beforeUsedMem)/1024;//KB

        System.out.printf("%-40s time=%6d ms memory=%,8d KB%n", descript, elapsed, usedMem);
    }
}
