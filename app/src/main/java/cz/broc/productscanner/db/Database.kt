package cz.broc.productscanner.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cz.broc.productscanner.db.entities.ListItemEntity

@Database(
    version = 1,
    exportSchema = true,
    entities = [ListItemEntity::class]
)

abstract class Database constructor(): RoomDatabase() {
    abstract fun listItemDao(): ListItemDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: cz.broc.productscanner.db.Database? = null

        fun getInstance(context: Context): cz.broc.productscanner.db.Database {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }


        private fun buildDatabase(context: Context): cz.broc.productscanner.db.Database {
            return Room.databaseBuilder(context, cz.broc.productscanner.db.Database::class.java, "TelomarDatabase")
                .build()
        }
    }
}