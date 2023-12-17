import java.io.File

fun main() {
    val lines = File("src/input/day16-2.txt").readLines().map { it.trim() }
    val input = lines.map { line -> line.map { it }.toList() }

    val start = BeamPoint(0, 0, Direction.RIGHT)
    val count = searchBeams(start, input)
    println("Part one is $count")

    // Part two
    val inputs = mutableListOf<BeamPoint>()
    for(i in input[0].indices) {
        inputs.add(BeamPoint(i, 0, Direction.DOWN))
        inputs.add(BeamPoint(i, input.size, Direction.UP))
    }
    for (i in input.indices) {
        inputs.add(BeamPoint(i, 0, Direction.RIGHT))
        inputs.add(BeamPoint(i, input[0].size, Direction.LEFT))
    }
    val partTwo = inputs.map { searchBeams(it, input)}.max()
    println("Part two is $partTwo")
}

fun searchBeams(start: BeamPoint, input: List<List<Char>>) : Int {
    val energized = Array<Array<Int>>(input.size) { Array<Int>(input[0].size) { 0 } }
    val beams = ArrayDeque<BeamPoint>()
    beams.add(start)
    val visited = mutableMapOf<BeamPoint, Boolean>()
    while (!beams.isEmpty()) {
        val current = beams.removeFirst()
        if (current.valid(input.size, input[0].size) && !visited.getOrDefault(current, false)) {
            visited[current] = true
            energized[current.y][current.x] = 1
            beams.addAll(current.encounters(input[current.y][current.x]))
        }
    }
    val count = energized.map { it.count { it == 1 } }.sum()
    return count
}

enum class Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT
}
data class BeamPoint(var x : Int, var y: Int, var dir: Direction) {
    fun valid(maxY: Int, maxX: Int) : Boolean {
        if (x < 0 || x >= maxX) return false
        if (y < 0 || y >= maxY) return false
        return true
    }
    fun sameDirection() : BeamPoint {
        return when (dir) {
            Direction.RIGHT -> BeamPoint(x + 1, y, dir)
            Direction.DOWN -> BeamPoint(x, y+1, dir)
            Direction.LEFT -> BeamPoint(x-1, y, dir)
            Direction.UP -> BeamPoint(x, y-1, dir)
        }
    }
    fun newDirection(dir: Direction, newX: Int = x, newY: Int = y) : BeamPoint {
        return BeamPoint(newX, newY, dir).sameDirection()
    }
    fun encounters(char: Char) : List<BeamPoint> {
        return when (char) {
            '.' -> {
                listOf(sameDirection())
            }
            '/' -> {
                return when (dir) {
                    Direction.RIGHT -> {
                        listOf(newDirection(Direction.UP))
                    }
                    Direction.LEFT -> {
                        listOf(newDirection(Direction.DOWN))
                    }
                    Direction.UP -> {
                        listOf(newDirection(Direction.RIGHT))
                    }
                    Direction.DOWN -> {
                        listOf(newDirection(Direction.LEFT))
                    }
                }
            }
            '\\' -> {
                return when (dir) {
                    Direction.RIGHT -> {
                        listOf(newDirection(Direction.DOWN))
                    }
                    Direction.LEFT -> {
                        listOf(newDirection(Direction.UP))
                    }
                    Direction.UP -> {
                        listOf(newDirection(Direction.LEFT))
                    }
                    Direction.DOWN -> {
                        listOf(newDirection(Direction.RIGHT))
                    }
                }
            }
            '|' -> {
                if (dir == Direction.UP || dir == Direction.DOWN) { return listOf(sameDirection())}
                else { listOf(newDirection(Direction.UP), newDirection(Direction.DOWN))}
            }
            '-' -> {
                if (dir == Direction.LEFT || dir == Direction.RIGHT) { return listOf(sameDirection())}
                else { listOf(newDirection(Direction.LEFT), newDirection(Direction.RIGHT)) }
            }
            else -> {
                throw Exception()
            }
        }
    }
}