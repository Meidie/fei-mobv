package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stellar_account_table")
data class StellarAccount (

    @PrimaryKey(autoGenerate = true)
    val accountId: Long = 0L,

    @ColumnInfo(name = "username")
    var userName: String,

    @ColumnInfo(name = "password")
    var password: String,

    @ColumnInfo(name = "public_key")
    var publicKey: String,

    @ColumnInfo(name = "private_key")
    var privateKey: String,

    @ColumnInfo(name = "balance")
    var balance: Double
)