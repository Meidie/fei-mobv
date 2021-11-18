package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stellar_account_table")
data class Account(

    @PrimaryKey(autoGenerate = true)
    val accountId: Long = 0L,

    @ColumnInfo(name = "public_key")
    var publicKey: String,

    @ColumnInfo(name = "ciphered_private_key")
    var cipheredPrivateKey: String,

    @ColumnInfo(name = "balance")
    var balance: String
)