package com.hitss.hackaton.blockchain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.hitss.hackaton.blockchain.vo.Block;
import com.hitss.hackaton.blockchain.vo.Transaction;
import com.hitss.hackaton.blockchain.vo.TransactionInput;
import com.hitss.hackaton.blockchain.vo.TransactionOutput;
import com.hitss.hackaton.blockchain.vo.Wallet;

/**
 * Ejemplo para comprender la realización de transacciones en Blockchain
 * 
 * @author Rams&eacute;s Hern&aacute;ndez
 *
 */
public class TransferExample {

	private static List<Block> blockChain = new ArrayList<>();
	public static Map<String, TransactionOutput> UTXOs = new HashMap<>();
	private static int difficulty = 5;
	public static float minTxs = 0.1f;
	private static Wallet walletOne;
	private static Wallet walletTwo;
	private static Transaction genesisTx;

	public static void main(String[] args) {

		Security.addProvider(new BouncyCastleProvider());

		walletOne = new Wallet();
		walletTwo = new Wallet();
		Wallet base = new Wallet();

		// Se crea la transacción Genesis
		genesisTx = new Transaction(base.getPublicKey(), walletOne.getPublicKey(), 100f, null);
		genesisTx.signValue(base.getPrivateKey());
		genesisTx.setId("0");
		genesisTx.getOutputs()
				.add(new TransactionOutput(genesisTx.getReceiverKey(), genesisTx.getVal(), genesisTx.getId()));
		UTXOs.put(genesisTx.getOutputs().get(0).getId(), genesisTx.getOutputs().get(0));

		System.out.println("Creando y minando el bloque Genesis ... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTx);
		addBlock(genesis);

		// Procedemos a realizar las transacciones
		Block block1 = new Block(genesis.getHash());
		System.out.println("\nEl Balance de WalletOne es: " + walletOne.getBalance());
		System.out.println("WalletOne realiza el envío de 40 coins a WalletTwo ...");
		Transaction t1 = walletOne.sendCoins(walletTwo.getPublicKey(), 40f);
		block1.addTransaction(t1);

		addBlock(block1);

		System.out.println("\nEl Balance de WalletOne es: " + walletOne.getBalance());
		System.out.println("El Balance de WalletTwo es: " + walletTwo.getBalance());

		// Segunda transacción
		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletOne intenta realizar el envío de 300 coins ...");
		block2.addTransaction(walletOne.sendCoins(walletTwo.getPublicKey(), 300f));

		addBlock(block2);

		System.out.println("\nEl Balance de WalletOne es: " + walletOne.getBalance());
		System.out.println("El Balance de WalletTwo es: " + walletTwo.getBalance());

		// Tercera transacción
		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletTwo intenta realizar el envío de 3 coins a WalletOne ...");
		block3.addTransaction(walletTwo.sendCoins(walletOne.getPublicKey(), 3));

		addBlock(block3);

		System.out.println("\nEl Balance de WalletOne es: " + walletOne.getBalance());
		System.out.println("El Balance de WalletTwo es: " + walletTwo.getBalance());

		isChainValid();
		
	}

	/**
	 * Valifa que la cadena sea válida
	 * 
	 * @return
	 */
	public static Boolean isChainValid() {
		Block currentBlock;
		Block previousBlock;
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		Map<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
		tempUTXOs.put(genesisTx.getOutputs().get(0).getId(), genesisTx.getOutputs().get(0));

		// Se itera a través del BlockChain para verificar las cadenas Hash
		for (int i = 1; i < blockChain.size(); i++) {

			currentBlock = blockChain.get(i);
			previousBlock = blockChain.get(i - 1);

			// Se comparan el hash almacenado con el nuevo hash calculado
			if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
				System.out.println("Los Registros Hash no son iguales ...");
				return false;
			}
			// Se compara el hash previo almacenado con el hash previo calculado
			if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
				System.out.println("Los Registros Hash previos no son iguales ...");
				return false;
			}
			// Se checa si el hash fué minado
			if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
				System.out.println("El bloque no fué minado ...");
				return false;
			}

			// Se iteran las transacciones actuales
			TransactionOutput tempOutput;
			for (int t = 0; t < currentBlock.getTxs().size(); t++) {
				Transaction currentTransaction = currentBlock.getTxs().get(t);

				if (!currentTransaction.verifySignature()) {
					System.out.println("La firma en la transacción " + t + " no es válida");
					return false;
				}
				if (currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println(
							"Las transacciones de entrada no son las mismas que las de salida en la transacción " + t
									+ "");
					return false;
				}

				for (TransactionInput input : currentTransaction.getInputs()) {
					tempOutput = tempUTXOs.get(input.getTransactionId());

					if (tempOutput == null) {
						System.out.println("Falta la referencia de entrada de la transacción número " + t);
						return false;
					}

					if (input.getUTXO().getVal() != tempOutput.getVal()) {
						System.out.println("La referencia de entrada de la transacción " + t + "no es válida");
						return false;
					}

					tempUTXOs.remove(input.getTransactionId());
				}

				for (TransactionOutput output : currentTransaction.getOutputs()) {
					tempUTXOs.put(output.getId(), output);
				}

				if (currentTransaction.getOutputs().get(0).getReceiver() != currentTransaction.getReceiverKey()) {
					System.out.println("Para la transacción " + t + " el receptor no es quien debería ser.");
					return false;
				}
				if (currentTransaction.getOutputs().get(1).getReceiver() != currentTransaction.getSenderKey()) {
					System.out.println(
							" La transacción " + t + " de salida para el 'cambio' no es para el que envió el pago");
					return false;
				}

			}

		}
		System.out.println("El Blockchain es válido");
		return true;
	}

	/**
	 * Agrega el bloque a la cadena
	 * 
	 * @param newBlock
	 */
	public static void addBlock(Block newBlock) {
		newBlock.mineBlock(difficulty);
		blockChain.add(newBlock);
	}

}
