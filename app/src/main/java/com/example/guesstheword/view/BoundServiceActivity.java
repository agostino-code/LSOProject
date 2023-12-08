package com.example.guesstheword.view;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;
import androidx.appcompat.app.AppCompatActivity;
import com.example.guesstheword.control.Controller;
import com.example.guesstheword.service.ServiceManager;

public abstract class BoundServiceActivity extends AppCompatActivity {

    /** Messenger for communicating with the service. */
    protected Messenger mService = null;

    /** Flag indicating whether we have called bind on the service. */
    protected boolean bound;

    /* Instance of controller */
    protected Controller controller = Controller.getInstance();

    /**
     * Class for interacting with the main interface of the service.
     */
    private final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            bound = true;
            ServiceManager.getInstance().setService(mService);
            ServiceManager.getInstance().setBound(bound);
        }

        public void onServiceDisconnected(ComponentName className) {
            mService = null;
            bound = false;
            ServiceManager.getInstance().setService(mService);
            ServiceManager.getInstance().setBound(bound);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, getServiceClass()), mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(mConnection);
            bound = false;
        }
    }

    protected abstract Class<?> getServiceClass();
}