@file:OptIn(ExperimentalMaterialApi::class)

package ru.winditest.presentation.components.backdrop

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.SwipeableState
import androidx.compose.material.swipeable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.collapse
import androidx.compose.ui.semantics.expand
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.*
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.reflect.full.memberProperties

/**
 * Possible values of [BackdropScaffoldState].
 */
@ExperimentalMaterialApi
enum class BackdropValue {
    /**
     * Indicates the back layer is concealed and the front layer is active.
     */
    Concealed,

    /**
     * Indicates the back layer is still visible but the front layer is active
     */
    Peeking,

    /**
     * Indicates the back layer is revealed and the front layer is inactive.
     */
    Revealed
}

/**
 * State of the [BackdropScaffold] composable.
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 * @param snackbarHostState The [SnackbarHostState] used to show snackbars inside the scaffold.
 */
@ExperimentalMaterialApi
@Stable
class BackdropScaffoldState(
    initialValue: BackdropValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BackdropValue) -> Boolean = { true },
    val snackbarHostState: SnackbarHostState = SnackbarHostState()
) : SwipeableState<BackdropValue>(
    initialValue = initialValue,
    animationSpec = animationSpec,
    confirmStateChange = confirmStateChange
) {
    /**
     * Whether the back layer is revealed.
     */
    val isRevealed: Boolean
        get() = currentValue == BackdropValue.Revealed

    /**
     * Whether the back layer is concealed.
     */
    val isConcealed: Boolean
        get() = currentValue == BackdropValue.Concealed

    /**
     * Reveal the back layer with animation and suspend until it if fully revealed or animation
     * has been cancelled.  This method will throw [CancellationException] if the animation is
     * interrupted
     *
     * @return the reason the reveal animation ended
     */
    suspend fun reveal() = animateTo(targetValue = BackdropValue.Revealed)

    /**
     * Conceal the back layer with animation and suspend until it if fully concealed or animation
     * has been cancelled. This method will throw [CancellationException] if the animation is
     * interrupted
     *
     * @return the reason the conceal animation ended
     */
    suspend fun conceal() = animateTo(targetValue = BackdropValue.Concealed)

    val nestedScrollConnection = this.PreUpPostDownNestedScrollConnection

    companion object {
        /**
         * The default [Saver] implementation for [BackdropScaffoldState].
         */
        fun Saver(
            animationSpec: AnimationSpec<Float>,
            confirmStateChange: (BackdropValue) -> Boolean,
            snackbarHostState: SnackbarHostState
        ): Saver<BackdropScaffoldState, *> = Saver(
            save = { it.currentValue },
            restore = {
                BackdropScaffoldState(
                    initialValue = it,
                    animationSpec = animationSpec,
                    confirmStateChange = confirmStateChange,
                    snackbarHostState = snackbarHostState
                )
            }
        )
    }
}

/**
 * Create and [remember] a [BackdropScaffoldState].
 *
 * @param initialValue The initial value of the state.
 * @param animationSpec The default animation that will be used to animate to a new state.
 * @param confirmStateChange Optional callback invoked to confirm or veto a pending state change.
 * @param snackbarHostState The [SnackbarHostState] used to show snackbars inside the scaffold.
 */
@Composable
@ExperimentalMaterialApi
fun rememberBackdropScaffoldState(
    initialValue: BackdropValue,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec,
    confirmStateChange: (BackdropValue) -> Boolean = { true },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): BackdropScaffoldState {
    return rememberSaveable(
        animationSpec,
        confirmStateChange,
        snackbarHostState,
        saver = BackdropScaffoldState.Saver(
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange,
            snackbarHostState = snackbarHostState
        )
    ) {
        BackdropScaffoldState(
            initialValue = initialValue,
            animationSpec = animationSpec,
            confirmStateChange = confirmStateChange,
            snackbarHostState = snackbarHostState
        )
    }
}

