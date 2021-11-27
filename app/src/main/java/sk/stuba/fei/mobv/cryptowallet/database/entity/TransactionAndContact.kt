package sk.stuba.fei.mobv.cryptowallet.database.entity

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.parcelize.Parcelize

@Parcelize
data class TransactionAndContact(

    @Embedded val transaction: Transaction,
    @Relation(
        parentColumn = "public_key",
        entityColumn = "public_key"
    )
    val contact: Contact
): Parcelable
