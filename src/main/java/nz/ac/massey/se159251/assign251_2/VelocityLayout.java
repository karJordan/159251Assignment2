package nz.ac.massey.se159251.assign251_2;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.StringWriter;
import java.util.Properties;

public class VelocityLayout extends Layout{

    /* c (logging name), d (date), m (message), p (log level/priority level)
    t (thread), n (line separator)
    * */

    private String pattern = "[$p] $c $d: $m$n";
    private final VelocityEngine engine;

    private VelocityEngine initEngine() {
        Properties props = new Properties();
        props.setProperty("runtime.logsystem.class","org.apache.velocity.runtime.log.NullLogChute");
        VelocityEngine ve = new VelocityEngine();
        ve.init(props);
        return ve;
    }

    public VelocityLayout(String pattern){
        this.pattern = pattern;
        this.engine = initEngine();
    }

    public VelocityLayout(){
        this.engine = initEngine();
    }

    public void setPattern(String pattern){
        this.pattern = pattern;
    }

    @Override
    public String format(LoggingEvent event) {
        VelocityContext ctx = new VelocityContext();
        ctx.put("c", event.getLoggerName());
        ctx.put("d", event.getTimeStamp());
        ctx.put("m", event.getMessage());
        ctx.put("p", event.getLevel());
        ctx.put("t", event.getThreadName());
        ctx.put("n", System.lineSeparator());

        StringWriter writer = new StringWriter();
        engine.evaluate(ctx,writer,"VelocityLayout", pattern);

        return writer.toString();
    }

    @Override
    public boolean ignoresThrowable() {
        return false;
    }

    @Override
    public void activateOptions() {

    }
}
