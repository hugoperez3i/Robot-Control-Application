package controllers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

import entities.ConStatus;
import entities.QueryGenerator;

public class Client {
    private static SocketChannel sck;
    private static final int RCV_BUFFER_SIZE = 6;

    public static ConStatus SERVER_CONNECTION_STATUS = new ConStatus();    

    public static int[] currentpositions = new int[27];

    public static void startUp(String host, int port) throws IOException{
        sck = SocketChannel.open(new InetSocketAddress(host, port));
        sck.write(QueryGenerator.connectToServer());
    }

    public static void selectMCU(String mcuName) throws IOException{
        sck.write(QueryGenerator.selectMCU(mcuName));
        String srvResponse=new String(Client.readSockMsg());
        if(srvResponse.contains("NACK")){throw new IOException("MCU selection failed");}
    }

    public static void updateCurrentPositions() throws IOException{
        sck.write(QueryGenerator.getMCUInfo());
        byte[] srvResponse=Client.readSockMsg();
        String strResponse=new String(srvResponse);
        if(strResponse.contains("NACK")){
            throw new IOException("Something went wrong with startup");
        }else if(strResponse.contains("iMCU")&&strResponse.endsWith("-e!")){

            if((srvResponse[8] & 0xFF) != 27){
                throw new IOException("Unexpected number of servos");
            }

            for (int i = 0; i < currentpositions.length; i++) {
                currentpositions[i]= srvResponse[10+2*i] & 0xFF;
            }
        }
    }

    public static void executeMovement(ArrayList<char[]> movements) throws IOException, Exception{
        
        Client.SERVER_CONNECTION_STATUS.MV_ORDER_SENT=true;
        sck.write(QueryGenerator.moveServos(movements));
        byte[] srvBytes = readSockMsg();
        String srvResponse=new String(srvBytes);
        
        if(srvResponse.contains("_ACK")){
            for (char[] cs : movements) {
                currentpositions[cs[0]]=cs[1];
            }
            Client.SERVER_CONNECTION_STATUS.MV_ORDER_ACCEPTED=true;
            waitForMVMTCompletionACK();
        }else{
            Client.SERVER_CONNECTION_STATUS.ERR_CODE=srvBytes[8] & 0xFF;
            Client.SERVER_CONNECTION_STATUS.ERR=true;        
        }

    }

    private static void waitForMVMTCompletionACK(){
        String r="";
        try {
            r=new String(readSockMsg());
            if(r.contains("_ACK")){
                Client.SERVER_CONNECTION_STATUS.MV_ORDER_COMPLETED=true;
            }else if(r.contains("NACK")){
                Client.SERVER_CONNECTION_STATUS.ERR_CODE=r.charAt(8);
                Client.SERVER_CONNECTION_STATUS.ERR=true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Client.SERVER_CONNECTION_STATUS.ERR_CODE=0;
            Client.SERVER_CONNECTION_STATUS.ERR=true;
        }
    }

    private static byte[] readSockMsg() throws IOException{
        ByteBuffer bbuff; int rb=0; ByteArrayOutputStream rq = new ByteArrayOutputStream();
        do {
            bbuff = ByteBuffer.allocate(RCV_BUFFER_SIZE);
            rb=sck.read(bbuff);
            if(rb == -1){throw new IOException();}
            if(rb == 0){break;}

            rq.write(bbuff.array(),0,rb);        

        } while (rb==RCV_BUFFER_SIZE && !rq.toString().endsWith("-e!"));       
        return rq.toByteArray();
    }
}
