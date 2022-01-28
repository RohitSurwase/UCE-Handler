package com.jampez.uceh.features.bitbucket

class BitBucket private constructor(builder: Builder)  {

    companion object{
        var repoName = ""
        var userName = ""
        var projectName = ""
        var appPassword = ""
    }

    class Builder{
        internal var repoName = ""
        internal var userName = ""
        internal var projectName = ""
        internal var appPassword = ""

        fun setRepoName(repoName: String) : Builder {
            this.repoName = repoName
            return this
        }

        fun setUsername(userName: String) : Builder {
            this.userName = userName
            return this
        }

        fun setProjectName(projectName: String) : Builder {
            this.projectName = projectName
            return this
        }

        fun setAppPassword(appPassword: String) : Builder {
            this.appPassword = appPassword
            return this
        }

        fun build(): BitBucket {
            return BitBucket(this)
        }
    }


    init {
        repoName = builder.repoName
        userName = builder.userName
        projectName = builder.projectName
        appPassword = builder.appPassword
    }
}