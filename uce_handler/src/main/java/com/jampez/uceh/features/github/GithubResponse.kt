package com.jampez.uceh.features.github

import com.google.gson.annotations.SerializedName

data class GithubResponse (
        @SerializedName("id") val id: Long,
        @SerializedName("number") val number: Long,
        @SerializedName("title") val title: String,
        @SerializedName("message") val message: String
)