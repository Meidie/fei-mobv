package sk.stuba.fei.mobv.cryptowallet.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "contact_table")
data class Contact (

    @PrimaryKey(autoGenerate = true)
    val contactId: Long = 0L,

    @ColumnInfo(name = "account_owner_id")
    var accountOwnerId: Long,

    @ColumnInfo(name = "contact_name")
    var name: String,

    @ColumnInfo(name = "public_key")
    var publicKey: String
) : Parcelable {
    constructor() : this(0L, 0L, "", "")
}