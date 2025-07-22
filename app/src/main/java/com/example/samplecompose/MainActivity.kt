package com.example.samplecompose

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.samplecompose.ui.theme.SampleComposeTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.data.document.FirebaseDocumentUploader
import com.example.data.fake.FakeUserDataSource
import com.example.data.local.LocalDocumentDataSource
import com.example.data.local.LocalUserDataSource
import com.example.data.local.db.AppDatabase
import com.example.data.repository.DocumentRepositoryImpl
import com.example.data.repository.UserRepositoryImpl
import com.example.data.sync.FirebaseSyncManager
import com.example.domain.usecase.AddUserProfileUseCase
import com.example.domain.usecase.DeleteUserProfileUseCase
import com.example.domain.usecase.GetUserProfilesUseCase
import com.example.domain.usecase.SyncUserProfilesUseCase
import com.example.presentation.documents.DocumentViewModel
import com.example.presentation.profile.UserViewModel
import com.example.samplecompose.navigation.AppNavHost
import com.example.samplecompose.ui.HomeScreen
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class MainActivity : ComponentActivity() {

    private var sharedFileUri: Uri? = null
    private var startFromShare: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 1: Create Room DB
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "user_profiles.db"
        ).build()

        // Step 2: Inject dependencies manually
        val dao = db.userProfileDao()
        val dataSource = LocalUserDataSource(dao)
        val repository = UserRepositoryImpl(dataSource)

        val getUserProfilesUseCase = GetUserProfilesUseCase(repository)
        val addUserProfileUseCase = AddUserProfileUseCase(repository)
        val deleteUserProfileUseCase = DeleteUserProfileUseCase(repository)

        val firebaseStorage = FirebaseStorage.getInstance()
        val firebaseDatabase = FirebaseDatabase.getInstance()

        val firebaseDocumentUploader = FirebaseDocumentUploader(
            firebaseStorage,
            firebaseDatabase,
            applicationContext
        )
        val localDocumentDao = db.documentDao()
        val localDocumentDataSource = LocalDocumentDataSource(localDocumentDao)
        val documentRepository = DocumentRepositoryImpl(localDocumentDataSource, firebaseDocumentUploader)
        val documentViewModel = DocumentViewModel(documentRepository)

        val syncUserProfilesUseCase = SyncUserProfilesUseCase(repository)
        val viewModel = UserViewModel(
            getUserProfilesUseCase,
            addUserProfileUseCase,
            deleteUserProfileUseCase,
            syncUserProfilesUseCase
        )

        // Step 3: Handle shared intent
        val intent = intent
        if (intent?.action == Intent.ACTION_SEND && intent.type != null) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            if (uri != null) {
                sharedFileUri = uri
                startFromShare = true
            }
        }

        setContent {
            val navController = rememberNavController()

            SampleComposeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Navigate if launched from share intent
                    LaunchedEffect(Unit) {
                        if (startFromShare && sharedFileUri != null) {
                            navController.navigate(
                                "uploadDetailsScreen?uri=${Uri.encode(sharedFileUri.toString())}&profileId="
                            )
                            startFromShare = false
                        }
                    }

                    AppNavHost(
                        navController = navController,
                        userViewModel = viewModel,
                        documentViewModel = documentViewModel
                    )
                }
            }
        }
    }
}

