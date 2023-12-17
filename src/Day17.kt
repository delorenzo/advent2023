import java.io.File
import java.lang.Math.abs
import java.util.*
import kotlin.system.measureTimeMillis

var DESTINATION_X = 0
var DESTINATION_Y = 0
//1040 is too low
//1050? no
//1066 is too high?
fun main() {
    val lines = File("src/input/day17-2.txt").readLines().map { it.trim() }
    val map = lines.map { line -> line.map { it.toString().toInt() } }

    val start = 0 to 0
    val destination = map[0].size -1 to map.size-1
    DESTINATION_X = map[0].size-1
    DESTINATION_Y = map.size-1

    val next = PriorityQueue<LavaPoint>()
    next.add(LavaPoint(0, 0, 0, 0, Direction.DOWN))
    next.add(LavaPoint(0, 0, 0, 0, Direction.RIGHT))
    val closedSet = mutableMapOf<MemoLavaPoint, Boolean>().withDefault { false }
    val gScore = mutableMapOf<MemoLavaPoint, Int>().withDefault { 0 }

    var min = Int.MAX_VALUE
    val measurement = measureTimeMillis {
        while (!next.isEmpty()) {
            val current = next.poll()
            val time = measureTimeMillis {
                if (current.x == destination.first && current.y == destination.second) {
                    min = minOf(current.heatLoss, min)
                    next.clear()
                } else {
                    val neighbors = current.neighbors(map)
                    neighbors.forEach { neighbor ->
                        if (closedSet[neighbor.memo] != true) {
                            val newG = neighbor.heatLoss
                            val neighborG = gScore.getValue(neighbor.memo)
                            if (newG < neighborG || neighborG == 0) {
                                gScore[neighbor.memo] = newG
                                next.add(neighbor)
                            }
                        }

                    }
                }
                closedSet[current.memo] = true
            }
//            if (time > 5) {
//                println("Time for node :  $time")
//            }
        }
    }
    println("Took $measurement")
    println("Min is $min")
    var min2 = Int.MAX_VALUE

    next.clear()
    next.add(LavaPoint(0, 0, 0, 0, Direction.DOWN))
    next.add(LavaPoint(0, 0, 0, 0, Direction.RIGHT))
    closedSet.clear()
    gScore.clear()
    // 1066 too high
    val measurement2 = measureTimeMillis {
        while (!next.isEmpty()) {
            val current = next.poll()
            val time = measureTimeMillis {
                if (current.x == destination.first && current.y == destination.second && current.stepsInDirection >= 4) {
                    min2 = minOf(current.heatLoss, min2)
                    //next.clear()
                } else {
                    val neighbors = current.ultraCrucibleNeighbors(map)
                    neighbors.forEach { neighbor ->
                        if (closedSet[neighbor.memo] != true) {
                            val newG = neighbor.heatLoss
                            val neighborG = gScore.getValue(neighbor.memo)
                            if (newG < neighborG || neighborG == 0) {
                                gScore[neighbor.memo] = newG
                                next.add(neighbor)
                            }
                        }

                    }
                }
                closedSet[current.memo] = true
            }
//            if (time > 5) {
//                println("Time for node :  $time")
//            }
        }
    }
    println("Took $measurement2")
    println("Part 2 min is $min2")
}

data class MemoLavaPoint(val x: Int, val y: Int, val stepsInDirection: Int, val direction: Direction)

data class LavaPoint(val x: Int, val y: Int, var heatLoss: Int, val stepsInDirection: Int, val direction: Direction) : Comparable<LavaPoint> {
    val memo: MemoLavaPoint by lazy {
        MemoLavaPoint(x, y, stepsInDirection, direction)
    }
    val manhattanDistance: Int by lazy {
        kotlin.math.abs(this.x - DESTINATION_X) + kotlin.math.abs(this.y - DESTINATION_Y)
    }

    fun combinedCost() : Int {
        return heatLoss + manhattanDistance
    }

    override fun compareTo(other: LavaPoint): Int {
        return this.combinedCost().compareTo(other.combinedCost())
    }

