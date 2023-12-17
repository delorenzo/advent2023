import java.io.File
import java.math.BigInteger

// 820847869

fun main() {
    val lines = File("src/input/day9-2.txt").readLines().map {
        it.trim()
    }
    val numRegex = Regex("\\-?[0-9]+")
    val histories = lines.map { numRegex.findAll(it).map { num -> num.value.toLong() }.toList() }
    val sequences: MutableList<List<List<Long>>> = mutableListOf()

    histories.forEachIndexed { index, history ->
        val sequence = mutableListOf<MutableList<Long>>()
        sequence.add(history.toMutableList())
        sequence.add(computeDifferences(history.toMutableList()))
        while (!sequence.last().all { num -> num == 0L }) {
            sequence.add(computeDifferences(sequence.last()))
        }
        sequence.last().add(0L)
        for (j in sequence.size - 2 downTo 0) {
            // end
            val newEnd = sequence[j + 1].last() + sequence[j].last()
            sequence[j].add(newEnd)

            // start
            val newStart = sequence[j].first() - sequence[j+1].first()
            sequence[j].add(0, newStart)
        }
        sequences.add(sequence)

    }
    //println(sequences)
    val partOne = sequences.mapIndexed { index, list -> list.first().last().toLong() }.sum()
    println("Part one is $partOne")

    val partTwo = sequences.map { it.first().first().toLong() }.sum()
    println("Part two is $partTwo")
}

fun computeDifferences(list: MutableList<Long>) : MutableList<Long> {
    val differences = mutableListOf<Long>()
    var i = 0
    while (i + 1 < list.size) {
        differences.add(list[i+1] - list[i])
        i++
    }
    return differences
}