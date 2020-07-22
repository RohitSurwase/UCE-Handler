package com.jampez.uceh.features.gitlab

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Url

interface GitLabService {

    @POST
    fun createGitLabIssue(@Url url: String, @Header("Authorization") token: String, @Body gitlabPostData: GitLabPostData):
            Call<GitLabResponse>
}