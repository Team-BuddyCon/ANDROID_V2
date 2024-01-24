package com.yapp.buddycon.data.di.qualifiers

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LoginClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BuddyConClient
