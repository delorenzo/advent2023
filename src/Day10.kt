import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.abs

/**
 *
| is a vertical pipe connecting north and south.
- is a horizontal pipe connecting east and west.
L is a 90-degree bend connecting north and east.
J is a 90-degree bend connecting north and west.
7 is a 90-degree bend connecting south and west.
F is a 90-degree bend connecting south and east.
. is ground; there is no pipe in this tile.
S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
 */
val validSouth = listOf("|", "7", "F", "S")
val validWest = listOf("-", "7", "J", "S")
val validEast = listOf("-", "L", "F", "S")
val validNorth = listOf("|", "L", "J", "S")
fun main() {
    val lines = File("src/input/day10-2.txt").readLines().map{it.trim()}
    val map = lines.map { line -> line.map { it.toString() } }
    val startY = map.indexOfFirst { it.contains("S") }
    val startX = map[startY].indexOfFirst { it == "S" }
    println(startY to startX)
    val queue = ArrayDeque<AnimalMapPoint>()
    queue.add(AnimalMapPoint("S", startY to startX, 0, mutableListOf(startY to startX)))
    var maxDistance = 0
    val visited = mutableMapOf<Pair<Int, Int>, Boolean>()
    val points = mutableListOf<Pair<Int, Int>>()
    var visitedSave = mutableListOf<Pair<Int,Int>>()
    while (!queue.isEmpty()) {
        val current = queue.removeFirst()
        if (visited.getOrDefault(current.position, false)) { continue }
        maxDistance = maxOf(maxDistance, current.distance)
        if (current.visited.size >= visitedSave.size) {
            visitedSave = current.visited
        }

        when (current.symbol) {
            "S" -> {
                checkTop(map, queue, current)
                checkLeft(map, queue, current)
                checkRight(map, queue, current)
                checkBottom(map, queue, current)
            }
            "-" -> {
                checkLeft(map, queue, current)
                checkRight(map, queue, current)
            }
            "|" -> {
                checkTop(map, queue, current)
                checkBottom(map, queue, current)
            }
            "L" -> {
                checkTop(map, queue, current)
                checkRight(map, queue, current)
            }
            "J" -> {
                checkTop(map, queue, current)
                checkLeft(map, queue, current)
            }
            "7" -> {
                checkBottom(map, queue, current)
                checkLeft(map, queue, current)
            }
            "F" -> {
                checkBottom(map, queue, current)
                checkRight(map, queue, current)
            }
            else -> throw Exception()
        }
        visited[current.position] = true
        points.add(current.position)
    }
    println("Part one:  $maxDistance")

    val pq = PriorityQueue<AnimalMapPoint>(compareByDescending<AnimalMapPoint> { it.visited.size })
    pq.add(AnimalMapPoint("S", startY to startX, 0, mutableListOf(startY to startX)))
    visited.clear()
    while (!pq.isEmpty()) {
        val current = pq.poll()
        if (current.symbol == "S" && current.visited.size > 1) {
            current.visited.add(current.position)
            continue
        }
        if (visited.getOrDefault(current.position, false)) { continue }
        maxDistance = maxOf(maxDistance, current.distance)
        if (current.visited.size >= visitedSave.size) {
            visitedSave = current.visited
        }

        when (current.symbol) {
            "S" -> {
                checkTop(map, pq, current)
                checkLeft(map, pq, current)
                checkRight(map, pq, current)
                checkBottom(map, pq, current)
            }
            "-" -> {
                checkLeft(map, pq, current)
                checkRight(map, pq, current)
            }
            "|" -> {
                checkTop(map, pq, current)
                checkBottom(map, pq, current)
            }
            "L" -> {
                checkTop(map, pq, current)
                checkRight(map, pq, current)
            }
            "J" -> {
                checkTop(map, pq, current)
                checkLeft(map, pq, current)
            }
            "7" -> {
                checkBottom(map, pq, current)
                checkLeft(map, pq, current)
            }
            "F" -> {
                checkBottom(map, pq, current)
                checkRight(map, pq, current)
            }
            else -> throw Exception()
        }
        visited[current.position] = true
        //visited[current] = true
        points.add(current.position)
    }
    println(maxDistance)

    val area = area(visitedSave)
    val picksFormula = picksFormula(area, visitedSave.size, 0)

    println(area)
    println("Part two:  $picksFormula")
}

