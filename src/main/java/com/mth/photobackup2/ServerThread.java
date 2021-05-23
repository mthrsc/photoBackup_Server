/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2;

import com.mth.photobackup2.Tools.LogTool;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

/**
 *
 * @author mth
 */
public class ServerThread extends Thread {

    private LogTool logTool = new LogTool();
    private int port;
    private Thread t;
    private String threadName;
    private Server server;

    public ServerThread(int port, String threadName) {
        this.port = port;
        this.threadName = threadName;
        t = new Thread(this, this.threadName);
        System.out.println("Creating thread " + this.threadName);
    }

    public void start() {
        System.out.println("-----Starting " + threadName);
        logTool.logToFile("Starting server");
        t.start();
    }

    public void stopServer() {
        logTool.logToFile("stopServer");
        System.out.println("-----Destroy server  " + threadName);
        try {
            server.stop();
        } catch (Exception ex) {
            logTool.logToFile("stopServer: " + ex.getMessage());
        }
    }

    @Override
    public void run() {
        server = new Server(port);

        ServletContextHandler ctx
                = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);

        ctx.setContextPath("/");
        server.setHandler(ctx);

        ServletHolder serHol = ctx.addServlet(ServletContainer.class, "/rest/*");
        serHol.setInitOrder(0);
        serHol.setInitParameter("jersey.config.server.provider.packages",
                "com.mth.photobackup2");

        serHol.setInitParameter("jersey.config.server.provider.classnames",
                "org.glassfish.jersey.jackson.JacksonFeature");
        //MULTIPART registration /!\
		
        serHol.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, MultiPartFeature.class.getCanonicalName());
//        serHol.setInitParameter(ServerProperties.PROVIDER_CLASSNAMES, "org.glassfish.jersey.media.JerseyMediaJsonJackson");

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            logTool.logToFile("Server start Error: " + ex.getMessage());
            System.out.println("--Error starting server");
        } finally {
            server.destroy();
        }
    }
}
