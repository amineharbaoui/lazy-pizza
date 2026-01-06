package com.example.lazypizza.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import com.example.designsystem.components.NoConnectionScreen
import com.example.designsystem.utils.isWideLayout
import com.example.lazypizza.navigation.AppNavigation
import com.example.lazypizza.navigation.BottomBar
import com.example.lazypizza.navigation.MenuRoute
import com.example.lazypizza.navigation.NavigationRail
import com.example.lazypizza.navigation.isTopLevel

@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val isWide = isWideLayout()
    val backStack = rememberSaveable { mutableStateListOf<NavKey>(MenuRoute) }
    val currentRoute: NavKey = backStack.last()

    val snackbarHostState = remember { SnackbarHostState() }
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()
    val badgeCount by viewModel.badgeCount.collectAsStateWithLifecycle()

    BackHandler(enabled = backStack.size > 1) {
        backStack.removeAt(backStack.lastIndex)
    }

//    LaunchedEffect(isOnline) {
//        if (!isOnline) {
//            snackbarHostState.showSnackbar(
//                message = context.getString(R.string.no_internet_connection),
//                duration = SnackbarDuration.Indefinite,
//            )
//        }
//    }

    Scaffold(
        bottomBar = {
            if (!isWide && currentRoute.isTopLevel()) {
                BottomBar(
                    currentRoute = currentRoute,
                    onRouteSelect = { newRoute ->
                        backStack.clear()
                        backStack.add(newRoute)
                    },
                    badgeCount = badgeCount,
                )
            }
        },
//        snackbarHost = {
//            SnackbarHost(
//                snackbarHostState,
//                modifier = Modifier.windowInsetsPadding(
//                    WindowInsets.safeDrawing.exclude(
//                        WindowInsets.ime,
//                    ),
//                ),
//            )
//        },
    ) { innerPadding ->
        Row(Modifier.fillMaxSize()) {
            if (isWide && currentRoute.isTopLevel()) {
                NavigationRail(
                    currentRoute = currentRoute,
                    onRouteSelect = { newRoute ->
                        backStack.clear()
                        backStack.add(newRoute)
                    },
                    badgeCount = badgeCount,
                )
            }
            Box {
                if (isOnline.not()) {
                    NoConnectionScreen()
                } else {
                    AppNavigation(
                        backStack = backStack,
                        onBack = { if (backStack.size > 1) backStack.removeAt(backStack.lastIndex) },
                        innerPadding = innerPadding,
                    )
                }
            }
        }
    }
}