fun area(points: List<Pair<Int, Int>>) : Int {
    var result = 0
    for (i in points.indices) {
        var xFirst = i-1
        var xLast = i+1
        if (xFirst < 0) { xFirst = points.size-1 }
        if (xLast >= points.size) { xLast = 0 }
        result += points[i].first * (points[xFirst].second - points[xLast].second)
    }
    return abs(result / 2)
}

fun picksFormula(a: Int, b: Int, h: Int) : Int {
    return a - b.div(2) - h + 1
}

fun checkBottom(map: List<List<String>>, queue: ArrayDeque<AnimalMapPoint>, current: AnimalMapPoint) {
    val bottom = current.position.first+1 to current.position.second
    if (bottom.first < map.size) {
        if (map[bottom.first][bottom.second] in validNorth) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(bottom)
            queue.add(AnimalMapPoint(map[bottom.first][bottom.second], bottom, current.distance+1, newVisited))
        }
    }
}
fun checkRight(map: List<List<String>>, queue: ArrayDeque<AnimalMapPoint>, current: AnimalMapPoint) {
    val right = current.position.first to current.position.second +1
    if (right.second < map[0].size) {
        if (map[right.first][right.second] in validWest) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(right)
            queue.add(AnimalMapPoint(map[right.first][right.second], right, current.distance+1, newVisited))
        }
    }
}

fun checkLeft(map: List<List<String>>, queue: ArrayDeque<AnimalMapPoint>, current: AnimalMapPoint) {
    val left = current.position.first to current.position.second -1
    if (left.second > -1) {
        if (map[left.first][left.second] in validEast) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(left)
            queue.add(AnimalMapPoint(map[left.first][left.second], left, current.distance+1, newVisited))
        }
    }
}

fun checkTop(map: List<List<String>>, queue: ArrayDeque<AnimalMapPoint>, current: AnimalMapPoint) {
    val top = current.position.first -1 to current.position.second
    if (top.first > -1) {
        if (map[top.first][top.second] in validSouth) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(top)
            queue.add(AnimalMapPoint(map[top.first][top.second], top, current.distance + 1, newVisited))
        }
    }
}

fun checkBottom(map: List<List<String>>, queue: PriorityQueue<AnimalMapPoint>, current: AnimalMapPoint) {
    val bottom = current.position.first+1 to current.position.second
    if (bottom.first < map.size) {
        if (map[bottom.first][bottom.second] in validNorth) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(bottom)
            queue.add(AnimalMapPoint(map[bottom.first][bottom.second], bottom, current.distance+1, newVisited))
        }
    }
}
fun checkRight(map: List<List<String>>, queue: PriorityQueue<AnimalMapPoint>, current: AnimalMapPoint) {
    val right = current.position.first to current.position.second +1
    if (right.second < map[0].size) {
        if (map[right.first][right.second] in validWest) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(right)
            queue.add(AnimalMapPoint(map[right.first][right.second], right, current.distance+1, newVisited))
        }
    }
}

fun checkLeft(map: List<List<String>>, queue: PriorityQueue<AnimalMapPoint>, current: AnimalMapPoint) {
    val left = current.position.first to current.position.second -1
    if (left.second > -1) {
        if (map[left.first][left.second] in validEast) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(left)
            queue.add(AnimalMapPoint(map[left.first][left.second], left, current.distance+1, newVisited))
        }
    }
}

fun checkTop(map: List<List<String>>, queue: PriorityQueue<AnimalMapPoint>, current: AnimalMapPoint) {
    val top = current.position.first -1 to current.position.second
    if (top.first > -1) {
        if (map[top.first][top.second] in validSouth) {
            val newVisited = current.visited.toMutableList()
            newVisited.add(top)
            queue.add(AnimalMapPoint(map[top.first][top.second], top, current.distance + 1, newVisited))
        }
    }
}

class AnimalMapPoint(val symbol: String, val position: Pair<Int, Int>, val distance: Int, val visited: MutableList<Pair<Int, Int>> = mutableListOf())