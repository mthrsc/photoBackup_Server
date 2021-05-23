/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mth.photobackup2.GUI;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import javax.swing.JLabel;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author mth
 */
public class IPRequests extends Thread {

    private JLabel localIPText;
    private JLabel publicIPText;

    private Thread t;
    private String threadName;

    public IPRequests(String threadName, JLabel localIPText, JLabel publicIPText) {
        this.threadName = threadName;
        this.localIPText = localIPText;
        this.publicIPText = publicIPText;
        t = new Thread(this, this.threadName);
        System.out.println("Creating thread " + this.threadName);
    }

    public void start() {
        System.out.println("Starting " + threadName);
        t.start();
    }

    @Override
    public void run() {
        while (true) {
            localIPText.setText(getLocalIP());
//            publicIPText.setText(getPublicIP());
            publicIPText.setText("Available in final version");
            try {
                Thread.sleep(600000);   //Verify IPs every 10 minutes
            } catch (InterruptedException ex) {
                System.out.println("IPRequests error thread sleep");
            }
        }
    }

    private String getLocalIP() {
        String localIP = new String();
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            localIP = String.valueOf(inetAddress.getHostAddress());
        } catch (UnknownHostException ex) {
            System.out.println("IPRequests error getLocalIP");
        }
        return localIP;
    }

    private String getPublicIP() {
        String publicIP = new String();
        try {
            HttpURLConnection conn = (HttpURLConnection) (new URL("http://ipv4bot.whatismyipaddress.com").openConnection());
            InputStream is = conn.getInputStream();
            publicIP = IOUtils.toString(is);
            is.close();
        } catch (Exception e) {
            System.out.println("IPRequests error getPublicIP");
            e.printStackTrace();
        }
        return publicIP;
    }
}
