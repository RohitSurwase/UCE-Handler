package com.jampez.uceh.features.uce

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.jampez.uceh.data.Resource
import com.jampez.uceh.features.bitbucket.BitBucketResponse
import com.jampez.uceh.features.bitbucket.Content
import com.jampez.uceh.features.supportissue.SupportIssueRepository
import com.jampez.uceh.features.github.GithubResponse

class UCEViewModel(private val supportIssueRepository: SupportIssueRepository) : ViewModel(){

    fun postGithubIssue(body: String?) : LiveData<Resource<GithubResponse>> {
        return supportIssueRepository.postGithubIssue(body)
    }

    fun postBitBucketIssue(content: Content) : LiveData<Resource<BitBucketResponse>> {
        return supportIssueRepository.postBitBucketIssue(content)
    }
}