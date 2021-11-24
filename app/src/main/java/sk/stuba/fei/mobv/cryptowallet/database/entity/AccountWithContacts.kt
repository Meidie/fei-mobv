package sk.stuba.fei.mobv.cryptowallet.database.entity

import androidx.room.Embedded
import androidx.room.Relation

data class AccountWithContacts(

    @Embedded val account: Account,
    @Relation(
        parentColumn = "accountId",
        entityColumn = "account_owner_id"
    )
    val contacts: List<Contact>
)
