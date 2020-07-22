package com.jampez.uceh.features.gitlab

import com.google.gson.annotations.SerializedName

data class GitLabResponse (
        @SerializedName("id") val id: Long,
        @SerializedName("iid") val iid: Long,
        @SerializedName("title") val title: String,
        @SerializedName("error") val error: String
)