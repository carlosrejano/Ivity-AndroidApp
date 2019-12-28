package com.example.ivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText userView;
    private EditText passView;
    private ProgressBar progressbarView;
    private TextView errorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userView = findViewById(R.id.loginUser);
        passView = findViewById(R.id.loginPass);
        progressbarView = findViewById(R.id.pb_loading_indicator);
        errorMessage = findViewById(R.id.errorMessage);
        configureLogin();
    }
    protected void configureLogin(){
        Button loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                URL loginUrl = ParseUtils.buildUrlLogin(userView.getText().toString(), passView.getText().toString());
                new LoginTask().execute(loginUrl);
            }
        });
    }
    public class LoginTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressbarView.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... params) {
            URL searchUrl = params[0];
            String results = null;
            try {
                results = ParseUtils.getResponseFromHttpUrl(searchUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return results;
        }


        @Override
        protected void onPostExecute(String results) {
            progressbarView.setVisibility(View.INVISIBLE);
            if (results != null && !results.equals("")) {

                if (results.contains("No records matching your query were found.")){
                    Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                    errorMessage.setText("Wrong user or password, try again!");

                    errorMessage.setVisibility(View.VISIBLE);
                    if(Build.VERSION.SDK_INT >= 26) {
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                    }else{
                        vibrator.vibrate(200);
                    }
                }else{
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("EXTRA_SESSION_ID", results);
                    startActivity(intent);


                }
            }
        }
    }

}

