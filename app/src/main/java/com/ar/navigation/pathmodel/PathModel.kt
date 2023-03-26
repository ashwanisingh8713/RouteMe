package com.ar.navigation.pathmodel

/**
 * Created by Ashwani Kumar Singh on 20,March,2023.
 */
data class PathModel(
    val Documents: List<Document> = emptyList(),
    val _count: Int = 0,
    val _rid: String = ""
)

data class Document(
    val _attachments: String,
    val _etag: String,
    val _rid: String,
    val _self: String,
    val _ts: Int,
    val cat: String,
    val city: String,
    val eaid: String,
    val ept: String,
    val id: String,
    val lat: Double,
    val len: Int,
    val loc: String,
    val long: Double,
    val part: String,
    val pts: List<Float>,
    val pub: Boolean,
    val said: String,
    val spt: String,
    val td: Int,
    val tu: Int,
    val ud: Float,
    val ver: Int
)