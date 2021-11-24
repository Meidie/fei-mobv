package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo

data class CipherData(

    @ColumnInfo(name = "salt", typeAffinity = ColumnInfo.BLOB)
    val salt: ByteArray,

    @ColumnInfo(name = "initialization_vector", typeAffinity = ColumnInfo.BLOB)
    val iv: ByteArray,

    @ColumnInfo(name = "cipher_text", typeAffinity = ColumnInfo.BLOB)
    val cipherText: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CipherData

        if (!salt.contentEquals(other.salt)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (!cipherText.contentEquals(other.cipherText)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = salt.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + cipherText.contentHashCode()
        return result
    }
}
