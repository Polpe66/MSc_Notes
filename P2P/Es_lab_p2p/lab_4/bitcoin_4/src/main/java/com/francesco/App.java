package com.francesco;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.params.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptOpCodes;
import org.bitcoinj.base.Address;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.base.Network;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.base.internal.ByteUtils;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

import org.bitcoinj.store.BlockStoreException;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.text.Utilities;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {

        readRawTx();
        
    }

    public static void readRawTx(){
        //
        String rawHexTx = "01000000034da64e544f47eed4075a65051aa62dfac20d36c053cc543bc04c0baffebaca22000000008c493046022100f164a3308e23142244bfbb6f2debcada07e17409ee393856486667238246834a022100f68653a5e16525929d5fcc4bab970aebb6eab04e108cc4c99a2fc387f8bf43ec014104d34775baab521d7ba2bd43997312d5f663633484ae1a4d84246866b7088297715a049e2288ae16f168809d36e2da1162f03412bf23aa5f949f235eb2e7141783ffffffff76937c1d288cee6e9994d2df430625c1ddd77594b1930200c89cf2de873259a8000000008b48304502205ca24c38699b1caa6c816471ef3d2e95ffa8eff7fec0220a87e265c16d04314d02210088bc9155d1df534470dd74d92fc47ba6d2cd690ac337efa1ddd9916d6be0adfa014104d34775baab521d7ba2bd43997312d5f663633484ae1a4d84246866b7088297715a049e2288ae16f168809d36e2da1162f03412bf23aa5f949f235eb2e7141783ffffffff6086ce9d364753ef707e0ebcbcadab9e7f2a2844b3452efe223958088e4e08e5010000008b4830450220316b4e18426493a6b85513152330b51ff44cb56f9eb0ae4ff501dee173ca6b280221008cf25daa2dd9430371ac3e3f410968705ba3627dafaef11330d69ea3706c5daa014104d34775baab521d7ba2bd43997312d5f663633484ae1a4d84246866b7088297715a049e2288ae16f168809d36e2da1162f03412bf23aa5f949f235eb2e7141783ffffffff06809698000000000017a9144266fc6f2c2861d7fe229b279a79803afca7ba3487809698000000000017a914292fb39df7cd619a396069383928e6bfb74ebec587809698000000000017a914c89ab551eab767697bc4d9caca650c41b39497c687809698000000000017a91455951b1e750beb68712edae7042ffeb7491c388d87809698000000000017a914813ee00988dc0188450da05b77bd2d3e887f2ca487e00f9700000000001976a9149bc0bbdd3024da4d0c38ed1aecf5c68dd1d3fa1288ac00000000";
        
        byte[] ba= hexStringToByteArray(rawHexTx);
        ByteBuffer bf = ByteBuffer.wrap(ba);
        Transaction tx= Transaction.read(bf);


        //Transaction tx = Transaction(rawHexTx);

        for(TransactionInput in:tx.getInputs()){
            byte[] inScript = in.getScriptBytes();
            System.out.println("Input script hex " + bytesToHex(inScript));
            System.out.println("Input script OPcodes " + bytesToOpcode(inScript));




        }

        for(TransactionOutput out:tx.getOutputs()){
            byte[] outScript = out.getScriptBytes();
            System.out.println("Output script hex " + bytesToHex(outScript));
            System.out.println("output script OPvodes " + bytesToOpcode(outScript));
            




        }
        

    }

public static String bytesToOpcode(byte[] script) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < script.length;) {
            
            // Sostituisce Utilities.readUnsignedByte(script[i]) del prof per non avere errori
            int val = script[i] & 0xFF; 
            
            // Aggiunto il percorso completo a ScriptOpCodes per la 0.17
            line.append(org.bitcoinj.script.ScriptOpCodes.getOpCodeName(val));
            line.append(" ");
            i++;
            
            if ((val >= 1) && (val <= 75)) {
                for (int j = 0; j < val; j++) {
                    line.append(byteToHex(script[i]));
                    line.append(" ");
                    i++;
                }
            } else if (val == 106) {
                while (i < script.length) {
                    line.append(hexToAscii(byteToHex(script[i])));
                    line.append(" ");
                    i++;
                }
            }
        }
        return line.toString();
    }



    //fatto a lez 4
public static String scriptTypeFromBytes(byte [] rawHex){

   

    return "";

}


//P2PKH = DUP HASH160 PUSH20 <20> EQUAL CHECKSIG
    public static boolean isScriptP2PKH(byte[] script){
        if(Utilities.readUnsignedByte(script[0]) == 118
                && Utilities.readUnsignedByte(script[1]) == XXX
                && Utilities.readUnsignedByte(script[2]) == 20 &&
                Utilities.readUnsignedByte(script[23]) == YYY
                && Utilities.readUnsignedByte(script[24]) == ZZZ){
            return true;
        }
        return false;
}







private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
public static String bytesToHex(byte[] bytes) {
char[] hexChars = new char[bytes.length * 2];
for (int j = 0; j < bytes.length; j++) {
int v = bytes[j] & 0xFF;
hexChars[j * 2] = HEX_ARRAY[v >>> 4];
hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
}
return new String(hexChars);
}
public static String byteToHex(byte byte1) {
char[] hexChars = new char[2];
int v = byte1 & 0xFF;
hexChars[0] = HEX_ARRAY[v >>> 4];
hexChars[1] = HEX_ARRAY[v & 0x0F];
return new String(hexChars);
}

public static String hexToAscii(String hex) {
return new String(BaseEncoding.base16().lowerCase().decode(hex), Charsets.US_ASCII);
}

public static byte[] hexStringToByteArray(String s) {
int len = s.length();
byte[] data = new byte[len / 2];
for (int i = 0; i < len; i += 2) {
data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
+ Character.digit(s.charAt(i+1), 16));
}
return data;
}


// --- CLASSE UTILITIES DEL PROF ---
    public static class Utilities {
        public static int readUnsignedByte(byte b) {
            return b & 0xFF;
        }
    }

}