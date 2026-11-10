package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.NoticeBox
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(onOpenPrivacy: () -> Unit) {
    val container = appContainer()
    val viewModel: SettingsViewModel = viewModel(
        factory = viewModelFactory {
            initializer { SettingsViewModel(container.settingsStore, container.repository) }
        },
    )
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ScreenTitle("Settings")

        SettingsGroup("Display and access") {
            Text(
                text = "Text size",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            MutedText("Changes the whole app right away")
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("S", "M", "L").forEach { size ->
                    TagChip(
                        text = size,
                        selected = settings.textSize == size,
                        onClick = { viewModel.setTextSize(size) },
                    )
                }
            }
            SwitchRow(
                label = "High contrast",
                hint = "Brighter secondary text and clearer outlines",
                checked = settings.highContrast,
                onCheckedChange = viewModel::setHighContrast,
            )
            SwitchRow(
                label = "Reduce motion",
                hint = "Turns off the card flip and bar animations",
                checked = settings.reduceMotion,
                onCheckedChange = viewModel::setReduceMotion,
            )
        }

        SettingsGroup("Reminders") {
            SwitchRow(
                label = "Daily reminder",
                hint = "Saved now. Notifications arrive in a later update.",
                checked = settings.dailyReminder,
                onCheckedChange = viewModel::setDailyReminder,
            )
            SwitchRow(
                label = "Break reminders",
                hint = "Saved now. Used in a later update.",
                checked = settings.breakReminders,
                onCheckedChange = viewModel::setBreakReminders,
            )
        }

        AppCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = onOpenPrivacy,
            onClickLabel = "Open Privacy Center",
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "Privacy Center",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        NoticeBox("Your settings are saved on this device and stay after you close the app.")
    }
}

@Composable
private fun SettingsGroup(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp),
        )
        AppCard(modifier = Modifier.fillMaxWidth(), content = content)
    }
}

@Composable
private fun SwitchRow(
    label: String,
    hint: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .toggleable(value = checked, role = Role.Switch, onValueChange = onCheckedChange),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (hint != null) MutedText(hint)
        }
        Switch(
            checked = checked,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = MaterialTheme.colorScheme.primary,
            ),
        )
    }
}