package com.hitss.hackaton.blockchain.vo;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import com.hitss.hackaton.blockchain.TransferExample;
import com.hitss.hackaton.blockchain.util.CryptoUtil;	

/**
 * Clase para las transacciones
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class Transaction {

	private String id;
	private PublicKey senderKey;
	private PublicKey receiverKey;
	private float val;
	private byte[] signature;
	private int increment = 0;

	private List<TransactionInput> inputs = new ArrayList<>();
	private List<TransactionOutput> outputs = new ArrayList<>();

	public Transaction(PublicKey senderKey, PublicKey receiverKey, float val, List<TransactionInput> inputs) {
		super();
		this.senderKey = senderKey;
		this.receiverKey = receiverKey;
		this.val = val;
		this.inputs = inputs;
	}

	/**
	 * Obtiene el hash de la transacci&oacute;n
	 * 
	 * @return Hash de la transacci&oacute;n
	 */
	private String getHash() {
		increment++;
		return CryptoUtil.applySha256(CryptoUtil.getStringFromKey(senderKey) + CryptoUtil.getStringFromKey(receiverKey)
				+ Float.toString(val) + increment);
	}

	/**
	 * Genera la firma de la transacci&oacute;n
	 * 
	 * @param privateKey
	 */
	public void signValue(PrivateKey privateKey) {

		String data = CryptoUtil.getStringFromKey(senderKey) + CryptoUtil.getStringFromKey(receiverKey)
				+ Float.toString(val);
		signature = CryptoUtil.applyECDSASignature(privateKey, data);

	}

	/**
	 * Verifica que la cadena firmada sea leg&iacute;tima
	 * 
	 * @return
	 */
	public boolean verifySignature() {

		String data = CryptoUtil.getStringFromKey(senderKey) + CryptoUtil.getStringFromKey(receiverKey)
				+ Float.toString(val);
		return CryptoUtil.verifyECDSASignature(senderKey, data, signature);

	}

	/**
	 * Procesa las transacciones
	 * 
	 * @return
	 */
	public boolean processTransaction() {

		boolean flag = false;
		float leftOver = 0;

		if (!verifySignature()) {
			System.out.println("Verificación de la Firma de la transacción fallida");
			return flag;
		}

		// Asignamos las transacciones entrantes
		for (TransactionInput txi : inputs) {
			txi.setUTXO(TransferExample.UTXOs.get(txi.getTransactionId()));
		}

		// Validamos que se tenga un mínimo de transacciones a procesar
		if (getInputsValue() < TransferExample.minTxs) {
			System.out.println("Monto de la transacción es muy poco... el mínimo es: " + TransferExample.minTxs);
			return flag;
		}

		leftOver = getInputsValue() - val;
		id = getHash();

		outputs.add(new TransactionOutput(receiverKey, val, id));
		outputs.add(new TransactionOutput(senderKey, leftOver, id));

		// Se agregan las transacciones a la lista de no gastados
		for (TransactionOutput txo : outputs) {
			TransferExample.UTXOs.put(txo.getId(), txo);
		}

		// Se quitan las transacciones de la lista de entrada como "gastadas"
		for (TransactionInput txi : inputs) {
			if (txi.getUTXO() == null) {
				continue;
			}
			TransferExample.UTXOs.remove(txi.getUTXO().getId());

		}
		flag = true;

		return flag;
	}

	/**
	 * Obtiene el total de las transacciones entrantes
	 * 
	 * @return
	 */
	public float getInputsValue() {
		float total = 0;

		for (TransactionInput txi : inputs) {
			if (txi.getUTXO() == null) {
				continue;
			}
			total += txi.getUTXO().getVal();
		}

		return total;
	}

	/**
	 * Obtiene la suma de los valores de salida
	 * 
	 * @return
	 */
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput txo : outputs) {
			total += txo.getVal();
		}

		return total;
	}

	// Getters y Setters

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public PublicKey getSenderKey() {
		return senderKey;
	}

	public void setSenderKey(PublicKey senderKey) {
		this.senderKey = senderKey;
	}

	public PublicKey getReceiverKey() {
		return receiverKey;
	}

	public void setReceiverKey(PublicKey receiverKey) {
		this.receiverKey = receiverKey;
	}

	public float getVal() {
		return val;
	}

	public void setVal(float val) {
		this.val = val;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int increment) {
		this.increment = increment;
	}

	public List<TransactionInput> getInputs() {
		return inputs;
	}

	public void setInputs(List<TransactionInput> inputs) {
		this.inputs = inputs;
	}

	public List<TransactionOutput> getOutputs() {
		return outputs;
	}

	public void setOutputs(List<TransactionOutput> outputs) {
		this.outputs = outputs;
	}

}
