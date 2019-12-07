package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.Center
import androidx.ui.layout.Column
import androidx.ui.layout.ColumnScope
import androidx.ui.material.TopAppBar
import com.inspiredandroid.braincup.R

@Composable
fun BaseScrollApp(
    title: String? = null,
    back: (() -> Unit?)? = null,
    children: @Composable() ColumnScope.() -> Unit
) {
    AppTheme {
        Column {
            MyTopAppBar(title, back)
            VerticalScroller {
                Center {
                    Column {
                        children()
                    }
                }
            }
        }
    }
}

@Composable
fun BaseApp(
    title: String? = null,
    back: (() -> Unit?)? = null,
    children: @Composable() ColumnScope.() -> Unit
) {
    AppTheme {
        Column {
            MyTopAppBar(title, back)
            Center {
                Column {
                    children()
                }
            }
        }
    }
}

@Composable
fun MyTopAppBar(title: String? = null, back: (() -> Unit?)? = null) {
    if (title != null) {
        if (back == null) {
            TopAppBar(title = { Text(title) })
        } else {
            TopAppBar(title = { Text(title) }, navigationIcon = {
                VectorImageButton(id = R.drawable.ic_baseline_arrow_back_24) {
                    back()
                }
            })
        }
    }
}