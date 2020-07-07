package com.jampez.uceh.features.supportissue

import android.util.Base64
import android.util.Base64.encodeToString
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jampez.uceh.data.Resource
import com.jampez.uceh.features.bitbucket.BitBucketPostData
import com.jampez.uceh.features.bitbucket.BitBucketResponse
import com.jampez.uceh.features.bitbucket.BitBucketService
import com.jampez.uceh.features.bitbucket.Content
import com.jampez.uceh.features.github.GithubPostData
import com.jampez.uceh.features.github.GithubResponse
import com.jampez.uceh.features.github.GithubService
import com.jampez.uceh.features.network.RetrofitClient
import com.jampez.uceh.features.uce.UCEHandler
import com.jampez.uceh.utils.AppException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportIssueRepository{

    val GITHUB_BASE_URL = "https://api.github.com/repos/%1s/%2s/issues/"

    private val githubService: GithubService get() = RetrofitClient.getClient(GITHUB_BASE_URL)!!.create(GithubService::class.java)

    fun postGithubIssue(body: String?): LiveData<Resource<GithubResponse>> {
        val data = MutableLiveData<Resource<GithubResponse>>()
        val githubPostData = GithubPostData(
                title = "Crash Detected",
                body = body,
                assignees = arrayListOf("jampez77"),
                labels = arrayListOf("crash")
        )

        val url = String.format(GITHUB_BASE_URL, UCEHandler._githubService!!.userName, UCEHandler._githubService!!.repoName).dropLast(1)

        githubService.createGithubIssue(
                url,
                "Bearer ${UCEHandler._githubService!!.accessToken}",
                githubPostData)
                .enqueue(object : Callback<GithubResponse> {
            override fun onResponse(call: Call<GithubResponse>?,
                                    response: Response<GithubResponse>?) {

                if (response?.isSuccessful != null && response.isSuccessful) {
                    data.value = Resource.success(response.body())
                } else {
                    val errorConverter = RetrofitClient
                            .getClient(url)?.
                            responseBodyConverter<GithubResponse>(GithubResponse::class.java,
                                    arrayOfNulls<Annotation>(0))

                    if (response?.errorBody() != null) {
                        val error = errorConverter?.convert(response.errorBody())
                        data.value = Resource.failed(error)
                    }
                }

            }

            override fun onFailure(call: Call<GithubResponse>?, t: Throwable?) {
                data.value = Resource.error(AppException(t))
            }
        })

        return data
    }

    val BITBUCKET_ISSUE_URL = "https://api.bitbucket.org/2.0/repositories/%1s/%2s/issues/"

    private val bitBucketService: BitBucketService get() = RetrofitClient.getClient(BITBUCKET_ISSUE_URL)!!.create(BitBucketService::class.java)

    fun postBitBucketIssue(content: Content): LiveData<Resource<BitBucketResponse>> {
        val data = MutableLiveData<Resource<BitBucketResponse>>()
        val bitBucketPostData = BitBucketPostData(
                title = "Crash Detected",
                content = content
        )

        val url = String.format(BITBUCKET_ISSUE_URL, UCEHandler._bitBucketService!!.repoName, UCEHandler._bitBucketService!!.projectName)

        val authString = encodeToString(java.lang.String.format("%s:%s", UCEHandler._bitBucketService!!.userName, UCEHandler._bitBucketService!!.appPassword).toByteArray(), Base64.DEFAULT)

        val authHeader = "Basic $authString".trim()

        bitBucketService.createBitBucketIssue(
                url,
                authHeader,
                bitBucketPostData)
                .enqueue(object : Callback<BitBucketResponse> {
                    override fun onResponse(call: Call<BitBucketResponse>?,
                                            response: Response<BitBucketResponse>?) {
                        Log.d("onResponse", response.toString())
                        if (response?.isSuccessful != null && response.isSuccessful) {
                            data.value = Resource.success(response.body())
                        } else {
                            val errorConverter = RetrofitClient
                                    .getClient(url)?.
                                    responseBodyConverter<BitBucketResponse>(BitBucketResponse::class.java,
                                            arrayOfNulls<Annotation>(0))

                            if (response?.errorBody() != null) {
                                val error = errorConverter?.convert(response.errorBody())
                                data.value = Resource.failed(error)
                            }
                        }

                    }

                    override fun onFailure(call: Call<BitBucketResponse>?, t: Throwable?) {
                        Log.d("onFailure", t?.message.toString())
                        data.value = Resource.error(AppException(t))
                    }
                })

        return data
    }
}