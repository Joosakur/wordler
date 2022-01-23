package net.joosa.wordler

import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.math.ceil

class Wordler(
    private val version: GameVersion
) {
    val resourceRoot = "src/main/resources/${version.name.lowercase()}"

    private val initialMainWords = File("$resourceRoot/main-words.txt")
        .readText()
        .split(",")
        .map { it.trim().lowercase() }
        .filter { it.isNotBlank() }

    private val initialExtraWords = File("$resourceRoot/extra-words.txt")
        .readText()
        .split(",")
        .map { it.trim().lowercase() }
        .filter { it.isNotBlank() }

    private val resultCache = mutableMapOf<GuessList, String>()

    private val blockList = mutableSetOf<String>()

    fun getNextGuess(guesses: GuessList = GuessList()): String {
        if (guesses.isEmpty()) {
            return when(version) {
                GameVersion.WORDLE -> "roate"
                GameVersion.SANULI -> "kasti"
            }
        }

        resultCache[guesses]?.also {
            return it
        }

        val mainWords = initialMainWords.filter(guesses::isValid) - blockList
        val extraWords = initialExtraWords.filter(guesses::isValid) - blockList
        val extraWordsMayBeCorrect = when(version) {
            GameVersion.WORDLE -> false
            GameVersion.SANULI -> true
        }

        if(mainWords.isNotEmpty() && mainWords.size <= 2) {
            return mainWords.first()
        }

        val threadCount = 12
        val pool = Executors.newFixedThreadPool(threadCount)

        val words = mainWords + if(mainWords.size > 20 || mainWords.isEmpty()) extraWords else emptyList()

        val tasks = (words)
            .chunked(ceil(words.size.toDouble() / threadCount).toInt())
            .map { Task(it, guesses, if(extraWordsMayBeCorrect) words else mainWords) }

        val result = pool.invokeAll(tasks).map { it.get() }.reduce { a, b -> a + b}

        return result.entries
            .minByOrNull { it.value }!!
            .key
            .also {
                if(guesses.size <= 3)
                    resultCache[guesses] = it
            }
    }

    fun blockWord(word: String) {
        blockList.add(word)
        resultCache.entries.filter { it.value == word }.forEach { resultCache.remove(it.key) }
    }
}

private class Task(
    private val words: List<String>,
    private val guesses: GuessList,
    private val possibleWords: List<String>
) : Callable<Map<String, Double>> {
    override fun call(): Map<String, Double> {
        return words.associateWith { nextGuess ->
            possibleWords
                .map { correctGuess ->
                    val output = nextGuess.toCharArray().zip(correctGuess.toCharArray())
                        .map { (gc, cc) ->
                            if (gc == cc) {
                                CharState.CORRECT
                            } else {
                                val withoutCorrect = correctGuess
                                    .toCharArray()
                                    .filterIndexed { i, c -> nextGuess[i] != c }
                                if (withoutCorrect.contains(gc)) CharState.PRESENT else CharState.ABSENT
                            }
                        }
                    val nextStats = guesses.addGuess(Guess(nextGuess, output))
                    possibleWords.count { nextNextGuess -> nextStats.isValid(nextNextGuess) }
                }
                .filter { it > 0 }
                .average()
        }
    }
}
