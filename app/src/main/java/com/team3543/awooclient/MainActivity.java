package com.team3543.awooclient;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener
{
    private AwooListener furry;

    private TextView tv;

    private TextToSpeech tts;

    private boolean debugMsgSaid = false;

    public static final int PORT = 13970;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String prevText = "";

        tts = new TextToSpeech(this, this);

        tts.setLanguage(Locale.US);

        tv = (TextView) findViewById(R.id.textView1);

        DataStore.text = ("IP: " + Utils.getIPAddress(true) + ":" + PORT);
        DataStore.prevText = ("IP: " + Utils.getIPAddress(true) + ":" + PORT);

        furry = new AwooListener("127.0.0.1", PORT, true);
        furry.run();

    }

    @Override
    public void onInit(int i)
    {
        if(i == TextToSpeech.SUCCESS)
        {
            tts.setLanguage(Locale.ENGLISH);
            tts.setPitch(1);
            if(!debugMsgSaid)
            {
                String deviceIP = Utils.getIPAddress(true);
                String separatedPortNum = "";
                String portStr = PORT + "";
                for(int idx = 0; idx < portStr.length(); idx++)
                {
                    separatedPortNum += (portStr.substring(idx, idx + 1) + " ");
                }
                tts.speak("Awoo Client initialized. My eye pee address is currently " + deviceIP + ". I am currently listening on port number " + separatedPortNum + ".", TextToSpeech.QUEUE_FLUSH, null);
                debugMsgSaid = true;
            }
        }
    }
}
