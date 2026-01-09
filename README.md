# Comic Generator - Android App

An AI-powered Android application that generates custom comic books for children based on simple prompts. The app uses OpenAI's GPT-4 for story generation and DALL-E 3 for creating comic panel images.

## Features

- **Simple Input Interface**: Enter a basic prompt like "spiderman fights a gorilla"
- **Customizable Parameters**:
  - Estimated reading time (1-15 minutes)
  - Child age (3-16 years)
- **AI-Powered Story Generation**: Creates age-appropriate stories with proper narrative structure
- **Dynamic Panel Count**: Automatically calculates the optimal number of panels based on reading time and age
- **Beautiful Comic Display**: Presents the story in a professional comic book format with:
  - AI-generated images for each panel
  - Character dialogue and narration
  - Panel-by-panel layout

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Networking**: Retrofit + OkHttp
- **Image Loading**: Coil
- **AI Services**: OpenAI API (GPT-4 + DALL-E 3)
- **Async Operations**: Kotlin Coroutines + Flow

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 24 or higher
- OpenAI API key

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Comic-gen
```

### 2. Get OpenAI API Key

1. Sign up for an OpenAI account at [https://platform.openai.com](https://platform.openai.com)
2. Navigate to API Keys section
3. Create a new API key
4. Copy the key (you won't be able to see it again)

### 3. Configure API Key

Open `app/src/main/java/com/comicgen/app/MainActivity.kt` and replace the placeholder:

```kotlin
private val OPENAI_API_KEY = "YOUR_API_KEY_HERE"
```

With your actual API key:

```kotlin
private val OPENAI_API_KEY = "sk-proj-..."
```

**⚠️ Security Note**: For production apps, never hardcode API keys. Use:
- BuildConfig fields
- Environment variables
- Secure key management solutions (like Android Keystore)

### 4. Build and Run

1. Open the project in Android Studio
2. Sync Gradle files
3. Connect an Android device or start an emulator
4. Click "Run" or press Shift+F10

## How to Use

1. **Launch the app**
2. **Enter your comic idea** in the text field (e.g., "a brave knight rescues a dragon from boredom")
3. **Adjust reading time** using the slider (affects number of panels)
4. **Select child age** using the slider (affects story complexity and content appropriateness)
5. **Tap "Generate Comic"**
6. **Wait for generation**:
   - Story structure is created first
   - Then images are generated for each panel
7. **View your comic** and scroll through the panels
8. **Create a new comic** by tapping "New Comic"

## Project Structure

```
app/src/main/java/com/comicgen/app/
├── MainActivity.kt                 # Main entry point
├── model/
│   └── ComicPanel.kt              # Data models for comics
├── api/
│   └── OpenAIService.kt           # API interface and DTOs
├── repository/
│   └── ComicRepository.kt         # Business logic for comic generation
├── viewmodel/
│   └── ComicViewModel.kt          # UI state management
└── ui/
    ├── ComicInputScreen.kt        # Input form UI
    └── ComicDisplayScreen.kt      # Comic viewer UI
```

## How It Works

### 1. Story Generation

The app sends a structured prompt to GPT-4 that includes:
- User's comic idea
- Child's age (for age-appropriate content)
- Desired reading time
- Required number of panels

GPT-4 returns a JSON structure with:
- Comic title
- Array of panels with descriptions and dialogue

### 2. Image Generation

For each panel:
- The description is converted into a detailed image prompt
- DALL-E 3 generates a comic-style illustration
- Images are displayed in the comic layout

### 3. Panel Count Calculation

The app dynamically calculates the optimal number of panels based on:
- **Age factor**: Younger children get simpler stories (4-6 panels), older children get more complex ones (8-10 panels)
- **Time factor**: Reading time is used as a multiplier
- **Result**: Between 4-16 panels per comic

## API Costs

Using OpenAI APIs incurs costs:
- **GPT-4**: ~$0.03-0.06 per story (depending on complexity)
- **DALL-E 3**: ~$0.04 per image (1024x1024 standard quality)
- **Total per comic**: $0.20-$0.80 (depending on panel count)

Monitor your usage at: [https://platform.openai.com/usage](https://platform.openai.com/usage)

## Customization

### Change AI Models

In `OpenAIService.kt`, you can modify:

```kotlin
// Use GPT-3.5 for lower costs
data class ChatCompletionRequest(
    val model: String = "gpt-3.5-turbo",  // Changed from "gpt-4"
    // ...
)

// Use different image sizes
data class ImageGenerationRequest(
    val model: String = "dall-e-3",
    val size: String = "1024x1024",  // Options: "1024x1024", "1792x1024", "1024x1792"
    val quality: String = "standard"  // or "hd" for better quality
)
```

### Adjust Story Parameters

In `ComicRepository.kt`, modify `calculatePanelCount()` to change how panels are calculated.

## Troubleshooting

### API Key Errors
- Ensure your API key is correctly copied
- Check that your OpenAI account has available credits
- Verify the key has permissions for GPT-4 and DALL-E 3

### Network Errors
- Check internet connection
- Ensure the app has internet permission (already configured in AndroidManifest.xml)
- Try increasing timeout values in `OpenAIService.kt`

### Build Errors
- Sync Gradle files (File → Sync Project with Gradle Files)
- Clean and rebuild (Build → Clean Project, then Build → Rebuild Project)
- Invalidate caches (File → Invalidate Caches / Restart)

## Future Enhancements

- [ ] Save generated comics locally
- [ ] Share comics with friends
- [ ] Multiple art styles (manga, superhero, cartoon)
- [ ] Edit panel dialogue
- [ ] Add sound effects and voiceover
- [ ] Offline mode with pre-generated templates
- [ ] User authentication and cloud storage
- [ ] In-app API key configuration

## License

This project is provided as-is for educational and personal use.

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues.

## Acknowledgments

- OpenAI for GPT-4 and DALL-E 3 APIs
- Android Jetpack Compose team
- All open-source library contributors
