package nz.ac.massey.se159251.assign251_2;

public interface MemAppenderMBean {
    String[] getLogMessages();
    int getLogsESize();
    int getDiscardedLogs();
}
