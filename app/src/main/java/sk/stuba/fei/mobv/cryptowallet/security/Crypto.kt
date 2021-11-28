package sk.stuba.fei.mobv.cryptowallet.security

import android.util.Base64
import sk.stuba.fei.mobv.cryptowallet.database.entity.CipherData
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

class Crypto {

    companion object {
        private const val IV_SIZE = 16
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH = 256
        private const val ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/CBC/PKCS7Padding"
        private const val KEY_DERIVATION_FUNCTION = "PBKDF2WithHmacSHA1"
    }

    val secretKeyGenerator: SecretKeyGenerator =
        SecretKeyGenerator(KEY_LENGTH, SALT_LENGTH, ALGORITHM, KEY_DERIVATION_FUNCTION)
    private val initializationVectorGenerator: InitializationVectorGenerator =
        InitializationVectorGenerator(IV_SIZE)

    fun encrypt(plainText: String, pin: String): CipherData {

        val key = secretKeyGenerator.generateSecretKey(pin)
        val ivSpec = initializationVectorGenerator.createInitializationVector()
        val cipher = getCipher(Cipher.ENCRYPT_MODE, key.secretKey, ivSpec)!!
        val cipherText =
            cipher.doFinal(Base64.encode(plainText.toByteArray(Charsets.UTF_8), Base64.DEFAULT))

        return CipherData(key.salt, ivSpec.iv, cipherText)
    }

    fun decrypt(cipherData: CipherData, pin: String): String {

        val secretKey = secretKeyGenerator.generateSecretKey(pin, cipherData.salt)
        val ivSpec = IvParameterSpec(cipherData.iv)
        val cipher = getCipher(Cipher.DECRYPT_MODE, secretKey, ivSpec)!!

        return String(
            Base64.decode(cipher.doFinal(cipherData.cipherText), Base64.DEFAULT),
            Charsets.UTF_8
        )
    }

    private fun getCipher(mode: Int, secretKey: SecretKey, iv: IvParameterSpec): Cipher? {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(mode, secretKey, iv)
        return cipher
    }
}