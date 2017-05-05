/*
 * Copyright 2016-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package examples

import quoteParser.QuoteParser
import quoteParser.containInReplyToHeader
import quoteParser.getEmailText
import quoteParser.getMimeMessage
import java.io.File

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

// This is an example of usage QuoteParser with list of strings.
// You can specify if the supposed eml file contain In-Reply-To or
// References header through the call of the quoteParserObj.parse(lines, hasInReplyTo).
// It has true default value which means that quotation criteria are weakened.
//
// Also you could use helper functions to get MimeMessage, check for 
// In-Reply-To or References header or extract plain text from MimeMessage.
fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            val start = System.currentTimeMillis()

            val msg = getMimeMessage(file)
            val hasInReplyTo = containInReplyToHeader(msg)
            val emlText = getEmailText(msg)
            
            val c = QuoteParser.Builder()
                    .build()
                    .parse(emlText.lines(), hasInReplyTo)

            val end = System.currentTimeMillis()
            println("Working time: ${end - start} ms.")
            println("-".repeat(50))
            println(c.toString(addMarks = true, uppercaseHeader = true))
        } else {
            println(incorrectFileMsg)
        }
    } else {
        println(noFilePathMsg)
    }
}