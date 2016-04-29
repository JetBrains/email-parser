package practice.email.executable

import practice.email.parser.Token
import practice.email.parser.getEditingDistance
import practice.email.parser.getEditingDistance2
import java.util.*

fun main(args: Array<String>) {

    val headers: MutableList<String> = ArrayList()
    headers.add("15 апреля 2016 г., 20:26 пользователь Mariya Davydova <mmm@jjj.com> написал:")
//    headers.add("29 февраля 2016 г., 17:56 пользователь Mariya Davydova <mdrefha@juy.com> написал:")
//
    headers.add("Воскресенье, 13 марта 2016, 6:55 +10:00 от Павел Жук < ppp@zzz.com >:")
//    headers.add("Воскресенье, 13 марта 2016, 7:09 +10:00 от Павел Жук <lkjk@jl.com>:")

    headers.add("On 04/15/2016 05:08 PM, Павел Жук wrote:")
    headers.add("El 02/04/16 a las 19:03, Zhuk Pavel escribió:")
//
    headers.add("On Mar 1, 2016, at 13:35, Павел Жук <pp@g.c> wrote:")
//    headers.add("On Thu, Mar 31, 2016 at 4:04 PM, Zhuk Pavel <y-k@jjjjj.com> wrote:")

    headers.add("Это в общих чертах, 15:22 на деле, видимо, еще придется учесть несколько частных случаев, но в целом, g@hh.er кажется, что задача поиска заголовка цитаты менее масштабная. Признаки, кроме: даты, не зависят от языка.")

    headers.forEachIndexed { i, a ->
        headers.filterIndexed { j, s -> j > i }.forEach { b ->
            println("a = $a")
            println("b = $b")
            println("Editing distance = ${getEditingDistance(a, b)}")
            printAlignment(a, b)
            println()
        }
    }

}

fun printAlignment(a: String, b: String) {
    var resA: MutableList<Token> = ArrayList()
    var resB: MutableList<Token> = ArrayList()
    getEditingDistance2(a, b, resA, resB)
    println(resA.joinToString(prefix = "a = ", separator = "|") { "${it.text}" })
    println(resB.joinToString(prefix = "b = ", separator = "|") { "${it.text}" })
}