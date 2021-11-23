package sk.stuba.fei.mobv.cryptowallet.security

import javax.crypto.SecretKey

data class Key(val secretKey: SecretKey, val salt: ByteArray) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Key

        if (secretKey != other.secretKey) return false
        if (!salt.contentEquals(other.salt)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = secretKey.hashCode()
        result = 31 * result + salt.contentHashCode()
        return result
    }
}
