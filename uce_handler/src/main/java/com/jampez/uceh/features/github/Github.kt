package com.jampez.uceh.features.github

class Github private constructor(builder: Builder)  {

    enum class Mode {
        Automatic, Manual
    }

    companion object{
        var repoName = ""
        var userName = ""
        var accessToken = ""
        var buttonText = ""
        var mode = Mode.Automatic
    }

    class Builder{
        internal var repoName = ""
        internal var userName = ""
        internal var accessToken = ""
        internal var buttonText = ""
        internal var mode = Mode.Automatic

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

        fun setButtonText(buttonText: String) : Builder {
            this.buttonText = buttonText
            return this
        }

        fun setMode(mode: Mode) : Builder {
            this.mode = mode
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
        buttonText = builder.buttonText
        mode = builder.mode
    }
}