package nz.ac.massey.se159251.assign251_2;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.Layout;

import java.util.Collections;
import java.util.List;

public class MemAppender extends AppenderSkeleton {
    private static MemAppender instance;
    private List<LoggingEvent> events;
    private int maxSize = 1000; //default, older logs deleted
    private long discardedCount = 0;

    // constructor singleton
    private MemAppender(List<LoggingEvent> eventList, Layout layout) {
        this.events = eventList;
        this.layout = layout;
    }

    // singleton accessor
    public static synchronized MemAppender getInstance(List<LoggingEvent> list, Layout layout) {
        if (instance == null) {
            instance = new MemAppender(list, layout);
        }
        return instance;
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
