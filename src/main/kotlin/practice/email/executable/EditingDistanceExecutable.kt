package practice.email.executable

import practice.email.parser.getEditingDistance
import java.util.*

fun main(args: Array<String>) {

    val headers: MutableList<String> = ArrayList()
    headers.add("15 апреля 2016 г., 20:26 пользователь Mariya Davydova <mmm@jjj.com> написал:\n")
    headers.add("Воскресенье, 13 марта 2016, 6:55 +10:00 от Павел Жук < ppp@zzz.com >:\n")
    headers.add("On 04/15/2016 05:08 PM, Павел Жук wrote:\n")
    headers.add("On Thu, Mar 31, 2016 at 4:04 PM, Zhuk Pavel <y-k@jjjjj.com> wrote:\n")
    headers.add("El 02/04/16 a las 19:03, Zhuk Pavel escribió:\n")

    headers.forEachIndexed { i, a ->
        headers.filterIndexed { j, s -> j > i }.forEach { b ->
            print("a = $a")
            print("b = $b")
            println("Editing distance = ${getEditingDistance(a, b)}")
            println()
        }
    }
}