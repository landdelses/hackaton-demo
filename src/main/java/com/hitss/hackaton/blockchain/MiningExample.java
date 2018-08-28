package com.hitss.hackaton.blockchain;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.hitss.hackaton.blockchain.util.ChainUtil;
import com.hitss.hackaton.blockchain.vo.Block;
import com.hitss.hackaton.blockchain.vo.Wallet;

/**
 * Ejemplo para comprender el minado de bloques
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class MiningExample {
	
	private static List<Block> blockchain = new ArrayList<Block>();
	public static final int DIFICULTY = 7;
	public static Wallet walletOne;
	public static Wallet walletTwo;
	
	public static void main(String[] args) {
		
//		Creamos instancias de bloques
		blockchain.add(new Block("Bloque 1", "0"));
		System.out.println("Se comienza con el minado del bloque 1 ...");
		blockchain.get(0).miner(DIFICULTY);		
		
		blockchain.add(new Block("Bloque 2", blockchain.get(blockchain.size()-1).getHash()));
		System.out.println("Se comienza con el minado del bloque 2 ...");
		blockchain.get(1).miner(DIFICULTY);
		
		blockchain.add(new Block("Bloque 3", blockchain.get(blockchain.size()-1).getHash()));
		System.out.println("Se comienza con el minado del bloque 3 ...");
		blockchain.get(2).miner(DIFICULTY);
		
		System.out.println("\n\nEl blockchain es válido: " + ChainUtil.isChainValid(blockchain));
		
//		Imprimimos los hash generados
		String blockchainFormated = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\n\nRepresentación del Blockchain: ");
		System.out.println(blockchainFormated);
		
	}
}
