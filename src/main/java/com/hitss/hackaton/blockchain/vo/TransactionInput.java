package com.hitss.hackaton.blockchain.vo;

/**
 * Clase para las transacciones de entrada
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class TransactionInput {
	
	private String transactionId;
	private TransactionOutput UTXO;  // Unspent Transaction Output
	
	public TransactionInput(String transactionId) {
		super();
		this.transactionId = transactionId;
	}

	// Setters y getters
	
	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public TransactionOutput getUTXO() {
		return UTXO;
	}

	public void setUTXO(TransactionOutput uTXO) {
		UTXO = uTXO;
	}

}
