package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_back
import org.jetbrains.compose.resources.stringResource

internal val LocalIsCompactHeight = staticCompositionLocalOf { false }
private val CompactHeightThreshold = 480.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    title: String? = null,
    onBack: (() -> Unit)? = null,
    scrollable: Boolean = true,
    content: @Composable ColumnScope.() -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (title != null) {
                        Text(title)
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.hoverHand(),
                        ) {
                            Icon(
                                imageVector = BackIcon,
                                contentDescription = stringResource(Res.string.button_back),
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
    ) { paddingValues ->
        val modifier = if (scrollable) {
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        } else {
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        }

        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScaffold(
    onBack: (() -> Unit)? = null,
    progressBar: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val compact = maxHeight < CompactHeightThreshold
        CompositionLocalProvider(LocalIsCompactHeight provides compact) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            if (compact && progressBar != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 16.dp),
                                ) {
                                    progressBar()
                                }
                            }
                        },
                        navigationIcon = {
                            if (onBack != null) {
                                IconButton(
                                    onClick = onBack,
                                    modifier = Modifier.hoverHand(),
                                ) {
                                    Icon(
                                        imageVector = BackIcon,
                                        contentDescription = stringResource(Res.string.button_back),
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            navigationIconContentColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                },
            ) { paddingValues ->
                if (compact) {
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                    ) {
                        val bodyHeight = this.maxHeight
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                                .heightIn(min = bodyHeight),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            content()
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (progressBar != null) {
                            progressBar()
                        }
                        Spacer(Modifier.weight(1f))
                        content()
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
