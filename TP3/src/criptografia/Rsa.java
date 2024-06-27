package criptografia;

import java.io.File;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.security.SecureRandom;

public class Rsa {
    private BigInteger p, q; // Números primos
    private BigInteger n, e, d; // Chaves pública e privada

    public static void main(String[] args) {
        File criptFile = new File("TP3/data/criptografado.db");
        File decriptFile = new File("TP3/data/decriptado.db");

        Rsa rsa = new Rsa(128); // Cria um objeto da classe Rsa com chaves de quantos bits passar como argumento (padrão 1024 bits)
        RandomAccessFile rafDb = null; // Cria um objeto da classe RandomAccessFile
        RandomAccessFile rafCript = null; // Cria um objeto da classe RandomAccessFile
        RandomAccessFile rafDecript = null; // Cria um objeto da classe RandomAccessFile
        try {
            rafDb = new RandomAccessFile("TP3/data/data.db", "r"); // Abre o arquivo "data.db" em modo de leitura
            rafCript = new RandomAccessFile(criptFile, "rw"); // Abre o arquivo "criptografado.db" em modo de leitura e escrita
            rafDecript = new RandomAccessFile(decriptFile, "rw"); // Abre o arquivo "decriptado.db" em modo de leitura e escrita

            while (rafDb.getFilePointer() < rafDb.length()) {
                byte b = rafDb.readByte(); // Lê um byte do arquivo
                BigInteger bigMessage = BigInteger.valueOf(b); // Converte o byte para um número grande
                BigInteger encrypted = rsa.encrypt(bigMessage); // Criptografa a mensagem
                rafCript.write(encrypted.toString().getBytes()); // Escreve a mensagem criptografada no arquivo
                rafCript.writeByte('\n'); // Escreve um novo byte de linha
            }

            rafCript.seek(0); // Volta para o início do arquivo
            while (rafCript.getFilePointer() < rafCript.length()) {
                String encryptedStr = rafCript.readLine(); // Lê uma linha do arquivo
                BigInteger encryptedMessage = new BigInteger(encryptedStr); // Converte a linha para um número grande
                BigInteger decrypted = rsa.decrypt(encryptedMessage); // Descriptografa a mensagem
                rafDecript.writeByte(decrypted.byteValue()); // Escreve a mensagem descriptografada no arquivo
            }

            rafDb.close(); // Fecha o arquivo "data.db"
            rafCript.close(); // Fecha o arquivo "criptografado.db"
            rafDecript.close(); // Fecha o arquivo "decriptado.db"
        
        } catch (Exception e) {
            e.printStackTrace(); // Imprime a pilha de chamadas de métodos que levou ao erro
        }
    }

    public Rsa() {
        generateKeys(1024); // Gera as chaves com 1024 bits (tamanho padrão para chaves RSA)
    }

    public Rsa(int bitLen) {
        generateKeys(bitLen); // Gera as chaves com o tamanho passado de bits
    }

    public static BigInteger generateRandomPrime(int bitLen) {
        SecureRandom random = new SecureRandom(); // Gera um número aleatório seguro contra previsões e ataques
        return BigInteger.probablePrime(bitLen, random); // Gera um número primo com o tamanho passado de bits com base no número aleatório 
        // (forma mais otimizada de gerar um número primo do que testar divisões por todos os números menores que ele)
    }

    public static BigInteger mdc(BigInteger a, BigInteger b) {
        while (!b.equals(BigInteger.ZERO)) {
            BigInteger temp = b;
            b = a.mod(b); // Usando o método mod() de BigInteger
            a = temp;
        }
        return a;
    }

    public BigInteger achaE(BigInteger z) {
        e = BigInteger.TWO;
        while (mdc(e, z).compareTo(BigInteger.ONE) != 0 || e.compareTo(z) < 0) {
            e = e.add(BigInteger.ONE);
        }
        return e;
    }

    public void generateKeys(int bitLen) {
        p = generateRandomPrime(bitLen); // Gera um número primo aleatório com o tamanho passado de bits
        q = generateRandomPrime(bitLen); // Gera um número primo aleatório com o tamanho passado de bits

        n = p.multiply(q); // Calcula o produto dos números primos
        BigInteger z = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // Calcula o valor de (p-1)(q-1)

        // e = achaE(z); // Calcula o valor de e (chave pública)
        e = BigInteger.valueOf(65537); // Valor de e comum para chaves RSA
        d = e.modInverse(z); // Calcula o valor de d (chave privada)
    }
    
    public BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n); // Calcula a mensagem elevada a e mod n
    }

    public BigInteger decrypt(BigInteger encryptedMessage) {
        return encryptedMessage.modPow(d, n); // Calcula a mensagem criptografada elevada a d mod n
    }

}
