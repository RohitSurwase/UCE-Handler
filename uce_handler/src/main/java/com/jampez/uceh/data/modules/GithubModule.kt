package com.jampez.uceh.data.modules

import com.jampez.uceh.data.repositories.GithubRepository
import org.koin.dsl.module

val githubModule = module { single { GithubRepository() } }