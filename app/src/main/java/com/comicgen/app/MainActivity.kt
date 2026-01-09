package com.comicgen.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.comicgen.app.ui.*
import com.comicgen.app.viewmodel.ComicViewModel
import com.comicgen.app.viewmodel.UiState

class MainActivity : ComponentActivity() {

    // IMPORTANT: Replace with your actual OpenAI API key
    private val OPENAI_API_KEY = "YOUR_API_KEY_HERE"

    private lateinit var viewModel: ComicViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ComicViewModel(OPENAI_API_KEY)

        setContent {
            ComicGenTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ComicGeneratorApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun ComicGeneratorApp(viewModel: ComicViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is UiState.Idle -> {
            ComicInputScreen(
                onGenerateClick = { prompt, readingTime, age ->
                    viewModel.generateComic(prompt, readingTime, age)
                }
            )
        }

        is UiState.Loading -> {
            LoadingScreen("Generating story structure...")
        }

        is UiState.StoryGenerated -> {
            LoadingScreen("Story created! Now generating images...")
        }

        is UiState.GeneratingImages -> {
            GeneratingImagesScreen(
                story = state.story,
                progress = state.progress,
                total = state.total
            )
        }

        is UiState.Success -> {
            ComicDisplayScreen(
                story = state.story,
                onBackClick = { viewModel.reset() }
            )
        }

        is UiState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = { viewModel.reset() }
            )
        }
    }
}

@Composable
fun ComicGenTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}
