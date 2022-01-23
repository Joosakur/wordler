package net.joosa.wordler

import java.lang.IllegalArgumentException

private val version = GameVersion.WORDLE

fun main(args: Array<String>) {
    if (args.size % 2 != 0) throw IllegalArgumentException()

    val guesses = args.toList().chunked(2).fold(GuessList()) { guessList, (word, output) ->
        guessList.addGuess(word, output)
    }

    val result = Wordler(version).getNextGuess(guesses)

    println("Best next guess is $result")
}
