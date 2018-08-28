package com.hitss.hackaton.blockchain.util;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;

/**
 * Funciones generales para la criptograf&iacute;a
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class CryptoUtil {

	private static final String SHA256 = "SHA-256";
	private static final String ECDSA = "ECDSA";
	private static final String BC = "BC";
	private static final String UTF8 = "UTF-8";

	/**
	 * Aplica el algoritmo Sha256 a la cadena proporcionada
	 * 
	 * @param input
	 *            Cadena a la que se le aplicar&aacute; el algoritmo Sha256
	 * @return Cadena con el algoritmo Sha256 aplicado
	 */
	public static String applySha256(String strInput) {
		try {
			MessageDigest md = MessageDigest.getInstance(SHA256);

			byte[] hash = md.digest(strInput.getBytes(UTF8));
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String strHex = Integer.toHexString(0xff & hash[i]);
				if (strHex.length() == 1)
					hexString.append('0');
				hexString.append(strHex);
			}

			return hexString.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Firma la cadena de enviada como par&aacute;metro
	 * 
	 * @param privateKey
	 *            Llave privada para realizar el firmado
	 * @param input
	 *            Cadena que ser&aacute; firmada
	 * @return Bytes de la cadena firmada
	 */
	public static byte[] applyECDSASignature(PrivateKey privateKey, String input) {

		Signature dsaSignature = null;
		byte[] realSignature = null;
		byte[] strByte = null;

		try {

			dsaSignature = Signature.getInstance(ECDSA, BC);
			dsaSignature.initSign(privateKey);
			strByte = input.getBytes();
			dsaSignature.update(strByte);
			realSignature = dsaSignature.sign();

		} catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
			System.out.println("Error al firmar la cadena: " + ex.getMessage());
		}

		return realSignature;
	}

	/**
	 * Verifica la firma de la cadena proporcionada
	 * 
	 * @param publicKey
	 * @param data
	 * @param signature
	 * @return
	 */
	public static boolean verifyECDSASignature(PublicKey publicKey, String data, byte[] signature) {

		Signature dsaSignature = null;
		Boolean valid = false;

		try {

			dsaSignature = Signature.getInstance(ECDSA, BC);
			dsaSignature.initVerify(publicKey);
			dsaSignature.update(data.getBytes());
			valid = dsaSignature.verify(signature);

		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException | SignatureException ex) {
			System.out.println("Error verificar la firma de la cadena: " + ex.getMessage());
		}

		return valid;

	}
	
	/**
	 * Obtiene la llave en codificaci&oacute;n Base64
	 * 
	 * @param key Llave a codificar en Base64
	 * @return Llave codificada en Base64
	 */
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

}
