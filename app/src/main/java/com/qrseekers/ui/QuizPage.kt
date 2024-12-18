package com.qrseekers.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.qrseekers.data.Question
import kotlinx.coroutines.tasks.await
import com.qrseekers.viewmodels.QuizViewModel
import androidx.compose.runtime.livedata.observeAsState
import coil.compose.AsyncImage


@Composable
fun QuizPage(
    quizViewModel: QuizViewModel,
    zoneId: String,
    zoneName: String,
    onSubmit: (Map<String, String>) -> Unit
) {

    // remember the chosen answers
    var answers by remember { mutableStateOf(mutableMapOf<String, String>()) }


    // Get the QuizViewModel instance
    val quizViewModel: QuizViewModel = viewModel()

    // Get the questions from the ViewModel
    val questions by remember { quizViewModel.questions }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch questions for the zone when the composable is first launched
    LaunchedEffect(zoneId) {
        quizViewModel.loadQuestions(zoneId)
    }

    // Display questions or an error message
    errorMessage?.let { Text(text = it) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Zone title
        Text(
            text = "ZONE: $zoneName",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // List of questions
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(questions.size) { index ->
                val question = questions[index]
                QuestionItem(
                    question = question,
                    answer = quizViewModel.answers.value[question.id],
                    onAnswerChange = { newAnswer ->
                        quizViewModel.updateAnswer(question.id, newAnswer)
                    },
                    index = index + 1
                )
            }
        }

        // Submit button
        Button(
            onClick = { onSubmit(quizViewModel.answers.value) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Submit")
        }
    }
}

@Composable
fun QuestionItem(
    question: Question,
    answer: String?,
    onAnswerChange: (String) -> Unit,
    index: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        // Question text
        Text(
            text = "$index. (${question.points} pts) ${question.text}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Optional image
        question.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = "Question Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        // Options or input field
        question.options?.let { options ->
            MultipleChoiceOptions(
                options = options,
                selectedOption = answer,
                onOptionSelected = onAnswerChange
            )
        } ?: OpenEndedQuestion(
            answer = answer,
            onAnswerChange = onAnswerChange
        )
    }
}

@Composable
fun MultipleChoiceOptions(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column {
        options.forEach { option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOptionSelected(option) }
                    .padding(4.dp)
            ) {
                RadioButton(
                    selected = option == selectedOption,
                    onClick = { onOptionSelected(option) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = option)
            }
        }
    }
}

@Composable
fun OpenEndedQuestion(
    answer: String?,
    onAnswerChange: (String) -> Unit
) {
    OutlinedTextField(
        value = answer.orEmpty(),
        onValueChange = onAnswerChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Your answer") }
    )
}

@Preview(showBackground = true)
@Composable
fun QuizPagePreview() {
    QuizPage(
        quizViewModel = viewModel(),
        zoneId = "6lkp5c174aJFdccLItuA",
        zoneName = "Las aaa",
        onSubmit = { answers ->
            println("Answers submitted: $answers")
        }
    )
}
