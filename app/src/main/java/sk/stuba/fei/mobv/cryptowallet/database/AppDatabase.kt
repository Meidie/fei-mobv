package sk.stuba.fei.mobv.cryptowallet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import sk.stuba.fei.mobv.cryptowallet.database.dao.ContactDao
import sk.stuba.fei.mobv.cryptowallet.database.dao.AccountDao
import sk.stuba.fei.mobv.cryptowallet.database.dao.TransactionDao
import sk.stuba.fei.mobv.cryptowallet.database.entity.Contact
import sk.stuba.fei.mobv.cryptowallet.database.entity.Account
import sk.stuba.fei.mobv.cryptowallet.database.entity.Transaction

@Database(
    entities = [Contact::class, Account::class, Transaction::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun transactionDao(): TransactionDao
    abstract fun AccountDao(): AccountDao

    companion object {

        /**
         * Singleton pattern to prevent multiple instances of database to be opened at the same time
         *
         * The value of a volatile variable will never be cached, and all writes and
         * reads will be done to and from the main memory. It means that changes made by one
         * thread to shared data are visible to other threads.
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {

            // if the INSTANCE is not null, then return it or if it is, then create the database
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "crypto_wallet_database"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}