package com.jampez.uceh.features.gitlab

import com.google.gson.annotations.SerializedName

data class GitLabPostData(
        @SerializedName("title")
        val title: String,

        @SerializedName("description")
        val description: String?,

        @SerializedName("labels")
        val labels: List<String>
)