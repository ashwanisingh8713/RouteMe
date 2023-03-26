package com.ar.navigation.pathmodel

import com.google.gson.Gson


/**
 * Created by Ashwani Kumar Singh on 20,March,2023.
 */
class RawJson {
    companion object {
        fun getPathModel(): PathModel {
            val gson = Gson()
            return gson.fromJson(Ashwani_work_station, PathModel::class.java)
        }

        private val Ashwani_work_station = "{\n" +
                "  \"_rid\": \"icRZAL30E+A=\",\n" +
                "  \"Documents\": [\n" +
                "    {\n" +
                "      \"id\": \"202303190718195131379789\",\n" +
                "      \"ver\": 1,\n" +
                "      \"part\": \"none\",\n" +
                "      \"city\": \"1356410365\",\n" +
                "      \"lat\": 12.90944,\n" +
                "      \"long\": 77.58521,\n" +
                "      \"loc\": \"Ashwani Home\",\n" +
                "      \"spt\": \"Ashwani work station\",\n" +
                "      \"ept\": \"Bedroom Balcony \",\n" +
                "      \"said\": \"d3b47820-98e4-491d-84eb-ed268b66e64a\",\n" +
                "      \"eaid\": \"\",\n" +
                "      \"pub\": true,\n" +
                "      \"cat\": \"Other\",\n" +
                "      \"len\": 8,\n" +
                "      \"tu\": 0,\n" +
                "      \"td\": 0,\n" +
                "      \"ud\": 0.01,\n" +
                "      \"pts\": [\n" +
                "        -8,\n" +
                "        0,\n" +
                "        -28,\n" +
                "        -23,\n" +
                "        21,\n" +
                "        15,\n" +
                "        -29,\n" +
                "        20,\n" +
                "        65,\n" +
                "        -37,\n" +
                "        21,\n" +
                "        115,\n" +
                "        -62,\n" +
                "        18,\n" +
                "        158,\n" +
                "        -82,\n" +
                "        20,\n" +
                "        205,\n" +
                "        -87,\n" +
                "        19,\n" +
                "        255,\n" +
                "        -80,\n" +
                "        19,\n" +
                "        304,\n" +
                "        -53,\n" +
                "        18,\n" +
                "        347,\n" +
                "        -3,\n" +
                "        18,\n" +
                "        356,\n" +
                "        46,\n" +
                "        17,\n" +
                "        369,\n" +
                "        85,\n" +
                "        19,\n" +
                "        401,\n" +
                "        113,\n" +
                "        21,\n" +
                "        443,\n" +
                "        140,\n" +
                "        19,\n" +
                "        486,\n" +
                "        162,\n" +
                "        20,\n" +
                "        531,\n" +
                "        170,\n" +
                "        19,\n" +
                "        581,\n" +
                "        183,\n" +
                "        18,\n" +
                "        630,\n" +
                "        192,\n" +
                "        18,\n" +
                "        663\n" +
                "      ],\n" +
                "      \"_rid\": \"icRZAL30E+DnAAAAAAAAAA==\",\n" +
                "      \"_self\": \"dbs/icRZAA==/colls/icRZAL30E+A=/docs/icRZAL30E+DnAAAAAAAAAA==/\",\n" +
                "      \"_etag\": \"\\\"0f01d93a-0000-0700-0000-6416b8040000\\\"\",\n" +
                "      \"_attachments\": \"attachments/\",\n" +
                "      \"_ts\": 1679210500\n" +
                "    }\n" +
                "  ],\n" +
                "  \"_count\": 1\n" +
                "}"
    }
}