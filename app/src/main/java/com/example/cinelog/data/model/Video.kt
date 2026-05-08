package com.example.cinelog.data.model

import com.google.gson.annotations.SerializedName

data class Video (
    @SerializedName("key") val key: String,       // El ID de YouTube
    @SerializedName("site") val site: String,     // Queremos asegurar que sea "YouTube"
    @SerializedName("type") val type: String      // Queremos que sea "Trailer"
)