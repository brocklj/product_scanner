package cz.broc.productscanner.db

import androidx.room.*
import cz.broc.productscanner.db.entities.ListItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListItemDao {
    @Query("SELECT * FROM ListItem order by datupd DESC")
    fun getAll(): Flow<List<ListItemEntity>>

    @Query("SELECT * FROM ListItem WHERE count > 0 order by datupd DESC")
    fun getAllLScannedItems(): Flow<List<ListItemEntity>>

    @Query("SELECT * FROM ListItem WHERE ean = :ean")
    fun findOne(ean: String): ListItemEntity

    @Insert(onConflict=OnConflictStrategy.ABORT)
    suspend fun insertAll(items: List<ListItemEntity>)

    @Update()
    suspend fun update(item:ListItemEntity)

    @Query("UPDATE ListItem set count = 0")
    suspend fun setZeroCount()

    @Query("DELETE FROM ListItem")
    suspend fun deleteAll()

}