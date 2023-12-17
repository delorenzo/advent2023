import java.io.File
import kotlin.contracts.contract

// 55189 too low
fun main() {
    val input = File("src/input/day1-2.txt").readLines().map {
        it.trim()
    }
    // part one
//    val sum = input.map {
//        returnPartOneNumber(it)
//    }.sum()
//    println("Part one: sum is $sum")

    val sum2 = input.map {
        returnPartTwoNumber(it).toLong()
    }.sum()
    println("Part two: sum is $sum2")
}

val digits = listOf('1', '2', '3', '4', '5', '6', '7', '8', '9', '0')

data class StringDigit(val value: Char, val length: Int, val stringRep: String)
val stringDigits = mutableMapOf<Char, List<StringDigit>>(
    'o' to listOf(StringDigit('1', 3, "one")),
    't' to listOf(StringDigit('2', 3, "two"), StringDigit('3', 5, "three")),
    'f' to listOf(StringDigit('4', 4, "four"), StringDigit('5', 4, "five")),
    's' to listOf(StringDigit('6', 3, "six"), StringDigit('7', 5, "seven")),
    'e' to listOf(StringDigit('8', 5, "eight")),
    'n' to listOf(StringDigit('9', 4, "nine")),
    'z' to listOf(StringDigit('0', 4, "zero"))
)

fun returnPartTwoNumber(str: String) : Int {
    var i = 0
    val newString = mutableListOf<Char>()
    while (i < str.length) {
        if (str[i] in digits) {
            newString.add(str[i])
            i++
        } else {
            if (stringDigits.containsKey(str[i])) {
                val options = stringDigits[str[i]]
                run iterate@{
                    options?.forEach {
                        if (i + it.length <= str.length) {
                            val substring = str.substring(i, i + it.length)
                            if (substring == it.stringRep) {
                                i += it.length-1 // can re-use letters.. I think
                                newString.add(it.value)
                                return@iterate
                            }
                        }
                    }
                    i++
                }
            } else {
                i++
            }
        }
    }
    val num = "" + newString.first() + newString.last()
    println(num)
    return num.toInt()
}

fun returnPartOneNumber(str: String) : Int {
    val numStr = str.filter { it in  digits }
    val num : String = "" + numStr.first() + numStr.last()
    return num.toInt()
}