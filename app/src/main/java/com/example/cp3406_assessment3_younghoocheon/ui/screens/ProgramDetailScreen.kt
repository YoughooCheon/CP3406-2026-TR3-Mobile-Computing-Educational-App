package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
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
import com.example.cp3406_assessment3_younghoocheon.ui.components.FacultyStrip
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.TopicTag
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Lime
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Peach
import com.example.cp3406_assessment3_younghoocheon.ui.theme.Sky

/** Details of one study program: what it covers, accreditation and the staff who teach and research. */
@Composable
fun ProgramDetailScreen(programId: String, onBack: () -> Unit, onOpenStaff: (String) -> Unit) {
    val program = UniversityData.programById(programId)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BackHeader(title = "Program", onBack = onBack)

        if (program == null) {
            MutedText("This program could not be found.")
            return@Column
        }

        TopicTag(
            text = program.level,
            color = when (program.level) {
                "Undergraduate" -> Lime
                "Postgraduate" -> Sky
                else -> Peach
            },
        )
        Text(
            text = program.name,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = program.summary,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        program.sections.forEach { section ->
            SectionHeader(section.heading)
            AppCard(modifier = Modifier.fillMaxWidth()) {
                section.points.forEachIndexed { i, point ->
                    if (i > 0) Spacer(Modifier.height(8.dp))
                    BulletText(point)
                }
            }
        }

        program.accreditation?.let { text ->
            SectionHeader("Accreditation")
            AppCard(modifier = Modifier.fillMaxWidth(), containerColor = MaterialTheme.colorScheme.primary) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        SectionHeader("Meet the staff")
        FacultyStrip(onOpenStaff = onOpenStaff)

        MutedText(UniversityData.SOURCE_NOTE)
    }
}