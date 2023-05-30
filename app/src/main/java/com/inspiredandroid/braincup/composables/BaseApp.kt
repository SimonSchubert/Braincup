package com.inspiredandroid.braincup.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.inspiredandroid.braincup.R

@Composable
fun BaseScrollApp(
    title: String? = null,
    modifier: Modifier = Modifier,
    back: (() -> Unit?)? = null,
    children: @Composable() ColumnScope.() -> Unit
) {
    AppTheme {
        Column {
            MyTopAppBar(title, back)
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                children()
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
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
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