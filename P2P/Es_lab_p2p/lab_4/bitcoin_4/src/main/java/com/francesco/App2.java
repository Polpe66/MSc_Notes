package com.francesco;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.script.Script;
import java.nio.ByteBuffer;

// Import aggiunti per il parser
import org.bitcoinj.base.Base58;
import org.bitcoinj.base.SegwitAddress;
import static org.bitcoinj.crypto.internal.CryptoUtils.sha256hash160;
import org.bitcoinj.params.MainNetParams;

// ... [qui ci sono gli import che ti ho messo sopra] ...

public class App2 {

    public static void main(String[] args) {
        // Il tuo main
        readRawTxAndDecode("..."); // o come hai chiamato il metodo
    }

    public static void readRawTxAndDecode(String rawHexTx) {
        // Tutto il tuo codice con il ciclo for degli input e output
        // ...
    }

    // I tuoi vari metodi di supporto (bytesToHex, hexStringToByteArray, ecc.)
    // ...
}

// =================================================================
// INIZIO CLASSI DEL PROFESSORE (Senza "public" davanti a "class")
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
    private static final int OP_PUSHDATA33 = 33;
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