package cz.broc.capriscan.restapi.model

import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ProductResponse(
    val count: Int = 0,
    val ean: String,
    val upc: String,
    val name: String,
    val unit: String

)