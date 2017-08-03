package com.playtech.ptargame3.test.runner;


import com.playtech.ptargame3.test.scenario.HostGameScenario;

import java.io.IOException;

public class Test1000Connections extends AbstractTest {

    public void testKeepAlive() throws IOException {
        // start server and connector
        setupServer();
        setupConnector();
        setupScenarioRunner();

        // wait a little for startup
        sleep(100);

        scenarioRunner.runScenario(HostGameScenario.class, 20000, 3);

        // wait for finish
        scenarioRunner.waitComplete(100000);

        // shutdown
        stopConnector();
        stopServer();
    }

    public static void main(String[] args) throws Exception {
        new Test1000Connections().testKeepAlive();
    }

}