package com.example.guesstheword.server;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;
import com.example.guesstheword.data.model.Request;
import com.example.guesstheword.data.model.Response;
import com.example.guesstheword.data.model.User;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class SocketManager extends Service {

    private static Socket socket;
    OutputStream os;
    InputStream is;

    // Random number generator.


    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Mi connetto con la Socket", Toast.LENGTH_SHORT).show();

        /*
          New thread to manage the connection
         */
        new Thread(() -> {
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress("192.168.1.178", 3000), 5000);
                os = socket.getOutputStream();
                is = socket.getInputStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
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

    @Override
    public IBinder onBind(Intent intent) {
        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(new IncomingHandler(this));
        return mMessenger.getBinder();
    }

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {
        private final Context applicationContext;

        IncomingHandler(@NotNull Context context) {
            applicationContext = context.getApplicationContext();
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SIGN_IN:
                    Toast.makeText(applicationContext, "Login in corso!", Toast.LENGTH_SHORT).show();
                    Request request = new Request("SIGN_IN", (User) msg.obj);
                    sendRequest(request.toJson());
                    Response response = new Response(getResponse());
                    Toast.makeText(applicationContext, response.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    Messenger mMessenger;




    public void sendRequest(@NotNull String msg) {
        //Thread to manage the connection with the server
        new Thread(() -> {
            byte[] byteArr;
            byteArr = msg.getBytes(StandardCharsets.UTF_8);
            try {
                os.write(byteArr);
                os.flush();
                System.out.println("Data Transmitted OK!");
                os.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

        public String getResponse() {
            //Thread to manage the connection with the server
            final String[] msg = new String[1];
            new Thread(() -> {
                byte[] byteArr;
                byteArr = new byte[1024];
                int readByteCount;
                try {
                    readByteCount = is.read();


                    if (readByteCount == -1)
                        throw new IOException();

                    msg[0] = new String(byteArr, 0, readByteCount, StandardCharsets.UTF_8);
                    System.out.println("Data Received OK!");
                    System.out.println("Message : " + msg[0]);

                    is.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            return msg[0];
    }

        public static final int SIGN_IN = 1;
}
