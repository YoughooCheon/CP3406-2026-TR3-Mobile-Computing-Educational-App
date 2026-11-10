package com.example.cp3406_assessment3_younghoocheon.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.BackHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.NoticeBox
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.screenBackground
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun PrivacyScreen(onBack: () -> Unit) {
    val container = appContainer()
    val viewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer { SettingsViewModel(container.settingsStore, container.repository) }
        },
    )
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var confirmDelete by remember { mutableStateOf(false) }
    val error = MaterialTheme.colorScheme.error

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Delete all data?") },
            text = { Text("This removes your review history and settings from this device. It cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAll()
                        confirmDelete = false
                    },
                ) { Text("Delete", color = error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDelete = false }) { Text("Cancel") }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .screenBackground()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BackHeader(title = "Privacy Center", onBack = onBack)

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "Stored on this device only",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Which card you reviewed, your rating, the time, and your settings",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Never collected",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Name, email, age, location, contacts, ads, analytics",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Notifications",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                MutedText("Off")
            }
            Spacer(Modifier.height(4.dp))
            MutedText("The app does not send notifications yet. It works fully without them.")
        }

        NoticeBox("YouTube and Google Scholar links open outside the app, and lecture thumbnails load from YouTube. Their privacy policies apply.")

        PillButton(
            text = "Export my data (JSON)",
            onClick = {
                scope.launch {
                    val json = viewModel.exportJson()
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "MemoryDesk data export")
                        putExtra(Intent.EXTRA_TEXT, json)
                    }
                    context.startActivity(Intent.createChooser(send, "Export data"))
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        OutlinedButton(
            onClick = { confirmDelete = true },
            modifier = Modifier.fillMaxWidth().heightIn(min = 56.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, error),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = error),
        ) { Text("Delete all data") }
    }
}