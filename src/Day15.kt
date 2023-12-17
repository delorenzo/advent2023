import java.io.File

fun main() {
    val lines = File("src/input/day15-2.txt").readLines().map { it.trim() }
    val instructions = lines.map { it.split(",") }.flatten()
    //println(instructions)
    val results = instructions.map { ins ->
        hash(ins)
    }
    println("Part one is ${results.sum()}")

    val boxes = List<MutableList<Pair<String, Int>>>(256) { mutableListOf() }

    val regex = Regex("([a-zA-z]+)([\\=\\-])(\\-?[0-9]+)?")
    instructions.forEach {  ins ->
        val match = regex.matchEntire(ins)!!
        val label = hash(match.groupValues[1])
        when (match.groupValues[2]) {
            "=" -> {
                val boxContents = boxes[label]
                val box = boxContents.firstOrNull { it.first == match.groupValues[1] }
                box?.let {
                    val index = boxContents.indexOf(box)
                    boxContents[index] = box.first to match.groupValues[3].toInt()
                } ?: run {
                    boxContents.add(match.groupValues[1] to match.groupValues[3].toInt())
                }
            }
            "-" -> {
                val box = boxes[label].firstOrNull { it.first == match.groupValues[1] }
                box?.let {
                    boxes[label].remove(box)
                }
            }
        }
        //println(boxes)
    }
    val partTwoNums = boxes.mapIndexed { boxIndex, mutableList ->
        val lensSum = mutableList.mapIndexed { lensIndex, pair ->
            (1 + boxIndex) * (1 + lensIndex) * pair.second
        }.sum()
        lensSum
    }.sum()
    println("Part two is $partTwoNums")
}

fun hash(str: String) : Int {
    var value = 0
    str.forEach { c ->
        value += c.toInt()
        value *= 17
        value %= 256
    }
    return value
}