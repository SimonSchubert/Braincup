package com.inspiredandroid.braincup.resources

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class VectorPathDataTest {

    /**
     * Compose hands a relative `m` to the platform's rMoveTo. Android 9's Skia resolves it
     * against the last drawn point instead of the closed subpath's start, so `z` followed
     * by `m` shifts every later subpath (issue #65: broken flags on Android 9).
     */
    @Test
    fun noRelativeMoveToAfterClose() {
        val drawables = File("src/commonMain/composeResources/drawable")
        assertTrue(drawables.isDirectory, drawables.absolutePath)
        val pathData = Regex("""pathData="([^"]*)"""")
        val closeThenRelativeMove = Regex("""[zZ]\s*m""")
        val offenders = drawables.listFiles { file -> file.extension == "xml" }!!
            .filter { file ->
                pathData.findAll(file.readText()).any { closeThenRelativeMove.containsMatchIn(it.groupValues[1]) }
            }
            .map { it.name }
            .sorted()
        assertTrue(offenders.isEmpty(), "Use an absolute M after z in: $offenders")
    }
}
