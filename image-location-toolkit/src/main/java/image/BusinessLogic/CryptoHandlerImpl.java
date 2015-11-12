package image.BusinessLogic;

import org.apache.log4j.Logger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class CryptoHandlerImpl implements CryptoHandler {
    private static SecretKey key;
    final static Logger log = Logger.getLogger(CryptoHandlerImpl.class);


    public CryptoHandlerImpl(){
        byte bytes[] = {23, 12, 98, 121, 5, 98, 13, 49};
        key = new SecretKeySpec(bytes, "DES");
    }

    /**
     * Obscures an image id displayed in its return page URL by encrypting it.
     *
     * @param id image id to be encrypted
     * @return String representing the encrypted image id
     */
    @Override
    public String encryptId(int id) {
        try {
            // Initializes the id into a byte array
            byte[] data = Integer.toString(id).getBytes();

            Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryption = ecipher.doFinal(data);

            return new BASE64Encoder().encode(encryption).replaceAll("=", "-").replaceAll("/", "_").replaceAll("\\+", ".");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException  e) {
            log.error("messed up encryption", e);
        }
        return "ERROR";
    }

    /**
     * Decrypts an encrypted image id.
     *
     * @param encryptedStr a String representing the encrypted image id
     * @return the decrypted image id, as an int
     */
    @Override
    public int decryptId(String encryptedStr) {
        try {
            encryptedStr = encryptedStr.replaceAll("_", "/").replaceAll("-", "=").replaceAll("\\.", "+");
            Cipher dcipher = Cipher.getInstance("DES");
            dcipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decryption = new BASE64Decoder().decodeBuffer(encryptedStr);
            byte[] data = dcipher.doFinal(decryption);

            return Integer.parseInt(new String(data, "UTF8"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IOException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            log.error("messed up dectyption, e");
        }
        return -1;
    }
}