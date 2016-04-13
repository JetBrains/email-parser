package practice.email.parser

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import javax.mail.internet.MailDateFormat

object TestEmails {
    private val path =
        ".${File.separator}src${File.separator}test${File.separator}resources${File.separator}" + 
        "testFiles${File.separator}email${File.separator}"
    
    private val mailDateFormat = MailDateFormat();

    private fun getAddressesList(vararg addresses: String): ArrayList<String> {
        val adrses: ArrayList<String> = ArrayList()
        addresses.forEach { adrses.add(it) }
        return adrses
    }

    fun readFile(file: File): String {
        Files.newBufferedReader(Paths.get(file.toURI()), charset("UTF-8")).use { br ->
            val builder = StringBuilder()
            var line = br.readLine()

            while (line != null) {
                builder.append("$line\n")
                line = br.readLine()
            }

            return builder.toString().dropLast(1)
        }
    }


    val simple = Email(
            mailDateFormat.parse("""Mon, 14 Mar 2016 17:18:00 +0300"""),
            getAddressesList("""Павел Жук <pav9147@yandex.ru>"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """some english и русский""",
            Content(
                    readFile(File(
                            "${path}simple.txt"
                    )),
                    null, null
            )
    )

    val simple_koi8 = Email(
            mailDateFormat.parse("""Mon, 14 Mar 2016 16:41:54 +0300"""),
            getAddressesList("""pav9147@yandex.ru"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """Тест из веб-клиента""",
            Content(
                    readFile(File(
                            "${path}simple_koi8.txt"
                    )),
                    null, null
            )
    )

    val simple_base64_encoding = Email(
            mailDateFormat.parse("""Sat, 27 Feb 2016 03:29:25 +0300"""),
            getAddressesList("""Павел Жук <ppzhuk@gmail.com>"""),
            getAddressesList("""Mariya Davydova <mariya.davydova@jetbrains.com>"""),
            """email-parser task#2""",
            Content(
                    readFile(File(
                            "${path}simple_base64_encoding.txt"
                    )),
                    null, null
            )
    )

    val simple_koi8_2 = Email(
            mailDateFormat.parse("""Sun, 13 Mar 2016 01:02:14 +0300"""),
            getAddressesList("""pav9147@yandex.ru"""),
            getAddressesList("""Паша Жук <jojograf@yandex.ru>"""),
            """Re: тема письма""",
            Content(
                    readFile(File(
                            "${path}simple_koi8_2.txt"
                    )),
                    null, null
            )
    )

    val multipart_alt = Email(
            mailDateFormat.parse("""Thu, 17 Mar 2016 14:12:06 +0300"""),
            getAddressesList("""Павел Жук <ppzhuk@gmail.com>"""),
            getAddressesList("""thebostor@gmail.com"""),
            """Fwd: Re: Практика""",
            Content(
                    readFile(File(
                            "${path}multipart_alt.txt"
                    )),
                    null, null
            )
    )

    val multipart_alt_koi8 = Email(
            mailDateFormat.parse("""Thu, 17 Mar 2016 14:07:01 +0300"""),
            getAddressesList("""Павел Жук <jojograf@yandex.ru>"""),
            getAddressesList("""pav9147@yandex.ru"""),
            """Fwd: тема письма""",
            Content(
                    readFile(File(
                            "${path}multipart_alt_koi8.txt"
                    )),
                    null, null
            )
    )

    val multipart_alt_ascii = Email(
            mailDateFormat.parse("""Tue, 15 Mar 2016 00:50:13 +0300"""),
            getAddressesList("""Yandex Mail <pav9147@yandex.ru>"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """theme""",
            Content(
                    readFile(File(
                            "${path}multipart_alt_ascii.txt"
                    )),
                    null, null
            )
    )

    val multipart_alt_koi8_2 = Email(
            mailDateFormat.parse("""Tue, 15 Mar 2016 00:24:04 +0300"""),
            getAddressesList("""Yandex Mail <pav9147@yandex.ru>"""),
            getAddressesList("""jojograf@yandex.ru"""),
            """Проверяем""",
            Content(
                    readFile(File(
                            "${path}multipart_alt_koi8_2.txt"
                    )), null, null
            )
    )

}