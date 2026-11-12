package com.example.cp3406_assessment3_younghoocheon.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cp3406_assessment3_younghoocheon.appContainer
import com.example.cp3406_assessment3_younghoocheon.data.MAX_CARD_TEXT
import com.example.cp3406_assessment3_younghoocheon.data.SampleData
import com.example.cp3406_assessment3_younghoocheon.ui.components.BackHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.MutedText
import com.example.cp3406_assessment3_younghoocheon.ui.components.PillButton
import com.example.cp3406_assessment3_younghoocheon.ui.components.SectionHeader
import com.example.cp3406_assessment3_younghoocheon.ui.components.TagChip
import com.example.cp3406_assessment3_younghoocheon.ui.viewmodel.CardsViewModel

@Composable
fun AddCardScreen(onBack: () -> Unit) {
    val container = appContainer()
    val viewModel: CardsViewModel = viewModel(
        factory = viewModelFactory { initializer { CardsViewModel(container.repository) } },
    )
    val topics = SampleData.topics.drop(1)

    var front by rememberSaveable { mutableStateOf("") }
    var back by rememberSaveable { mutableStateOf("") }
    var topic by rememberSaveable { mutableStateOf(topics.first()) }
    var showErrors by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BackHeader(title = "New card", onBack = onBack)
        MutedText("Write a question on the front and the answer on the back.")

        CardTextField(
            label = "Question",
            value = front,
            onValueChange = { if (it.length <= MAX_CARD_TEXT) front = it },
            isError = showErrors && front.isBlank(),
        )
        CardTextField(
            label = "Answer",
            value = back,
            onValueChange = { if (it.length <= MAX_CARD_TEXT) back = it },
            isError = showErrors && back.isBlank(),
        )

        SectionHeader("Topic")
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            topics.forEach { name ->
                TagChip(text = name, selected = name == topic, onClick = { topic = name })
            }
        }

        PillButton(
            text = "Save card",
            onClick = {
                if (front.isBlank() || back.isBlank()) {
                    showErrors = true
                } else {
                    viewModel.addCard(front.trim(), back.trim(), topic, onBack)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        )
        MutedText("Your cards stay on this device.")
    }
}

@Composable
private fun CardTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        supportingText = {
            Text(if (isError) "This field is required" else "${value.length}/$MAX_CARD_TEXT")
        },
        isError = isError,
        minLines = 2,
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier.fillMaxWidth(),
    )
}