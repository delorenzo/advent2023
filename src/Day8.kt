import java.io.File
import java.lang.Exception

fun main() {
    val lines = File("src/input/day8-2.txt").readLines().map {
        it.trim()
    }
    val instructions = lines.first().map { it.toString() }

    val goal = "ZZZ"
    val maze = mutableMapOf<String, Pair<String, String>>()
    val regex = Regex("([0-9A-Z]+) = \\(([0-9A-Z]+), ([0-9A-Z]+)\\)")
    for (i in 2 until lines.size) {
        val match = regex.matchEntire(lines[i])!!
        maze[match.groupValues[1]] = match.groupValues[2] to match.groupValues[3]
    }

    // part one
    var current = "AAA"
    var steps = 0
    var i = 0
    while (true) {
        val instruction = instructions[i]
        current = when (instruction) {
            "R" -> maze[current]!!.second
            "L" -> maze[current]!!.first
            else -> throw Exception()
        }
        i++
        steps++
        if (current == goal) break
        if (i == instructions.size) { i = 0 }
    }

    println("Part one:  took $steps steps to reach ZZZ")

    val aRegex = Regex("[0-9A-Z]+A")
    val zRegex = Regex("[0-9A-Z]+Z")
    val currentPositions = maze.keys.filter { aRegex.matches(it) }.toMutableList()
    val goals = maze.keys.filter { zRegex.matches(it) }

    // part two
//    var steps = 0
//    var i = 0
//    while (true) {
//        val instruction = instructions[i]
//        when (instruction) {
//            "R" -> {
//                currentPositions.forEachIndexed { index, c ->
//                    currentPositions[index] = maze[c]!!.second
//                }
//            }
//            "L" -> {
//                currentPositions.forEachIndexed { index, c ->
//                    currentPositions[index] = maze[c]!!.first
//                }
//            }
//            else -> throw Exception()
//        }
//    }
//        currentPositions.forEachIndexed { index, c ->
//            currentPositions[index] = when (instruction) {
//                "R" -> maze[c]!!.second
//                "L" -> maze[c]!!.first
//                else -> throw Exception()
//            }
//        }
//        i++
//        steps++
//        if (currentPositions.any { zRegex.matches(it)}) {
//            println("Step $steps $currentPositions")
//        }
//        if (currentPositions.all { zRegex.matches(it) }) break
//        if (i == instructions.size) { i = 0 }
//    }
//    println(steps)
    val nums = listOf(11309L, 19199L, 12361L, 16043L, 13939L, 18673L)
    println("Part two:  ${lcm(nums)}")
}

fun gcd(first: Long, second: Long) : Long {
    var a = first
    var b = second
    while (b != 0L) {
        val temp = b
        b = a % b
        a = temp
    }
    return a
}
//
fun lcm(a: Long, b: Long) : Long {
    return a * b / gcd(a, b)
}

fun lcm(nums: List<Long>) : Long {
    var result = nums.first()
    for (i in nums.indices) {
        result = lcm(result, nums[i])
    }
    return result
}

// 13939
// 1 AT 56545, 67854, 79163 qvz EVERY            11309
// 2 at 57597, 76796  tqz                        19199
// 3 at 49444, 61805, 74166 jxz                  12361
// 4 at 16043, 32086 48129 qtz                   16043
// 5 at 27878, 41817, 55756, 69695  = zzz (EVERY 13939)
// 6 at 18673 37346 56019 QGZ                    18673