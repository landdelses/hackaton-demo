package com.hitss.hackaton.blockchain.util;

import java.util.ArrayList;
import java.util.List;

import com.hitss.hackaton.blockchain.vo.Block;
import com.hitss.hackaton.blockchain.vo.Transaction;

/**
 * Clase para realizar operaciones para el Chain
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class ChainUtil {

	/**
	 * Se valida que el blockchain sea v&aacute;lido
	 * 
	 * @param blockchain
	 * @return
	 */
	public static boolean isChainValid(List<Block> blockchain) {
		Block currentBlock = null;
		Block previousBlock = null;

		// Validamos que el blockchain tenga al menos dos elementos
		if (blockchain.size() > 2) {
			for (int i = 1; i < blockchain.size(); i++) {
				currentBlock = blockchain.get(i);
				previousBlock = blockchain.get(i - 1);

				// Se valida que el Hash almacenado sea el mismo que el que se genera,
				// lo cual nos valida que no ha sufrido alteraciones
				if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
					System.out.println("Los valores del Hash no corresponden");
					return false;
				}

				// Se valida que los Hash anteriores corresponden
				if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
					System.out.println("Los valores de los Hash anteriores no corresponden");
					return false;
				}
			}
		} else {
			return false;
		}
		return true;
	}

	/**
	 * Obtiene el String de la difucultad
	 * 
	 * @param difficulty
	 * @return
	 */
	public static String getDifficultyStr(int difficulty) {
		return new String(new char[difficulty]).replace('\0', '0');
	}

	/**
	 * Obtiene el MerkleRoot
	 * 
	 * @param txs
	 *            Lista de transacciones
	 * @return MerkleRoot
	 */
	public static String getMerkleRoot(List<Transaction> txs) {
		int count = txs.size();
		List<String> previousTreeLayer = new ArrayList<>();
		List<String> treeLayer = null;

		for (Transaction transaction : txs) {
			previousTreeLayer.add(transaction.getId());
		}
		treeLayer = previousTreeLayer;

		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(CryptoUtil.applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}
}
