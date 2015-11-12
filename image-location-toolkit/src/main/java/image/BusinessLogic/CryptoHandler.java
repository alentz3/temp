package image.BusinessLogic;

/**
 * Created by 571743 on 11/5/2015.
 */
public interface CryptoHandler {
    String encryptId(int id);

    int decryptId(String encryptedStr);
}
