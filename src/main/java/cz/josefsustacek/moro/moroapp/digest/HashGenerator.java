package cz.josefsustacek.moro.moroapp.digest;

import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public interface HashGenerator {
    byte[] hash(String value, String salt);

    byte[] generateSalt(int length);
}

@Component
class Sha256HashGenerator implements HashGenerator {

    private final String algorithm = "SHA-256";

    public byte[] hash(String value, String salt) {

        MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("Cannot hash user's password", nsae);
        }

        messageDigest.update(salt.getBytes(StandardCharsets.UTF_8));

        byte[] hash = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));

        return hash;
    }

    @Override
    public byte[] generateSalt(@Min(10) int length) {

        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[length];
        random.nextBytes(salt);

        return salt;
    }

}
