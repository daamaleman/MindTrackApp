package ni.edu.uam.mindtrack.engine

import ni.edu.uam.mindtrack.model.Option
import ni.edu.uam.mindtrack.model.PlayerState

class GameEngine {
    fun applyOption(state: PlayerState, option: Option): PlayerState {
        return state.copy(
            energy = (state.energy + option.energyEffect).coerceIn(0, 100),
            stress = (state.stress + option.stressEffect).coerceIn(0, 100),
            progress = (state.progress + option.progressEffect).coerceIn(0, 100),
            money = (state.money + option.moneyEffect).coerceAtLeast(0)
        )
    }

    fun evaluateFinal(state: PlayerState, path: List<Int>): String {
        return when {
            state.stress >= 85 || state.energy <= 10 -> "Burnout"
            state.progress >= 80 && state.stress <= 40 -> "Éxito equilibrado"
            state.money <= 20 && state.progress < 50 -> "Fracaso"
            else -> "Desbalance"
        }
    }
}
