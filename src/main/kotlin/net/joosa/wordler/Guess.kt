package net.joosa.wordler

import java.lang.IllegalArgumentException

data class Guess(
    val word: String,
    val output: List<CharState>
) {
    init {
        if(word.length != 5) throw IllegalArgumentException()
        if(output.size != 5) throw IllegalArgumentException()
    }

    constructor(word: String, output: String) : this(
        word,
        output.map { c -> when(c) {
            'g' -> CharState.CORRECT
            'y' -> CharState.PRESENT
            'b' -> CharState.ABSENT
            else -> throw IllegalArgumentException()
        } }
    )
}
