package com.inspiredandroid.braincup

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher

internal expect val ApplicationDispatcher: CoroutineDispatcher
internal expect var settings: Settings?