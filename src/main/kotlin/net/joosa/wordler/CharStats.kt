package net.joosa.wordler

data class CharStats(
    val correctIndexes: List<Int> = emptyList(),
    val incorrectIndexes: List<Int> = emptyList(),
    val minCount: Int = 0,
    val maxCount: Int = 5
){
    fun merge(other: CharStats) = CharStats(
        correctIndexes = (this.correctIndexes + other.correctIndexes).distinct(),
        incorrectIndexes = (this.incorrectIndexes + other.incorrectIndexes).distinct(),
        minCount = maxOf(this.minCount, other.minCount),
        maxCount = minOf(this.maxCount, other.maxCount)
    )
}
