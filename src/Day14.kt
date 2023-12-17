import java.io.File
import kotlin.system.measureTimeMillis

data class Day14Point(var x: Int, var y: Int, val item: Char)
fun main() {
    val lines = File("src/input/day14-2.txt").readLines().map { it.trim() }
    val input = lines.map { line -> line.map { it.toString() }.toMutableList() }.toMutableList()
    val input2 = lines.map { line -> line.map { it }.toMutableList() }.toMutableList()
    var tiltCache = mutableMapOf<Pair<MutableList<MutableList<String>>, CardinalDirection>, MutableList<MutableList<String>>>()
    var tiltCycle = mutableMapOf<MutableList<MutableList<String>>, MutableList<MutableList<String>>>()
    val tiltColumn = mutableMapOf<Pair<List<String>,CardinalDirection>, MutableList<String>>()

    val test = input2.mapIndexed { y, mutableList ->
        mutableList.mapIndexed { x, s ->
            Day14Point(x, y, s)
        }
    }.flatten()
    //val movableRocks = test.map { list -> list.filter { it.item == 'O' } }
    val movableRocksX = test.filter { it.item == 'O' }.groupBy { it.x }
    val movableRocksY = test.filter { it.item == 'O' }.groupBy { it.y }
    val unmovableRocksX = test.filter { it.item == '#' }.groupBy { it.x }
    val unmovableRocksY = test.filter { it.item == '#' }.groupBy { it.y }
    // moving east
    // movableRocksY[0] = row
    // update rocks in row, remove from column?
    //val unmovableRocks = test.map { list -> list.filter { it.item == '#' }}
    val maxY = input2.size
    val maxX = input2[0].size


    val tiltedInput = tiltNorthSouth(input, CardinalDirection.NORTH, tiltCache, tiltColumn)

//    for (i in input.indices) {
//        for (k in input.first().indices) {
//            print(input[i][k])
//        }
//        println()
//    }
//    println()
    for (i in tiltedInput.indices) {
        for (k in tiltedInput.first().indices) {
            print(tiltedInput[i][k])
        }
        println()
    }

    var totalLoad = 0
    var loadFactor = 1
    for (i in tiltedInput.size-1 downTo 0) {
        val load = tiltedInput[i].count { it == "O" } * loadFactor
        totalLoad += load
        loadFactor++
    }
    println(totalLoad)


    // Part two
    var partTwo: MutableList<MutableList<String>> = input

    var percent = 0
    //for (i in 0 until 1000000000) {
    for (i in 0 until 1000000000) {
        if (i % 10000000 == 0) { println(percent); percent++ }
        if (tiltCycle.containsKey(partTwo)) { partTwo = tiltCycle[partTwo]!! }
        else {
            val time = measureTimeMillis {
                val save = partTwo
                partTwo = tiltNorthSouth(partTwo, CardinalDirection.NORTH, tiltCache, tiltColumn)
                partTwo = tiltEastWest(partTwo, CardinalDirection.WEST, tiltCache, tiltColumn)
                partTwo = tiltNorthSouth(partTwo, CardinalDirection.SOUTH, tiltCache, tiltColumn)
                partTwo = tiltEastWest(partTwo, CardinalDirection.EAST, tiltCache, tiltColumn)
                tiltCycle[save] = partTwo
            }
//            if (time > 0) {
//                println("Time in cycle is $time")
//            }
        }
        // 100178 is too low
        //100311
        //100299
        //100251
        /*
            Load is 100178 - 1276000
            Load is 100311 - 1277000
            Load is 100177 - 1278000
            Load is 100299 - 1279000
            Load is 100197 - 1280000
            Load is 100253 - 1281000
            Load is 100251 - 1282000
            Load is 100178 - 1283000
            Load is 100301 - 1284000
            Load is 100145 - 1285000
            Load is 100294 - 1286000
            Load is 100146 - 1287000
            Load is 100310 - 1288000
         */
        if (i% 1000 == 0) {
            val load = totalLoad(partTwo)
//            if (load == 64) {
//                println(load)
//            }
            println("Load is $load - $i")
//            print(partTwo)
//            println()
//            println()
        }
    }

    var partTwoTotalLoad = 0
    var partTwoLoadFactor = 1
    for (i in partTwo.size-1 downTo 0) {
        val load = partTwo[i].count { it == "O" } * partTwoLoadFactor
        partTwoTotalLoad += load
        partTwoLoadFactor++
    }
    println(partTwoTotalLoad)
}

fun print(partTwo: MutableList<MutableList<String>>) {
    for (i in partTwo.indices) {
        for (k in partTwo.first().indices) {
            print(partTwo[i][k])
        }
        println()
    }
    println()
    println()
}

fun totalLoad(partTwo: MutableList<MutableList<String>>): Int {
    var partTwoTotalLoad = 0
    var partTwoLoadFactor = 1
    for (i in partTwo.size-1 downTo 0) {
        val load = partTwo[i].count { it == "O" } * partTwoLoadFactor
        partTwoTotalLoad += load
        partTwoLoadFactor++
    }
    return partTwoTotalLoad
}

