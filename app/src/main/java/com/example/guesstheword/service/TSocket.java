package com.example.guesstheword.service;

import android.os.Handler;
import android.os.Message;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TSocket extends Thread{
    private Socket socket;
    private OutputStream outputStream;
    private BufferedReader inputReader;

    private Handler handler;

    // Costruttore
    public TSocket(Handler handler) {
        this.handler = handler;
    }

    // Costanti
    public static final int SOCKET_CONNECTED = 0;
    public static final int SOCKET_DISCONNECTED = 1;
    public static final int SOCKET_CONNECTION_ERROR = 2;
    public static final int SOCKET_RESPONSE = 3;

    @Override
    public void run() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress("172.17.0.1", 3000), 5000);
            handler.sendMessage(Message.obtain(handler, SOCKET_CONNECTED));
            outputStream = socket.getOutputStream();
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            while (true) {
                readResponse();
            }

        } catch (IOException e) {
            handler.sendMessage(Message.obtain(handler, SOCKET_CONNECTION_ERROR));
            //throw new RuntimeException(e);
        } finally {
            closeSocket();
            handler.sendMessage(Message.obtain(handler, SOCKET_DISCONNECTED));
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        closeSocket();
    }

        public void sendRequest(String message) {
            if (outputStream == null) {
                throw new IllegalStateException("Output stream is not initialized yet");
            }
            try {
                outputStream.write((message + "\n").getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void readResponse() {
            try {
                char[] buffer = new char[1024];
                int charsRead = inputReader.read(buffer, 0, 1024);
                String response = new String(buffer, 0, charsRead);
                if (!response.isEmpty()) {
//                    Message msg = new Message();
//                    msg.obj = response;
////                    handler.sendMessage(Message.obtain(handler, SOCKET_RESPONSE, msg));
//                    handler.sendMessage(msg);
                    Message msg = Message.obtain(handler, SOCKET_RESPONSE, response);
                    handler.sendMessage(msg);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    // Chiudi la socket e notifica il thread principale
    public void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
