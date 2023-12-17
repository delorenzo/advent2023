import java.io.File
import kotlin.math.pow

@ExperimentalStdlibApi
fun main() {
    val lines = File("src/input/day4-2.txt").readLines().map {
        it.trim()
    }
    // Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
    val regex = Regex("[0-9]+")
    val cards : List<ScratchCard> = lines.mapIndexed {index, line ->
        val parts = line.split("|")
        val winningNumbers = regex.findAll(parts.first().split(":").last()).map { it.value.toInt() }.toList()
        val numbersYouHave = regex.findAll(parts.last()).map { it.value.toInt() }.toList()
        ScratchCard(index + 1, winningNumbers, numbersYouHave)
    }
    val partOne = cards.sumByDouble { it.value() }
    println(partOne)

    // Part two
    val cardCounts = mutableMapOf<Int, Int>()
    cards.map { cardCounts[it.id] = 1 }
    cards.forEachIndexed { index, scratchCard ->
        val multiplier = cardCounts[index+1]!!
        scratchCard.scratchCards.map {
            cardCounts[it] = cardCounts[it]!! + multiplier
        }
    }
    val partTwo = cardCounts.values.sum()
    println(partTwo)
}

data class ScratchCard(val id: Int, val winningNumbers: List<Int>, val numbersYouHave: List<Int>) {
    fun value() : Double {
        val winningNumbers = winningNumbers.filter { it in numbersYouHave }
        if (winningNumbers.isEmpty()) return 0.0
        return 2.0.pow(winningNumbers.size.toDouble()-1)
    }
    val scratchCards: List<Int> by lazy {
        val winningNumbers = winningNumbers.filter { it in numbersYouHave }
        if (winningNumbers.isEmpty()) emptyList()
        else IntRange(id+1, id+winningNumbers.size).toList()
    }
}