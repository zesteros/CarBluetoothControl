package com.asde.carbluetoothcontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.asde.carbluetoothcontrol.bt.BluetoothConnectionThread;
import com.asde.carbluetoothcontrol.bt.DeviceDialog;
import com.asde.carbluetoothcontrol.util.GlobalVariable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Button connectButton = (Button) findViewById(R.id.connect_button);
        Button forwardButton = (Button) findViewById(R.id.forward_button);
        Button backwardButton = (Button) findViewById(R.id.backward_button);
        Button rightButton = (Button) findViewById(R.id.right_button);
        Button leftButton = (Button) findViewById(R.id.left_button);


        connectButton.setOnClickListener(this);

        forwardButton.setOnTouchListener(this);
        backwardButton.setOnTouchListener(this);
        rightButton.setOnTouchListener(this);
        leftButton.setOnTouchListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        BluetoothConnectionThread connectionThread = GlobalVariable.getInstance().getConnectionInstance();

        switch (v.getId()) {
            case R.id.connect_button:
                if (isBluetoothEnabled()) {
                    try {
                        if (!GlobalVariable.getInstance().isConnected()) showDialog();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else { //IF BT ISNT ENABLED HIDE UI AND TOAST TO THE USER
                    showToast("ACTIVA TU BLUETOOTH");
                }
                break;
        }
    }

    /**
     * @return true if the bluetooth is enabled, also if is not, request bluetooth activation to the user
     */
    public boolean isBluetoothEnabled() {
        setBluetoothAdapter();
        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                //INSTANTIATE A NEW ACTIVITY FROM SYSTEM
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
                if (btAdapter.isEnabled()) return true;
                else return false;
            } else return true;
        } else return false;
    }


    /**
     * Instantiate Bluetooth adapter
     */
    public void setBluetoothAdapter() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    /**
     * Show device list connect dialog
     */
    private void showDialog() {
        try {
            new DeviceDialog(this).showDeviceListDialog();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param text the id of string resource for show message
     */
    public void showToast(String text) {
        /*Find the root view*/
        View view = findViewById(android.R.id.content);
        /*Show the snackbar*/
        Snackbar.make(view, text, Snackbar.LENGTH_LONG).show();
        //Toast.makeText(getApplicationContext(), getResources().getString(id),
        //      Toast.LENGTH_SHORT).show();

    }
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        BluetoothConnectionThread connectionThread = GlobalVariable.getInstance().getConnectionInstance();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                switch (v.getId()) {
                    case R.id.forward_button:
                        if (connectionThread != null) {
                            connectionThread.write("F");
                        }
                        break;
                    case R.id.backward_button:
                        if (connectionThread != null) {
                            connectionThread.write("B");
                        }
                        break;
                    case R.id.right_button:
                        if (connectionThread != null) {
                            connectionThread.write("R");
                        }
                        break;
                    case R.id.left_button:
                        if (connectionThread != null) {
                            connectionThread.write("L");
                        }
                        break;
                }
                // PRESSED
                return true; // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                // RELEASED
                if (connectionThread != null) {
                        connectionThread.write("O");
                }
                return true; // if you want to handle the touch event
        }
        return false;
    }
}
