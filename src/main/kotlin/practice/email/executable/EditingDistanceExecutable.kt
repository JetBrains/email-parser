package practice.email.executable

import practice.email.parser.Token
import practice.email.parser.getEditingDistance

fun main(args: Array<String>) {
    val a = "15 апреля 2016 г., 20:26 пользователь Mariya Davydova <mariya.davydova@jetbrains.com> написал:\n"
    val b = "Воскресенье, 13 марта 2016, 6:55 +10:00 от Павел Жук < ppzhuk@gmail.com >:\n"
    val c = "On 04/15/2016 05:08 PM, Павел Жук wrote:\n"
    val d = "On Thu, Mar 31, 2016 at 4:04 PM, YouTrack Support <youtrack-feedback@jetbrains.com> wrote:\n"
    
    println(getEditingDistance(a, b))
    println(getEditingDistance(a, c))
    println(getEditingDistance(a, d))
    println(getEditingDistance(b, c))
    println(getEditingDistance(b, d))
    println(getEditingDistance(c, d))
}