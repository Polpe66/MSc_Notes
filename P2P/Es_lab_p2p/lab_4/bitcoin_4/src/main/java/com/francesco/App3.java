package com.francesco;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.core.Block;
import org.bitcoinj.utils.BlockFileLoader;
import org.bitcoinj.base.Base58;
import org.bitcoinj.base.SegwitAddress;
import static org.bitcoinj.crypto.internal.CryptoUtils.sha256hash160;
import org.bitcoinj.params.MainNetParams;

import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;
import java.util.LinkedList;
import java.util.Locale;

public class App3 {

    public static void main(String[] args) {
        try {
            // ATTENZIONE: Cambia questo percorso con la cartella dove hai i file blk00000.dat!
            // Su Windows usa i doppi slash, es: "C:\\Utenti\\Nome\\AppData\\Roaming\\Bitcoin\\blocks\\"
            String cartellaBlocchi = "/percorso/della/tua/cartella/blocks/"; 
            
            // Il file dove verranno salvati tutti i risultati
            File fileOutput = new File("risultati_blockchain.txt");
            
            System.out.println("Inizio scansione dei blocchi...");
            
            BCParser parser = new BCParser(cartellaBlocchi);
            
            // Scansiona i primi 10 blocchi (puoi aumentare questo numero)
            parser.parseNoUtxo(fileOutput, 10); 
            
            System.out.println("Scansione completata! Controlla il file: " + fileOutput.getAbsolutePath());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// =================================================================
// 1. BCParser: Legge i file della blockchain e ne estrae i dati
// =================================================================
class BCParser {
    String chaindataFolder;
    int DEBUGtotalTxs;
    int DEBUGcoinbaseCounter;
    int DEBUGwitnessTxs;
    int DEBUGnullAddresses;
    int[] DEBUGscriptTypes;

    public BCParser(String f) {
        chaindataFolder = f;
        DEBUGtotalTxs = 0;
        DEBUGcoinbaseCounter = 0;
        DEBUGwitnessTxs = 0;
        DEBUGnullAddresses = 0;
        
        DEBUGscriptTypes = new int[ScriptTypeCustom.SUPPORTEDSCRIPTTYPES];
        for (int i = 0; i < DEBUGscriptTypes.length; i++) {
            DEBUGscriptTypes[i] = 0;
        }
    }

    public static List<File> buildList(String PREFIX) {
        List<File> list = new LinkedList<File>();
        for (int i = 0; true; i++) {
            File file = new File(PREFIX + String.format(Locale.US, "blk%05d.dat", i));
            if (!file.exists())
                break;
            list.add(file);
        }
        return list;
    }

    public void parseNoUtxo(File out, int n) throws IOException {
        BlockFileLoader loader = new BlockFileLoader(MainNetParams.get().network(), buildList(chaindataFolder));
        BufferedWriter bw = new BufferedWriter(new FileWriter(out));
        int blockCounter = 0;

        for (Block block: loader) {
            if (blockCounter >= n) break;
            if (blockCounter % 20000 == 0) {
                System.out.println("Analizzati " + blockCounter + " blocchi.");
                System.out.println(blockCounter + " - " + block.getHashAsString());
            }
            parseBlockExact(block, bw);
            blockCounter++;
        }
        
        bw.close();
        System.out.println("Totale Tx " + DEBUGtotalTxs + " , di cui coinbase: " + DEBUGcoinbaseCounter + " e witness (Segwit): " + DEBUGwitnessTxs);
        System.out.println("Tipi di Script trovati:");
        int ttemp = 0;
        for (int i = 0; i < DEBUGscriptTypes.length; i++) {
            System.out.println(DEBUGscriptTypes[i] + " " + ScriptTypeCustom.typeName(i));
            ttemp += DEBUGscriptTypes[i];
        }
        System.out.println("Totale output: " + ttemp + " (" + DEBUGnullAddresses + " indirizzi nulli).");
    }

    public void parseBlockExact(Block block, BufferedWriter bw) throws IOException {
        boolean isCoinbase;
        boolean first;
        StringBuilder line;
        
        for (Transaction tx : block.getTransactions()) {
            DEBUGtotalTxs++;
            line = new StringBuilder();
            line.append(block.getTime()).append(",");
            line.append(block.getHashAsString()).append(",");
            line.append(tx.getTxId().toString()).append(",");
            
            if (tx.isCoinBase()) {
                isCoinbase = true;
                line.append("1");
            } else {
                isCoinbase = false;
                line.append("0");
            }
            line.append(",").append(tx.getMessageSize()).append(",");
            
            boolean hasWitness = false;
            for(TransactionInput in : tx.getInputs()) {
                if(in.hasWitness()) {
                    hasWitness = true;
                    break;
                }
            }

            if (hasWitness) {
                DEBUGwitnessTxs++;
                line.append("1");
            } else {
                line.append("0");
            }
            line.append(":");
            
            if (isCoinbase) {
                DEBUGcoinbaseCounter++;
            } else {
                first = true;
                for (TransactionInput ii : tx.getInputs()) {
                    if (first) first = false;
                    else line.append(";");
                    
                    line.append(ii.getOutpoint().getHash().toString()).append(",");
                    line.append(ii.getOutpoint().getIndex());
                }
            }
            line.append(":");
            
            first = true;
            for (TransactionOutput oo : tx.getOutputs()) {
                if (first) first = false;
                else line.append(";");
                
                byte[] outScript = oo.getScriptBytes();
                String outAddr = ScriptParser.addrFromOut(outScript);
                int outType = ScriptParser.typeFromOut(outScript);
                
                DEBUGscriptTypes[outType]++;
                
                if (outAddr == null) {
                    outAddr = "#" + DEBUGnullAddresses;
                    DEBUGnullAddresses++;
                }
                line.append(outAddr).append(",");
                line.append(oo.getValue().getValue()).append(",");
                line.append(outType);
            }
            bw.write(line.toString());
            bw.newLine();
        }
    }
}

// =================================================================
// 2. ScriptParser: Analizza i byte per capire il tipo di transazione
// =================================================================
class ScriptParser {
    private static final int OP_DUP = 118;
    private static final int OP_HASH160 = 169;
    private static final int OP_EQUALVERIFY = 136;
    private static final int OP_CHECKSIG = 172;
    private static final int OP_CHECKSIGVERIFY = 173;
    private static final int OP_EQUAL = 135;
    private static final int OP_RETURN = 106;
    
    private static final int OP_PUSHDATA20 = 20;
    private static final int OP_PUSHDATA32 = 32;
    private static final int OP_PUSHDATA65 = 65;
    private static final int OP_0 = 0;

    private static boolean isOpCode(byte b, int opcode) {
        return Utilities.readUnsignedByte(b) == opcode;
    }

    public static String addrFromOut(byte[] script) {
        if ((script == null) || (script.length < 1)) return null;
        if (isOpCode(script[0], OP_RETURN)) return null;
        else if (isOpCode(script[0], OP_DUP) && (script.length >= 23)) {
            if (isOpCode(script[1], OP_HASH160) && isOpCode(script[2], OP_PUSHDATA20)) {
                byte[] res = new byte[20];
                System.arraycopy(script, 3, res, 0, 20);
                return getAddressFromPubHash(res);
            } else return null;
        } else if (isOpCode(script[0], OP_PUSHDATA65) && (script.length >= 66)) {
            byte[] res = new byte[65];
            System.arraycopy(script, 1, res, 0, 65);
            return getAddressFromPubKey(res);
        } else if ((script.length == 66) && ((isOpCode(script[script.length - 1], OP_CHECKSIG)) || (isOpCode(script[script.length - 1], OP_CHECKSIGVERIFY)))) {
            byte[] res = new byte[65];
            System.arraycopy(script, 0, res, 0, 65);
            return getAddressFromPubKey(res); 
        } else if (isOpCode(script[0], OP_HASH160)) {
            if ((script.length >= 23) && isOpCode(script[1], OP_PUSHDATA20) && (isOpCode(script[script.length - 1], OP_EQUAL) || isOpCode(script[script.length - 1], OP_EQUALVERIFY))) {
                byte[] res = new byte[20];
                System.arraycopy(script, 2, res, 0, 20);
                return getAddressFromScriptHash(res);
            } else return null;
        } else if (isOpCode(script[0], OP_0)) {
            if (isOpCode(script[1], OP_PUSHDATA20) && (script.length == 22)) {
                byte[] res = new byte[20];
                System.arraycopy(script, 2, res, 0, 20);
                return SegwitAddress.fromHash(MainNetParams.get().network(), res).toBech32();
            } else if (isOpCode(script[1], OP_PUSHDATA32) && (script.length == 34)) {
                byte[] res = new byte[32];
                System.arraycopy(script, 2, res, 0, 32);
                return SegwitAddress.fromHash(MainNetParams.get().network(), res).toBech32();
            } else return null;
        }
        return null;
    }

    public static int typeFromOut(byte[] script) {
        if ((script == null) || (script.length < 1)) return ScriptTypeCustom.EMPTY; 
        if (isOpCode(script[0], OP_RETURN)) return ScriptTypeCustom.RETURN;
        else if (isOpCode(script[0], OP_DUP) && (script.length == 24)) {
            if (isOpCode(script[1], OP_HASH160) && isOpCode(script[2], OP_PUSHDATA20) && isOpCode(script[22], OP_EQUALVERIFY) && isOpCode(script[23], OP_CHECKSIG)) {
                return ScriptTypeCustom.P2PKH;
            } else return ScriptTypeCustom.UNKNOWN;
        } else if (isOpCode(script[0], OP_PUSHDATA65) && (script.length >= 66)) {
            return ScriptTypeCustom.P2PK;
        } else if ((script.length == 66) && ((isOpCode(script[script.length - 1], OP_CHECKSIG)) || (isOpCode(script[script.length - 1], OP_CHECKSIGVERIFY)))) {
            return ScriptTypeCustom.P2PK;
        } else if (isOpCode(script[0], OP_HASH160)) {
            if ((script.length >= 23) && isOpCode(script[1], OP_PUSHDATA20) && (isOpCode(script[script.length - 1], OP_EQUAL) || isOpCode(script[script.length - 1], OP_EQUALVERIFY))) {
                return ScriptTypeCustom.P2SH;
            } else return ScriptTypeCustom.UNKNOWN;
        } else if (isOpCode(script[0], OP_0)) {
            if (isOpCode(script[1], OP_PUSHDATA20) && (script.length == 22)) return ScriptTypeCustom.P2WPKH;
            else if (isOpCode(script[1], OP_PUSHDATA32) && (script.length == 34)) return ScriptTypeCustom.P2WSH;
            else return ScriptTypeCustom.UNKNOWN;
        }
        return ScriptTypeCustom.UNKNOWN;
    }

    public static String getAddressFromPubHash(byte[] b) {
        byte[] version = {0};
        return Base58.encodeChecked(version[0], b);
    }

    public static String getAddressFromPubKey(byte[] b) {
        return getAddressFromPubHash(sha256hash160(b));
    }

    public static String getAddressFromScriptHash(byte[] b) {
        byte[] version = {5};
        return Base58.encodeChecked(version[0], b);
    }

    public static class Utilities {
        public static int readUnsignedByte(byte b) {
            return b & 0xFF;
        }
    }
}

// =================================================================
// 3. ScriptTypeCustom: Le costanti per identificare i tipi di script
// =================================================================
class ScriptTypeCustom {
    public static final int UNKNOWN = 0;
    public static final int P2PK = 1;
    public static final int P2PKH = 2;
    public static final int P2SH = 3;
    public static final int RETURN = 4;
    public static final int EMPTY = 5;
    public static final int P2WPKH = 6;
    public static final int P2WSH = 7;
    public static final int SUPPORTEDSCRIPTTYPES = 8;

    public static String typeName(int code) {
        switch (code) {
            case UNKNOWN: return "UNKNOWN";
            case P2PK: return "P2PK";
            case P2PKH: return "P2PKH";
            case P2SH: return "P2SH";
            case RETURN: return "PROVABLY UNSPENDABLE";
            case EMPTY: return "ANYONE CAN SPEND";
            case P2WPKH: return "P2WPKH";
            case P2WSH: return "P2WSH";
            default: return "ERROR - UNRECOGNIZED SCRIPT CODE";
        }
    }
}