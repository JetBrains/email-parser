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
import java.io.File

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

// This is an example of typical usage of QuoteParser.
// You must use QuoteParser.Builder to instantiate.
// You can also customize QuoteParser parameters with that builder,
// but all of them have default value.
fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
            val start = System.currentTimeMillis()

            val c = QuoteParser.Builder()
                    .build()
                    .parse(file)
            
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