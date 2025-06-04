package cz.broc.productscanner.db.entities

import android.content.Context
import cz.broc.productscanner.db.Database
import cz.broc.productscanner.db.ListItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppDatabaseModule {

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext context: Context): Database {
        return Database.getInstance(context)
    }

    @Provides
    fun providesListItemDao(database: Database): ListItemDao {
        return database.listItemDao()
    }

}