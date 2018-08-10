package com.team3543.awooclient;

import android.util.Log;

import com.github.arteam.simplejsonrpc.server.JsonRpcServer;

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

    private JsonRpcServer rpcServer;

    public AwooListener(String IPAddr, int portNum, boolean runAsServer)
    {
        this.IPAddr = IPAddr;
        this.portNum = portNum;
        this.runAsServer = runAsServer;
        rpcServer = new JsonRpcServer();
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
        hThread = new Thread(new Runnable()
        {
            public void run()
            {
                loopMethod();
            }
        }
        );
        hThread.start();
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

                    new Thread(new GenericSocketHandlerThread(sock, rpcServer)).start();
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
                    new GenericSocketHandlerThread(sock, rpcServer).run();
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

    private JsonRpcServer rpcServer;
    private TestRPCService testRPCService;

    public GenericSocketHandlerThread(Socket socket, JsonRpcServer rpcServer)
    {
        this.socket = socket;
        this.rpcServer = rpcServer;
        testRPCService = new TestRPCService();
    }

    public void run()
    {
        try
        {
            Log.d("AwooListener", "Listening to " + socket.getRemoteSocketAddress());
            String receiveMessage = "";
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

                try
                {
                    Log.d("AwooListener", "Waiting for message...");
                    String tmpLn = "";
                    if ((tmpLn = receiveRead.readLine()) != null)
                    {
                        Log.d("AwooListener", "RX: " + tmpLn);
                        receiveMessage = (tmpLn + "\n");
                    }

                    String response = rpcServer.handle(receiveMessage, testRPCService);
                    Log.d("AwooListener", "TX: " + response);
                    bw.write(response + "\n");
                    bw.flush();
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