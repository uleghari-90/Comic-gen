package com.comicgen.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comicgen.app.model.ComicPanel
import com.comicgen.app.model.ComicRequest
import com.comicgen.app.model.ComicStory
import com.comicgen.app.repository.ComicRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UiState {
    object Idle : UiState()
    object Loading : UiState()
    data class StoryGenerated(val story: ComicStory) : UiState()
    data class GeneratingImages(val story: ComicStory, val progress: Int, val total: Int) : UiState()
    data class Success(val story: ComicStory) : UiState()
    data class Error(val message: String) : UiState()
}

class ComicViewModel(private val apiKey: String) : ViewModel() {
    private val repository = ComicRepository(apiKey)

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun generateComic(prompt: String, readingTime: Int, age: Int) {
        if (prompt.isBlank()) {
            _uiState.value = UiState.Error("Please enter a comic idea")
            return
        }

        if (apiKey.isBlank() || apiKey == "YOUR_API_KEY_HERE") {
            _uiState.value = UiState.Error("Please configure your OpenAI API key in MainActivity")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = UiState.Loading

                // Generate story structure
                val request = ComicRequest(prompt, readingTime, age)
                val result = repository.generateComic(request)

                result.fold(
                    onSuccess = { story ->
                        _uiState.value = UiState.StoryGenerated(story)

                        // Generate images for each panel
                        generateImagesForPanels(story)
                    },
                    onFailure = { error ->
                        _uiState.value = UiState.Error("Failed to generate story: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    private suspend fun generateImagesForPanels(story: ComicStory) {
        val updatedPanels = mutableListOf<ComicPanel>()

        story.panels.forEachIndexed { index, panel ->
            _uiState.value = UiState.GeneratingImages(
                story = story.copy(panels = updatedPanels + story.panels.drop(index)),
                progress = index,
                total = story.panels.size
            )

            val imageResult = repository.generateImageForPanel(panel)

            imageResult.fold(
                onSuccess = { imageUrl ->
                    updatedPanels.add(panel.copy(imageUrl = imageUrl))
                },
                onFailure = {
                    // Add panel without image on failure
                    updatedPanels.add(panel.copy(imageUrl = null))
                }
            )
        }

        _uiState.value = UiState.Success(story.copy(panels = updatedPanels))
    }

    fun reset() {
        _uiState.value = UiState.Idle
    }
}
