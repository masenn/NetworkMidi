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
    //String ip = "216.56.9.162";
    static String ip = "192.168.1.201";
    static Deque<String> msg = new ArrayDeque<>();
    public static void main(String[] args) throws IOException, InterruptedException, InvalidMidiDataException, MidiUnavailableException {
        Scanner sc = new Scanner(System.in);
        int i = sc.nextInt();
        if (i==1){
            Socket host = net.connectHost(port);
            MidiDevice d = midi.selectDevice();
            midi.initDevice(d);
            Receiver r = d.getReceiver();
            long timeStamp = -1;
            ShortMessage ee = new ShortMessage(NOTE_ON, 2, 60, 127);
                r.send(ee, timeStamp);
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
            MidiDevice d = midi.selectDevice();
            String devicename = midi.getDeviceName();
            midi.initDevice(d, new networkReceiver(ip,port,devicename));
        }
    }
}
