package com.inspiredandroid.braincup.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
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
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import braincup.composeapp.generated.resources.Res
import braincup.composeapp.generated.resources.button_back
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

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

/**
 * Share of the bar an action may take before it starts eating the title. [TopAppBar] measures its
 * actions at their natural width and hands the title whatever is left over, so an action that grew
 * with the font scale can leave the title a few pixels - and a title with a few pixels wraps a line
 * per letter until the bar has swallowed the screen.
 */
private const val ActionsMaxWidthFraction = 0.45f

/** Floor for a bar title that has shrunk to share its bar with an action. */
private val MinSharedBarTitleSize = 12.sp

/**
 * True once the user's text is large enough that two labels stop fitting beside each other. The
 * layouts that read this stack instead of splitting a row they can no longer share.
 */
@Composable
@ReadOnlyComposable
internal fun isLargeFontScale(): Boolean = LocalDensity.current.fontScale >= 1.3f

/** Caps a child at [fraction] of the width it is offered, leaving the remainder to its siblings. */
private fun Modifier.widthFractionAtMost(fraction: Float): Modifier = layout { measurable, constraints ->
    val cap = if (constraints.maxWidth == Constraints.Infinity) {
        constraints.maxWidth
    } else {
        (constraints.maxWidth * fraction).roundToInt()
    }
    val placeable = measurable.measure(constraints.copy(minWidth = 0, maxWidth = cap))
    layout(placeable.width, placeable.height) { placeable.place(0, 0) }
}

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
                        if (actions == null) {
                            // The whole bar to itself: two lines, then an ellipsis, so a long title
                            // can never grow the bar a line at a time.
                            Text(title, maxLines = 2, overflow = TextOverflow.Ellipsis)
                        } else {
                            // Sharing the bar with an action, the title shrinks to hold its single
                            // line instead of splitting a word across two - "Arit" over "hmetic"
                            // reads as breakage, where a smaller whole word just reads as a title.
                            Text(
                                title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                autoSize = TextAutoSize.StepBased(
                                    minFontSize = MinSharedBarTitleSize,
                                    maxFontSize = MaterialTheme.typography.titleLarge.fontSize,
                                ),
                            )
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
                actions = {
                    if (actions != null) {
                        Row(
                            modifier = Modifier.widthFractionAtMost(ActionsMaxWidthFraction),
                            verticalAlignment = Alignment.CenterVertically,
                            content = actions,
                        )
                    }
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
                            // Centred by arrangement in a column of its own rather than by a pair
                            // of weighted spacers around it. The spacers were siblings of whatever
                            // the body put in this column, so a game that weights one of its own
                            // parts - a board told to yield height to the button under it - was
                            // splitting the free space three ways with the centring instead of
                            // taking what its siblings had left.
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                            ) {
                                body()
                            }
                        }
                    }
                }
            }
        }
    }
}
