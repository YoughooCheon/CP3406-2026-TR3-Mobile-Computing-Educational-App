package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cp3406_assessment3_younghoocheon.data.UniversityData
import com.example.cp3406_assessment3_younghoocheon.ui.components.AppCard
import com.example.cp3406_assessment3_younghoocheon.ui.components.BackHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.BulletText
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.StaffPhoto
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Mint
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky

private val expertiseColors = listOf(Sky, Mint, Peach)

/** Profile of one staff member: photo, qualifications, expertise, summary, highlights and publications. */
@Composable
fun FacultyDetailScreen(staffId: String, onBack: () -> Unit) {
    val member = UniversityData.staffById(staffId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BackHeader(title = "Faculty", onBack = onBack)

        if (member == null) {
            MutedText("This staff profile could not be found.")
            return@Column
        }

        StaffPhoto(
            photo = member.photo,
            name = member.name,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
            shape = RoundedCornerShape(28.dp),
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = member.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            MutedText(member.title)
            MutedText("James Cook University in Singapore")
        }

        SectionHeader("Areas of expertise")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            member.expertise.forEachIndexed { i, area ->
                TopicTag(text = area, color = expertiseColors[i % expertiseColors.size])
            }
        }

        SectionHeader("About")
        AppCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = member.summary,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        SectionHeader("Qualifications")
        AppCard(modifier = Modifier.fillMaxWidth()) {
            member.qualifications.forEachIndexed { i, item ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                BulletText(item)
            }
        }

        SectionHeader("Highlights")
        AppCard(modifier = Modifier.fillMaxWidth()) {
            member.highlights.forEachIndexed { i, item ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                BulletText(item)
            }
        }

        SectionHeader("Selected publications")
        AppCard(modifier = Modifier.fillMaxWidth()) {
            member.publications.forEachIndexed { i, item ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                BulletText(item)
            }
        }

        MutedText(UniversityData.SOURCE_NOTE)
    }
}