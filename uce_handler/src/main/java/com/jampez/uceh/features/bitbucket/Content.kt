package com.jampez.uceh.features.bitbucket

import com.google.gson.annotations.SerializedName

data class Content(
        @SerializedName("raw")
        val raw: String?
)