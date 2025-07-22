package com.example.samplecompose.navigation

import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.presentation.documents.DocumentViewModel
import com.example.samplecompose.ui.CreateProfileScreen
import com.example.samplecompose.ui.HomeScreen
import com.example.presentation.profile.UserViewModel
import com.example.samplecompose.ui.AiPromptScreen
import com.example.samplecompose.ui.DocumentDetailScreen
import com.example.samplecompose.ui.DocumentListScreen
import com.example.samplecompose.ui.UploadDetailsScreen
import kotlinx.coroutines.flow.map

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModel: UserViewModel,
    documentViewModel : DocumentViewModel
) {
//    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                viewModel = userViewModel,
                onAddProfileClick = {
                    navController.navigate("create") // Add new profile
                },
                onEditProfileClick = { profileId ->
                    navController.navigate("create/$profileId") // Edit existing profile
                },
                onProfileClick = { profileId ->
                    navController.navigate("documents/$profileId") // 🔥 Tap on card → open document list
                }
            )
        }

        composable("create") {
            CreateProfileScreen(
                viewModel = userViewModel,
                profileId = null,
                onProfileCreated = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = "create/{profileId}",
            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId")
            CreateProfileScreen(
                viewModel = userViewModel,
                profileId = profileId,
                onProfileCreated = { navController.popBackStack() },
                onCancel = { navController.popBackStack() }
            )
        }

        composable(
            route = "documents/{profileId}",
            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""
            DocumentListScreen(
                profileId = profileId,
                navController = navController,
                onBack = { navController.popBackStack() },
                viewModel = documentViewModel
            )
        }



        composable(
            route = "docDetail/{docId}/{profileId}",
            arguments = listOf(
                navArgument("docId") { type = NavType.IntType },
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getInt("docId")
            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""

            val documentList by documentViewModel.documentList.collectAsState()
            val document = remember(documentList, docId) {
                documentList.find { it.id == docId }
            }

            document?.let {
                DocumentDetailScreen(
                    fileUri = Uri.parse(it.fileUri),
                    profileId = profileId,
                    document = it,
                    onBack = { navController.popBackStack() },
                    onDelete = {
                        documentViewModel.deleteDocument(it)
                        navController.popBackStack()
                    },
                    viewModel = documentViewModel
                )
            }
        }


        composable(
            route = "uploadDetailsScreen?uri={uri}&profileId={profileId}",
            arguments = listOf(
                navArgument("uri") { type = NavType.StringType },
                navArgument("profileId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val uri = backStackEntry.arguments?.getString("uri")?.let { Uri.parse(it) }
            val profileId = backStackEntry.arguments?.getString("profileId") ?: ""

            if (uri != null) {
                UploadDetailsScreen(
                    fileUri = uri,
                    profileId = profileId,
                    viewModel = documentViewModel, // or get from hilt
                    userViewModel = userViewModel,
                    navController = navController,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(
            route = "aiPromptScreen?text={text}&prompt={prompt}",
            arguments = listOf(
                navArgument("text") { type = NavType.StringType },
                navArgument("prompt") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val initialText = backStackEntry.arguments?.getString("text") ?: ""
            val initialPrompt = backStackEntry.arguments?.getString("prompt") ?: ""
            AiPromptScreen(initialText = initialText, initialPrompt = initialPrompt)
        }

    }
}