/**
 * Extended version of default [BackdropScaffold][androidx.compose.material.BackdropScaffold] that
 * enables proper interaction in [Profile Screen][ru.winditest.presentation.screen.app.profile.Profile]
 *
 * @param backLayerContent The content of the back layer.
 * @param frontLayerContent The content of the front layer.
 * @param modifier Optional [Modifier] for the root of the scaffold.
 * @param scaffoldState The state of the scaffold.
 * @param gesturesEnabled Whether or not the backdrop can be interacted with by gestures.
 * @param peekHeight The height of the visible part of the back layer when it is concealed.
 * @param headerHeight The minimum height of the front layer when it is inactive.
 * By default, it will always be shown above the back layer's content. If this is set to `false`,
 * the back layer will automatically switch between the app bar and its content with an animation.
 * @param stickyFrontLayer Whether the front layer should stick to the height of the back layer.
 * @param backLayerBackgroundColor The background color of the back layer.
 * @param backLayerContentColor The preferred content color provided by the back layer to its
 * children. Defaults to the matching content color for [backLayerBackgroundColor], or if that
 * is not a color from the theme, this will keep the same content color set above the back layer.
 * @param frontLayerShape The shape of the front layer.
 * @param frontLayerElevation The elevation of the front layer.
 * @param frontLayerBackgroundColor The background color of the front layer.
 * @param frontLayerContentColor The preferred content color provided by the back front to its
 * children. Defaults to the matching content color for [frontLayerBackgroundColor], or if that
 * is not a color from the theme, this will keep the same content color set above the front layer.
 * @param frontLayerScrimColor The color of the scrim applied to the front layer when the back
 * layer is revealed. If the color passed is [Color.Unspecified], then a scrim will not be
 * applied and interaction with the front layer will not be blocked when the back layer is revealed.
 * @param snackbarHost The component hosting the snackbars shown inside the scaffold.
 */
