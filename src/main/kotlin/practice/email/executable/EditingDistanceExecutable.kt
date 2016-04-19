package practice.email.executable

import practice.email.parser.getEditingDistance

fun main(args: Array<String>) {
    val a = "editing"
    val b = "distance"

    println(getEditingDistance(a, b))
}