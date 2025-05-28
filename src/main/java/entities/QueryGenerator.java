package entities;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class QueryGenerator {

    public static ByteBuffer connectToServer(){
        return ByteBuffer.wrap("!s-Client_here-e!".getBytes());
    }

    public static ByteBuffer selectMCU(String mcuName){
        String q = "!s-sMCU-" + mcuName + "-e!";
        return ByteBuffer.wrap(q.getBytes());
    }

    public static ByteBuffer getMCUInfo(){
        String q = "!s-iMCU-e!";
        return ByteBuffer.wrap(q.getBytes());
    }

    public static ByteBuffer moveServos(ArrayList<char[]> movements) throws Exception{
        String q="!s-SRVP-"+ (char)movements.size() +"-";
        for (char[] cs : movements) {
            q+=(char)(cs[0]+1); q+=":"; q+= cs[1]; q+="-";
        }
        q+="e!";
        return ByteBuffer.wrap(q.getBytes(StandardCharsets.ISO_8859_1));
    }

}