package sk.stuba.fei.mobv.cryptowallet.security

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

class SecretKeyGenerator(
    private val keyLength: Int,
    private val saltLength: Int,
    private val algorithm: String,
    private val keyDerivationFunction: String
) {

    fun generateSecretKey(pin: String): Key {

        val salt = ByteArray(saltLength)
        SecureRandom().nextBytes(salt)
        val pbKeySpec = PBEKeySpec(pin.toCharArray(), salt, 1324, keyLength)
        val secretKeyFactory: SecretKeyFactory =
            SecretKeyFactory.getInstance(keyDerivationFunction)
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded

        return Key(SecretKeySpec(keyBytes, algorithm), salt)
    }

    fun generateSecretKey(pin: String, salt: ByteArray): SecretKeySpec {
        val pbKeySpec = PBEKeySpec(pin.toCharArray(), salt, 1324, keyLength)
        val secretKeyFactory: SecretKeyFactory =
            SecretKeyFactory.getInstance(keyDerivationFunction)
        val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded

        return SecretKeySpec(keyBytes, algorithm)
    }
}