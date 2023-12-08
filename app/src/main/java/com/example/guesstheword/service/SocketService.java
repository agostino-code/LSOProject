package com.example.guesstheword.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;
import com.example.guesstheword.data.SharedPreferencesManager;
import com.example.guesstheword.data.model.Request;
import com.example.guesstheword.data.model.Response;
import com.example.guesstheword.data.model.User;
import org.jetbrains.annotations.NotNull;


public class SocketService extends Service {

    /**
     * Target we publish for clients to send messages to IncomingHandler.
     */
    Messenger mMessenger;
    public static final int SIGN_IN = 0;
    public static final int SIGN_UP = 1;
    TSocket tSocket ;
    //Variable for last request to server
    String lastRequest = "";

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Connessione al server in corso!", Toast.LENGTH_SHORT).show();
        tSocket = new TSocket(new Handler() {
            @Override
            public void handleMessage(@NotNull Message msg) {
                switch (msg.what)
                {
                    case TSocket.SOCKET_CONNECTED:
                        Toast.makeText(getApplicationContext(), "Connessione al server stabilita!", Toast.LENGTH_SHORT).show();
                        break;
                    case TSocket.SOCKET_DISCONNECTED:
                        Toast.makeText(getApplicationContext(), "Connessione al server persa!", Toast.LENGTH_SHORT).show();
                        break;
                    case TSocket.SOCKET_CONNECTION_ERROR:
                        Toast.makeText(getApplicationContext(), "Errore di connessione al server!", Toast.LENGTH_SHORT).show();
                        break;
                    case TSocket.SOCKET_RESPONSE:
//                        Toast.makeText(getApplicationContext(), "Messaggio ricevuto dal server!", Toast.LENGTH_SHORT).show();
                        Response response = new Response((String) msg.obj);
                        if(response.getResponseType().equals("ERROR")) {
                            Toast.makeText(getApplicationContext(),response.getData(), Toast.LENGTH_SHORT).show();
                            if (ServiceManager.getInstance().getResponseListener() != null) {
                                ServiceManager.getInstance().getResponseListener().onResponse(false);
                            }
                        }
                        else{
                            if(response.getResponseType().equals("SUCCESS")){
                                switch (lastRequest){
                                    case "SIGN_IN":
                                        User user = new User(response.getData());
                                        SharedPreferencesManager.getInstance().saveUserData(user);
                                        Toast.makeText(getApplicationContext(), "Login effettuato con successo!", Toast.LENGTH_SHORT).show();
                                        if (ServiceManager.getInstance().getResponseListener() != null) {
                                            ServiceManager.getInstance().getResponseListener().onResponse(true);
                                        }
                                        break;
                                }
                            }
                        }
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
                    Request request = new Request("SIGN_IN", (User) msg.obj);
                    lastRequest = request.getRequestType();
                    tSocket.sendRequest(request.toString());
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }




}
