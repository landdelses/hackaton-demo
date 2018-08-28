package com.hitss.hackaton.blockchain.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.hitss.hackaton.blockchain.util.ChainUtil;
import com.hitss.hackaton.blockchain.util.CryptoUtil;

/**
 * Representaci&oacute;n de bloque
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class Block {

	private String hash; // Firma digital
	private String previousHash; // Firma anterior
	private String merkleRoot;
	private List<Transaction> txs = new ArrayList<>();
	private String data;
	private int nonce = 0;
	private long timeStamp;

	/**
	 * Constructor de bloque
	 * 
	 * @param previousHash
	 * @param data
	 */
	public Block(String data, String previousHash) {
		super();
		this.previousHash = previousHash;
		this.data = data;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}
	
	/**
	 * Constructor de bloque
	 * 
	 * @param previousHash
	 */
	public Block(String previousHash ) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		
		this.hash = calculateHash();
	}

	/**
	 * Calcula el Hash con el Algoritmo SHA256
	 * 
	 * @return Cadena del hash
	 */
	public String calculateHash() {
		String hash = CryptoUtil
				.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return hash;
	}

	/**
	 * Realiza el minado
	 * 
	 * @param difficulty
	 */
	public void miner(int difficulty) {
		String target = new String(new char[difficulty]).replace('\0', '0');

		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Bloque minado con hash generado: " + hash);

	}

	/**
	 * Mina los bloques con MerkleRoot
	 * 
	 * @param difficulty
	 */
	public void mineBlock(int difficulty) {
		merkleRoot = ChainUtil.getMerkleRoot(txs);
		String target = ChainUtil.getDifficultyStr(difficulty);
		while (!hash.substring(0, difficulty).equals(target)) {
			nonce++;
			hash = calculateHash();
		}
		System.out.println("Bloque minado : " + hash);
	}

	/**
	 * Procesa la transacci&oacute;n y verifica que sea v&aacute;lida, 
	 * a menos que sea un bloque Genesis, en este caso lo ignora
	 * 
	 * @param tx
	 * @return
	 */
	public boolean addTransaction(Transaction tx) {
		
		if (tx == null)
			return false;
		if ((!previousHash.equals("0"))) {
			if ((tx.processTransaction() != true)) {
				System.out.println("No se pudo procesar la transacción, será descartada.");
				return false;
			}
		}
		txs.add(tx);
		System.out.println("Transacción procesada correctamente.");
		return true;
	}

	// Getters y Setters

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public List<Transaction> getTxs() {
		return txs;
	}

	public void setTxs(List<Transaction> txs) {
		this.txs = txs;
	}

}
