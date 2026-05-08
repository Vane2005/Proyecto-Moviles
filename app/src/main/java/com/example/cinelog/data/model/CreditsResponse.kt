package com.example.cinelog.data.model

import com.google.gson.annotations.SerializedName

data class CreditsResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("cast") val cast: List<CastMember>,
    @SerializedName("crew") val crew: List<CrewMember>
)

data class CastMember(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("character") val character: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class CrewMember(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("job") val job: String,
    @SerializedName("department") val department: String
)
