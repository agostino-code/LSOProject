package com.example.guesstheword.data.DAO;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.example.guesstheword.data.model.Request;
import com.example.guesstheword.data.model.Response;
import com.example.guesstheword.data.model.User;
import com.example.guesstheword.server.JSONGeneratorOf;
import com.example.guesstheword.server.SocketManager;
import org.json.JSONException;

import java.io.IOException;

public class UserDAO {
    /**
     * Generics errors from the server, for istance:
     * - email already existent
     * - username already taken
     * - wrong password
     * - user not found
     */
    public static class ResponseErrorException extends Exception {
        public ResponseErrorException(String message) {
            super(message);
        }
    }

    private User user;

    private SocketManager socketManager;
    boolean socketBound = false;
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SocketManager.LocalBinder binder = (SocketManager.LocalBinder) service;
            socketManager = binder.getService();
            socketBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            socketBound = false;
        }
    };

    /**
     * Constructor that create the connection with the server
     * @param context The Activity who this class came from
     */
    public UserDAO(Context context) {
        Intent intent = new Intent(context, SocketManager.class);
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    /**
     * Called when the user logs in. It retrieves the user from the server
     * @param email
     * @param password
     * @return
     * @throws ResponseErrorException if the server returns an error like "user not found" or "wrong password"
     */
    public void retriveUser(String email, String password) throws ResponseErrorException, JSONException, IOException {
        //TODO per agostino: implement this method. Retrieve user from server
        Request request = new Request("SIGN_IN", new User(email, password));
        socketManager.sendRequest(request.toJson());
        Response response = new Response(socketManager.getResponse());
//        switch()
//        if () {
//            if (response.getResponseType()!="SUCCESS")
//            throw new UserNotFoundException();
//        } else {
//            user = new User(response.getObject());
//        }
    }

    /**
     * Called when the user signs up. It sends the new user to the server
     * @throws ResponseErrorException if the server returns an error like "email already existent" or "username already taken"
     */
    public void sendNewUserToServer(User user) throws ResponseErrorException, JSONException, IOException {
        //TODO per agostino: implement this method. Send user to server
    }

    public User getUser() {
        return user;
    }
}
