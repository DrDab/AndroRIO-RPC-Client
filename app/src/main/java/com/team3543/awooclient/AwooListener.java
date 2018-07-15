package com.team3543.awooclient;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

@SuppressWarnings("all")
public class AwooListener
{
    private String IPAddr;
    private int portNum;
    private boolean runAsServer;

    private ServerSocket ssock;
    private Socket sock;

    private Thread hThread;

    public AwooListener(String IPAddr, int portNum, boolean runAsServer)
    {
        this.IPAddr = IPAddr;
        this.portNum = portNum;
        this.runAsServer = runAsServer;
    }

    public String getIPAddr()
    {
        return IPAddr;
    }

    public boolean isRunAsServer()
    {
        return runAsServer;
    }

    public int getPortNum()
    {
        return portNum;
    }

    public void setRunAsServer(boolean runAsServer)
    {
        if (this.runAsServer != runAsServer)
        {
            this.runAsServer = runAsServer;
            hThread.stop();
            hThread = runAsServer ? new Thread(new AwooServerHandlerThread((ssock))) : new Thread(new AwooClientHandlerThread((sock)));
            hThread.start();
        }
    }

    public void run()
    {
        try
        {
            if (runAsServer)
            {
                ssock = new ServerSocket(portNum);
            }
            else
            {
                sock = new Socket(IPAddr, portNum);
            }
            hThread = runAsServer ? new Thread(new AwooServerHandlerThread((ssock))) : new Thread(new AwooClientHandlerThread((sock)));
            hThread.start();
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void killServer()
    {
        hThread.stop();
    }
}

@SuppressWarnings("all")
class AwooClientHandlerThread implements Runnable
{
    private Socket sock;

    public AwooClientHandlerThread(Socket sock)
    {
        this.sock = sock;
    }

    public void run()
    {
        new GenericSocketHandlerThread(sock).run();
    }
}

@SuppressWarnings("all")
class AwooServerHandlerThread implements Runnable
{
    private ServerSocket ssock;

    public AwooServerHandlerThread(ServerSocket ssock)
    {
        this.ssock = ssock;
    }

    public void run()
    {
        for(;;)
        {
            Log.d("AwooListener", "GenericSocketHandlerThread started. ");
            Socket sock = null;
            try
            {
                sock = ssock.accept();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            Log.d("AwooListener", "GenericSocketHandlerThread sock launched. " + sock.getRemoteSocketAddress().toString());
            new Thread(new GenericSocketHandlerThread(sock)).start();
        }
    }

}

@SuppressWarnings("all")
class GenericSocketHandlerThread implements Runnable
{
    private Socket socket;
    private BufferedWriter bw;

    public GenericSocketHandlerThread(Socket socket)
    {
        this.socket = socket;
    }

    public void run()
    {
        try
        {
            String receiveMessage;
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            InputStream istream = socket.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
            for(;;)
            {
                try
                {
                    if (socket.isClosed())
                    {
                        break;
                    }
                    receiveMessage = receiveRead.readLine();
                    if(receiveMessage != null)
                    {
                        if(receiveMessage.matches("HELLO"))
                        {
                            bw.write("GOODBYE\n");
                            bw.flush();
                            socket.close();
                        }
                        else
                        {
                            DataStore.text = receiveMessage;
                            bw.write("SUCCESS\n");
                            bw.flush();
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

    }
}
