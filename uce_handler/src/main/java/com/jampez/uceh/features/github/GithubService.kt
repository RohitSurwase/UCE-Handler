package com.jampez.uceh.features.github

import retrofit2.Call
import retrofit2.http.*

interface GithubService {

    @POST
    fun createGithubIssue(@Url url: String, @Header("Authorization") token: String, @Body githubPostData: GithubPostData):
            Call<GithubResponse>
}