package net.joosa.wordler

class GuessList {
    private val charMap = mutableMapOf<Char, CharStats>()
    private val guesses = mutableListOf<Guess>()

    fun addGuess(word: String, output: String) = addGuess(Guess(word, output))

    fun addGuess(guess: Guess): GuessList {
        val copy = GuessList()
        charMap.forEach { (key, value) -> copy.charMap[key] = value.copy() }
        guesses.forEach { copy.guesses.add(it.copy() )}

        wordToCharIndexes(guess.word).forEach { (c, indexes) ->
            val (correctIndexes, incorrectIndexes) = indexes.partition { i -> guess.output[i] == CharState.CORRECT }

            val charStats = CharStats(
                correctIndexes = correctIndexes,
                incorrectIndexes = incorrectIndexes,
                minCount = indexes.count { i -> guess.output[i] != CharState.ABSENT },
                maxCount = run {
                    if (indexes.any { i -> guess.output[i] == CharState.ABSENT }) {
                        correctIndexes.size + if (indexes.any { i -> guess.output[i] == CharState.PRESENT }) 1 else 0
                    } else {
                        5
                    }
                }
            )

            copy.charMap.merge(c, charStats, CharStats::merge)
        }
        copy.guesses.add(guess)

        return copy
    }

    fun isValid(word: String): Boolean {
        val charIndexes = wordToCharIndexes(word)
        charMap.forEach { (c, stats) ->
            val indexes = charIndexes[c] ?: emptySet()
            if(!indexes.containsAll(stats.correctIndexes)) return false
            if(indexes.any { i -> stats.incorrectIndexes.contains(i) }) return false
            if(indexes.size < stats.minCount) return false
            if(indexes.size > stats.maxCount) return false
        }

        return true
    }

    fun isEmpty() = charMap.isEmpty()

    val size: Int
        get() = guesses.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GuessList

        if (guesses != other.guesses) return false

        return true
    }

    override fun hashCode(): Int {
        return guesses.hashCode()
    }
}

private fun wordToCharIndexes(word: String): Map<Char, Set<Int>> {
    return word
        .mapIndexed { i, c -> c to i }
        .groupBy({ it.first }, { it.second })
        .mapValues { it.value.toSet() }
}
