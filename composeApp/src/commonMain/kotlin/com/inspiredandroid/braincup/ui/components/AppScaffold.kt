package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.movableContentWithReceiverOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_back
import org.jetbrains.compose.resources.stringResource

internal val LocalIsCompactHeight = staticCompositionLocalOf { false }

/**
 * Default maximum edge for a square board cell. Boards cap their width at this times the grid
 * size, so a cell never grows past it while still shrinking to fit narrow screens.
 */
@Suppress("ktlint:standard:property-naming")
internal val gridCellMaxSize: Dp
    @Composable
    @ReadOnlyComposable
    get() = if (LocalIsCompactHeight.current) 56.dp else 72.dp

/**
 * Height of the scaffold body (viewport minus the top bar), provided on the compact paths only.
 * Those paths wrap the content in a vertical scroll, so content is measured with unbounded height
 * and cannot read the real viewport from its own constraints. Null when nothing measured it.
 */
internal val LocalScaffoldBodyHeight = staticCompositionLocalOf<Dp?> { null }

private val CompactHeightThreshold = 480.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScaffold(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBack: (() -> Unit)? = null,
    scrollable: Boolean = true,
    bottomBar: (@Composable () -> Unit)? = null,
    provideCompactHeight: Boolean = false,
    actions: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    // The compact and regular branches below put the body in different Columns. Handing the slot
    // straight to both would drop everything it remembers whenever the window crosses the compact
    // threshold; as movable content the same nodes are moved across instead.
    val body = remember(content) { movableContentWithReceiverOf<ColumnScope>(content) }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    if (title != null) {
                        Text(title)
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        BackPrism(
                            color = MaterialTheme.colorScheme.primary,
                            contentDescription = stringResource(Res.string.button_back),
                            onClick = onBack,
                            modifier = Modifier.hoverHand(),
                        )
                    }
                },
                actions = {
                    actions?.invoke(this)
                },
                // Transparent so the bar shows the Scaffold background (same value as surface) and
                // changes in lockstep with the rest of the screen on a theme switch. A fixed
                // containerColor would animate independently via TopAppBar's internal color
                // transition and lag behind the instant background change, which looks glitchy.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary,
                ),
            )
        },
        bottomBar = { bottomBar?.invoke() },
    ) { paddingValues ->
        if (provideCompactHeight) {
            // Measure the body viewport (Scaffold already subtracts the top/bottom bars from
            // paddingValues) so demos can switch to their compact sizing on short screens, and so
            // the scrolled content still centers when it fits. Mirrors GameScaffold's compact path.
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                val compact = maxHeight < CompactHeightThreshold
                val bodyHeight = maxHeight
                CompositionLocalProvider(
                    LocalIsCompactHeight provides compact,
                    LocalScaffoldBodyHeight provides bodyHeight,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .heightIn(min = bodyHeight),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        body()
                    }
                }
            }
        } else {
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
                body()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScaffold(
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    progressBar: (@Composable () -> Unit)? = null,
    fillContent: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    // Compact moves the progress bar into the top bar and the body into a scrolling Column; both
    // slots are invoked from more than one place, so they travel as movable content and keep their
    // state (game animations, scroll position) across a resize or rotation.
    val bar = remember(progressBar) { progressBar?.let { movableContentOf(it) } }
    val body = remember(content) { movableContentWithReceiverOf<ColumnScope>(content) }

    BoxWithConstraints(modifier.fillMaxSize()) {
        val compact = maxHeight < CompactHeightThreshold
        CompositionLocalProvider(LocalIsCompactHeight provides compact) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            if (compact && bar != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 16.dp),
                                ) {
                                    bar()
                                }
                            }
                        },
                        navigationIcon = {
                            if (onBack != null) {
                                BackPrism(
                                    color = MaterialTheme.colorScheme.primary,
                                    contentDescription = stringResource(Res.string.button_back),
                                    onClick = onBack,
                                    modifier = Modifier.hoverHand(),
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            scrolledContainerColor = Color.Transparent,
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
                        CompositionLocalProvider(LocalScaffoldBodyHeight provides bodyHeight) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                                    .heightIn(min = bodyHeight),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                body()
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (bar != null) {
                            bar()
                        }
                        if (fillContent) {
                            body()
                        } else {
                            Spacer(Modifier.weight(1f))
                            body()
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
