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

    @ColumnInfo(name = "contact_first_name")
    var firstName: String,

    @ColumnInfo(name = "contact_last_name")
    var lastName: String,

    @ColumnInfo(name = "public_key")
    var publicKey: String
) : Parcelable