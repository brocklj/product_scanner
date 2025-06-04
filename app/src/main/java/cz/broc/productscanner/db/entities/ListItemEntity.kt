package cz.broc.productscanner.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import cz.broc.productscanner.models.ListItem

@Entity(tableName="ListItem", indices = [Index(value=["ean"], unique = true)])
data class ListItemEntity(
    @PrimaryKey() val ean: String = "",
    @ColumnInfo(name = "upc") var upc: String = "",
    @ColumnInfo(name = "name") var name: String = "",
    @ColumnInfo(name = "count") var count: Int = 0,
    @ColumnInfo(name = "unit") var unit:  String = "",
    @ColumnInfo(name = "datins") var datins: Long? = System.currentTimeMillis(),
    @ColumnInfo(name = "datupd") var datupd: Long? = System.currentTimeMillis(),
)


fun List<ListItemEntity>.asDomainModel(): List<ListItem> {
    return map {
        ListItem(
            it.ean,
            it.name,
            it.upc,
            it.count,
            it.datins ?: 0,
            it.datupd ?: 0,
            it.unit,
        )
    }
}