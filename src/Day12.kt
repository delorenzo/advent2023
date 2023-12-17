import java.io.File
import java.lang.Exception
import java.util.*

fun main() {
    val lines = File("src/input/day12-2.txt").readLines().map { it.trim() }
    val input = lines.map {
        val line = it.split(" ")
        val spring = line.first().map { it }
        val config = line.last().split(",").map{it.toInt()}
        SpringInput(spring, config)
    }
    val cache = mutableMapOf<ArrangementCache, Long>()

    val counts = input.map {
        val springs = it.springs.toCharArray()
        arrangements(springs, it.config.toTypedArray(), 0, cache)
    }
    counts.map { println(it) }
    println("Part 1 : ${counts.sum()}")

    val partTwoCounts = input.map {
        val springs = it.bigSprings.toCharArray()
        arrangements(springs, it.bigConfig.toTypedArray(), 0, cache)
    }
    partTwoCounts.map { println(it) }
    println("Part 2 : ${partTwoCounts.sum()}")
}

data class ArrangementCache(val springs: CharArray, val config: Array<Int>, val len: Int) {
    override fun hashCode(): Int {
        var hash = 13
        hash = 31 * hash + springs.contentHashCode()
        hash = 31 * hash + config.contentHashCode()
        hash = 31 * hash + len.hashCode()
        return hash
    }

    override fun equals(other: Any?): Boolean {
        if (other !is ArrangementCache) { return false }
        return len == other.len && config.contentEquals(other.config) && springs.contentEquals(other.springs)
    }
}

fun arrangements(springs: CharArray, config: Array<Int>, len: Int, cache: MutableMap<ArrangementCache, Long>) : Long {
    val cacheEntry = ArrangementCache(springs, config, len)
    if (cache.containsKey(cacheEntry)) { return cache[cacheEntry]!!}
    if (springs.isEmpty()) {
        if (config.isEmpty() && len == 0) { return 1 }
        if (config.size == 1 && config[0] == len) { return 1 }
        cache[cacheEntry] = 0
        return 0
    }
    if (config.isEmpty() && len > 0) return 0
    if (config.isNotEmpty() && len > config[0]) return 0
    if (config.isNotEmpty() && len == config[0] && springs[0] != '#') {
        val result = arrangements(springs.sliceArray(1 until springs.size), config.sliceArray(1 until config.size), 0, cache)
        cache[cacheEntry] = result
        return result
    }
    if (config.isNotEmpty() && len == config[0] && springs[0] == '#') {
        cache[cacheEntry] = 0
        return 0
    }
    return when (springs[0]) {
        '.' -> {
            if (len > 0) {
                cache[cacheEntry] = 0
                0
            }
            else {
                val result = arrangements(springs.sliceArray(1 until springs.size), config, 0, cache)
                cache[cacheEntry] = result
                result
            }
        }
        '#' -> {
            val result = arrangements(springs.sliceArray(1 until springs.size), config, len + 1, cache)
            cache[cacheEntry] = result
            result
        }
        '?' -> {
            if (len > 0) {
                val result = arrangements(springs.sliceArray(1 until springs.size), config, len+1, cache)
                cache[cacheEntry] = result
                result
            } else {
                val result =arrangements(
                    springs.sliceArray(1 until springs.size),
                    config,
                    0,
                    cache
                ) + arrangements(springs.sliceArray(1 until springs.size), config, len + 1, cache)
                cache[cacheEntry] = result
                result
            }
        }
        else -> { throw Exception() }
    }
}

data class SpringInput(val springs: List<Char>, val config: List<Int>) {

    val bigSprings: List<Char> by lazy {
        val newList = mutableListOf<Char>()
        for (i in 0 until 5) {
            newList.addAll(springs)
            if (i != 4) {
                newList.add('?')
            }
        }
        newList
    }

    val bigConfig: List<Int> by lazy {
        val newList = mutableListOf<Int>()
        for (i in 0 until 5) {
            newList.addAll(config)
        }
        newList
    }
}