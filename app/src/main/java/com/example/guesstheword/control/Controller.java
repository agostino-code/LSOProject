package com.example.guesstheword.control;

import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.example.guesstheword.data.SharedPreferencesManager;
import com.example.guesstheword.data.model.User;
import com.example.guesstheword.service.ServiceManager;
import com.example.guesstheword.service.SocketService;

public class Controller {

    //Singleton
    private static Controller instance = null;

    private User user = null;

    private Controller() {
        // Private constructor to prevent instantiation
        /* TODO: Check if the user is logged in
         * If the user is logged in, send the user's data to the server
         * */
//        if(SharedPreferencesManager.getInstance().isUserLoggedIn()){
//            //Send the user's data to the server
//            User user = SharedPreferencesManager.getInstance().getUserData();
//            Message msg = Message.obtain(null, SocketService.SIGN_IN, user);
//            try {
//                ServiceManager.getInstance().getService().send(msg);
//            } catch (RemoteException e) {
//                SharedPreferencesManager.getInstance().deleteUserData();
//                e.printStackTrace();
//            }
//        }
    }

    public static synchronized Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    /**
     * Messenger for communicating with the service.
     */

    public void SignIn(String email, String password) {
        Messenger mService = ServiceManager.getInstance().getService();
        boolean bound = ServiceManager.getInstance().isBound();

        if (!bound) return;

        User user = new User(email, password);
        Message msg = Message.obtain(null, SocketService.SIGN_IN, user);


        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //Sign up
    public void SignUp(String email, String password, String username, int avatar) {
        Messenger mService = ServiceManager.getInstance().getService();
        boolean bound = ServiceManager.getInstance().isBound();

        if (!bound) return;

        user = new User(email, password, username, avatar);
        Message msg = Message.obtain(null, SocketService.SIGN_UP, user);
        try {
            mService.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
