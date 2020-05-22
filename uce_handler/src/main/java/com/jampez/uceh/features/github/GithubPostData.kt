package com.jampez.uceh.features.github

import com.google.gson.annotations.SerializedName

data class GithubPostData(
        @SerializedName("title")
        val title: String,

        @SerializedName("body")
        val body: String?,

        @SerializedName("assignees")
        val assignees: List<String>,

        @SerializedName("labels")
        val labels: List<String>
)