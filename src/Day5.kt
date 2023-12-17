import java.io.File
import java.lang.Long.min
//15880236
val numRegex = Regex("[0-9]+")
var iterator = 0
fun main() {
    val lines = File("src/input/day5-2.txt").readLines().map {
        it.trim()
    }
    //val seedRegex = Regex("^seeds: ([0-9 ]+)$")
    val seeds = numRegex.findAll(lines.first().split(":").last()).map { it.value.toLong() }.toList()
    iterator = 1
    val seedToSoil = generateMap(lines)
    val soilToFertilizer = generateMap(lines)
    val fertilizerToWater = generateMap(lines)
    val waterToLight = generateMap(lines)
    val lightToTemperature = generateMap(lines)
    val temperatureToHumidity = generateMap(lines)
    val humidityToLocation = generateMap(lines)

    // memo?
    val seedToLocation = mutableMapOf<Long, Long>()
    val soilToLocation = mutableMapOf<Long, Long>()

    val partOne = seeds.map { seed ->
        if (seedToLocation.containsKey(seed)) {
            seedToLocation[seed]!!
        } else {
            val soil = seedToSoil.getDestinationFromList(seed)
            if (soilToLocation.containsKey(soil)) {
                val loc = soilToLocation[soil]!!
                seedToLocation[seed] = loc
                loc
            } else {
                val fertilizer = soilToFertilizer.getDestinationFromList(soil)
                val water = fertilizerToWater.getDestinationFromList(fertilizer)
                val light = waterToLight.getDestinationFromList(water)
                val temp = lightToTemperature.getDestinationFromList(light)
                val humidity = temperatureToHumidity.getDestinationFromList(temp)
                val location = humidityToLocation.getDestinationFromList(humidity)
                seedToLocation[seed] = location
                soilToLocation[soil] = location
                location
            }
        }
    }.min()

    println("Part one is $partOne")
    var i = 0
    var minLocation = Long.MAX_VALUE
    while (i < seeds.size) {
        var seed = seeds[i]
        var range = seeds[i+1]
        for (j in seed until seed + range) {
            if (seedToLocation.containsKey(j)) {
                minLocation = minOf(minLocation, seedToLocation[j]!!)
            } else {
                val soil = seedToSoil.getDestinationFromList(j)
                if (soilToLocation.containsKey(soil)) {
                    val loc = soilToLocation[soil]!!
                    seedToLocation[j] = loc
                    minLocation = minOf(minLocation, loc)
                } else {
                    val fertilizer = soilToFertilizer.getDestinationFromList(soil)
                    val water = fertilizerToWater.getDestinationFromList(fertilizer)
                    val light = waterToLight.getDestinationFromList(water)
                    val temp = lightToTemperature.getDestinationFromList(light)
                    val humidity = temperatureToHumidity.getDestinationFromList(temp)
                    val location = humidityToLocation.getDestinationFromList(humidity)
                    seedToLocation[seed] = location
                    minLocation = minOf(minLocation, location)
                }
            }
        }
        i+=2
    }
    println("part two is $minLocation")
}


fun List<SourceDestinationMap>.getDestinationFromList(i: Long) : Long {
    val map = this.firstOrNull { it.sourceInRange(i) }
    return map?.destination(i) ?: i
}

data class SourceDestinationMap(val source: Long, val destination: Long, val range: Long) {
    fun sourceInRange(i: Long): Boolean {
        return i >= source && i < source + range
    }
    fun destination(i: Long) : Long {
        val offset = i - source
        return destination + offset
    }
}

fun generateMap(lines: List<String>): List<SourceDestinationMap> {
    val result = mutableListOf<SourceDestinationMap>()
    iterator+=2
    var current = lines[iterator]
    while (current.isNotEmpty()) {
        val nums = numRegex.findAll(current).map { it.value.toLong() }.toList()
        var destination = nums.first()
        var source = nums[1]
        var range = nums.last()
        result.add(SourceDestinationMap(source, destination, range))
        iterator++
        if (iterator < lines.size) {
            current = lines[iterator]
        } else {
            return result
        }
    }
    return result
}

//fun generateMap(lines: List<String>): MutableMap<Long, Long> {
//    iterator+=2
//    val result = mutableMapOf<Long, Long>()
//    var current = lines[iterator]
//    while (current.isNotEmpty()) {
//        val nums = numRegex.findAll(current).map { it.value.toLong() }.toList()
//        var destination = nums.first()
//        var source = nums[1]
//        var range = nums.last()
//        for (i in source until source + range) {
//            result[i] = destination
//            destination++
//        }
//        iterator++
//        if (iterator < lines.size) {
//            current = lines[iterator]
//        } else {
//            return result
//        }
//    }
//    return result
//}