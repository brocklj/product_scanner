package cz.broc.productscanner.datasource

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.icu.text.SimpleDateFormat
import android.os.Environment
import android.os.Looper
import android.util.Log
import cz.broc.capriscan.restapi.api.IProduct
import cz.broc.productscanner.db.ListItemDao
import cz.broc.productscanner.db.entities.ListItemEntity
import cz.broc.productscanner.db.entities.asDomainModel
import cz.broc.productscanner.models.ListItem
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse
import java.io.File
import java.io.FileWriter
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ListDataSource @Inject constructor(
    @ApplicationContext var context: Context,
    var listItemDao: ListItemDao,
    var iProduct: IProduct
) {
   private val delimiter = ";" // TODO: "," nebo ";"?

    fun getAllListItems(): Flow<List<ListItem>> {
       return listItemDao.getAll().map { it.asDomainModel() }
    }

    fun getAllLScannedItems(): Flow<List<ListItem>> {
        return listItemDao.getAllLScannedItems().map { it.asDomainModel() }
    }

    fun findOne(ean: String): ListItem? {
        val item = listItemDao.findOne(ean)
        if(item == null) {
            throw NotFoundException()
        }
        return listOf<ListItemEntity>(item).asDomainModel().firstOrNull()
    }
    suspend fun insertListItemIntoDbForCsvReader() {
        deleteDb()

        val listItems = arrayListOf<ListItemEntity>()

        // #X TODO: Zkontroluj duplicity, pokud ANO vypiš chybu a skonči - řešit pomocí DB
       // reader.forEachLine {

        // for demonstration
        val productsStringResponse = iProduct.fetchProduct("generated_products.json").awaitResponse()

        if(productsStringResponse.isSuccessful) {
            val values = productsStringResponse.body()

                values?.forEach {
                    listItems.add(
                        ListItemEntity(
                            ean = it.ean ?: "",
                            upc = it.upc ?: "",
                            name = it.name ?: "",
                            count = it.count.toInt() ?: 0,
                            unit = it.unit ?: "",
                        )
                    )
                }

        listItemDao.insertAll(listItems)
        } else {
            throw Throwable(productsStringResponse.message().toString() ?: "")
        }
    }

    suspend fun setCount(ean: String, count: Int): Int {
        val item: ListItemEntity = this.listItemDao.findOne(ean)
        // TODO: Add DB datupd Trigger
        item.datupd = System.currentTimeMillis()
        item.count = count
        listItemDao.update(item)
        return count
    }

    suspend fun addCount(ean: String, count: Int): Int {
        val item: ListItemEntity = this.listItemDao.findOne(ean)
        if(item == null) {
            throw NotFoundException()
        }
        // TODO: Add DB datupd Trigger
        item.datupd = System.currentTimeMillis()
        item.count += count
        listItemDao.update(item)
        return count
    }

    suspend fun exportDbIntoCSV(context: Context, userId: String, items: List<ListItem>) {
        val dataToExport = mutableListOf<String>()
        val delimiter = ";"

        // Filtrovat pouze naskenované položky (příklad: pokud count > 0)
        val filteredItems = items.filter { it.count > 0 }

        filteredItems.forEach {
            val date = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(it.datupd)
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(it.datupd)
            dataToExport.add("${it.ean}$delimiter${it.upc}$delimiter${it.count}$delimiter$date$delimiter$time")
        }

        // Použij privátní veřejnou složku aplikace
        val directory = File(context.getExternalFilesDir(null), "scanner")
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val fileName = "${userId}_${System.currentTimeMillis()}.csv"
        val file = File(directory, fileName)

        try {
            withContext(Dispatchers.IO) {
                FileWriter(file).use { writer ->
                    dataToExport.forEach { line ->
                        writer.write("$line\n")
                    }
                }
            }
            Log.d("Export", "Soubor uložen: ${file.absolutePath}")
        } catch (e: IOException) {
            Log.e("Export", "Chyba při exportu CSV: ${e.message}")
            throw e
        }
    }


    suspend fun deleteDb() {
        listItemDao.deleteAll()

    }

    suspend fun setAllZeroCount() {
        listItemDao.setZeroCount()
    }
}