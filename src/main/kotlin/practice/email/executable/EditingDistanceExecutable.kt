package practice.email.executable

import practice.email.parser.Token
import practice.email.parser.getEditingDistance

fun main(args: Array<String>) {
    val a = "editing"
    val b = "distance"

    println(getEditingDistance(a, b))
    println(Token("<>").hasWithAngleBrackets)
    println(Token("<sdf$>,").hasWithAngleBrackets)
    println(Token("<sdf$>").hasWithAngleBrackets)
    println(Token("12s3").isDigits)
}