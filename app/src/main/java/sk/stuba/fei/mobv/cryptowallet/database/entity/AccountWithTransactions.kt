package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AccountWithTransactions(

    @Embedded val account: Account,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "account_owner_id"
    )
    val transactions: List<Transaction>
)
