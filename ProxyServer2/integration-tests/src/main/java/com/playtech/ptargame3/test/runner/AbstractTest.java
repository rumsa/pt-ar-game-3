package com.playtech.ptargame3.test.runner;

import com.playtech.ptargame3.api.ProxyMessageFactory;
import com.playtech.ptargame3.api.ProxyMessageParser;
import com.playtech.ptargame3.common.callback.CallbackHandlerImpl;
import com.playtech.ptargame3.common.io.NioServerConnector;
import com.playtech.ptargame3.common.io.NioServerListener;
import com.playtech.ptargame3.common.message.MessageParser;
import com.playtech.ptargame3.common.task.TaskExecutorImpl;
import com.playtech.ptargame3.common.task.TaskFactory;
import com.playtech.ptargame3.common.task.TaskFactoryImpl;
import com.playtech.ptargame3.server.LogicResourcesImpl;
import com.playtech.ptargame3.server.ProxyConnectionFactory;
import com.playtech.ptargame3.server.database.DatabaseAccessImpl;
import com.playtech.ptargame3.server.registry.GameRegistry;
import com.playtech.ptargame3.server.registry.ProxyClientRegistry;
import com.playtech.ptargame3.server.registry.ProxyLogicRegistry;
import com.playtech.ptargame3.test.TestConnectionFactory;
import com.playtech.ptargame3.test.TestLogicRegistry;
import com.playtech.ptargame3.test.TestLogicResources;
import com.playtech.ptargame3.test.TestLogicResourcesImpl;
import com.playtech.ptargame3.test.registry.GameRegistryStub;
import com.playtech.ptargame3.test.registry.TestSleepManager;
import com.playtech.ptargame3.test.scenario.ScenarioFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class AbstractTest {

    protected MessageParser messageParser;
    private ScheduledExecutorService maintenanceService;

    protected NioServerListener proxy;
    protected CallbackHandlerImpl proxyCallbackHandler;
    protected TaskExecutorImpl proxyTaskExecutor;

    protected NioServerConnector connector;
    protected CallbackHandlerImpl connectorCallbackHandler;
    protected TestLogicResources connectorLogicResources;
    protected TaskExecutorImpl connectorTaskExecutor;

    protected ScenarioFactory scenarioFactory;
    protected ScenarioRunner scenarioRunner;

    public AbstractTest() {
        maintenanceService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "back-" + count.addAndGet(1));
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    protected void setupMessageParser() {
        if (messageParser == null) {
            ProxyMessageFactory messageFactory = new ProxyMessageFactory();
            messageFactory.initialize();
            messageParser = new ProxyMessageParser(messageFactory);
        }
    }

    protected void setupServer() throws Exception {
        // initialize parser
        setupMessageParser();

        // callback and task processing
        ProxyClientRegistry clientRegistry = new ProxyClientRegistry();
        proxyCallbackHandler = new CallbackHandlerImpl(clientRegistry, maintenanceService);
        proxyCallbackHandler.start();
        proxyTaskExecutor = new TaskExecutorImpl("te", 2);
        ProxyLogicRegistry logicRegistry = new ProxyLogicRegistry();
        TaskFactory taskFactory = new TaskFactoryImpl(proxyTaskExecutor, logicRegistry);
        GameRegistry gameRegistry = new GameRegistry(maintenanceService);
        DatabaseAccessImpl databaseAccess = new DatabaseAccessImpl(maintenanceService);
        databaseAccess.setup();
        LogicResourcesImpl logicResources = new LogicResourcesImpl(proxyCallbackHandler, messageParser, clientRegistry, gameRegistry, taskFactory, databaseAccess);
        logicRegistry.initialize(logicResources);
        ProxyConnectionFactory connectionFactory = new ProxyConnectionFactory(messageParser, proxyCallbackHandler, clientRegistry, gameRegistry, taskFactory);

        // setup server
        proxy = new NioServerListener(connectionFactory, 8000);
        new Thread(proxy.start(),"proxy").start();
    }

    protected void stopServer() {
        proxy.stop();
        proxyCallbackHandler.stop();
        proxyTaskExecutor.stop();
    }

    protected void setupConnector() {
        // initialize parser
        setupMessageParser();

        // callback and task processing
        ProxyClientRegistry clientRegistry = new ProxyClientRegistry();
        connectorCallbackHandler = new CallbackHandlerImpl(clientRegistry, maintenanceService);
        connectorCallbackHandler.start();
        TestLogicRegistry testLogicRegistry = new TestLogicRegistry();
        connectorTaskExecutor = new TaskExecutorImpl("ct", 2);
        TaskFactory connectorTaskFactory = new TaskFactoryImpl(connectorTaskExecutor, testLogicRegistry);
        TestSleepManager testSleepManager = new TestSleepManager(maintenanceService);
        testSleepManager.start();
        connectorLogicResources = new TestLogicResourcesImpl(connectorCallbackHandler, messageParser, clientRegistry, new GameRegistryStub(), testSleepManager);
        testLogicRegistry.initialize(connectorLogicResources);
        TestConnectionFactory connectorFactory = new TestConnectionFactory(messageParser, connectorCallbackHandler, clientRegistry, connectorTaskFactory);

        // setup connector
        connector = new NioServerConnector(connectorFactory);
        new Thread(connector.start(),"connector").start();
    }

    protected void stopConnector() {
        connector.stop();
        connectorCallbackHandler.stop();
        connectorTaskExecutor.stop();
    }

    protected InetSocketAddress getServerAddress() {
        return new InetSocketAddress("127.0.0.1", 8000);
    }

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignore) {}
    }

    protected void setupScenarioRunner() {
        this.scenarioFactory = new ScenarioFactory(connectorTaskExecutor, connectorLogicResources, connector);
        this.scenarioRunner = new ScenarioRunner(scenarioFactory);
    }
}
