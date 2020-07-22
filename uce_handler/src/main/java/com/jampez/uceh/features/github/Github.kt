package com.jampez.uceh.features.github

import com.jampez.uceh.utils.Mode

class Github private constructor(builder: Builder)  {

    companion object{
        var repoName = ""
        var userName = ""
        var accessToken = ""
    }

    class Builder{
        internal var repoName = ""
        internal var userName = ""
        internal var accessToken = ""

        fun setRepoName(repoName: String) : Builder {
            this.repoName = repoName
            return this
        }

        fun setUsername(userName: String) : Builder {
            this.userName = userName
            return this
        }

        fun setAccessToken(accessToken: String) : Builder {
            this.accessToken = accessToken
            return this
        }

        fun build(): Github {
            return Github(this)
        }
    }


    init {
        repoName = builder.repoName
        userName = builder.userName
        accessToken = builder.accessToken
    }
}