package com.francesco;

import org.bitcoinj.core.*;
import org.bitcoinj.params.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.base.Address;
import org.bitcoinj.base.ScriptType;
import org.bitcoinj.base.Network;
import org.bitcoinj.base.Sha256Hash;
import org.bitcoinj.crypto.ECKey;
import org.bitcoinj.base.internal.ByteUtils;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.store.BlockStore;
import org.bitcoinj.store.MemoryBlockStore;
import org.bitcoinj.store.BlockStoreException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
public class App {

    // 1. Il main deve gestire Exception per far girare connectionTest
    public static void main(String[] args) throws Exception {
        //connectionTest();
        //createNewAddr();
        vanityAddr("q"); // è esponenziale aumenta sempre di + più aumento il nuemro di lettere
        //getBtcGenesis();
    }

    public static void printNetStats1(PeerGroup peerGroup) {
        System.out.println("\n\nNETWORK INFO:");
        // 2. Corretto: .size() per avere il numero di peer connessi
        System.out.println("Current connections: " + peerGroup.getConnectedPeers().size());
        System.out.println("Max connections: " + peerGroup.getMaxConnections());
        
        // 3. Corretto: p.getAddress() sostituisce il vecchio p.getAddr() della 0.13
        for (Peer p : peerGroup.getConnectedPeers()) {
            System.out.println("Connected to: " + p.getAddress());
        }
        
        
    }

    public static void connectionTest() throws InterruptedException {
        NetworkParameters netParams = TestNet3Params.get();

        BlockStore blockStore = new MemoryBlockStore(netParams.getGenesisBlock());
        BlockChain blockChain;
        try {
            blockChain = new BlockChain(netParams.network(), blockStore);
            PeerGroup peerGroup = new PeerGroup(netParams, blockChain);
            peerGroup.setUserAgent("Sample App", "1.0");
            peerGroup.addPeerDiscovery(new DnsDiscovery(netParams));
            peerGroup.start();
            Thread.sleep(10000);
            printNetStats1(peerGroup);
            for (Peer p: peerGroup.getConnectedPeers()) {
                System.out.println(p.getAddr());
            }
            while (peerGroup.getConnectedPeers().isEmpty())
                Thread.sleep(5000);
            
            Sha256Hash blockHash = Sha256Hash.wrap("0000000000000adc6423b570d751efcdf5e019d3d955fee155c28925913cb667");
            Block block;
            boolean flag = true;
            try {
                while (flag) {
                    Peer pFirst = peerGroup.getConnectedPeers().get(peerGroup.getConnectedPeers().size() - 1);
                    Future < Block > future = pFirst.getBlock(blockHash);
                    block = future.get(5, TimeUnit.SECONDS);
                    System.out.println("Here is the block: " + block);
                    flag = false;
                }
            } catch (TimeoutException ex) {
                //do nothing, just try a new peer
            } catch (ExecutionException ex) {
                //do nothing, just try a new peer
            }
            
            Thread.sleep(10000);
            printNetStats1(peerGroup);
            peerGroup.stop();
        } catch (BlockStoreException ex) {
            System.getLogger(App.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
        }
    }

    public static void printNetStats(PeerGroup peerGroup) {
        System.out.println("\n\nNETWORK INFO:");
        System.out.println("Max conections: " + peerGroup.getMaxConnections());
        System.out.println("Current conenctions: " + peerGroup.numConnectedPeers());
        System.out.println("Chain height: " + peerGroup.getMostCommonChainHeight());
        System.out.println("\n\n");
    }


  public static void createNewAddr() {
        
        NetworkParameters netParams1 = TestNet3Params.get();

        ECKey key = new ECKey();
        System.out.println("We created key " + key);
        Address addressFromKey = key.toAddress(ScriptType.P2PKH, netParams1.network());

        System.out.println("On the " + netParams1.network() + " network , we can use this address " + addressFromKey);

        NetworkParameters netParams2 = MainNetParams.get();
        addressFromKey = key.toAddress(ScriptType.P2PKH, netParams2.network());

        System.out.println("On the " + netParams2.network() + " network , we can use this address " + addressFromKey);

        addressFromKey = key.toAddress(ScriptType.P2WPKH, netParams2.network());

        System.out.println("On the " + netParams2.network() + " network , we can use this address " + addressFromKey);
    }


public static void vanityAddr(String prefix) {
    // 1. Usiamo MainNet per avere indirizzi che iniziano con "1"
    NetworkParameters netParams1 = org.bitcoinj.params.MainNetParams.get();
    boolean flag = true;
    int count = 0; // Se cerchi prefissi lunghi, ricorda di mettere 'long' invece di 'int'

    ECKey key = new ECKey(); 
    // 2. MODIFICA 0.17: Usiamo key.toAddress passandogli il Network (non netParams1 intero)
    Address addressTest = key.toAddress(org.bitcoinj.base.ScriptType.P2PKH, netParams1.network());

    while(flag) {
        count++;
        
        if(addressTest.toString().startsWith("1" + prefix)) {
            flag = false;
        } else {
            key = new ECKey(); 
            // 3. MODIFICA 0.17: Stessa cosa qui dentro il ciclo
            addressTest = key.toAddress(org.bitcoinj.base.ScriptType.P2PKH, netParams1.network());
        }
    }
    
    System.out.println("Address " + addressTest + " found after " + count + " Attempts");
}

    //ora guardiamo il genesis block


//Get Bitcoin Genesis
    //0x000000000019d6689c085ae165831e934ff763ae46a2a6c172b3f1b60a8ce26f
    public static void getBtcGenesis() throws InterruptedException {
        NetworkParameters netParams = MainNetParams.get();

        Block genesis = netParams.getGenesisBlock();

        System.out.println(genesis);

        TransactionInput txIn = genesis.getTransactions().get(0).getInputs().get(0);
        byte[] scriptBytes = txIn.getScriptBytes();


        System.out.println(bytesToHex(txIn.getScriptBytes()));

        String message = hexToAscii(bytesToHex(txIn.getScriptBytes()));

        System.out.println(message);

        printBlockInfo(genesis);

        printTxInfo(genesis.getTransactions().get(0));
    }

    public static void printBlockInfo(Block blk) throws InterruptedException {
        System.out.println("Hash " + blk.getHashAsString());
        System.out.println("Prev Hash " + blk.getPrevBlockHash());
        System.out.println("Timestamp " + blk.getTimeSeconds());
        System.out.println("Timestamp " + blk.time());
    }
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    public static void printTxInfo(org.bitcoinj.core.Transaction tx) {
        System.out.println("--- TX INFO ---");
        System.out.println("Tx Hash: " + tx.getTxId());
        System.out.println("Inputs: " + tx.getInputs().size());
        System.out.println("Outputs: " + tx.getOutputs().size());
    }


}