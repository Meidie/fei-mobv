package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance_table")
data class Balance(

    @PrimaryKey(autoGenerate = true)
    val balanceId: Long = 0L,

    @ColumnInfo(name = "account_owner_id")
    val accountOwnerId: Long,

    @ColumnInfo(name = "currency")
    var currency: String,

    @ColumnInfo(name = "amount")
    var amount: String,
)
