package com.route.modal.ktM2

/**
 * Created by Ashwani Kumar Singh on 05,March,2023.
 */
data class KtResM2(
    val Documents: List<Document>,
    val _count: Int,
    val _rid: String
)

data class Document(
    val _attachments: String,
    val _etag: String,
    val _rid: String,
    val _self: String,
    val _ts: Int,
    val anchors: List<Anchor>,
    val id: String,
    val locId: String,
    val part: String
)

data class Anchor(
    val id: String,
    val offsets: List<Double>,
    val rotations: List<Double>,
    val routes: List<Route>,
    val sizes: List<Double>
)

data class Route(
    val id: String,
    val name: String
)