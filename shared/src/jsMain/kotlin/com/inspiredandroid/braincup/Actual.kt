package com.inspiredandroid.braincup

import com.russhwolf.settings.JsSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

internal actual val ApplicationDispatcher: CoroutineDispatcher = Dispatchers.Default
internal actual var settings: Settings? = JsSettings()