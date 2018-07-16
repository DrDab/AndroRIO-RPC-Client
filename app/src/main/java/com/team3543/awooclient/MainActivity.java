package com.team3543.awooclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private AwooListener furry;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.textView1);

        Thread thread = new Thread()
        {
            @Override
            public void run()
            {
                for(;;)
                {
                    try
                    {
                        Thread.sleep(50);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Log.d("Msg", DataStore.text);
                            tv.setText(DataStore.text);
                        }
                    });
                }
            }
        };

        thread.start();

        furry = new AwooListener("127.0.0.1", 13970, true);
        furry.run();
        furry.killServer();
        furry.setRunAsServer(true);
    }
}
