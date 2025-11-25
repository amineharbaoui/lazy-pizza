package com.example.lazypizza.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.designsystem.utils.isWideLayout
import com.example.lazypizza.navigation.BottomBar
import com.example.lazypizza.navigation.NavigationRail
import com.example.lazypizza.navigation.RootNavGraph
import com.example.lazypizza.navigation.Route
import com.example.lazypizza.navigation.TopLevelBackStack


@Composable
fun MainScreen() {
    val isWide = isWideLayout()
    val topLevelBackStack = remember { TopLevelBackStack<Any>(Route.TopLevel.Menu) }
    val currentRoute: Route = topLevelBackStack.topLevelKey as? Route ?: Route.TopLevel.Menu

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (!isWide) {
                BottomBar(
                    currentRoute = currentRoute,
                    onRouteSelect = { newRoute ->
                        topLevelBackStack.addTopLevel(newRoute)
                    }
                )
            }
        }
    ) { innerPadding ->
        Row(Modifier.padding(innerPadding)) {
            if (isWide) {
                NavigationRail(
                    currentRoute = currentRoute,
                    onRouteSelect = { newRoute ->
                        topLevelBackStack.addTopLevel(newRoute)
                    }
                )
            }

            Box(Modifier.weight(1f)) {
                RootNavGraph(
                    backStack = topLevelBackStack.backStack,
                    onBack = { topLevelBackStack.removeLast() },
                    innerPadding = PaddingValues(0.dp)
                )
            }
        }
    }
}