@Composable
@ExperimentalMaterialApi
fun BackdropScaffold(
    backLayerContent: @Composable () -> Unit,
    frontLayerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    scaffoldState: BackdropScaffoldState = rememberBackdropScaffoldState(BackdropValue.Peeking),
    gesturesEnabled: Boolean = true,
    peekHeight: Dp = BackdropScaffoldDefaults.PeekHeight,
    partialPeekHeight: Dp,
    headerHeight: Dp = BackdropScaffoldDefaults.HeaderHeight,
    stickyFrontLayer: Boolean = true,
    backLayerBackgroundColor: Color = MaterialTheme.colorScheme.background,
    backLayerContentColor: Color = contentColorFor(backLayerBackgroundColor),
    frontLayerShape: Shape = BackdropScaffoldDefaults.frontLayerShape,
    frontLayerElevation: Dp = BackdropScaffoldDefaults.FrontLayerElevation,
    frontLayerBackgroundColor: Color = MaterialTheme.colorScheme.background,
    frontLayerContentColor: Color = contentColorFor(frontLayerBackgroundColor),
    frontLayerScrimColor: Color = BackdropScaffoldDefaults.frontLayerScrimColor,
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) }
) {
    val topRadiusPx = (frontLayerShape as RoundedCornerShape).topStart.toPx(
        shapeSize = Size(width = 1920f, height = 1080f),
        density = LocalDensity.current
    )
    val peekHeightPx = with(LocalDensity.current) { peekHeight.toPx() }
    val partialPeekPx = with(LocalDensity.current) { partialPeekHeight.toPx() }
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }


    val cornerRadius = androidx.compose.ui.util.lerp(
        start = 0f,
        stop = topRadiusPx,
        fraction = (scaffoldState.offset.value / (peekHeightPx + partialPeekPx)).coerceIn(0f, 1f)
    )

    val calculateBackLayerConstraints: (Constraints) -> Constraints = {
        it.copy(minWidth = 0, minHeight = 0).offset(vertical = -headerHeightPx.roundToInt())
    }

    val backLayer = @Composable {
        Column {
            backLayerContent()
        }
    }

    // Back layer
    Surface(
        color = backLayerBackgroundColor,
        contentColor = backLayerContentColor
    ) {
        val scope = rememberCoroutineScope()
        BackdropStack(
            modifier.fillMaxSize(),
            backLayer,
            calculateBackLayerConstraints
        ) { constraints, backLayerHeight ->
            val fullHeight = constraints.maxHeight.toFloat()
            var revealedHeight = fullHeight - headerHeightPx
            if (stickyFrontLayer) {
                revealedHeight = min(revealedHeight, backLayerHeight)
            }
            val nestedScroll = if (gesturesEnabled) {
                Modifier.nestedScroll(scaffoldState.nestedScrollConnection)
            } else {
                Modifier
            }

            val swipeable = Modifier
                .then(nestedScroll)
                .swipeable(
                    state = scaffoldState,
                    anchors = mapOf(
                        peekHeightPx to BackdropValue.Concealed,
                        partialPeekPx to BackdropValue.Peeking,
                        revealedHeight - topRadiusPx to BackdropValue.Revealed
                    ),
                    orientation = Orientation.Vertical,
                    enabled = gesturesEnabled,
                    resistance = null
                )
                .semantics {
                    if (scaffoldState.isConcealed) {
                        collapse {
                            scope.launch { scaffoldState.reveal() }
                            true
                        }
                    } else {
                        expand {
                            scope.launch { scaffoldState.conceal() }
                            true
                        }
                    }
                }

            // Front layer
            Surface(
                modifier = Modifier
                    .offset { IntOffset(0, scaffoldState.offset.value.roundToInt()) }
                    .then(swipeable),
                shape = RoundedCornerShape(
                    topStart = cornerRadius,
                    topEnd = cornerRadius
                ),
                shadowElevation = frontLayerElevation,
                color = frontLayerBackgroundColor,
                contentColor = frontLayerContentColor
            ) {
                Box(Modifier.padding(bottom = peekHeight)) {
                    frontLayerContent()
                    Scrim(
                        color = frontLayerScrimColor,
                        onDismiss = {
                            scope.launch { scaffoldState.conceal() }
                        },
                        visible = scaffoldState.targetValue == BackdropValue.Revealed
                    )
                }
            }

            // Snackbar host
            Box(
                Modifier
                    .padding(
                        bottom = if (scaffoldState.isRevealed &&
                            revealedHeight == fullHeight - headerHeightPx
                        ) headerHeight else 0.dp
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {
                snackbarHost(scaffoldState.snackbarHostState)
            }
        }
    }
}

@Composable
private fun Scrim(
    color: Color,
    onDismiss: () -> Unit,
    visible: Boolean
) {
    if (color.isSpecified) {
        val alpha by animateFloatAsState(
            targetValue = if (visible) 1f else 0f,
            animationSpec = TweenSpec()
        )
        val dismissModifier = if (visible) {
            Modifier.pointerInput(Unit) { detectTapGestures { onDismiss() } }
        } else {
            Modifier
        }
        Canvas(
            Modifier
                .fillMaxSize()
                .then(dismissModifier)
        ) {
            drawRect(color = color, alpha = alpha)
        }
    }
}

@Composable
@UiComposable
private fun BackdropStack(
    modifier: Modifier,
    backLayer: @Composable @UiComposable () -> Unit,
    calculateBackLayerConstraints: (Constraints) -> Constraints,
    frontLayer: @Composable @UiComposable (Constraints, Float) -> Unit
) {
    SubcomposeLayout(modifier) { constraints ->
        val backLayerPlaceable =
            subcompose(BackdropLayers.Back, backLayer).first()
                .measure(calculateBackLayerConstraints(constraints))

        val backLayerHeight = backLayerPlaceable.height.toFloat()

        val placeables =
            subcompose(BackdropLayers.Front) {
                frontLayer(constraints, backLayerHeight)
            }.fastMap { it.measure(constraints) }

        var maxWidth = max(constraints.minWidth, backLayerPlaceable.width)
        var maxHeight = max(constraints.minHeight, backLayerPlaceable.height)
        placeables.fastForEach {
            maxWidth = max(maxWidth, it.width)
            maxHeight = max(maxHeight, it.height)
        }

        layout(maxWidth, maxHeight) {
            backLayerPlaceable.placeRelative(0, 0)
            placeables.fastForEach { it.placeRelative(0, 0) }
        }
    }
}

private enum class BackdropLayers { Back, Front }

/**
 * Contains useful defaults for [BackdropScaffold].
 */
object BackdropScaffoldDefaults {

    /**
     * The default peek height of the back layer.
     */
    val PeekHeight = 56.dp

    /**
     * The default header height of the front layer.
     */
    val HeaderHeight = 48.dp

    /**
     * The default shape of the front layer.
     */
    val frontLayerShape: Shape
        @Composable
        get() = MaterialTheme.shapes.large
            .copy(topStart = CornerSize(16.dp), topEnd = CornerSize(16.dp))

    /**
     * The default elevation of the front layer.
     */
    val FrontLayerElevation = 1.dp

    /**
     * The default color of the scrim applied to the front layer.
     */
    val frontLayerScrimColor: Color
        @Composable get() = MaterialTheme.colorScheme.surface.copy(alpha = 0.60f)
}

internal val <T> SwipeableState<T>.PreUpPostDownNestedScrollConnection: NestedScrollConnection
    get() = object : NestedScrollConnection {

        // Doing some reflection since minBound is internal
        val minBound = this@PreUpPostDownNestedScrollConnection::class.memberProperties.find {
            it.name == "minBound"
        }!!

        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.toFloat()
            return if (delta < 0 && source == NestedScrollSource.Drag) {
                performDrag(delta).toOffset()
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            return if (source == NestedScrollSource.Drag) {
                performDrag(available.toFloat()).toOffset()
            } else {
                Offset.Zero
            }
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            val toFling = Offset(available.x, available.y).toFloat()

            val mb = minBound.getter.call(this@PreUpPostDownNestedScrollConnection) as Float

            return if (toFling < 0 && offset.value > mb) {
                performFling(velocity = toFling)
                // since we go to the anchor with tween settling, consume all for the best UX
                available
            } else {
                Velocity.Zero
            }
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            performFling(velocity = Offset(available.x, available.y).toFloat())
            return available
        }

        private fun Float.toOffset(): Offset = Offset(0f, this)

        private fun Offset.toFloat(): Float = this.y
    }
