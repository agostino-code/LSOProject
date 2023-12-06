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
import com.example.guesstheword.data.model.User;
import org.jetbrains.annotations.NotNull;


public class SocketService extends Service {
    TSocket tSocket ;

    @SuppressLint("HandlerLeak")
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getApplicationContext(), "Connessione al server in corso!", Toast.LENGTH_SHORT).show();
        tSocket = new TSocket(new Handler() {
            @Override
            public void handleMessage(@NotNull Message msg) {
                Toast.makeText(getApplicationContext(), "Messaggio ricevuto dal server!", Toast.LENGTH_SHORT).show();
                String message = (String) msg.obj;
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(applicationContext, "Login in corso!", Toast.LENGTH_SHORT).show();
                    Request request = new Request("SIGN_IN", (User) msg.obj);
                    tSocket.sendRequest(request.toString());
//                    Toast.makeText(applicationContext, response, Toast.LENGTH_SHORT).show();
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
    public static final int SIGN_IN = 1;
}
