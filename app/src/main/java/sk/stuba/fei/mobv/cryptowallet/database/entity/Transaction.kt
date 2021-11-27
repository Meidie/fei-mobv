package sk.stuba.fei.mobv.cryptowallet.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "transaction_table")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    val transactionId: Long,

    @ColumnInfo(name = "account_owner_id")
    var accountOwnerId: Long,

    @ColumnInfo(name = "amount")
    var amount: String,

    @ColumnInfo(name = "transaction_type")
    var type: TransactionType,

    @ColumnInfo(name = "public_key")
    var publicKey: String,

    @ColumnInfo(name = "dateTime_created")
    var dateTime: String

): Parcelable