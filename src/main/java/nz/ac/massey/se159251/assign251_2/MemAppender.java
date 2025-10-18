package nz.ac.massey.se159251.assign251_2;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Layout;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MemAppender extends AppenderSkeleton implements MemAppenderMBean{

    private static MemAppender instance;
    private List<LoggingEvent> events;
    private int maxSize = 1000; //default, older logs deleted
    private long discardedCount = 0;

    // constructor
    private MemAppender(List<LoggingEvent> eventList, Layout layout) {
        this.events = eventList;
        this.layout = layout;
        try{
            MBeanServer server = ManagementFactory.getPlatformMBeanServer();
            ObjectName name = new ObjectName("nz.ac.massey:type=MemAppender,name=" + this.hashCode());
            server.registerMBean(this, name);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //reset instance for testing
    public static void resetInstance(){
        instance = null;
    }

    // singleton accessor
    public static MemAppender getInstance(List<LoggingEvent> list, Layout layout) {
        if (instance == null){
            instance = new MemAppender(list,layout);
        }
        return instance;
    }
    
    //methods for MBEAN
    @Override
    public int getLogsESize(){
        if (instance == null){
            return 0;
        }
            return instance.getCurrentLogs().
                    stream()
                    .filter(e ->e.getRenderedMessage() != null)
                    .mapToInt(e->e.getRenderedMessage().length()).sum();
    }
    @Override
    public int getDiscardedLogs(){
        return instance.getDiscardedLogs();
    }
    @Override
    public String[] getLogMessages(){
        if (instance == null){
            return new String[0];
        }
        return instance.getCurrentLogs().stream().
                map(e->e.getRenderedMessage()).toArray(String[]::new);
    }

    // called automatically by Log4j when there is a log
    @Override
    protected void append(LoggingEvent event) {
        if (events == null) {
            return;
        }
        if (events.size() >= maxSize) {
        events.remove(0);
        discardedCount++;
        }
        events.add(event);
    }

    //return list of logging events
    public List<LoggingEvent> getCurrentLogs(){
        return Collections.unmodifiableList(events);
    }

    //return formated event strings using layout
    public List<String> getEventStrings(){
        if (layout == null) throw new IllegalStateException("no layout is set");
        return events.stream()
                .map(layout::format)
                .toList();
    }

    // print and clear the logs
    public void printlogs(){
        if (layout == null) throw new IllegalStateException("no layout is set");
        for (LoggingEvent e: events){
            System.out.println(layout.format(e));
        }
        events.clear();
    }

    public long getDiscardedCount(){
        return discardedCount;
    }

    public void setMaxSize(int maxSize){
        this.maxSize = maxSize;
    }

    @Override
    public void close() {
        events.clear();
    }

    @Override
    public boolean requiresLayout() {
        return true;
    }
}
