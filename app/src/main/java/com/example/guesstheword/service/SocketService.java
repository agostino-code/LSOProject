package com.example.guesstheword.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.widget.Toast;
import com.example.guesstheword.data.model.JSONData;
import com.example.guesstheword.data.model.Request;
import com.example.guesstheword.data.model.Response;
import org.jetbrains.annotations.NotNull;


public class SocketService extends Service {

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    Messenger mMessenger;

    public static final int SIGN_IN = 0;
    public static final int SIGN_UP = 1;

    static TSocket tSocket;

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Connessione al server in corso!", Toast.LENGTH_SHORT).show();
        tSocket = new TSocket(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NotNull Message msg) {
                switch (msg.what) {
                    case TSocket.SOCKET_CONNECTED:
                        Toast.makeText(getApplicationContext(), "Connessione al server stabilita!", Toast.LENGTH_SHORT).show();
                        break;
                    case TSocket.SOCKET_DISCONNECTED:
                        Toast.makeText(getApplicationContext(), "Connessione al server persa!", Toast.LENGTH_SHORT).show();
                        break;
                    case TSocket.SOCKET_CONNECTION_ERROR:
                        showConnectionErrorPopup("Errore di connessione al server!");
                        break;
                    case TSocket.SOCKET_RESPONSE:
//                        Toast.makeText(getApplicationContext(), "Messaggio ricevuto dal server!", Toast.LENGTH_SHORT).show();
                        Response response = new Response((String) msg.obj);
                        if (response.getResponseType().equals("ERROR"))
                            Toast.makeText(getApplicationContext(), response.getData(), Toast.LENGTH_SHORT).show();
                        ServiceManager.getInstance().completeResponseFuture(response);
                        break;
                }
            }
        });
        tSocket.start();
    }

    @Override
    public void onDestroy() {
        tSocket.closeSocket();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        Toast.makeText(getApplicationContext(), "binding", Toast.LENGTH_SHORT).show();
        mMessenger = new Messenger(new IncomingHandler());
        return mMessenger.getBinder();
    }

    static class IncomingHandler extends Handler{

        IncomingHandler() {
            super(Looper.getMainLooper());
        }
        public String whatToString(int what) {
            switch (what) {
                case SIGN_IN:
                    return "SIGN_IN";
                case SIGN_UP:
                    return "SIGN_UP";
                // Add more cases as needed
                default:
                    return "UNKNOWN";
            }
        }
        @Override
        public void handleMessage(Message msg) {
            Request request;
            request = new Request(whatToString(msg.what), (JSONData) msg.obj);
            tSocket.sendRequest(request.toString());
            //default super

        }
    }

    private void showConnectionErrorPopup(String message) {
        Intent connectionErrorIntent = new Intent("CONNECTION_ERROR");
        connectionErrorIntent.putExtra("msg", message);
        sendBroadcast(connectionErrorIntent);
    }
}