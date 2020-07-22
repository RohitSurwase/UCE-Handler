package com.jampez.uceh.features.gitlab

import com.jampez.uceh.utils.Mode

class GitLab private constructor(builder: Builder)  {

    companion object{
        var projectID = ""
        var accessToken = ""
    }

    class Builder{
        internal var projectID = ""
        internal var accessToken = ""

        fun setProjectID(projectID: String) : Builder {
            this.projectID = projectID
            return this
        }

        fun setAccessToken(accessToken: String) : Builder {
            this.accessToken = accessToken
            return this
        }

        fun build(): GitLab {
            return GitLab(this)
        }
    }


    init {
        projectID = builder.projectID
        accessToken = builder.accessToken
    }
}