package com.example.guesstheword.view;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Messenger;

import androidx.appcompat.app.AlertDialog;
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

        ConnectionErrorReceiver connectionErrorReceiver = new ConnectionErrorReceiver();
        IntentFilter filter = new IntentFilter("CONNECTION_ERROR");
        registerReceiver(connectionErrorReceiver, filter);
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

    private class ConnectionErrorReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Handle the connection error here
            // You can show a pop-up, update UI, or perform any other action
            String message = intent.getStringExtra("msg");
            showConnectionErrorPopup(message);
        }
    }

    private void showConnectionErrorPopup(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Errore di connessione");
        builder.setMessage(message);
        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 recreate(); //Restart the service
            }
        });
        builder.setCancelable(false); // Prevent the user from dismissing the dialog by clicking outside of it
        builder.show();
    }
}