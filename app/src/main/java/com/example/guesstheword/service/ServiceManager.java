package com.example.guesstheword.service;

import android.os.Messenger;

public class ServiceManager {
    private static ServiceManager instance = null;
    private Messenger mService = null;
    private boolean bound;

    private ServiceManager() {
        // Private constructor to prevent instantiation
    }

    public static synchronized ServiceManager getInstance() {
        if (instance == null) {
            instance = new ServiceManager();
        }
        return instance;
    }

    public void setService(Messenger service) {
        this.mService = service;
    }

    public Messenger getService() {
        return mService;
    }

    public void setBound(boolean bound) {
        this.bound = bound;
    }

    public boolean isBound() {
        return bound;
    }

    public interface ResponseListener {
        void onResponse(boolean success);
    }

    private ResponseListener responseListener;

    public void setResponseListener(ResponseListener listener) {
        this.responseListener = listener;
    }

    public ResponseListener getResponseListener() {
        return responseListener;
    }
}