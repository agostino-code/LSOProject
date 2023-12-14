package com.example.guesstheword.service;

import android.os.Messenger;
import com.example.guesstheword.data.model.Response;

import java.util.concurrent.CompletableFuture;

public class ServiceManager {
    private static ServiceManager instance = null;
    private Messenger mService = null;
    private boolean bound;

    private CompletableFuture<Response> responseFuture;

    public CompletableFuture<Response> getResponseFuture() {
        responseFuture = new CompletableFuture<>();
        return responseFuture;
    }

    public void completeResponseFuture(Response response) {
        if (responseFuture != null) {
            responseFuture.complete(response);
        }
    }
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


}