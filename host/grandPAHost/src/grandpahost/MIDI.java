/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package grandpahost;


import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;


/**
 *
 * @author Stephan Zapotocky / zactrack.com
 */
public class MIDI
{
    private static List<MidiDevice> ins = new ArrayList<>();
    private static List<MidiDevice> outs = new ArrayList<>();
    private static MidiDevice outputDevice;
    
    
    protected static void Init()
    {
        MidiDevice device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        
        for (int i = 0; i < infos.length; i++) 
        {
            try 
            {
                device = MidiSystem.getMidiDevice(infos[i]);
                Receiver rec = device.getReceiver();

                outs.add(device);
            } 
            catch (MidiUnavailableException e)
            {
                ins.add(device);  
            }
        }
        
        listMidiDevices();
        
        openOutputDevice("LoopBe Internal MIDI");
    }
    
    protected static void listMidiDevices()
    {
        System.out.println("MIDI Inputs\n----------------");
        for(MidiDevice dev : ins)
            System.out.println(dev.getDeviceInfo().getName());
        System.out.println();
        
        System.out.println("MIDI Outputs\n----------------");
        for(MidiDevice dev : outs)
            System.out.println(dev.getDeviceInfo().getName());
        System.out.println();
    }
    
    protected static boolean openOutputDevice(String name)
    {
        for(MidiDevice dev : outs)
        {
            if(dev.isOpen())
                dev.close();
            
            if(dev.getDeviceInfo().getName().equals(name))
            {
                try
                {
                    dev.open();
                    outputDevice = dev;
                    System.out.println("Opened MIDI Output Device '" + name + "'");
                    return true;
                }
                catch(MidiUnavailableException ex)
                {
                    outputDevice = null;
                    return false;
                }            
            } 
        }
        
        return false;
    }
    
    public static void sendNoteMessage(short channel, short note, short velocity, boolean off)
    {
        ShortMessage msg = new ShortMessage();
        
        try
        {
            // Start playing the note Middle C (60), 
            // moderately loud (velocity = 93).
            msg.setMessage(off ? ShortMessage.NOTE_OFF : ShortMessage.NOTE_ON, channel, note, velocity);
  
            sendMessage(msg);
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    private static void sendMessage(ShortMessage msg) throws MidiUnavailableException
    {
        if(outputDevice!=null)
            outputDevice.getReceiver().send(msg, -1);
    }
}