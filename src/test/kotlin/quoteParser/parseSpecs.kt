package quoteParser

import org.jetbrains.spek.api.Spek
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.stream.Collectors
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by Zhuk Pavel on 23.08.2016.
 */
class ParseSpecs : Spek() {
    private val pathDatasets = ".${File.separator}src${File.separator}main${File.separator}" +
            "resources${File.separator}datasets${File.separator}"
    private val pathEmails = "C:${File.separator}YT${File.separator}"


    private val FILTER_STRING = "##- Please type your reply above this line -##"

    private val EMAILS_COUNT = 23055
    
    init {


        given("quoteParser") {
            on("parsing emails") {
                it("must conform to the expected headers") {
                    val start = System.currentTimeMillis()
                    val expected_headers = readHeaders(File("${pathDatasets}expected_headers.txt"), verbose = true)
                    val actual_headers = getActualHeaders(verbose = true)
                    println("Expected headers: ${expected_headers.size}")
                    println("Actual headers: ${actual_headers.size}")
                    val result = compareHeaders(expected_headers, actual_headers)
                    val end = System.currentTimeMillis()
                    println("Working time: ${(end - start) / 1000.0} sec.")
                    
                    assertEquals(expected_headers.size, actual_headers.size, 
                            "Actual headers amount doesn't meet to the expected headers amount.")
                    
                    result.forEach { 
                        assertTrue("One or more headers are not equals.") { it } 
                    }
                }
            }
        }
        
    }

    private fun readHeaders(file: File, verbose: Boolean = false): List<Pair<Int, List<String>>> {
        if (verbose) {
            print("Reading expected headers...")
        }

        val inf = BufferedReader(InputStreamReader(FileInputStream(file)))
        var num: Int
        val lines = inf.lines().collect(Collectors.toList())
        var i = 0
        val headers: MutableList<Pair<Int, List<String>>> = mutableListOf()
        while (i < lines.size) {
            val line = (lines[i] as String)
            num = line.toInt()
            i++
            val header: MutableList<String> = mutableListOf()
            while (i < lines.size && (lines[i] as String) != "") {
                header.add(lines[i] as String)
                i++
            }
            headers.add(Pair(num, header))
            ++i
        }

        if (verbose) {
            println("Done")
        }

        return headers
    }

    private fun getActualHeaders(verbose: Boolean = false): List<Pair<Int, List<String>>> {
        if (verbose) {
            println("Evaluateing actual headers...")
        }

        val headers: MutableList<Pair<Int, List<String>>> = mutableListOf()

        for (i in 0..EMAILS_COUNT - 1) {
            val header: List<String>?
            val emailText: List<String>
            try {
                emailText = getEmailText(File("${pathEmails}${i}.eml")).lines()

                if (!emailText[0].trim().equals(FILTER_STRING)) {
                    val H = QuoteParser(emailText).parse().header
                    if (H != null && H.text.isEmpty()) {
                        header = listOf(emailText[H.startIndex])
                    } else {
                        header = H?.text
                    }
                } else {
                    header = null
                }
            } catch(e: Exception) {
                println("${i}.eml gave an error while parsing: ${e.message}")
                println("Skipping...")
                continue
            } catch(e: Error) {
                println("Some error with eml ${i}.")
                println("Message: ${e.message}")
                println("Skipping...")
                continue
            }

            if (header != null) {
                try {
                    headers.add(Pair(i, header))
                } catch (e: StringIndexOutOfBoundsException) {
                    println("Indexing error with eml ${i}.")
                    throw e
                }
            }

            if (verbose && i % 100 == 0) {
                println("$i is passed")
            }
        }

        if (verbose) {
            println("Done")
        }

        return headers
    }

    private fun compareHeaders(expected_headers: List<Pair<Int, List<String>>>,
                               actual_headers: List<Pair<Int, List<String>>>): Array<Boolean> {
        val result = Array(actual_headers.size) { false }
        val not_eqFile = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
                "${pathDatasets}not_equals.txt"
        ))))
        val missedFile = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
                "${pathDatasets}missed.txt"
        ))))
        val newFile = BufferedWriter(OutputStreamWriter(FileOutputStream(File(
                "${pathDatasets}new.txt"
        ))))

        var passed = true
        var equals = 0
        var non_equals = 0
        var new = 0
        var missed = 0

        var expected_index = 0
        var actual_index = 0
        while (expected_index < expected_headers.size && actual_index < actual_headers.size) {
            val expected_email_num = expected_headers[expected_index].first
            val expected_header = expected_headers[expected_index].second
            val actual_email_num = actual_headers[actual_index].first
            val actual_header = actual_headers[actual_index].second

            if (expected_email_num == actual_email_num) {
                if (expected_header.equals(actual_header)) {
                    result[actual_index] = true
                    expected_index++
                    actual_index++
                    equals++
                    continue
                }
                write_not_equals(not_eqFile, actual_email_num, expected_header, actual_header)
                expected_index++
                actual_index++
                non_equals++
            } else {
                if (expected_email_num > actual_email_num) {
                    write_mail(newFile, actual_email_num, actual_header)
                    actual_index++
                    new++
                } else if (expected_email_num < actual_email_num) {
                    write_mail(missedFile, expected_email_num, expected_header)
                    expected_index++
                    missed++
                }
            }
            passed = false
        }

        while (expected_index < expected_headers.size) {
            val expected_email_num = expected_headers[expected_index].first
            val expected_header = expected_headers[expected_index].second
            write_mail(missedFile, expected_email_num, expected_header)
            expected_index++
        }
        while (actual_index < actual_headers.size) {
            val actual_email_num = actual_headers[actual_index].first
            val actual_header = actual_headers[actual_index].second
            write_mail(newFile, actual_email_num, actual_header)
            actual_index++
        }

        if (passed) {
            println("All passed!")
        }

        println("Equals: $equals")
        println("Non equals: $non_equals")
        println("New: $new")
        println("Missed: $missed")

        missedFile.close()
        newFile.close()
        not_eqFile.close()

        return result
    }

    private fun write_mail(out: BufferedWriter, email_num: Int, header: List<String>) {
        out.write(email_num.toString())
        out.newLine()
        header.forEach {
            out.write(it)
            out.newLine()
        }
        out.newLine()
    }

    private fun write_not_equals(out: BufferedWriter, email_num: Int, expected_header: List<String>, actual_header: List<String>) {
        out.write(email_num.toString())
        out.newLine()
        out.newLine()
        out.write("Expected:\n")
        expected_header.forEach {
            out.write(it)
            out.newLine()
        }
        out.newLine()
        out.write("Actual:\n")
        actual_header.forEach {
            out.write(it)
            out.newLine()
        }
        out.newLine()
        out.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX\n")
    }
}