package com.jampez.uceh.features.bitbucket

import com.google.gson.annotations.SerializedName

data class BitBucketResponse(
        @SerializedName("id") val id: Long,
        @SerializedName("title") val title: String,
        @SerializedName("type") val type: String
)