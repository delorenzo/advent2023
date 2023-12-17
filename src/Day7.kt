import java.io.File



//cards A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2
// not 247922464
//     249266340 too high
//     248750248
fun main() {
    val lines = File("src/input/day7-1.txt").readLines().map {
        it.trim()
    }
//    val hands = lines.map { line ->
//        val hand = line.split(" ")
//        val replacedCards = hand.first().replace("T", "10")
//        val cards = hand.first().map { hand -> hand.toString() }.map { it.replace("T", "10").replace("J", "11").replace("Q", "12").replace("K", "13").replace("A", "14").toInt() }
//        val bid = hand.last().toInt()
//        val counts = cards.groupBy { card -> card }.map { entry -> entry.key to entry.value.size }
//        val countMap = counts.toMap()
//        CamelHand(cards, bid, countMap, counts)
//    }
//    val sortedHands = hands.sorted().mapIndexed { index, camelHand -> index + 1 to camelHand.bid }
//    val winnings = sortedHands.map { it.first * it.second }.sum()
//    println(winnings)

    val partTwoHands = lines.map { line ->
        val hand = line.split(" ")
        val replacedCards = hand.first().replace("T", "10")
        val cards = hand.first().map { hand -> hand.toString() }.map { it.replace("T", "10").replace("J", "1").replace("Q", "12").replace("K", "13").replace("A", "14").toInt() }
        val bid = hand.last().toInt()
        val counts = cards.groupBy { card -> card }.map { entry -> entry.key to entry.value.size }.sortedBy { it.second }.reversed()
        val countMap = counts.toMap()
        CamelHand(cards, bid, countMap, counts)
    }
    val sortedPartTwoHands = partTwoHands.sorted().mapIndexed { index, camelHand -> index + 1 to camelHand.bid }
    val partTwoWinning = sortedPartTwoHands.map { it.first * it.second }.sum()
    println(partTwoWinning)
}

data class CamelHand(val cards: List<Int>, val bid: Int, val counts: Map<Int, Int>, val countPairs: List<Pair<Int, Int>>) : Comparable<CamelHand> {

    val typeScore: Int by lazy {
        val jokers = counts.getOrDefault(1, 0)
        val filteredCounts = countPairs.filterNot { it.first == 1 }
        val highest = filteredCounts.firstOrNull() ?: 0 to 0
        val secondHighest = filteredCounts.getOrNull(1) ?: 0 to 0
        when {
            jokers == 5 || highest.second == 5 || highest.second + jokers >= 5 -> 100
            jokers == 4 || highest.second == 4 || highest.second + jokers >= 4 -> 99
            highest.second == 3 && secondHighest.second == 2 -> 98
            highest.second + secondHighest.second + jokers >= 5 -> 98
            jokers == 3 || highest.second == 3 || highest.second + jokers >= 3 -> 97
            highest.second == 2 && secondHighest.second == 2 -> 96
            highest.second + secondHighest.second + jokers >= 4 -> 96
            highest.second == 2 -> 95
            highest.second + jokers >=2 -> 95
            else -> 1
        }
    }

//    val typeScore: Int by lazy {
//        when {
//            counts.any { it.value == 5 } -> 100
//            counts.any{ it.value == 4 } -> 99
//            counts.any{ it.value == 3} && counts.any { it.value == 2 } -> 98
//            counts.any{ it.value == 3} -> 97
//            counts.count { it.value == 2 } == 2 -> 96
//            counts.any { it.value == 2 } -> 95
//            else -> 1
//        }
//    }

    override fun compareTo(other: CamelHand): Int {
        if (this.typeScore < other.typeScore) return -1
        if (this.typeScore > other.typeScore) return 1
        for (i in this.cards.indices) {
            if (this.cards[i] < other.cards[i]) return -1
            if (this.cards[i] > other.cards[i]) return 1
        }
        return 0
    }
}