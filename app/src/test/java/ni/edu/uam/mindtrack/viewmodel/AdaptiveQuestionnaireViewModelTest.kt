package ni.edu.uam.mindtrack.viewmodel

import org.junit.Test
import org.junit.Assert.*

class AdaptiveQuestionnaireViewModelTest {

    @Test
    fun initialScoresAreNeutral() {
        val vm = AdaptiveQuestionnaireViewModel()
        val snapshot = vm.getProfileSnapshot()
        for ((_, v) in snapshot) {
            assertEquals(50.0, v, 0.0001)
        }
    }

    @Test
    fun likertAnswerUpdatesTrait() {
        val vm = AdaptiveQuestionnaireViewModel()
        val q = vm.currentQuestion ?: return
        // find a likert question in bank
        val likert = vm.currentQuestion?.takeIf { it.type == QuestionType.LIKERT }
        if (likert != null) {
            // answer strongly agree (5)
            vm.submitAnswer(likert.id, "5", likertValue = 5)
            val after = vm.getProfileSnapshot()
            // At least one trait should have changed from 50
            assertTrue(after.values.any { it != 50.0 })
        } else {
            // fallback: pick first LIKERT from bank
            val bankLikert = vm.run { javaClass.getDeclaredField("questions").apply { isAccessible = true }.get(vm) as List<Question> }
            val lk = bankLikert.first { it.type == QuestionType.LIKERT }
            vm.submitAnswer(lk.id, "5", likertValue = 5)
            val after = vm.getProfileSnapshot()
            assertTrue(after.values.any { it != 50.0 })
        }
    }

    @Test
    fun recomputeConfidencesProducesValidRange() {
        val vm = AdaptiveQuestionnaireViewModel()
        // simulate some updates
        vm.submitAnswer("Q001", "5", likertValue = 5)
        vm.submitAnswer("Q004", "1", likertValue = 1)
        // submitAnswer already recalcula confianzas internamente
        val confMap = vm.confidencePerTrait
        for ((_, v) in confMap) {
            assertTrue(v >= 0.0 && v <= 1.0)
        }
    }

    @Test
    fun generateProfileReportReturnsReport() {
        val vm = AdaptiveQuestionnaireViewModel()
        vm.submitAnswer("Q001", "5", likertValue = 5)
        val report = vm.generateProfileReport()
        assertNotNull(report)
        assertTrue(report.traitScores.isNotEmpty())
        assertTrue(report.confidences.isNotEmpty())
    }
}


