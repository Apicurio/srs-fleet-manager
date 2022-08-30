package org.bf2.srs.fleetmanager.it.component;

/**
 * @author Jakub Senko <m@jsenko.net>
 */
public interface Component {

    Environment getEnv();

    void start() throws Exception;

    void stop() throws Exception;

    void restart() throws Exception;

    boolean isRunning();

    boolean isContainer();

    String getName();

    String getStdOut();

    String getStdErr();

    void stopAndCollectLogs(String clazz, String testName) throws Exception;
}
