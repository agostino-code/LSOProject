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
     * Email must be unique
     */
    public static class EmailAlreadyExistentException extends Exception {}

    /**
     * Username is primary key, so it must be unique
     */
    public static class UsernameAlreadyTakenException extends Exception {}

    /**
     * User not found in the database
     */
    public static class UserNotFoundException extends Exception {}

    /**
     * password or email incorrect
     */
    public static class WrongPasswordException extends Exception {}

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
     * @throws UserNotFoundException
     * @throws WrongPasswordException
     */
    public void retriveUser(String email, String password) throws UserNotFoundException, WrongPasswordException, JSONException, IOException {
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
     * @throws EmailAlreadyExistentException
     * @throws UsernameAlreadyTakenException
     */
    public void sendNewUserToServer(User user) throws EmailAlreadyExistentException, UsernameAlreadyTakenException {
        //TODO per agostino: implement this method. Send user to server
    }

    public User getUser() {
        return user;
    }
}