enum class CardinalDirection {
    NORTH,
    EAST,
    SOUTH,
    WEST
}

fun tiltEastWest(input: MutableList<MutableList<String>>, cardinalDirection: CardinalDirection,
                 tiltCache: MutableMap<Pair<MutableList<MutableList<String>>, CardinalDirection>, MutableList<MutableList<String>>>,
tiltColumn: MutableMap<Pair<List<String>,CardinalDirection>, MutableList<String>>): MutableList<MutableList<String>> {
    if (tiltCache.containsKey(input to cardinalDirection)) { return tiltCache[input to cardinalDirection]!! }
    val time = measureTimeMillis {
        val output = input.map { it.toMutableList() }.toMutableList()
        for (i in input.indices) {
            val row = input[i]
            val newRow = tiltList(row, cardinalDirection, tiltColumn)
            output[i] = newRow
        }
        tiltCache[input to cardinalDirection] = output
    }
//    if (time > 0) {
//        println("Time in tiltEastWest is $time")
//    }
    return tiltCache[input to cardinalDirection]!!
}


fun tiltNorthSouth(input: MutableList<MutableList<String>>, cardinalDirection: CardinalDirection, tiltCache: MutableMap<Pair<MutableList<MutableList<String>>,
        CardinalDirection>, MutableList<MutableList<String>>>,
                   tiltColumn: MutableMap<Pair<List<String>,CardinalDirection>, MutableList<String>>): MutableList<MutableList<String>> {
    if (tiltCache.containsKey(input to cardinalDirection)) { return tiltCache[input to cardinalDirection]!! }
    val time = measureTimeMillis {
        val output = input.map { it.toMutableList() }.toMutableList()
        //val output = input
        for (x in input.first().indices) {
            val column = input.map { it.filterIndexed { index, s -> index == x } }.flatten()
            val newColumn = tiltList(column, cardinalDirection, tiltColumn)

            for (y in input.indices) {
                output[y][x] = newColumn[y]
            }
        }
        tiltCache[input to cardinalDirection] = output
    }
//    if (time > 0 ) {
//        println("Time in tiltNorthSouth is $time")
//    }
    return tiltCache[input to cardinalDirection]!!
}

fun tiltList(list: List<String>, direction: CardinalDirection, tiltColumn: MutableMap<Pair<List<String>,CardinalDirection>, MutableList<String>>) : MutableList<String> {
    if (tiltColumn.containsKey(list to direction)) { return tiltColumn[list to direction]!! }
    val time = measureTimeMillis {
        val rocks = list.mapIndexed { index, s -> index to s }.filter { it.second == "#" }.map { it.first }
        if (rocks.isNotEmpty()) {
            var newList = mutableListOf<String>()
            var start = 0
            rocks.forEach {
                val sublist = list.subList(start, it)
                newList.addAll(tiltSublist(sublist, direction, tiltColumn))
                newList.add("#")
                start = it + 1
            }
            if (newList.size < list.size) {
                newList.addAll(tiltSublist(list.subList(start, list.size), direction, tiltColumn))
            }
            tiltColumn[list to direction] = newList
        } else {
            tiltColumn[list to direction] = tiltSublist(list, direction, tiltColumn)
        }
    }
//    if (time > 0 ) {
//        println("Time in tiltList is $time")
//    }
    return tiltColumn[list to direction]!!
}

//fun tiltList(list: List<String>, direction: CardinalDirection, rocks: List<Int>) : List<Int> {
//    val time = measureTimeMillis {
//        val rocks = list.mapIndexed { index, s -> index to s }.filter { it.second == "#" }.map { it.first }
//        if (rocks.isNotEmpty()) {
//            var newList = mutableListOf<String>()
//            var start = 0
//            rocks.forEach {
//                val sublist = list.subList(start, it)
//                newList.addAll(tiltSublist(sublist, direction))
//                newList.add("#")
//                start = it + 1
//            }
//            if (newList.size < list.size) {
//                newList.addAll(tiltSublist(list.subList(start, list.size), direction))
//            }
//        } else {
//        }
//    }
//    if (time > 0 ) {
//        println("Time in tiltList is $time")
//    }
//}

fun tiltSublist(direction: CardinalDirection, rocks: List<Int>, range: IntRange) : List<Int> {
    if (direction == CardinalDirection.SOUTH || direction == CardinalDirection.EAST) {
        return IntRange(0, rocks.size).toList()
    } else {
        return IntRange(rocks.size, range.last).toList()
    }
}

fun tiltSublist(list: List<String>, direction: CardinalDirection, tiltColumn: MutableMap<Pair<List<String>,CardinalDirection>, MutableList<String>>) : MutableList<String> {
    if (tiltColumn.containsKey(list to direction)) { return tiltColumn[list to direction]!! }
    val time = measureTimeMillis {
        if (direction == CardinalDirection.SOUTH || direction == CardinalDirection.EAST) {
            tiltColumn[list to direction] = list.sorted().toMutableList()
        } else {
            tiltColumn[list to direction] = list.sorted().reversed().toMutableList()
        }
    }
//    if (time > 0) {
//        println("Time in tiltSublist is $time")
//    }
    return tiltColumn[list to direction]!!
}