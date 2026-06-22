package ni.edu.uam.mindtrack.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ni.edu.uam.mindtrack.R
import ni.edu.uam.mindtrack.viewmodel.AdaptiveQuestionnaireViewModel
import ni.edu.uam.mindtrack.viewmodel.QuestionType

@Composable
fun QuestionnaireScreen(
    viewModel: AdaptiveQuestionnaireViewModel,
    onFinish: (report: ni.edu.uam.mindtrack.viewmodel.ProfileReport) -> Unit = {}
) {
    val q = viewModel.currentQuestion
    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = stringResource(R.string.q_title), style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(8.dp))

                if (q == null) {
                    // Cuando no hay más preguntas mostramos el informe final generado por el ViewModel
                    val report = viewModel.generateProfileReport(context)
                    Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                        Text(text = stringResource(R.string.q_final_result, report.mainLabel), style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = stringResource(R.string.q_global_confidence, (report.globalConfidence * 100).toInt()), style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Puntajes por rasgo y confianza
                        report.traitScores.forEach { (trait, score) ->
                            val conf = report.confidences[trait] ?: 0.5
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = trait, style = MaterialTheme.typography.bodySmall)
                                    Text(text = "${stringResource(R.string.q_score, score.toInt())} • ${stringResource(R.string.q_confidence, (conf * 100).toInt())}", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        if (report.strengths.isNotEmpty()) {
                            Text(text = stringResource(R.string.q_strengths), style = MaterialTheme.typography.labelMedium)
                            report.strengths.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (report.growthAreas.isNotEmpty()) {
                            Text(text = stringResource(R.string.q_growth_areas), style = MaterialTheme.typography.labelMedium)
                            report.growthAreas.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        if (report.patterns.isNotEmpty()) {
                            Text(text = stringResource(R.string.q_detected_patterns), style = MaterialTheme.typography.labelMedium)
                            report.patterns.forEach { Text("• $it", style = MaterialTheme.typography.bodySmall) }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Button(onClick = { viewModel.reset() }) {
                                Text(stringResource(R.string.q_reset))
                            }
                                            Button(onClick = { onFinish(report) }) {
                                                Text(stringResource(R.string.q_finish))
                                            }
                        }
                    }
                    return@Column
                }

        Text(text = q.text, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))

        when (q.type) {
            QuestionType.LIKERT -> LikertQuestion(onAnswer = { value ->
                viewModel.submitAnswer(q.id, value.toString(), likertValue = value)
            })

            QuestionType.MULTIPLE_CHOICE -> MultipleChoiceQuestion(q = q, onAnswer = { idx, option ->
                viewModel.submitAnswer(q.id, option, optionIndex = idx)
            })

            QuestionType.FORCED_CHOICE -> MultipleChoiceQuestion(q = q, onAnswer = { idx, option ->
                viewModel.submitAnswer(q.id, option, optionIndex = idx)
            })

            QuestionType.OPEN, QuestionType.SCENARIO -> OpenQuestion(onAnswer = { text ->
                viewModel.submitAnswer(q.id, text)
            })
        }
    }
}

@Composable
private fun LikertQuestion(onAnswer: (Int) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val idx = i
            Button(
                onClick = {
                    onAnswer(idx)
                },
                modifier = Modifier.padding(4.dp)
            ) {
                Text(idx.toString())
            }
        }
    }
}

@Composable
private fun MultipleChoiceQuestion(q: ni.edu.uam.mindtrack.viewmodel.Question, onAnswer: (Int, String) -> Unit) {
    Column {
        q.options.forEachIndexed { idx, option ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable { onAnswer(idx, option) }
                .padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = false, onClick = { onAnswer(idx, option) })
                Spacer(modifier = Modifier.width(8.dp))
                Text(option)
            }
        }
    }
}

@Composable
private fun OpenQuestion(onAnswer: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Column {
        OutlinedTextField(value = text, onValueChange = { text = it }, modifier = Modifier
            .fillMaxWidth()
            .height(120.dp), placeholder = { Text(stringResource(R.string.q_placeholder)) })
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onAnswer(text) }) {
            Text(stringResource(R.string.q_send))
        }
    }
}
