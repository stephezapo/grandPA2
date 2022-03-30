package grandpahost;


import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;
import javax.swing.UIManager;
import org.apache.commons.net.telnet.TelnetClient;


public class GrandPAHost implements Runnable
{
    private static TelnetClient client;
    private static InputStream in;
    private static OutputStream out;
    
    public static void main(String[] args)
    {
        MIDI.Init();
        ConnectTelnet();

        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception ex)
        {
            
        }
        
        //new Window(out).setVisible(true);
        new MidiWindow().setVisible(true);
    }
    
    private static void ConnectTelnet()
    {
        try
        {
            client = new TelnetClient();
            
            client.connect("127.0.0.1", 30000);
            client.setKeepAlive(true);
            in = client.getInputStream();
            out = client.getOutputStream();
            
            new Thread(new GrandPAHost()).start();

            Send("Login \"web\" \"pass\"", false);
            System.out.println("Logged into TelNet");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected static void SendKey(Constants.HardwareKey key, boolean pressed)
    {
        try
        {
            String command;
            if(pressed)
            {
                command = "LUA \"gma.canbus.hardkey(" + key.getValue() + ", true, false)\"\n";
            }
            else
            {
                command = "LUA \"gma.canbus.hardkey(" + key.getValue() + ", false, false)\"\n";
            }

            out.write(command.getBytes());
            out.flush();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    protected static void Send(String command, boolean lua)
    {
        try
        {
            if (lua)
            {
                command = "Lua " + "\"" + command + "\"";
            }
            command += "\n";
            out.write(command.getBytes());
            out.flush();
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private static void artnetTest()
    {
        try
        {
            DatagramSocket socket = new DatagramSocket();
            byte[] data = new byte[512];
            
            ArtDmxPacket packet = new ArtDmxPacket();
            packet.setUniverse(0, 1);
            
            while(true)
            {
                Arrays.fill(data, (byte)(255 & 0xff));
                packet.setDMX(data, 512);
                socket.send(new DatagramPacket(packet.getData(), 0, packet.getData().length, InetAddress.getByName("127.0.0.1"), 6454));
                
                Arrays.fill(data, (byte)(0 & 0xff));
                packet.setDMX(data, 512);
                socket.send(new DatagramPacket(packet.getData(), 0, packet.getData().length, InetAddress.getByName("127.0.0.1"), 6454));
                
                Thread.sleep(500);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    public void run()
    {
        try
        {
            byte[] buff = new byte[1024];
            int ret_read = 0;

            do
            {
                ret_read = in.read(buff);
                if(ret_read > 0)
                {
                    String data = new String(buff, 0, ret_read);
                    if(data.equals(""))
                        return;

                    System.out.print(data);
                }
            }
            while (ret_read >= 0);
        }
        catch (Exception e)
        {
            System.err.println("Exception while reading socket:" + e.getMessage());
        }
    }
}