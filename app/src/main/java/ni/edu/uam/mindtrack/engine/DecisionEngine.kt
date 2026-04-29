package ni.edu.uam.mindtrack.engine

class DecisionEngine {
    fun analyze(choices: List<String>): String {
        val counts = choices.groupingBy { it }.eachCount()
        
        val rational = counts.getOrDefault("rational", 0)
        val impulsive = counts.getOrDefault("impulsive", 0)
        val balanced = counts.getOrDefault("balanced", 0)
        
        return when {
            rational > impulsive && rational > balanced -> "Racional"
            impulsive > rational && impulsive > balanced -> "Impulsivo"
            else -> "Equilibrado"
        }
    }
}