    fun ultraCrucibleNeighbors(map: List<List<Int>>): List<LavaPoint> {
        val list = mutableListOf<LavaPoint>()
        when (direction) {
            Direction.UP -> {
                if (stepsInDirection < 10) {
                    list.add(LavaPoint(x, y-1, heatLoss, stepsInDirection+1, direction))
                }
                if (stepsInDirection >= 4) {
                    // left
                    list.add(LavaPoint(x-1, y, heatLoss, 1, Direction.LEFT))
                    // right
                    list.add(LavaPoint(x+1, y, heatLoss, 1, Direction.RIGHT))
                }
            }
            Direction.RIGHT -> {
                if (stepsInDirection < 10) {
                    list.add(LavaPoint(x+1, y, heatLoss, stepsInDirection+1, direction))
                }
                if (stepsInDirection >= 4) {
                    // UP
                    list.add(LavaPoint(x, y - 1, heatLoss, 1, Direction.UP))
                    // DOWN
                    list.add(LavaPoint(x, y + 1, heatLoss, 1, Direction.DOWN))
                }
            }
            Direction.DOWN -> {
                if (stepsInDirection < 10) {
                    list.add(LavaPoint(x, y+1, heatLoss, stepsInDirection+1, direction))
                }
                if (stepsInDirection >= 4) {
                    // right
                    list.add(LavaPoint(x + 1, y, heatLoss, 1, Direction.RIGHT))
                    // left
                    list.add(LavaPoint(x - 1, y, heatLoss, 1, Direction.LEFT))
                }

            }
            Direction.LEFT -> {
                if (stepsInDirection < 10) {
                    list.add(LavaPoint(x-1, y, heatLoss, stepsInDirection+1, direction))
                }
                if (stepsInDirection >= 4) {
                    // UP
                    list.add(LavaPoint(x, y - 1, heatLoss, 1, Direction.UP))
                    // DOWN
                    list.add(LavaPoint(x, y + 1, heatLoss, 1, Direction.DOWN))
                }
            }
        }
        val filteredList = list.filter { it.x >= 0 && it.y >= 0 && it.x < map[0].size && it.y < map.size }
        filteredList.map { it.heatLoss = it.heatLoss + map[it.y][it.x] }
        return filteredList
    }

    fun neighbors(map: List<List<Int>>): List<LavaPoint> {
        val list = mutableListOf<LavaPoint>()
        when (direction) {
            Direction.UP -> {
                if (stepsInDirection < 3) {
                    list.add(LavaPoint(x, y-1, heatLoss, stepsInDirection+1, direction))
                }
                // left
                list.add(LavaPoint(x-1, y, heatLoss, 1, Direction.LEFT))
                // right
                list.add(LavaPoint(x+1, y, heatLoss, 1, Direction.RIGHT))
            }
            Direction.RIGHT -> {
                if (stepsInDirection < 3) {
                    list.add(LavaPoint(x+1, y, heatLoss, stepsInDirection+1, direction))
                }
                // UP
                list.add(LavaPoint(x, y-1, heatLoss, 1, Direction.UP))
                // DOWN
                list.add(LavaPoint(x, y+1, heatLoss, 1, Direction.DOWN))
            }
            Direction.DOWN -> {
                if (stepsInDirection < 3) {
                    list.add(LavaPoint(x, y+1, heatLoss, stepsInDirection+1, direction))
                }
                // right
                list.add(LavaPoint(x+1, y, heatLoss, 1, Direction.RIGHT))
                // left
                list.add(LavaPoint(x-1, y, heatLoss, 1, Direction.LEFT))

            }
            Direction.LEFT -> {
                if (stepsInDirection < 3) {
                    list.add(LavaPoint(x-1, y, heatLoss, stepsInDirection+1, direction))
                }
                // UP
                list.add(LavaPoint(x, y-1, heatLoss, 1, Direction.UP))
                // DOWN
                list.add(LavaPoint(x, y+1, heatLoss, 1, Direction.DOWN))
            }
        }
        val filteredList = list.filter { it.x >= 0 && it.y >= 0 && it.x < map[0].size && it.y < map.size }
        filteredList.map { it.heatLoss = it.heatLoss + map[it.y][it.x] }
        return filteredList
    }
}