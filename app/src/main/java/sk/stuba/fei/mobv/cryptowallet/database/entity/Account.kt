package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "account_table")
data class Account(

    @PrimaryKey(autoGenerate = true)
    val accountId: Long = 0L,

    @ColumnInfo(name = "public_key")
    val publicKey: String,

    @Embedded(prefix = "pk_")
    val privateKeyData: CipherData?,

    @Embedded(prefix = "pin_")
    val pinData: PinData,

    @ColumnInfo(name = "active_account")
    var active: Boolean
)