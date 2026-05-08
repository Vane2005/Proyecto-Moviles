package com.example.cinelog.data.model

import com.google.gson.annotations.SerializedName

class VideoResponse (
    @SerializedName("results") val results: List<Video>
)