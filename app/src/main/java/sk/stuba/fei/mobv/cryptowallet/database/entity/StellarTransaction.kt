package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import sk.stuba.fei.mobv.cryptowallet.model.enum.TransactionType

@Entity(tableName = "transaction_table")
data class StellarTransaction (

    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0L,

    @ColumnInfo(name = "account_owner_id")
    var accountOwnerId: Long,

    @ColumnInfo(name = "amount")
    var amount: Double,

    // TODO enum -> treba converter
    @ColumnInfo(name = "transaction_type")
    var type: String,

    @ColumnInfo(name = "public_key")
    var publicKey: String
)