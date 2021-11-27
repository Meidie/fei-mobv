package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo

data class PinData(

    @ColumnInfo(name = "salt", typeAffinity = ColumnInfo.BLOB)
    val salt: ByteArray,

    @ColumnInfo(name = "hashed_pin", typeAffinity = ColumnInfo.BLOB)
    val pin: ByteArray

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PinData

        if (!salt.contentEquals(other.salt)) return false
        if (!pin.contentEquals(other.pin)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = salt.contentHashCode()
        result = 31 * result + pin.contentHashCode()
        return result
    }
}
