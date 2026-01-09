package com.comicgen.app.model

data class ComicPanel(
    val panelNumber: Int,
    val description: String,
    val dialogue: String,
    val imageUrl: String? = null,
    val isGenerating: Boolean = false
)

data class ComicStory(
    val title: String,
    val panels: List<ComicPanel>,
    val totalPanels: Int
)

data class ComicRequest(
    val prompt: String,
    val readingTimeMinutes: Int,
    val childAge: Int
)
