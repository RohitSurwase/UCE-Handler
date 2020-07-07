package com.jampez.uceh.features.supportissue

import org.koin.dsl.module

val supportIssueModule = module { single { SupportIssueRepository() } }