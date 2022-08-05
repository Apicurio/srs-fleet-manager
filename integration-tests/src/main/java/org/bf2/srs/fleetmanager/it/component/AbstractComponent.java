package org.bf2.srs.fleetmanager.it.component;

import lombok.Getter;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @author Fabian Martinez
 * @author Jakub Senko <m@jsenko.net>
 */
public abstract class AbstractComponent implements Component {

    protected Logger logger;

    @Getter
    protected Environment env;

    @Getter
    protected boolean isRunning = false;

    public AbstractComponent(Logger logger, Environment env) {
        this.logger = logger;
        this.env = env;
    }

    @Override
    public void restart() throws Exception {
        if (!isRunning) {
            logger.info("Component {} is not running. Skipping.", getName());
            return;
        }
        stop();
        start();
    }

    @Override
    public boolean isContainer() {
        return false;
    }

    @Override
    public String getStdOut() {
        return null;
    }

    @Override
    public String getStdErr() {
        return null;
    }

    @Override
    public void stopAndCollectLogs(String clazz, String testName) throws Exception {
        if (!isRunning) {
            logger.info("Component {} is not running. Skipping.", getName());
            return;
        }

        Path logsPath = Paths.get("target/logs/", clazz, testName);

        final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentDate = simpleDateFormat.format(Calendar.getInstance().getTime());
        Files.createDirectories(logsPath);
        //non containerized processes have to be stopped before being able to read log output
        if (!isContainer()) {
            try {
                stop();
                Thread.sleep(3000);
            } catch (Exception e) {
                logger.error("Error stopping process " + getName(), e);
            }
        }
        if (getStdOut() != null && !getStdOut().isEmpty()) {

            Path filePath = logsPath.resolve(currentDate + "-" + getName() + "-" + "stdout.log");
            logger.info("Storing logs to {}", filePath);
            Files.write(filePath, getStdOut().getBytes(StandardCharsets.UTF_8));
            String stdErr = getStdErr();
            if (stdErr != null && !stdErr.isEmpty()) {
                Path stderrFile = logsPath.resolve(currentDate + "-" + getName() + "-" + "stderr.log");
                Files.write(stderrFile, stdErr.getBytes(StandardCharsets.UTF_8));
            }
        }
        if (isContainer()) {
            try {
                stop();
            } catch (Exception e) {
                logger.error("Error stopping process " + getName(), e);
            }
        }
    }
}
