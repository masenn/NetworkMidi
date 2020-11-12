package midi;

import java.io.*;
import java.util.*;
import java.net.*;        
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import static javax.sound.midi.ShortMessage.*;

public class net {
    public static boolean clientConnected = false;
    public static boolean hostConnected = false;
    //opens Host socket
    public static Socket connectHost(int port) throws IOException{
        ServerSocket ss = new ServerSocket(port);
        Socket wait = ss.accept();
        hostConnected=true;
        System.out.println("Host Connected");
        return wait;
    }
    //opens client socket
    public static Socket connectClient(String ip, int port) throws IOException{
        Socket s = new Socket(ip,port);
        clientConnected=true;
        System.out.println("Client Connect");
        return s;
    }
    //writes message over TCP
    static void write(String e, Socket socket) throws IOException{
        DataOutputStream out;
        out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(e);
        out.flush();
    }
    //recieves messages from the socket
    static String getMessage(Socket s, Deque deque) throws IOException{
        DataInputStream in = new DataInputStream(s.getInputStream());
        String e = in.readUTF();
        deque.add(e);
        return e;
    }
    //allows all message data to be sent through a singular string
    //the message is pulled apart and sent as the appropriate message
    public static ShortMessage pullApart(String cmd) throws InvalidMidiDataException{
        int velocity = 0;
        String[] out = cmd.split("-");
        String command = out[0]; int icommand = Integer.parseInt(command);
        System.out.println("Command: "+command);
        String channel = out[1];int ichannel = Integer.parseInt(channel);
        System.out.println("Channel: "+channel);
        String data1 = out[2]; int idata1 = Integer.parseInt(data1);
        System.out.println("Data 1: "+data1);
        String data2 = out[3]; int idata2 = Integer.parseInt(data2);
        if (idata2==64){
            icommand=NOTE_ON;
        } if(idata2==0){
            icommand=NOTE_OFF;
        }
        System.out.println("Data 2: "+data2);
        ShortMessage mes = new ShortMessage(icommand, ichannel, idata1, idata2);
        return mes;
    }
}
