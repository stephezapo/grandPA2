/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandpahost;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 *
 * @author Stephan Zapotocky / zactrack.com
 */
public class Artnet implements Runnable
{
    private static Artnet instance;
    private static Thread thread;
    private boolean running = true;
    private DatagramSocket socket;
    private InetAddress destination;
    private ArtDmxPacket dmxPacket;
    private DatagramPacket dataPacket;
    
    public static Artnet Get()
    {
        if(instance==null)
        {
            instance = new Artnet();
            thread = new Thread(instance);
            thread.start();
        }
        
        return instance;
    }
    
    public static void Shutdown()
    {
        if(instance!=null)
        {
            try
            {
                instance.shutdown();
                thread.join();
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    private void shutdown()
    {
        running = false;
    }
    
    private Artnet()
    {
        try
        {
            socket = new DatagramSocket();
            destination = InetAddress.getByName("2.0.0.22");
            dmxPacket = new ArtDmxPacket();
            dmxPacket.setUniverse(6, 4);
            dmxPacket.setDMX(new byte[512], 512);
            dataPacket = new DatagramPacket(dmxPacket.getData(), dmxPacket.getData().length, destination, 6454);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        while(running)
        {
            try
            {
                sendArtnet(-1, -1);
                Thread.sleep(20);
            }
            catch(Exception ex)
            {
                System.err.println(ex);
            }
        }
    }
    
    public void sendArtnet(int channel, int value)
    {
        if(channel>0)
        {
            byte[] dmx = dmxPacket.getDmxData();
            dmx[channel-1] = (byte)(value & 0xff);
            dmxPacket.setDMX(dmx, dmx.length);
            //dataPacket.setData(dmxPacket.getData());
        }
        
        try
        {
            socket.send(dataPacket);
        }
        catch(Exception ex)
        {
            System.err.println(ex.toString());
        }
    }
}
