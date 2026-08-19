package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.inspiredandroid.braincup.ui.theme.ContentMaxWidth

/**
 * The reading measure every result-style screen uses: centred, capped at [ContentMaxWidth], inset.
 *
 * Exposed as a modifier rather than only as [SectionColumn] because the same chain is applied two
 * ways: some callers wrap children in a column, others hand it straight to a card that is already
 * the section. Adding a column around those would insert a layout node for nothing.
 */
fun ColumnScope.sectionWidth(): Modifier = Modifier
    .widthIn(max = ContentMaxWidth)
    .padding(horizontal = 24.dp)
    .align(Alignment.CenterHorizontally)
    .fillMaxWidth()

/** [sectionWidth] as a column, for callers that are stacking several children inside the measure. */
@Composable
fun ColumnScope.SectionColumn(content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = sectionWidth(), content = content)
}
