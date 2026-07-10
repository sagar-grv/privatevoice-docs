package com.privatevoicedocs.ui.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PrivacyCentreScreen() {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("Privacy centre", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
        PrivacyItem("Network", "This build does not request Android internet permission.")
        PrivacyItem("Storage", "Imported files are copied into app-private storage; the external URI is not the permanent source.")
        PrivacyItem("Backups", "Cloud backup and device-to-device transfer are disabled for app files, databases, and preferences.")
        PrivacyItem("Deletion", "Deleting a document removes its private namespace and cascades page/chunk records. Flash storage cannot guarantee physical overwrite.")
        PrivacyItem("AI status", "PDF extraction, OCR, embeddings, local RAG, speech, and personal voice inference are not implemented in this milestone.")
    }
}

@Composable
private fun PrivacyItem(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
