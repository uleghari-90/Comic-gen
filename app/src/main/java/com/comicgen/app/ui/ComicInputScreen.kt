package com.comicgen.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComicInputScreen(
    onGenerateClick: (String, Int, Int) -> Unit
) {
    var prompt by remember { mutableStateOf("") }
    var readingTime by remember { mutableStateOf(5) }
    var childAge by remember { mutableStateOf(8) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Comic Generator",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Create amazing comics with AI",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Prompt Input
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Comic Idea") },
            placeholder = { Text("e.g., 'spiderman fights a gorilla'") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        // Reading Time Slider
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Reading Time: $readingTime minutes",
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = readingTime.toFloat(),
                onValueChange = { readingTime = it.toInt() },
                valueRange = 1f..15f,
                steps = 13,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Longer reading time = more panels",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Age Selector
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Child Age: $childAge years",
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = childAge.toFloat(),
                onValueChange = { childAge = it.toInt() },
                valueRange = 3f..16f,
                steps = 12,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Age affects story complexity and content",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Generate Button
        Button(
            onClick = { onGenerateClick(prompt, readingTime, childAge) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = prompt.isNotBlank()
        ) {
            Text(
                text = "Generate Comic",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
