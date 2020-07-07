package com.jampez.uceh.features.bitbucket

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface BitBucketService {
    @POST
    fun createBitBucketIssue(@Url url: String, @Header("Authorization") token: String, @Body bitBucketPostData: BitBucketPostData):
            Call<BitBucketResponse>
}