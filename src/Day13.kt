import java.io.File
import java.lang.Exception

fun main() {
    val lines = File("src/input/day13-2.txt").readLines().map { it.trim() }
    val inputs = mutableListOf<List<List<String>>>()
    val current = mutableListOf<List<String>>()
    var lineIndex = 0
    while (lineIndex < lines.size) {
        while (lineIndex < lines.size && lines[lineIndex] != "") {
            current.add(lines[lineIndex].map { it.toString() })
            lineIndex++
        }
        inputs.add(current.toMutableList())
        current.clear()
        lineIndex++
    }

    val results = inputs.map { input ->
        reflectionResult(input)
    }
    val horizontal = results.filter { it.second == 0 }.map { it.first }.sum()
    val vertical = results.filter { it.second ==1  }.map { it.first }.sum()
    val partOne = (horizontal * 100) + vertical
    println("Part one is $partOne")

    // part two
    val newResults = inputs.mapIndexed { index, input ->
        unsmudge(input, results[index])
    }
    val newHorizontal = newResults.filter { it.second == 0 }.map { it.first }.sum()
    val newVertical = newResults.filter { it.second ==1  }.map { it.first }.sum()
    val partTwo = (newHorizontal * 100) + newVertical
    println("Part two is $partTwo")
}

fun unsmudge(input: List<List<String>>, oldResult: Pair<Int, Int>) : Pair<Int, Int> {
    //horizontal reflection
    for (i in 0 until input.size-1) {
        if (oldResult.second == 0 && oldResult.first == i+1) { continue }
        if (horizontalSmudge(i, input)) {
            return i+1 to 0
        }
    }
    //vertical reflection
    for (i in 0 until input.first().size-1) {
        if (oldResult.second == 1 && oldResult.first == i+1 ) { continue }
        if (verticalSmudge(i, input)) {
            return i+1 to 1
        }
    }
    throw Exception()
}

fun reflectionResult(input: List<List<String>>) : Pair<Int, Int> {
    //horizontal reflection
    for (i in 0 until input.size-1) {
        if (horizontalReflection(i, input)) {
            return i+1 to 0
        }
    }
    //vertical reflection
    for (i in 0 until input.first().size-1) {
        if (verticalReflection(i, input)) {
            return i+1 to 1
        }
    }
    throw Exception()
}

fun horizontalSmudge(index: Int, input: List<List<String>>) : Boolean {
    var smudgeUsed = false
    var topHalf = index
    var bottomHalf = index + 1
    while (topHalf > -1 && bottomHalf < input.size) {
        if (input[topHalf] != input[bottomHalf]) {
            if (smudgeUsed) { return false }
            for (a in input[topHalf].indices) {
                if (input[topHalf][a] != input[bottomHalf][a]) {
                    if (smudgeUsed) return false
                    else {
                        smudgeUsed = true
                    }
                }
            }
        }
        topHalf--
        bottomHalf++
    }
    return true
}

fun verticalSmudge(index: Int, input:List<List<String>>): Boolean {
    var leftHalf = index
    var rightHalf = index + 1
    var smudgeUsed = false
    while (leftHalf > -1 && rightHalf < input.first().size) {
        val leftColumn = input.map { it.filterIndexed { index, s -> index == leftHalf } }.flatten()
        val rightColumn = input.map { it.filterIndexed { index, s -> index == rightHalf } }.flatten()
        if (leftColumn != rightColumn) {
            if (smudgeUsed) { return false }
            for (a in leftColumn.indices) {
                if (leftColumn[a] != rightColumn[a]) {
                    if (smudgeUsed) return false
                    smudgeUsed = true
                }
            }
        }
        leftHalf--
        rightHalf++
    }
    return true
}

fun horizontalReflection(index: Int, input: List<List<String>>) : Boolean {
    var topHalf = index
    var bottomHalf = index + 1
    while (topHalf > -1 && bottomHalf < input.size) {
        if (input[topHalf] != input[bottomHalf]) return false
        topHalf--
        bottomHalf++
    }
    return true
}

fun verticalReflection(index: Int, input:List<List<String>>): Boolean {
    var leftHalf = index
    var rightHalf = index + 1
    while (leftHalf > -1 && rightHalf < input.first().size) {
        val leftColumn = input.map { it.filterIndexed { index, s -> index == leftHalf } }.flatten()
        val rightColumn = input.map { it.filterIndexed { index, s -> index == rightHalf } }.flatten()
        if (leftColumn != rightColumn) return false
        leftHalf--
        rightHalf++
    }
    return true
}