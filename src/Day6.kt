import java.io.File

fun main() {
    val lines = File("src/input/day6-2.txt").readLines().map {
        it.trim()
    }
    val numRegex = Regex("[0-9]+")
    val times = numRegex.findAll(lines.first()).map { it.value.toLong() }.toList()
    val distances = numRegex.findAll(lines[1]).map { it.value.toLong() }.toList()

    val memo = mutableMapOf<Long, Long>()

    val partOne = times.mapIndexed { index, time ->
        val distanceToBeat = distances[index]
        var ways = 0
        for (i in 1 until time-1) {
            val speed = i
            val distance = speed * (time-i)
            if (distance > distanceToBeat) { ways ++ }
        }
        ways
    }.filter { it -> it > 1 }.reduceRight { i, acc -> acc * i }
    println(partOne)

    // part two
    val time = times.map { it.toString() }.reduceRight { s, acc -> s + acc }.toLong()
    val distanceToBeat = distances.map { it.toString() }.reduceRight { s, acc -> s + acc }.toLong()
    var ways = 0
    for (i in 1 until time) {
        val speed = i
        val distance =  speed * (time-i)
        if (distance > distanceToBeat) { ways ++ }
    }
    println(ways)
}