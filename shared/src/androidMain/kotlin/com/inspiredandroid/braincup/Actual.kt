package com.inspiredandroid.braincup

import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope

actual var settings: Settings? = null
actual var gCoroutineScope: CoroutineScope = GlobalScope