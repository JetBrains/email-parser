package practice.email.executable

import practice.email.parser.*
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.regex.Pattern
import java.util.stream.Collectors


fun main(args: Array<String>) {
    val text = "From: xxx yyy xxx-yyy@xxx.com"
    val res = text.matches(QuotesHeaderSuggestionsRegEx.MIDDLE_COLON.regex)

    println(res)

    val s = "From: xxx yyy xxx-yyy@xxx.com"

    val r = s.matches(QuotesHeaderSuggestionsRegEx.MIDDLE_COLON.regex)

    println(r)
    println()


    println(text[4] == s[4])
    println(String.format("\\u%04x", text[4].toInt()))
    println(String.format("\\u%04x", s[4].toInt()))
}

