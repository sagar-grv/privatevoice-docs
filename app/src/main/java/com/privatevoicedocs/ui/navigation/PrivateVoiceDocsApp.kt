package com.privatevoicedocs.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.privatevoicedocs.ui.chat.ChatPlaceholderScreen
import com.privatevoicedocs.ui.documents.DocumentLibraryScreen
import com.privatevoicedocs.ui.documents.DocumentLibraryViewModel
import com.privatevoicedocs.ui.privacy.PrivacyCentreScreen

private enum class Destination(val route: String, val label: String, val shortLabel: String) {
    Documents("documents", "Documents", "D"),
    Chat("chat", "Chat", "C"),
    Privacy("privacy", "Privacy", "P"),
}

@Composable
fun PrivateVoiceDocsApp(libraryViewModel: DocumentLibraryViewModel) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                Destination.entries.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(destination.shortLabel) },
                        label = { Text(destination.label) },
                    )
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destination.Documents.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Destination.Documents.route) { DocumentLibraryScreen(libraryViewModel) }
            composable(Destination.Chat.route) { ChatPlaceholderScreen() }
            composable(Destination.Privacy.route) { PrivacyCentreScreen() }
        }
    }
}
