package com.unina.guesstheword.service;

import android.content.Context;
import android.net.wifi.WifiManager;
import com.unina.guesstheword.Constants;
import com.unina.guesstheword.GuessTheWordApplication;
import com.unina.guesstheword.control.GameChatController;
import com.unina.guesstheword.data.model.Game;
import com.unina.guesstheword.data.model.GameChatResponse;
import com.unina.guesstheword.data.model.Response;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.CompletableFuture;

public class MulticastServer extends Thread{
    protected MulticastSocket socket;
    protected WifiManager.MulticastLock lock;

    String address;
    protected byte[] buf = new byte[1024];

    private final InetAddress group;

    public MulticastServer(String address) {
        this.address = address;
        WifiManager wifi = (WifiManager) GuessTheWordApplication.getInstance()
                .getCurrentActivity().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            lock = wifi.createMulticastLock("HelloAndroid");
            lock.acquire();
        }
        try {
            group = InetAddress.getByName(address);
            socket = new MulticastSocket(Constants.PORT);
            socket.joinGroup(group);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.start();
    }

    public void run() {
        try {
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                GameChatResponse gameChatResponse = new GameChatResponse(received);
                GameChatController.getInstance().listenServer(gameChatResponse);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMessages(String message) {
        CompletableFuture.runAsync(() -> {
            try {
                byte[] buf = message.getBytes();
                DatagramPacket dp = new DatagramPacket(buf,0,buf.length,group,Constants.PORT);
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
            this.interrupt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
