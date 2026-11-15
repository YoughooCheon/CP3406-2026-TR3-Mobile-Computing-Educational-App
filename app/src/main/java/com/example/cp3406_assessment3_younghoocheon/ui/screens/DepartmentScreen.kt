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

/** Overview of the Information Technology department at JCU Singapore. */
@Composable
fun DepartmentScreen(onBack: () -> Unit, onOpenStaff: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BackHeader(title = "Department", onBack = onBack)

        Text(
            text = "Information Technology at JCU Singapore",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = UniversityData.departmentIntro,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        SectionHeader("Research areas")
        AppCard(modifier = Modifier.fillMaxWidth()) {
            UniversityData.departmentAreas.forEachIndexed { i, area ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                BulletText(area)
            }
        }

        SectionHeader("Professional bodies")
        AppCard(modifier = Modifier.fillMaxWidth()) {
            MutedText("IT students are entitled to membership of two professional bodies.")
            Spacer(Modifier.height(10.dp))
            UniversityData.professionalBodies.forEachIndexed { i, body ->
                if (i > 0) Spacer(Modifier.height(8.dp))
                BulletText(body)
            }
        }

        SectionHeader("Activities for students")
        UniversityData.activities.forEach { activity ->
            AppCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                MutedText(activity.description)
            }
        }

        SectionHeader("Faculty")
        FacultyStrip(onOpenStaff = onOpenStaff)

        MutedText(UniversityData.SOURCE_NOTE)
    }
}