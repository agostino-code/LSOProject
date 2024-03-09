package com.unina.guesstheword.service;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.unina.guesstheword.Constants;
import com.unina.guesstheword.GuessTheWordApplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;

public class MulticastServer{
    protected MulticastSocket socket;
    protected WifiManager.MulticastLock lock;

    protected int port;
    protected byte[] buf = new byte[1024];

    private final InetAddress group;

    public MulticastServer(int port) {
        this.port = port;
        WifiManager wifi = (WifiManager) GuessTheWordApplication.getInstance()
                .getCurrentActivity().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            lock = wifi.createMulticastLock("HelloAndroid");
            lock.acquire();
        }
        try {
            group = InetAddress.getByName(Constants.MULTICAST_IP);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        try {
            socket = new MulticastSocket();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            socket.joinGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        handleMessages();
    }

    public void handleMessages() {
        CompletableFuture.runAsync(() -> {
            try {
                while (true) {
                    DatagramPacket packet = new DatagramPacket(buf,0, buf.length);
                    packet.setAddress(group);
                    packet.setPort(port);
                    socket.receive(packet);
                    String received = new String(packet.getData(), 0, packet.getLength());
                    if ("end".equals(received)) {
                        break;
                    }else{
                        System.out.println(received);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sendMessages(String message) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] buf = message.getBytes();
                DatagramPacket dp = new DatagramPacket(buf,0,buf.length,group,port);
                System.out.println("Sending message: " + message);
                System.out.println("Port: " + port);
                socket.send(dp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).join();
    }

    public void close() {
        try {
            socket.leaveGroup(group);
            socket.close();
            lock.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
