package practice.email.executable

import practice.email.parser.getEditDistance
import practice.email.parser.getEditDistanceAlignment
import java.util.*

fun main(args: Array<String>) {
    val headersArray: Array<String> = arrayOf(
            "15 апреля 2016 г., 20:26 пользователь Mariya Davydova <mmm@jjj.com> написал:",
            "29 февраля 2016 г., 17:56 пользователь Mariya Davydova <mdrefha@juy.com> написал:",
            "Воскресенье, 13 марта 2016, 6:55 +10:00 от Павел Жук < ppp@zzz.com >:",
            "Воскресенье, 13 марта 2016, 7:09 +10:00 от Павел Жук <lkjk@jl.com>:",
            "On 04/15/2016 05:08 PM, Павел Жук wrote:",
            "El 02/04/16 a las 19:03, Zhuk Pavel escribió:",
            "On Mar 1, 2016, at 13:35, Павел Жук <pp@g.c> wrote:",
            "On Thu, Mar 31, 2016 at 4:04 PM, Zhuk Pavel <y-k@jjjjj.com> wrote:",
            "Это в общих чертах, 15:22 на деле, видимо, еще придется учесть несколько частных случаев, но в целом, g@hh.er кажется, что задача поиска заголовка цитаты менее масштабная. Признаки, кроме: даты, не зависят от языка."
    )


    val headers: MutableList<String> = ArrayList()

    if (args.size == 0) {
        headers.addAll(headersArray)
    } else {
        args.forEach {
            val idx: Int
            try {
                idx = it.toInt()
            } catch (e: NumberFormatException) {
                println("\"$it\" is not a number.")
                return
            }
            if (idx < 0 || idx >= headersArray.size) {
                println("Incorrect index: $idx")
                println("Input range is from 0 to ${headersArray.size - 1}.")
                return
            }
            headers.add(headersArray[idx])
        }
    }

    val distances: MutableList<Triple<String, String, Int>> = arrayListOf()

    headers.forEachIndexed { i, a ->
        headers.filterIndexed { j, s -> j > i }.forEach { b ->
            distances.add(Triple(a, b, getEditDistance(a, b)))
        }
    }

    distances.sortBy { it.third }
    distances.forEach {
        println("a = ${it.first}")
        println("b = ${it.second}")
        println("Editing distance = ${it.third}")
        printAlignment(it.first, it.second)
        println()
    }

}

fun printAlignment(a: String, b: String) {
    val (resA, resB) = getEditDistanceAlignment(a, b)
    println(resA.joinToString(prefix = "a = ", separator = "|") { "${it.text}" })
    println(resB.joinToString(prefix = "b = ", separator = "|") { "${it.text}" })

}