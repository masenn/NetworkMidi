package midi;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;
import static javax.sound.midi.ShortMessage.NOTE_ON;
import midi.networkmidi.networkReceiver;

/**
 *
 * @author masenn
 */
public class main {
    static int port = 7777;
    //String ip = "216.143.9.162";
    static String ip = "91.323.1.201";
    //Deque for storing midi messages as them are received
    static Deque<String> msg = new ArrayDeque<>();
    public static void main(String[] args) throws IOException, InterruptedException, InvalidMidiDataException, MidiUnavailableException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter 1 to host and 2 to receive.");
        int i = sc.nextInt();
        if (i==1){
            //initializes TCP socket
            Socket host = net.connectHost(port);
            //allows the user to select a device
            MidiDevice d = midi.selectDevice();
            //initializes the device and retrieves receiver
            midi.initDevice(d);
            Receiver r = d.getReceiver();
            /* Test
            long timeStamp = -1;
            ShortMessage ee = new ShortMessage(NOTE_ON, 2, 60, 127);
                r.send(ee, timeStamp);
            */
            //receives messages from the host and after decoding, sends the appropriate message to the selected device
            while(net.hostConnected){
                net.getMessage(host, msg);
                if (msg.size()!=0){
                    String cmd = msg.getFirst();
                    ShortMessage send = net.pullApart(cmd);
                    r.send(send, 0);
                    System.out.println("Send");
                    msg.removeFirst();
                }
            }
        }
        if (i==2){
            //allows user to select a device
            MidiDevice d = midi.selectDevice();
            String devicename = midi.getDeviceName();
            //initializes device and creates a NetworkReceiver instance to send messages in a format that the host can read
            midi.initDevice(d, new networkReceiver(ip,port,devicename));
        }
    }
}
