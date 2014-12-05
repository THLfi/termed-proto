package fi.thl.termed.util;

import java.security.GeneralSecurityException;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.codec.binary.Base64.decodeBase64;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public final class EncryptionUtils {

    private static final String AES = "AES";
    private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5PADDING";

    private EncryptionUtils() {
    }

    /**
     * @param cipherTextAndIv array with encrypted message and
     *                        initialization vector both encoded in Base 64
     * @param secretKey       16 byte secret key encoded in Base 64
     * @return clear text
     * @throws java.security.GeneralSecurityException
     */
    public static String decrypt(String[] cipherTextAndIv, String secretKey)
            throws GeneralSecurityException {
        return decrypt(cipherTextAndIv[0], cipherTextAndIv[1], secretKey);
    }

    /**
     * @param cipherText encrypted message encoded in Base 64
     * @param iv         initialization vector encoded in Base 64
     * @param secretKey  16 byte secret key encoded in Base 64
     * @return clear text
     * @throws java.security.GeneralSecurityException
     */
    public static String decrypt(String cipherText, String iv, String secretKey)
            throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
        cipher.init(Cipher.DECRYPT_MODE,
                new SecretKeySpec(decodeBase64(secretKey), AES),
                new IvParameterSpec(decodeBase64(iv)));
        return new String(cipher.doFinal(decodeBase64(cipherText)), UTF_8);
    }

    /**
     * @param clearText clear text message to be encrypted
     * @param secretKey 16 byte secret key encoded in Base 64
     * @return array with encrypted message and
     *         initialization vector both encoded in Base 64
     * @throws java.security.GeneralSecurityException
     */
    public static String[] encrypt(String clearText, String secretKey)
            throws GeneralSecurityException {
        SecretKeySpec key = new SecretKeySpec(decodeBase64(secretKey), AES);
        Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        String encryptedMessage =
                encodeBase64String(cipher.doFinal(clearText.getBytes(UTF_8)));
        String iv = encodeBase64String(cipher.getIV());
        return new String[]{encryptedMessage, iv};
    }

}
