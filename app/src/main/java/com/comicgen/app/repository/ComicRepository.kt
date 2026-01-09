package com.comicgen.app.repository

import com.comicgen.app.api.*
import com.comicgen.app.model.ComicPanel
import com.comicgen.app.model.ComicRequest
import com.comicgen.app.model.ComicStory
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ComicRepository(private val apiKey: String) {
    private val openAIService = OpenAIService.create()
    private val gson = Gson()

    suspend fun generateComic(request: ComicRequest): Result<ComicStory> = withContext(Dispatchers.IO) {
        try {
            // Step 1: Generate story structure
            val story = generateStoryStructure(request)

            Result.success(story)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateImageForPanel(panel: ComicPanel): Result<String> = withContext(Dispatchers.IO) {
        try {
            // Create detailed prompt for image generation
            val imagePrompt = buildImagePrompt(panel)

            val imageRequest = ImageGenerationRequest(
                prompt = imagePrompt,
                n = 1,
                size = "1024x1024"
            )

            val response = openAIService.generateImage(
                authorization = "Bearer $apiKey",
                request = imageRequest
            )

            if (response.data.isNotEmpty()) {
                Result.success(response.data[0].url)
            } else {
                Result.failure(Exception("No image generated"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateStoryStructure(request: ComicRequest): ComicStory {
        // Calculate number of panels based on reading time and age
        val panelsCount = calculatePanelCount(request.readingTimeMinutes, request.childAge)

        val systemPrompt = """
            You are a creative children's comic book writer. Create engaging, age-appropriate stories.
            Generate a comic story with exactly $panelsCount panels.

            Return ONLY a valid JSON object with this structure:
            {
              "title": "Comic Title",
              "panels": [
                {
                  "panelNumber": 1,
                  "description": "Visual description of what's happening",
                  "dialogue": "Character dialogue or narration"
                }
              ]
            }
        """.trimIndent()

        val userPrompt = """
            Create a ${request.childAge}-year-old appropriate comic story about: "${request.prompt}"

            Requirements:
            - Exactly $panelsCount panels
            - Age-appropriate for ${request.childAge} years old
            - Engaging and fun
            - Each panel should have clear visual description and dialogue
            - Story should have beginning, middle, and end

            Return only the JSON, no other text.
        """.trimIndent()

        val chatRequest = ChatCompletionRequest(
            model = "gpt-4",
            messages = listOf(
                Message("system", systemPrompt),
                Message("user", userPrompt)
            ),
            temperature = 0.8
        )

        val response = openAIService.generateStory(
            authorization = "Bearer $apiKey",
            request = chatRequest
        )

        // Parse the response
        val content = response.choices.firstOrNull()?.message?.content
            ?: throw Exception("No response from AI")

        return parseStoryResponse(content)
    }

    private fun parseStoryResponse(content: String): ComicStory {
        try {
            // Extract JSON from response (sometimes GPT adds markdown code blocks)
            val jsonContent = content
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val jsonObject = JsonParser.parseString(jsonContent).asJsonObject
            val title = jsonObject.get("title").asString
            val panelsArray = jsonObject.getAsJsonArray("panels")

            val panels = panelsArray.map { panelElement ->
                val panelObj = panelElement.asJsonObject
                ComicPanel(
                    panelNumber = panelObj.get("panelNumber").asInt,
                    description = panelObj.get("description").asString,
                    dialogue = panelObj.get("dialogue").asString,
                    imageUrl = null
                )
            }

            return ComicStory(
                title = title,
                panels = panels,
                totalPanels = panels.size
            )
        } catch (e: Exception) {
            throw Exception("Failed to parse story: ${e.message}")
        }
    }

    private fun buildImagePrompt(panel: ComicPanel): String {
        return """
            Comic book style illustration: ${panel.description}

            Style: Colorful, vibrant comic book art style suitable for children.
            Clean lines, bright colors, friendly and engaging.
            Professional comic book illustration.
        """.trimIndent()
    }

    private fun calculatePanelCount(readingTimeMinutes: Int, childAge: Int): Int {
        // Estimate panels based on reading time and age
        // Younger children: simpler stories with fewer panels
        // Older children: more complex stories with more panels

        val basePanels = when {
            childAge <= 5 -> 4
            childAge <= 8 -> 6
            childAge <= 12 -> 8
            else -> 10
        }

        // Adjust for reading time (roughly 1 panel per minute)
        val timeFactor = (readingTimeMinutes / 2.0).toInt().coerceAtLeast(1)

        return (basePanels * timeFactor).coerceIn(4, 16)
    }
}
