import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

// Source: https://github.com/Multibit-Legacy/multibit/blob/21e2e9d653d291a7dc36d21b6fc14b2a0da48985/src/main/java/org/multibit/crypto/KeyCrypterOpenSSL.java
// The is not the entire file but just what I need to do the decryption
public class KeyCrypterOpenSSL {

    // The string encoding to use when converting strings to bytes
    public static final String STRING_ENCODING = "UTF-8";

    // OpenSSL salted prefix text
    public static final String OPENSSL_SALTED_TEXT = "Salted__";

    // OpenSSL salted prefix bytes - also used as magic number for encrypted key file.
    public byte[] openSSLSaltedBytes;

    // Magic text that appears at the beginning of every OpenSSL encrypted file.
    // Used in identifying encrypted key files.
    private String openSSLMagicText = null;

    public static final int NUMBER_OF_CHARACTERS_TO_MATCH_IN_OPENSSL_MAGIC_TEXT = 10;

    // number of times the password & salt are hashed during key creation.
    private static final int NUMBER_OF_ITERATIONS = 1024;

    // Key length.
    private static final int KEY_LENGTH = 256;

    // Initialization vector length.
    private static final int IV_LENGTH = 128;

    public KeyCrypterOpenSSL() {
        try {
            openSSLSaltedBytes = OPENSSL_SALTED_TEXT.getBytes(STRING_ENCODING);

            openSSLMagicText = Base64.encodeBase64String(
                    KeyCrypterOpenSSL.OPENSSL_SALTED_TEXT.getBytes(KeyCrypterOpenSSL.STRING_ENCODING)).substring(0,
                    KeyCrypterOpenSSL.NUMBER_OF_CHARACTERS_TO_MATCH_IN_OPENSSL_MAGIC_TEXT);

        } catch (UnsupportedEncodingException e) {
            System.out.printf("Error: Could not construct EncrypterDecrypter - Message: %s", e.getMessage());
        }
    }

    /**
     * Decrypt text previously encrypted with this class.
     *
     * @param textToDecode
     *            The code to decrypt
     * @param password THe password to use
     *            password to use for decryption
     * @return The decrypted text
     * @throws KeyCrypterException
     */
    public String decrypt(String textToDecode, CharSequence password) throws KeyCrypterException {
        try {
            final byte[] decodeTextAsBytes = Base64.decodeBase64(textToDecode.getBytes(STRING_ENCODING));

            // Strip off the bytes due to the OPENSSL_SALTED_TEXT prefix text.
            int saltPrefixTextLength = openSSLSaltedBytes.length;

            byte[] cipherBytes = new byte[decodeTextAsBytes.length - saltPrefixTextLength];
            System.arraycopy(decodeTextAsBytes, saltPrefixTextLength, cipherBytes, 0, decodeTextAsBytes.length
                    - saltPrefixTextLength);

            byte[] decryptedBytes = decrypt(cipherBytes, password);

            return new String(decryptedBytes, STRING_ENCODING).trim();
        } catch (Exception e) {
            throw new KeyCrypterException("Could not decrypt input string", e);
        }
    }

    /**
     * Decrypt bytes previously encrypted with this class.
     *
     * @param bytesToDecode
     *            The bytes to decrypt
     * @param password The password to use
     *            password to use for decryption
     * @return The decrypted bytes
     * @throws KeyCrypterException
     */
    public byte[] decrypt(byte[] bytesToDecode, CharSequence password) throws KeyCrypterException {
        try {
            int salt_length = OPENSSL_SALTED_TEXT.length();

            // separate the salt and bytes to decrypt
            byte[] salt = new byte[salt_length];

            System.arraycopy(bytesToDecode, 0, salt, 0, salt_length);

            byte[] cipherBytes = new byte[bytesToDecode.length - salt_length];
            System.arraycopy(bytesToDecode, salt_length, cipherBytes, 0, bytesToDecode.length - salt_length);

            org.spongycastle.crypto.params.ParametersWithIV key = (org.spongycastle.crypto.params.ParametersWithIV) getAESPasswordKey(password, salt);

            // decrypt the message
            org.spongycastle.crypto.BufferedBlockCipher cipher = new org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher(new org.spongycastle.crypto.modes.CBCBlockCipher(new org.spongycastle.crypto.engines.AESFastEngine()));
            cipher.init(false, key);

            byte[] decryptedBytes = new byte[cipher.getOutputSize(cipherBytes.length)];
            final int processLength = cipher.processBytes(cipherBytes, 0, cipherBytes.length, decryptedBytes, 0);
            final int doFinalLength = cipher.doFinal(decryptedBytes, processLength);

            return Arrays.copyOf(decryptedBytes, processLength + doFinalLength);
        } catch (Exception e) {
            throw new KeyCrypterException("Could not decrypt input string", e);
        }
    }

    private org.spongycastle.crypto.CipherParameters getAESPasswordKey(CharSequence password, byte[] salt) throws KeyCrypterException {
        try {
            org.spongycastle.crypto.PBEParametersGenerator generator = new org.spongycastle.crypto.generators.OpenSSLPBEParametersGenerator();
            generator.init(org.spongycastle.crypto.PBEParametersGenerator.PKCS5PasswordToBytes(convertToCharArray(password)), salt, NUMBER_OF_ITERATIONS);

            // Intellij gives the following error message:
            //   Inconvertible types; cannot cast 'CipherParameters' to 'org.spongycastle.crypto.params.ParametersWithIV'
            // But the code compiles and works, so I guess whatever 😅
            org.spongycastle.crypto.params.ParametersWithIV key = (org.spongycastle.crypto.params.ParametersWithIV) generator.generateDerivedParameters(KEY_LENGTH, IV_LENGTH);

            return key;
        } catch (Exception e) {
            throw new KeyCrypterException("Could not generate key from password of length " + password.length()
                    + " and salt '" + Hex.encodeHexString(salt), e);
        }
    }

    private char[] convertToCharArray(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }

        char[] charArray = new char[charSequence.length()];
        for(int i = 0; i < charSequence.length(); i++) {
            charArray[i] = charSequence.charAt(i);
        }
        return charArray;
    }
}
