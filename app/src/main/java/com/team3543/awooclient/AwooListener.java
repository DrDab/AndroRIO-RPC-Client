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
        this.runAsServer = runAsServer;
        hThread.currentThread().interrupt();
        hThread = null;
        run();
    }

    public void run()
    {
        new Thread(new Runnable()
        {
            public void run()
            {
                loopMethod();
            }
        }
        ).start();
    }

    public void loopMethod()
    {
        try
        {
            if (runAsServer)
            {
                ssock = new ServerSocket(portNum);
                while(!Thread.interrupted())
                {
                    if(ssock.isClosed())
                    {
                        Thread.currentThread().interrupt();
                        break;
                    }

                    try
                    {
                        sock = ssock.accept();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }

                    GenericSocketHandlerThread gsht = new GenericSocketHandlerThread(sock);
                    gsht.run();
                }
            }
            else
            {
                while(!Thread.interrupted())
                {
                    sock = new Socket(IPAddr, portNum);
                    if (sock.isClosed())
                    {
                        Thread.currentThread().interrupt();
                        return;
                    }
                    new GenericSocketHandlerThread(sock).run();
                }
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public void killServer()
    {
        hThread.currentThread().interrupt();
        try
        {
            if(sock != null)
            {
                sock.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        try
        {
            if(ssock != null)
            {
                ssock.close();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

@SuppressWarnings("all")
class GenericSocketHandlerThread implements Runnable
{
    private Socket socket;
    private BufferedWriter bw;

    private ServerSocket serverSocket = null;

    public GenericSocketHandlerThread(Socket socket)
    {
        this.socket = socket;
    }

    public GenericSocketHandlerThread(Socket socket, ServerSocket serverSocket)
    {
        this.socket = socket;
        this.serverSocket = serverSocket;
    }

    public void run()
    {
        try
        {
            String receiveMessage;
            bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            InputStream istream = socket.getInputStream();
            BufferedReader receiveRead = new BufferedReader(new InputStreamReader(istream));
            while(!Thread.interrupted())
            {
                if (socket.isClosed())
                {
                    bw.close();
                    socket.close();
                    Thread.currentThread().interrupt();
                }

                if(serverSocket != null)
                {
                    if (serverSocket.isClosed())
                    {
                        bw.close();
                        socket.close();
                        Thread.currentThread().interrupt();
                    }
                }

                try
                {
                    receiveMessage = receiveRead.readLine();
                    if(receiveMessage != null)
                    {
                        if(receiveMessage.matches("BYE"))
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