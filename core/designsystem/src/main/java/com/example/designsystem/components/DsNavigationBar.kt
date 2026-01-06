package com.example.designsystem.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.designsystem.R
import com.example.designsystem.theme.AppColors
import com.example.designsystem.theme.AppTypography
import com.example.designsystem.theme.LazyPizzaThemePreview

object DsNavigationBar {

    @Composable
    fun Bottom(
        menuItems: List<NavItem>,
        modifier: Modifier = Modifier,
    ) {
        Surface(
            shadowElevation = 12.dp,
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            modifier = modifier.fillMaxWidth(),
        ) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
                menuItems.forEach { item ->
                    NavigationBarItem(
                        selected = item.isSelected,
                        onClick = { if (!item.isSelected) item.onClick() },
                        icon = { NavIcon(item.icon, item.label, item.badgeCount) },
                        label = { Text(text = item.label) },
                        colors = NavigationBarItemColors(
                            selectedIconColor = AppColors.Primary,
                            selectedTextColor = AppColors.TextPrimary,
                            selectedIndicatorColor = AppColors.Primary_8,
                            unselectedIconColor = AppColors.TextSecondary,
                            unselectedTextColor = AppColors.TextSecondary,
                            disabledIconColor = AppColors.TextSecondary,
                            disabledTextColor = AppColors.TextSecondary,
                        ),
                    )
                }
            }
        }
    }

    @Composable
    fun BubbleNotchBottomBar(
        menuItems: List<NavItem>,
        modifier: Modifier = Modifier,
    ) {
        val barHeight = 80.dp
        val bubbleSize = 48.dp
        val bubbleLift = bubbleSize / 2

        val durationMs = 600

        val moveSpec = tween<Dp>(
            durationMillis = durationMs,
            easing = FastOutSlowInEasing,
        )

        val selectedIndex = remember(menuItems) {
            menuItems.indexOfFirst { it.isSelected }.let { if (it >= 0) it else 0 }
        }

        BoxWithConstraints(
            modifier = modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .height(barHeight),
        ) {
            val totalWidth = maxWidth
            val itemCount = menuItems.size.coerceAtLeast(1)
            val slotWidth = totalWidth / itemCount

            val bubbleTargetX = slotWidth * selectedIndex + (slotWidth / 2) - (bubbleSize / 2)
            val bubbleX by animateDpAsState(
                targetValue = bubbleTargetX,
                animationSpec = moveSpec,
                label = "bubbleX",
            )

            val notchWidth = 90.dp
            val notchHeight = 32.dp
            val bubbleCenterX = bubbleX + (bubbleSize / 2)
            val notchX = bubbleCenterX - (notchWidth / 2)

            HorizontalDivider(color = AppColors.TextPrimary, thickness = 5.dp)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.SurfaceHigher),
            )

            NotchShape(
                modifier = Modifier
                    .offset(x = notchX, y = 0.dp)
                    .width(notchWidth)
                    .height(notchHeight)
                    .align(Alignment.TopStart)
                    .zIndex(0f),
                color = AppColors.Overlay.copy(alpha = 0.1f),
            )

            Box(
                modifier = Modifier
                    .offset(x = bubbleX, y = -bubbleLift)
                    .size(bubbleSize)
                    .clip(CircleShape)
                    .background(AppColors.Primary)
                    .align(Alignment.TopStart),
            )

            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                menuItems.forEachIndexed { index, item ->
                    BottomBarItem(
                        item = item,
                        isSelected = item.isSelected,
                        barHeight = barHeight,
                        durationMs = durationMs,
                        moveSpec = moveSpec,
                        onClick = { if (!item.isSelected) item.onClick() },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    @Composable
    private fun BottomBarItem(
        item: NavItem,
        isSelected: Boolean,
        barHeight: Dp,
        durationMs: Int,
        moveSpec: FiniteAnimationSpec<Dp>,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
    ) {
        val selectedIconOffset = -(barHeight / 2) + 8.dp
        val iconOffset by animateDpAsState(
            targetValue = if (isSelected) selectedIconOffset else 0.dp,
            animationSpec = moveSpec,
            label = "iconOffset",
        )

        val transition = updateTransition(targetState = isSelected, label = "itemTransition")
        val iconTint by transition.animateColor(
            transitionSpec = {
                keyframes {
                    durationMillis = durationMs
                    AppColors.TextSecondary at 0
                    AppColors.TextPrimary at durationMs / 2
                    AppColors.SurfaceHigher at durationMs
                }
            },
            label = "iconTint",
        ) { selected ->
            if (selected) AppColors.SurfaceHigher else AppColors.TextSecondary
        }

        val barHeightPx = with(LocalDensity.current) { barHeight.roundToPx() }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = modifier
                .fillMaxHeight()
                .clickable(onClick = onClick),
        ) {
            BadgedBox(
                modifier = Modifier.offset(y = iconOffset),
                badge = {
                    if (item.badgeCount > 0) {
                        Badge(
                            modifier = Modifier.offset(x = 4.dp, y = (-4).dp),
                        ) {
                            Text(
                                text = item.badgeCount.toString(),
                            )
                        }
                    }
                },
            ) {
                Icon(
                    painter = item.icon,
                    contentDescription = item.label,
                    tint = iconTint,
                )
            }

            AnimatedVisibility(
                visible = isSelected,
                enter = slideInVertically(
                    initialOffsetY = { barHeightPx },
                    animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
                ) + fadeIn(animationSpec = tween(durationMs, easing = FastOutSlowInEasing)),
                exit = slideOutVertically(
                    targetOffsetY = { barHeightPx },
                    animationSpec = tween(durationMs, easing = FastOutSlowInEasing),
                ) + fadeOut(animationSpec = tween(durationMs, easing = FastOutSlowInEasing)),
            ) {
                Text(
                    text = item.label,
                    style = AppTypography.Label2SemiBold,
                    color = AppColors.TextPrimary,
                )
            }
        }
    }

    @Composable
    fun NotchShape(
        modifier: Modifier = Modifier,
        color: Color,
        controlPoint1XFactor: Float = 0.8f,
        controlPoint2XFactor: Float = 0.8f,
        verticalScaleFactor: Float = 1f,
    ) {
        Canvas(modifier = modifier) {
            val width = size.width
            val height = size.height * verticalScaleFactor

            fun mirror(x: Float) = 1f - x

            val rightControlPoint1X = width * controlPoint1XFactor
            val rightControlPoint2X = width * controlPoint2XFactor
            val bottomCenterX = width * 0.5f

            val leftControlPoint2X = width * mirror(controlPoint2XFactor)
            val leftControlPoint1X = width * mirror(controlPoint1XFactor)

            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(width, 0f)

                cubicTo(
                    rightControlPoint1X,
                    0f,
                    rightControlPoint2X,
                    height,
                    bottomCenterX,
                    height,
                )

                cubicTo(
                    leftControlPoint2X,
                    height,
                    leftControlPoint1X,
                    0f,
                    0f,
                    0f,
                )

                close()
            }

            drawPath(path = path, color = color)
        }
    }

    @Composable
    fun Rail(
        menuItems: List<NavItem>,
        modifier: Modifier = Modifier,
    ) {
        NavigationRail(
            containerColor = Color.Transparent,
            modifier = modifier.statusBarsPadding(),
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                menuItems.forEach { item ->
                    NavigationRailItem(
                        selected = item.isSelected,
                        onClick = { if (!item.isSelected) item.onClick() },
                        icon = { NavIcon(item.icon, item.label, item.badgeCount) },
                        label = { Text(item.label) },
                        colors = NavigationRailItemColors(
                            selectedIconColor = AppColors.Primary,
                            selectedTextColor = AppColors.TextPrimary,
                            selectedIndicatorColor = AppColors.Primary_8,
                            unselectedIconColor = AppColors.TextSecondary,
                            unselectedTextColor = AppColors.TextSecondary,
                            disabledIconColor = AppColors.TextSecondary,
                            disabledTextColor = AppColors.TextSecondary,
                        ),
                    )
                }
            }
        }
    }

    @Composable
    private fun NavIcon(
        painter: Painter,
        label: String,
        badgeCount: Int,
        iconTint: Color = AppColors.TextSecondary,
        modifier: Modifier = Modifier,
    ) {
        val iconBase = @Composable {
            Icon(
                painter = painter,
                contentDescription = label,
                tint = iconTint,
            )
        }

        if (badgeCount > 0) {
            BadgedBox(
                badge = {
                    Badge(
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.OnPrimary,
                        modifier = Modifier.offset(x = 10.dp, y = (-8).dp),
                    ) {
                        Text(text = badgeCount.toString())
                    }
                },
            ) {
                iconBase()
            }
        } else {
            iconBase()
        }
    }

    data class NavItem(
        val label: String,
        val icon: Painter,
        val badgeCount: Int = 0,
        val isSelected: Boolean,
        val onClick: () -> Unit,
    )
}

@Preview(
    name = "P1 - Phone",
    device = Devices.PHONE,
    showSystemUi = true,
    showBackground = true,
)
@Composable
private fun BubbleNotchBottomBarPreview() {
    LazyPizzaThemePreview {
        Column(modifier = Modifier.padding(vertical = 30.dp)) {
            var selected by remember { mutableIntStateOf(1) }
            val items = listOf(
                DsNavigationBar.NavItem(
                    icon = painterResource(R.drawable.menu),
                    badgeCount = 0,
                    label = "Menu",
                    isSelected = selected == 0,
                    onClick = { selected = 0 },
                ),
                DsNavigationBar.NavItem(
                    icon = painterResource(R.drawable.shopping_cart),
                    badgeCount = 3,
                    label = "Cart",
                    isSelected = selected == 1,
                    onClick = { selected = 1 },
                ),
                DsNavigationBar.NavItem(
                    icon = painterResource(R.drawable.orders),
                    badgeCount = 0,
                    label = "History",
                    isSelected = selected == 2,
                    onClick = { selected = 2 },
                ),
            )

            DsNavigationBar.BubbleNotchBottomBar(menuItems = items)
        }
    }
}
