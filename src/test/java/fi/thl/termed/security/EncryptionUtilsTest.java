package fi.thl.termed.security;

import org.junit.Test;

import static com.google.common.base.Charsets.UTF_8;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EncryptionUtilsTest {

  private String secretKey =
      encodeBase64String("0123456789ABCDEF".getBytes(UTF_8));

  @Test
  public void shouldEncryptMessage() throws Exception {
    String message = "Hello, world!";
    String encryptedMessage = EncryptionUtils.encrypt(message, secretKey)[0];

    assertNotEquals(message, encryptedMessage);
  }

  @Test
  public void shouldDecryptMessage() throws Exception {
    String message = "Hello, world!";
    String[] encryptedMessage = EncryptionUtils.encrypt(message, secretKey);

    assertEquals(message, EncryptionUtils.decrypt(encryptedMessage, secretKey));
  }

  @Test
  public void differentClearTextsShouldProduceDifferentCiphertexts()
      throws Exception {
    String message1 = "Hello, world!";
    String message2 = "Hi, world!";

    assertNotEquals(EncryptionUtils.encrypt(message1, secretKey)[0],
                    EncryptionUtils.encrypt(message2, secretKey)[0]);
  }

  @Test
  public void initializationVectorsShouldBeDifferentInSubsequentCiphertexts()
      throws Exception {
    String message = "Hello, world!";

    String iv1 = EncryptionUtils.encrypt(message, secretKey)[1];
    String iv2 = EncryptionUtils.encrypt(message, secretKey)[1];

    assertNotEquals(iv1, iv2);
  }

}