package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.R
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.Avatar
import com.example.cp3406_assessment3_younghoocheon.ui.components.MenuRow
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.NicknameDialog
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Lime
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sand
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.OverviewViewModel

/** Profile tab: nickname, personal numbers and shortcuts to cards, faculty, settings and privacy. */
@Composable
fun ProfileScreen(
    onOpenCards: () -> Unit,
    onOpenExplore: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenPrivacy: () -> Unit,
) {
    val container = appContainer()
    val viewModel: OverviewViewModel = viewModel(
        factory = viewModelFactory {
            initializer { OverviewViewModel(container.repository, container.settingsStore) }
        },
    )
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val cards by viewModel.cards.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    var showNickname by rememberSaveable { mutableStateOf(false) }
    var showAbout by rememberSaveable { mutableStateOf(false) }

    if (showNickname) {
        NicknameDialog(
            current = settings.nickname,
            onDismiss = { showNickname = false },
            onSave = {
                viewModel.setNickname(it)
                showNickname = false
            },
        )
    }
    if (showAbout) {
        AlertDialog(
            onDismissRequest = { showAbout = false },
            icon = {
                Image(
                    painter = painterResource(R.drawable.logo_mark),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier.size(48.dp),
                )
            },
            title = { Text("About MemoryDesk") },
            text = {
                Text(
                    "A study companion for university IT students. Cards, reviews and settings stay on " +
                            "this device. Faculty and program information is summarised from the JCU Singapore " +
                            "Information Technology booklet (SIN07/26).",
                )
            },
            confirmButton = { TextButton(onClick = { showAbout = false }) { Text("Close") } },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        ScreenTitle("Profile")

        AppCard(modifier = Modifier.fillMaxWidth(), glow = MaterialTheme.colorScheme.primary) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Avatar(
                    initial = settings.nickname,
                    size = 112.dp,
                    color = MaterialTheme.colorScheme.primary,
                    image = R.drawable.avatar_student,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = settings.nickname,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText("No account. Your data stays on this device.")
                Spacer(Modifier.height(16.dp))
                PillButton(
                    text = "Edit nickname",
                    onClick = { showNickname = true },
                    container = MaterialTheme.colorScheme.surfaceVariant,
                    content = MaterialTheme.colorScheme.onSurface,
                    showArrow = false,
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ProfileStat("Reviewed", stats.totalReviewed.toString(), Modifier.weight(1f))
            ProfileStat("My cards", cards.count { it.custom }.toString(), Modifier.weight(1f))
            ProfileStat("Days", stats.streakDays.toString(), Modifier.weight(1f))
        }

        AppCard(modifier = Modifier.fillMaxWidth()) {
            MenuRow(Icons.Filled.Create, Lime, "My cards", onOpenCards)
            MenuRow(Icons.Filled.Person, Sky, "Faculty directory", onOpenExplore)
            MenuRow(Icons.Filled.Settings, Mint, "Settings", onOpenSettings)
            MenuRow(Icons.Filled.Lock, Peach, "Privacy Center", onOpenPrivacy)
            MenuRow(Icons.Filled.Info, Sand, "About this app", { showAbout = true })
        }
    }
}

@Composable
private fun ProfileStat(label: String, value: String, modifier: Modifier = Modifier) {
    AppCard(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        MutedText(label)
    }
}