package com.example.lazypizza.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavKey
import com.example.designsystem.utils.isWideLayout
import com.example.lazypizza.navigation.BottomBar
import com.example.lazypizza.navigation.NavigationRail
import com.example.lazypizza.navigation.RootNavGraph
import com.example.menu.presentation.MenuRoute


@Composable
fun MainScreen() {
    val isWide = isWideLayout()
    val backStack = remember { mutableStateListOf<NavKey>(MenuRoute) }
    val currentRoute: NavKey = backStack.last()
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (!isWide) {
                BottomBar(
                    currentRoute = currentRoute,
                    onRouteSelect = { newRoute ->
                        backStack.clear()
                        backStack.add(newRoute)
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
                        backStack.clear()
                        backStack.add(newRoute)
                    }
                )
            }

            Box(Modifier.weight(1f)) {
                RootNavGraph(
                    backStack = backStack,
                    onBack = { if (backStack.size > 1) backStack.removeLast() },
                    innerPadding = PaddingValues(0.dp)
                )
            }
        }
    }
}

