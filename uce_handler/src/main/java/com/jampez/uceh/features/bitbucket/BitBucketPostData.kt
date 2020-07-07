package com.jampez.uceh.features.bitbucket

import com.google.gson.annotations.SerializedName

data class BitBucketPostData(
        @SerializedName("title")
        val title: String,

        @SerializedName("content")
        val content: Content
)