package sk.stuba.fei.mobv.cryptowallet.repository

interface IRepository<T> {

    suspend fun find(id: Long) : T?

    suspend fun insert(entity : T)

    suspend fun update(entity : T)

    suspend fun delete(entity : T)
}