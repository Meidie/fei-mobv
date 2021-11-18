package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    val transactionId: Long,

    @ColumnInfo(name = "account_owner_id")
    var accountOwnerId: Long,

    @ColumnInfo(name = "amount")
    var amount: Double,

    @ColumnInfo(name = "transaction_type")
    var type: TransactionType,

    @ColumnInfo(name = "public_key")
    var publicKey: String
)