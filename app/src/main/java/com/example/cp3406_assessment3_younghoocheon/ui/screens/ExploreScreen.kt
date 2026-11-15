package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.example.cp3406_assessment3_younghoocheon.data.CampusActivity
import com.example.cp3406_assessment3_younghoocheon.data.ExploreSearch
import com.example.cp3406_assessment3_younghoocheon.data.ProgramInfo
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.data.StaffMember
import com.example.cp3406_assessment3_younghoocheon.data.UniversityData
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.ScreenTitle
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.StaffPhoto
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.components.Thumbnail
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Lime
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky
import java.net.URLEncoder

private const val SCHOLAR_HOME = "https://scholar.google.com/"

private fun scholarUrl(query: String): String {
    val trimmed = query.trim()
    if (trimmed.isEmpty()) return SCHOLAR_HOME
    return SCHOLAR_HOME + "scholar?q=" + URLEncoder.encode(trimmed, "UTF-8")
}

private fun levelColor(level: String): Color = when (level) {
    "Undergraduate" -> Lime
    "Postgraduate" -> Sky
    else -> Peach
}

/** Explore the department: search, programs, research areas, faculty and campus activities. */
@Composable
fun ExploreScreen(
    onOpenStaff: (String) -> Unit,
    onOpenProgram: (String) -> Unit,
    onOpenDepartment: () -> Unit,
    onOpenLearn: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    var query by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("All") }
    val searching = query.isNotBlank()

    val programHits = ExploreSearch.programs(query)
    val staffHits = ExploreSearch.staff(query)
    val staffShown = if (searching) staffHits else ExploreSearch.staffByCategory(category)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                ScreenTitle("Explore")
                MutedText("Information Technology at JCU Singapore")
            }
        }
        item {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                onSearch = { if (searching) uriHandler.openUri(scholarUrl(query)) },
            )
        }

        if (searching) {
            item { SectionHeader("Programs (${programHits.size})") }
            items(programHits, key = { it.id }) { program ->
                ProgramCard(program = program, onClick = { onOpenProgram(program.id) })
            }
            item { SectionHeader("Faculty (${staffHits.size})") }
            items(staffHits, key = { it.id }) { member ->
                StaffRow(member = member, onClick = { onOpenStaff(member.id) })
            }
            if (programHits.isEmpty() && staffHits.isEmpty()) {
                item { MutedText("No programs or faculty match your search.") }
            }
            item {
                PillButton(
                    text = "Search Google Scholar",
                    onClick = { uriHandler.openUri(scholarUrl(query)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                MutedText("Opens in your browser. Your search words go to Google, not to this app.")
            }
        } else {
            item { DepartmentCard(onClick = onOpenDepartment) }
            item { SectionHeader("Programs") }
            items(UniversityData.programs, key = { it.id }) { program ->
                ProgramCard(program = program, onClick = { onOpenProgram(program.id) })
            }
            item { SectionHeader("Research areas") }
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    UniversityData.researchAreas.forEach { area ->
                        TagChip(text = area, selected = area == category, onClick = { category = area })
                    }
                }
            }
            items(staffShown, key = { it.id }) { member ->
                StaffRow(member = member, onClick = { onOpenStaff(member.id) })
            }
            item { SectionHeader("Campus life") }
            item {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    UniversityData.activities.forEach { activity -> ActivityCard(activity) }
                }
            }
            item { SectionHeader("Lectures") }
            item { LectureShortcut(onClick = onOpenLearn) }
            item { MutedText(UniversityData.SOURCE_NOTE) }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, onSearch: () -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search programs, staff or topics") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = androidx.compose.foundation.text.KeyboardActions(onSearch = { onSearch() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}

@Composable
private fun DepartmentCard(onClick: () -> Unit) {
    val ink = MaterialTheme.colorScheme.onPrimary
    val lime = MaterialTheme.colorScheme.primary
    AppCard(modifier = Modifier.fillMaxWidth(), containerColor = lime) {
        Text("DEPARTMENT", style = MaterialTheme.typography.labelMedium, color = ink)
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Information Technology",
            style = MaterialTheme.typography.headlineSmall,
            color = ink,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "IoT, cybersecurity, AI, data science and game design.",
            style = MaterialTheme.typography.bodyMedium,
            color = ink,
        )
        Spacer(Modifier.height(16.dp))
        PillButton(text = "About the department", onClick = onClick, container = ink, content = lime)
    }
}

@Composable
private fun ProgramCard(program: ProgramInfo, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        onClickLabel = "Open ${program.name}",
    ) {
        TopicTag(text = program.level, color = levelColor(program.level))
        Spacer(Modifier.height(10.dp))
        Text(
            text = program.name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = program.summary,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun StaffRow(member: StaffMember, onClick: () -> Unit) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        onClickLabel = "Open ${member.name}",
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StaffPhoto(photo = member.photo, name = member.name, modifier = Modifier.size(64.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                MutedText(member.title)
                Text(
                    text = member.expertise.take(2).joinToString(", "),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ActivityCard(activity: CampusActivity) {
    AppCard(modifier = Modifier.width(240.dp), containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        TopicTag(text = "Campus", color = Mint)
        Spacer(Modifier.height(10.dp))
        Text(
            text = activity.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(4.dp))
        MutedText(activity.description)
    }
}

@Composable
private fun LectureShortcut(onClick: () -> Unit) {
    val featured = SampleData.links.first { it.kind == "Lecture" }
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        onClickLabel = "Open lectures",
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Thumbnail(
                url = featured.thumbnailUrl,
                label = featured.shortTitle,
                modifier = Modifier.size(width = 112.dp, height = 72.dp),
            )
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = featured.shortTitle,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                MutedText("Loaded live from YouTube")
            }
        }
    }
}