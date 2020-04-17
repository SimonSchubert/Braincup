package com.inspiredandroid.braincup.composables

import androidx.compose.Composable
import androidx.ui.core.Alignment
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.foundation.VerticalScroller
import androidx.ui.layout.*
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
                Column(
                    modifier = Modifier.fillMaxWidth() + Modifier.fillMaxHeight(),
                    horizontalGravity = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    children()
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
            Column(
                modifier = Modifier.fillMaxWidth() + Modifier.fillMaxHeight(),
                horizontalGravity = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                children()
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