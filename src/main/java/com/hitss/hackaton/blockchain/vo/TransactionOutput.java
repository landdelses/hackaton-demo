package com.hitss.hackaton.blockchain.vo;

import java.security.PublicKey;

import com.hitss.hackaton.blockchain.util.CryptoUtil;

/**
 * Clase para las transacciones de salida
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class TransactionOutput {

	private String id;
	private PublicKey receiver;
	private float val;
	private String parentTxId;

	public TransactionOutput(PublicKey receiver, float val, String parentTxId) {
		super();
		this.receiver = receiver;
		this.val = val;
		this.parentTxId = parentTxId;
		this.id = CryptoUtil.applySha256(CryptoUtil.getStringFromKey(receiver) + Float.toString(val) + parentTxId);
	}
	
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == receiver);
	}

	// Getters y Setters
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PublicKey getReceiver() {
		return receiver;
	}

	public void setReceiver(PublicKey receiver) {
		this.receiver = receiver;
	}

	public float getVal() {
		return val;
	}

	public void setVal(float val) {
		this.val = val;
	}

	public String getParentTxId() {
		return parentTxId;
	}

	public void setParentTxId(String parentTxId) {
		this.parentTxId = parentTxId;
	}

}
