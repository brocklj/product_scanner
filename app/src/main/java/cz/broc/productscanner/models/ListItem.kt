package cz.broc.productscanner.models

data class ListItem(
    val ean: String,
    val name: String = "",
    val upc: String = "",
    val count: Int,
    val datins: Long = 0,
    val datupd: Long = 0,
    val unit: String = ""
)
