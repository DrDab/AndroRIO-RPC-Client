package com.team3543.awooclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    private AwooListener furry;

    private TextView tv;

    public static final int PORT = 13970;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String prevText = "";

        tv = (TextView) findViewById(R.id.textView1);

        furry = new AwooListener("127.0.0.1", PORT, true);
        furry.run();

    }
}
