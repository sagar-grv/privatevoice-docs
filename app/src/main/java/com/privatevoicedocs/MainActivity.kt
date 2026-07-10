package com.privatevoicedocs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.privatevoicedocs.app.PrivateVoiceDocsApplication
import com.privatevoicedocs.domain.repository.DocumentRepository
import com.privatevoicedocs.ui.documents.DocumentLibraryViewModel
import com.privatevoicedocs.ui.navigation.PrivateVoiceDocsApp
import com.privatevoicedocs.ui.onboarding.OnboardingScreen
import com.privatevoicedocs.ui.theme.PrivateVoiceDocsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as PrivateVoiceDocsApplication).container.documentRepository
        val preferences = getSharedPreferences("onboarding", MODE_PRIVATE)
        setContent {
            PrivateVoiceDocsTheme {
                var onboarded by rememberSaveable {
                    mutableStateOf(preferences.getBoolean("completed", false))
                }
                if (onboarded) {
                    val libraryViewModel: DocumentLibraryViewModel = viewModel(
                        factory = DocumentLibraryViewModelFactory(repository),
                    )
                    PrivateVoiceDocsApp(libraryViewModel)
                } else {
                    OnboardingScreen(onContinue = {
                        preferences.edit { putBoolean("completed", true) }
                        onboarded = true
                    })
                }
            }
        }
    }
}

private class DocumentLibraryViewModelFactory(
    private val repository: DocumentRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(DocumentLibraryViewModel::class.java))
        @Suppress("UNCHECKED_CAST")
        return DocumentLibraryViewModel(repository) as T
    }
}
