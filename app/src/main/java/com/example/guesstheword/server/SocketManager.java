package com.example.guesstheword.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;


public class SocketManager extends Service {

    private static Socket socket;
    OutputStream os;
    InputStream is;

    private final IBinder binder = new LocalBinder();
    // Random number generator.

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SocketManager getService() {
            // Return this instance of LocalService so clients can call public methods.
            return SocketManager.this;
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        try {

            // socket = new Socket("localhost", 5001);

            socket = new Socket();
            System.out.println("Server Connecting..");
            socket.connect(new InetSocketAddress("localhost", 2000));
            System.out.println("Server Connection OK!");


            is = socket.getInputStream();

            os = socket.getOutputStream();


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    public void sendRequest(@NotNull String msg) throws IOException {
        byte[] byteArr;
        byteArr = msg.getBytes(StandardCharsets.UTF_8);
        os.write(byteArr);
        os.flush();
        System.out.println("Data Transmitted OK!");
        os.close();
    }

    public String getResponse() throws IOException {
        byte[] byteArr;
        byteArr = new byte[1024];
        int readByteCount = is.read();

        if(readByteCount == -1)
            throw new IOException();

        String msg = new String(byteArr, 0, readByteCount, StandardCharsets.UTF_8);
        System.out.println("Data Received OK!");
        System.out.println("Message : " + msg);

        is.close();
        return msg;
    }
}
