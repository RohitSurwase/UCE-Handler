package com.jampez.uceh.features.uce

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jampez.uceh.data.Resource
import com.jampez.uceh.data.repositories.GithubRepository
import com.jampez.uceh.features.github.GithubResponse

class UCEViewModel(private val uceRepository: GithubRepository) : ViewModel(){

    fun postGithubIssue(body: String?) : LiveData<Resource<GithubResponse>> {
        return uceRepository.postGithubIssue(body)
    }
}