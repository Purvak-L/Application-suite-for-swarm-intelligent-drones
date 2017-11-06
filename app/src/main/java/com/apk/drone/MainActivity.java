package com.apk.drone;

import android.content.Intent;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
        import java.util.Locale;

        import android.app.Activity;
        import android.content.ActivityNotFoundException;
        import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
        import android.speech.RecognizerIntent;
        import android.view.Menu;
        import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;
import android.net.Uri;
import android.os.Bundle;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static android.os.Build.VERSION_CODES.M;


import java.util.HashMap;
import java.util.Map;

import static android.R.attr.data;
import static android.R.attr.port;
import static com.apk.drone.R.id.videobutton;
import static com.apk.drone.R.layout.videofeed;

public class MainActivity extends Activity {

    private TextView txtSpeechInput;
    private Button btnSpeak;
    private Button videob ;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String info_text ;
    private static String ip_ = "192.168.43.30" ;
    private static String port_ = ":8000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);
        btnSpeak = (Button) findViewById(R.id.btnSpeak);
        videob = (Button) findViewById(R.id.videobutton);

        // hide the action bar
        //getActionBar().hide();

        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        videob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, Temp.class);
                startActivity(i);
            }
    });




    }

    /**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    txtSpeechInput.setText(result.get(0));
                    info_text = result.get(0);
                    receive(info_text);
                }
                break;
            }

        }
    }

    public void receive(String info_text)
    {

        DroneTest req = new DroneTest();
        Toast.makeText(getApplicationContext(),info_text,Toast.LENGTH_SHORT).show();
        req.execute(info_text);
    }



    public class DroneTest extends AsyncTask<String, Integer, String> {
        @Override
        protected void onPreExecute() {

        };

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            // Call your web service here
            String targetURL = "http://"+ip_+port_+"/" ;
            String urlParameters = params[0] ;
            HttpURLConnection connection = null;

            try {
                //Create connection

                URL url = new URL(targetURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length",
                        Integer.toString(urlParameters.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream (
                        connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
                String line;
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                Toast.makeText(getApplicationContext(),"Connection success!",Toast.LENGTH_LONG).show();

                return response.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            // Update your UI here
            Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
            return;
        }
    }

/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

    /*public void videofeed(View view)
    {
        Intent intent = new Intent(getApplicationContext(), VideoFeed.class);
        startActivity(intent);
    }*/

}



