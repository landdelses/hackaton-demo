package com.hitss.hackaton.blockchain.vo;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hitss.hackaton.blockchain.TransferExample;

/**
 * Clase de las cuentas
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class Wallet {

	private static final String ALGORITHM_ECDSA = "ECDSA";
	private static final String ALGORITHM_SHA1PRNG = "SHA1PRNG";
	private static final String PROVIDER = "BC";
	private static final String CURVE_NAME = "prime192v1";

	// Sirve para firmar las transacciones
	private PrivateKey privateKey;

	// Actua como el identificador para recibir transferencias
	private PublicKey publicKey;

	// UTXOs de esta cartera
	private Map<String, TransactionOutput> UTXOs = new HashMap<>();

	public Wallet() {
		generateKeys();
	}

	/**
	 * Genera las llaves p&uacute;blica y privada
	 */
	public void generateKeys() {
		try {

			KeyPairGenerator kpg = KeyPairGenerator.getInstance(ALGORITHM_ECDSA, PROVIDER);
			SecureRandom sr = SecureRandom.getInstance(ALGORITHM_SHA1PRNG);
			ECGenParameterSpec egps = new ECGenParameterSpec(CURVE_NAME);

			kpg.initialize(egps, sr);
			KeyPair kp = kpg.generateKeyPair();

			privateKey = kp.getPrivate();
			publicKey = kp.getPublic();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Obtiene el balance de la Cartera
	 * 
	 * @param UTXOs
	 *            Mapa con las transacciones no Gastadas
	 * @return La sumatoria total de las transacciones pertenecientes a la cartera
	 */
	public float getBalance() {
		float total = 0;
		TransactionOutput UTXO = null;

		for (Map.Entry<String, TransactionOutput> item : TransferExample.UTXOs.entrySet()) {
			UTXO = item.getValue();
			if (UTXO.isMine(publicKey)) {
				this.UTXOs.put(UTXO.getId(), UTXO);
				total += UTXO.getVal();
			}
		}

		return total;
	}

	/**
	 * Realiza el envío de fondos a otra cartera
	 * 
	 * @param pKey
	 *            Llave p&uacute;blica del receptor
	 * @param coins
	 *            Cantidad a enviar
	 * @param UTXOs
	 *            Transacciones no gastadas
	 * @return Transacci&oacute;n a realizar
	 */
	public Transaction sendCoins(PublicKey pKey, float coins) {
		float total = 0;
		Transaction tx = null;
		List<TransactionInput> txi = new ArrayList<>();
		TransactionOutput txo = null;

		if (getBalance() < coins) {
			System.out.println("No se cuenta con fondos suficientes.");
			return tx;
		}

		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			txo = item.getValue();
			total += txo.getVal();
			txi.add(new TransactionInput(txo.getId()));
			if (total > coins)
				break;

		}

		tx = new Transaction(publicKey, pKey, coins, txi);
		tx.signValue(privateKey);

		for (TransactionInput input : txi) {
			this.UTXOs.remove(input.getTransactionId());
		}

		return tx;

	}

	// Getters y Setters

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

}
