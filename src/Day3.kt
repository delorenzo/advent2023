import java.io.File

val num = Regex("[0-9]+")
val symbol = Regex("[*=&%/@\\-+\$#!?()^+]")
fun main() {
    val symbolMap = mutableMapOf<Pair<Int, Int>, String>()
    val lines = File("src/input/day3-2.txt").readLines().map {
        it.trim()
    }
    var possibleNumbers = mutableListOf<Number>()
    lines.forEachIndexed { y, list ->
        num.findAll(list).forEach {
            possibleNumbers.add(Number(it.value.toInt(), it.range.first to y))
        }
        symbol.findAll(list).forEach {
            symbolMap[it.range.first to y] = it.value
        }
    }
    val partNumbers = possibleNumbers.filter { nums -> nums.border.any { point -> symbolMap.containsKey(point) }}
    println(partNumbers.map { it.value }.reduceRight { l, acc -> l + acc })

    val gearRatios = mutableListOf<Int>()
    symbolMap.keys.forEach { symbol ->
        val adjacentParts = partNumbers.filter { nums -> nums.border.any{ point -> point == symbol }}
        if (adjacentParts.size == 2) {
            gearRatios.add(adjacentParts.map { it.value }.reduceRight { i, acc -> i * acc })
        }
    }
    val partTwo = gearRatios.sum()
    println(partTwo)

}

val set: MutableSet<String> = mutableSetOf()

data class Number(
    val value: Int,
    val start: Pair<Int, Int>) {
    val length = value.toString().length
    val border: List<Pair<Int, Int>> by lazy {
        val border = mutableListOf<Pair<Int, Int>>()
        IntRange(start.first-1, start.first+length).map { x->
            border.add(x to start.second-1)
            border.add(x to start.second+1)
        }
        border.add(start.first-1 to start.second)
        border.add(start.first+length to start.second)
        border
    }
}