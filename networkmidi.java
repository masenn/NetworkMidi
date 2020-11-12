package midi;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.*;
import static javax.sound.midi.ShortMessage.*;


public class networkmidi {
    public static boolean clientConnected = false;
    public static boolean hostConnected = false;
    public static Socket connectHost(int port) throws IOException{
        ServerSocket ss = new ServerSocket(port);
        Socket wait = ss.accept();
        hostConnected=true;
        System.out.println("Connected");
        return wait;
    }
    public static Socket connectClient(String ip, int port) throws IOException{
        Socket s = new Socket(ip,port);
        clientConnected=true;
        return s;
    }
    public static String read (Socket s, Deque e){
        String read = "";
        DataInputStream in;
        try {
            in = new DataInputStream(s.getInputStream());
            read = in.readUTF();
        } catch (IOException ex) {
            System.out.println("Cannot open input stream");
        }
        
            System.out.println("Read");
            e.addFirst(e);
        return read;
    }
    public static void write(Socket s, String e){
        try {
            PrintWriter out = new PrintWriter(s.getOutputStream());
            out.write(e);
            out.flush();
        } catch (IOException ex) {
            System.out.println("Cannot write");
        }
    }
    public static boolean hasNext(Deque deque) {
        if (deque.size()!=0){
            return true;
        }
        return false;
    }
    public static ShortMessage pullApart(String cmd) throws InvalidMidiDataException{
        String[] out = cmd.split("-");
        String command = out[0]; int icommand = Integer.parseInt(command);
        System.out.println(command);
        String channel = out[1];int ichannel = Integer.parseInt(channel);
        System.out.println(channel);
        String data1 = out[2]; int idata1 = Integer.parseInt(data1);
        System.out.println(data1);
        String data2 = out[3]; int idata2 = Integer.parseInt(data2);
        System.out.println(data2);
        ShortMessage mes = new ShortMessage(icommand, ichannel, idata1, idata2);
        return mes;
    }
    //midi receiver for sending messages
    public static class networkReceiver implements Receiver {
        static String ip = "91.321.24.201";
        Socket s;
        public static String device;
        public networkReceiver(String ip, int port, String device){
            try {
                s = networkmidi.connectClient(ip,port);
                this.device = device;
                System.out.println("opened");
            } catch (IOException ex) {
                System.out.println("Aaaaaaaah");
            }
        }
        
        @Override
        public void send(MidiMessage message, long timeStamp) {
            if (message instanceof ShortMessage) {
                ShortMessage mes = (ShortMessage) message;
                if (device.contains("mio")){
                    if (mes.getCommand()!=240){
                        if (mes.getData2()==64){
                            String read= NOTE_ON+"-"+mes.getChannel()+"-"+mes.getData1()+"-"+mes.getData2();
                            try {
                                net.write(read, s);
                            } catch (IOException ex) {
                                System.out.println("Cannot write");
                            }
                        }
                        if (mes.getData2()==0){
                            String read= NOTE_OFF+"-"+mes.getChannel()+"-"+mes.getData1()+"-"+mes.getData2();
                            try {
                                net.write(read, s);
                            } catch (IOException ex) {
                                System.out.println("Cannot write");
                            }
                        }
                    }
                } else {
                    if (mes.getCommand()!=240){
                        try {
                            String read= mes.getCommand()+"-"+mes.getChannel()+"-"+mes.getData1()+"-"+mes.getData2();
                            net.write(read, s);
                        } catch (IOException ex) {
                            System.out.println("Cannot write");
                        }
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
