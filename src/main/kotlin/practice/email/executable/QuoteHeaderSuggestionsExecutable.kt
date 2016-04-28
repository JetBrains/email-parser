package practice.email.executable

import practice.email.parser.QuotesHeaderSuggestions

fun main(args: Array<String>) {
    val text =
"""Скажи, пожалуйста, что ты думаешь по поводу выделения заголовка цитаты по
дате/времени?"""
    println(QuotesHeaderSuggestions.getQuoteHeader(text))
}