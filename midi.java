package midi;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
//import network.*;
import javax.sound.midi.*;
import static javax.sound.midi.ShortMessage.*;

public class midi {
    public static MidiDevice.Info[] midiInfo = MidiSystem.getMidiDeviceInfo();
    public static String devicename = "";
    //lists midi devices 
    public static void listMidiDevices(){
        midiInfo = MidiSystem.getMidiDeviceInfo();
        int i = 0;
        for (MidiDevice.Info info : midiInfo) {
            System.out.println(i + ": " +info.getName());
            MidiDevice e = null;
            try {
                 e = MidiSystem.getMidiDevice(midiInfo[i]);
                i+=1; 
            } catch (MidiUnavailableException ex) {
                System.out.println("Device unavailable");
            }
            try {
                e.getReceiver();
                System.out.println("Valid receiver");
            } catch (MidiUnavailableException ex) {
                System.out.println("Invalid receiver");
            }
            try {
                e.getTransmitter();System.out.println("Valid transmitter.");
            } catch (MidiUnavailableException ex) {
                System.out.println("Invalid transmitter");
            } 
        }
    }
    //returns a midi device based on user's selection
    public static MidiDevice selectDevice (){
        midi.listMidiDevices();
        MidiDevice selectedDevice=null;
        System.out.println("\n Enter number of desired device:");
        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();
        try {
            devicename = midiInfo[i].getName();
            selectedDevice = MidiSystem.getMidiDevice(midiInfo[i]);
        } catch (MidiUnavailableException ex) {
            
        }
        return selectedDevice;
    }
    //sets up the device
    public static void initDevice (MidiDevice device){
        try {
            Transmitter t = device.getTransmitter();
            t.setReceiver(new receiver ());
            
        } catch (MidiUnavailableException ex) {
            System.out.println("Cannot get transmitter.");
        }
        try {
            device.open();
        } catch (MidiUnavailableException ex) {
            System.out.println("Cannot open device.");
        }
    }
    //initializes the device based on a custom receiver (in this case, the network receiver)
    public static void initDevice (MidiDevice device, Receiver receiver){
        try {
            Transmitter t = device.getTransmitter();
            t.setReceiver(receiver);
            
        } catch (MidiUnavailableException ex) {
            System.out.println("Cannot get transmitter.");
        }
        try {
            device.open();
        } catch (MidiUnavailableException ex) {
            System.out.println("Cannot open device.");
        }
    }
    //method to play a midi file (not currently used in this project, but still useful)
    public static void playMidi(MidiDevice device,String file){
        try {
            midi.initDevice(device);
            Sequencer sequencer = MidiSystem.getSequencer();
            Receiver receiver = device.getReceiver();
            sequencer.getTransmitter().setReceiver(new receiver());
            sequencer.open();
            sequencer.setSequence(MidiSystem.getSequence(new File(file)));
            sequencer.start();
            sequencer.addMetaEventListener(new MetaEventListener() {
                @Override
                public void meta(MetaMessage meta) {
                    if(meta.getType() == 47)
                    {
                        sequencer.close();
                    }
                }
            });
        } catch (MidiUnavailableException ex ) {
            Logger.getLogger(midi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidMidiDataException ex) {
            Logger.getLogger(midi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(midi.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static String getDeviceName(){
        return devicename;
    }
    //receiver to print out midi messages
    public static class receiver implements Receiver {
        
        @Override
        public void send(MidiMessage message, long timeStamp) {
            if (message instanceof ShortMessage) {
                ShortMessage s = (ShortMessage) message;
                if (s.getCommand()!=240) {
                    int channel = s.getChannel();
                    if (s.getCommand()==NOTE_ON){
                        System.out.println(channel+": "+s.getData1()+" on");
                        
                    }
                    if (s.getCommand()==NOTE_OFF){
                        System.out.println(channel+": "+s.getData1()+" off");
                    }
                    if (s.getCommand()==CONTROL_CHANGE){
                        //System.out.println("cc: "+s.getData1());
                    }
                }
            }
        }

        @Override
        public void close() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
class Host {
    int port = 7777;
    String ip = "";
    public Host(int port, String ip){
        this.port = port;
        this.ip = ip;
        
    }
}
