package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AccountWithBalances(

    @Embedded val account: Account,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "account_owner_id"
    )
    val balances: List<Balance>

)