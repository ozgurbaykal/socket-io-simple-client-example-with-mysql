package com.ozgur.foodorderapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity {

    Button buttonLogin;
    Button buttonRegister;
    EditText username, password;
    TextView textview;

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        textview = findViewById(R.id.textview);

        mSocket.connect();
        mSocket.on("loginResponse", onNewMessage);
        mSocket.on("register_response", onNewMessage);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, " buttonLogin Click ");
                if (username.getText().length() == 0 || password.getText().length() == 0)
                    Toast.makeText(getApplicationContext(), "Lütfen boş alan bırakmayın.", Toast.LENGTH_LONG).show();
                else{
                    try {
                        sendLoginEmit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, " buttonRegister Click ");
                if (username.getText().length() == 0 || password.getText().length() == 0)
                    Toast.makeText(getApplicationContext(), "@string/app_name", Toast.LENGTH_LONG).show();
                else{
                    try {
                        sendRegisterEmit();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    private Socket mSocket;

    {
        try {
            mSocket = IO.socket("ws://192.168.1.121:3000");
        } catch (URISyntaxException e) {
            Log.e(TAG, " mSocket ex: " + e.getMessage());
        }
    }

    private void sendLoginEmit() throws JSONException {

            JSONObject object = new JSONObject();
            object.put("username", username.getText());
            object.put("password", password.getText());
            mSocket.emit("login", object);

    }

    private void sendRegisterEmit() throws JSONException {

        JSONObject object = new JSONObject();
        object.put("userName", username.getText());
        object.put("userPassw", password.getText());
        mSocket.emit("register", object);

    }


    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    Log.i(TAG, " data: " + data);

                    try {
                        textview.setText("Sunucudan Dönen Cevap:\n" + data.getString("message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };
}