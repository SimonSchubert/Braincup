package com.inspiredandroid.braincup

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope

actual var gCoroutineScope: CoroutineScope = GlobalScope
internal actual var settings: Settings? = null