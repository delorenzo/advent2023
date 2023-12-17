import java.io.File

fun main() {
    val input = File("src/input/day2-2.txt").readLines().map {
        it.trim()
    }
    val regex = Regex("^Game [0-9]+: (.*)$")
    val regex2 = Regex(" ?([0-9]+) ([a-z]+) ?")
    val games = input.map {
        val game = regex.matchEntire(it)!!.groups[1]!!.value
        val sets = game.split(";")
        val cubes = sets.map { set -> set.split(",").map { cube ->
            val match = regex2.matchEntire(cube)!!
            Cubes(match.groupValues[1]!!.toInt(), match.groupValues[2]!!)
        } }
        cubes
    }
    println(games)
    val startingCubes = mapOf(
        "red" to 12,
        "green" to 13,
        "blue" to 14
    )
    val possibleGames = mutableListOf<Int>()
    games.mapIndexed { index, list ->
        if (isPossibleWithInput(startingCubes, list)) { possibleGames.add(index+1) }
    }
    //println(possibleGames)
    println(possibleGames.sum())

    // Part two
    val powers = games.map { findPowerOfGames(it) }
    println(powers)
    println(powers.sum())
}

fun findPowerOfGames(game: List<List<Cubes>>): Long {
    val min = mutableMapOf(
        "red" to 0L,
        "blue" to 0L,
        "green" to 0L
    )
    game.map { set ->
        set.map { cubes ->
            val count = min[cubes.color]!!
            if (cubes.num > count) {
                min[cubes.color] = cubes.num.toLong()
            }
        }
    }
    return min.values.reduce { acc, i -> acc * i }
}

fun isPossibleWithInput(startingCubes: Map<String, Int>, game: List<List<Cubes>>) : Boolean {
    game.map { set ->
        set.map { cubes ->
            val total = startingCubes.getOrDefault(cubes.color, 0)
            if (total < cubes.num) { return false }
        }
    }
    return true
}

data class Cubes(val num: Int, val color: String)