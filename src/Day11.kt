import java.io.File
import kotlin.math.abs
import kotlin.math.exp

fun main() {
    val lines = File("src/input/day11-2.txt").readLines().map { it.trim() }
    val map = lines.map { line -> line.map { it.toString() }.toMutableList() }.toMutableList()
    //val expandedMap = expandMap(map)
    val expandedMap = ExpandedMap(map, expansion = 1000000)
//    for (y in 0 until expandedMap.size) {
//        for (x in 0 until expandedMap.first().size) {
//            print(expandedMap[y][x])
//        }
//        println()
//    }
//    val galaxy = mutableListOf<Pair<Int, Int>>()
//    for (y in expandedMap.indices) {
//        for (x in expandedMap.first().indices) {
//            if (expandedMap[y][x] == "#") { galaxy.add(y to x) }
//        }
//    }
    val pairs = mutableListOf<Pair<Int,Int>>()
    val shortestPaths = mutableListOf<Pair<Long, Pair<Int, Int>>>()
    val used = mutableMapOf<Pair<Int, Int>, Boolean>()
    for (a in expandedMap.galaxies.indices) {
        for (b in 1 until expandedMap.galaxies.size) {
            if (a == b) continue
            else if (used.getOrDefault(b to a, false)) {
                continue
            } else {
                pairs.add(a to b)
                used[a to b] = true
            }
        }
    }
    pairs.forEach {
        shortestPaths.add(expandedMap.shortestPath(it))
    }
    val result = shortestPaths.map { it.first }.sum()
    println(result)
}

data class GalaxyNode(val point: Pair<Int, Int>, val distance: Int)

fun shortestPath(pair: Pair<Int, Int>, galaxies: List<Pair<Int, Int>>, expandedMap: List<List<String>>): Pair<Long, Pair<Int, Int>> {
    val a = galaxies[pair.first]
    val b = galaxies[pair.second]
    val visited = mutableMapOf<GalaxyNode, Boolean>()
    val queue = ArrayDeque<GalaxyNode>()
    queue.add(GalaxyNode(a, 0))
    while (!queue.isEmpty()) {
        val current = queue.removeFirst()
        if (visited.getOrDefault(current, false)) continue
        visited[current] = true
        if (current.point == b) {
            return current.distance.toLong() to pair
        }
        val neighbors = neighbors(current.point, expandedMap)
        neighbors.map { queue.add(GalaxyNode(it, current.distance + 1)) }
    }
    throw Exception()
}

fun neighbors(current: Pair<Int, Int>, expandedMap: List<List<String>>) : List<Pair<Int, Int>> {
    return listOf(
        -1 to 0,
        1 to 0,
        0 to 1,
        0 to -1
    ).map {
        it.first + current.first to
        it.second + current.second
    }.filterNot {
        it.first < 0 || it.second < 0 || it.first > expandedMap.size || it.second > expandedMap.first().size
    }
}

fun expandMap(map: MutableList<MutableList<String>>) : List<List<String>> {
    val expandedMap = map.toMutableList()
    val noGalaxyRows = map.mapIndexed { index, list -> index to list }.filter { !it.second.contains("#") }.map { it.first }
    val noGalaxyColumns = mutableListOf<Int>()
    for (x in 0 until map.first().size) {
        var containsStar = false
        for (y in 0 until map.size) {
            if (map[y][x] == "#") {
                containsStar = true
                break
            }
        }
        if (!containsStar) {
            noGalaxyColumns.add(x)
        }
    }
    noGalaxyRows.mapIndexed { index, it ->
        expandedMap.add(it+index, expandedMap[it+index].toMutableList())
    }
    noGalaxyColumns.mapIndexed { it, index ->
        for (y in 0 until expandedMap.size) {
            expandedMap[y].add(it+index, ".")
        }
    }
    return expandedMap
}

data class ExpandedMap(val map: MutableList<MutableList<String>>, val expansion: Int = 2) {
    var noGalaxyRows: List<Int>
    var noGalaxyColumns: MutableList<Int>
    var galaxies: MutableList<Pair<Int, Int>>
    init {
        noGalaxyRows = map.mapIndexed { index, list -> index to list }.filter { !it.second.contains("#") }.map { it.first }
        noGalaxyColumns = mutableListOf()
        galaxies = mutableListOf()
        for (x in map.first().indices) {
            var containsStar = false
            for (y in map.indices) {
                if (map[y][x] == "#") {
                    containsStar = true
                    break
                }
            }
            if (!containsStar) {
                noGalaxyColumns.add(x)
            }
        }
        for (y in map.indices) {
            for (x in map.first().indices) {
                if (map[y][x] == "#") { galaxies.add(y to x) }
            }
        }
        galaxies = galaxies.map { galaxy ->
            val addToY = noGalaxyRows.count { it < galaxy.first } * (expansion-1)
            val addToX = noGalaxyColumns.count{it < galaxy.second} * (expansion-1)
            (galaxy.first + addToY) to (galaxy.second + addToX)
        }.toMutableList()
    }
    fun shortestPath(pair : Pair<Int, Int>) : Pair<Long, Pair<Int, Int>> {
        val a = galaxies[pair.first]
        val b = galaxies[pair.second]
        return (abs(a.first - b.first) + abs(a.second-b.second)).toLong() to pair
//        val visited = mutableMapOf<GalaxyNode, Boolean>()
//        val queue = ArrayDeque<GalaxyNode>()
//        queue.add(GalaxyNode(a, 0))
//        while (!queue.isEmpty()) {
//            val current = queue.removeFirst()
//            if (visited.getOrDefault(current, false)) continue
//            visited[current] = true
//            if (current.point == b) {
//                return current.distance.toLong() to pair
//            }
//            //down
//            if (b.first < a.first) {
//
//            }
//            val neighbors = neighbors(current.point)
//            neighbors.map { queue.add(GalaxyNode(it, current.distance + 1)) }
//        }
//        throw Exception()
    }
    fun neighbors(current: Pair<Int, Int>) : List<Pair<Int, Int>> {
        return listOf(
            -1 to 0,
            1 to 0,
            0 to 1,
            0 to -1
        ).map {
            it.first + current.first to
                    it.second + current.second
        }.filterNot {
            it.first < 0 || it.second < 0 || it.first > size() || it.second > rowSize()
        }
    }
    fun size(): Int {
        return map.size + (noGalaxyColumns.size * (expansion-1))
    }
    fun rowSize() : Int {
        return map.first().size + (noGalaxyRows.size * (expansion-1))
    }
//    fun get(y: Int, x: Int) {
//        var remainingY = y
//        var remainingx = x
//        for (y in map.indices) {
//            for (x in map.first().indices) {
//
//                if (noGalaxyRows.contains(y)) {
//                    remainingY--
//                }
//            }
//        }
//    }
}