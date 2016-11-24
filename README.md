# email-parser
This library provides ability to separate the quotation from the useful content in email messages. The main purpose of this library is to process as much different quotation formats as possible. It is also independent from the language used in email.

Efficiency estimation we have got during testing: **> 97.5 %** correctly processed emails.

For now it works only with text/plain Content-Type. Text/HTML content type will be added soon.

# Usage
You can download library sources and add them into your project.

### To run in the console
Clone project via git and change directory to `email-parser`.

### Process .eml file:
Enter `gradlew runProcessing -PemlFile="path"` in the console, where `path` is path to eml file.

**Output format**        
Header of the quotation is in uppercase.         
Quotation is marked with '>' symbol beginning with the first line of the header of the quotation till the end of the message.          
Working time is also provided.            

### Tests:
Enter `gradlew test` in the console.

### Documentation
To get documentation in [dokka](https://github.com/Kotlin/dokka) format enter `gradlew dokka` in the console. Then run `build/dokka/email-parser/index.html`

To get documentation in Javadoc format enter `gradlew dokkaJavadoc` in the console. Then run `build/javadoc/index.html`

### Description
The main package of the library is [quoteParser](src/main/kotlin/quoteParser). Its main class is [QuoteParser](src/main/kotlin/quoteParser/QuoteParser.kt#L63). 

To use parser call `quoteParserObj.parse()` method. This method will return [Content](src/main/kotlin/quoteParser/Content.kt) object with a separate body, header of the quote if exists and quotation itself if exists.

For more information, read the documentation.

# Examples
Simple usage example:
```kotlin
val content = QuoteParser.Builder()
        .build()
        .parse(file)
```
You also can parse list of strings and customize parser parameters via different builder methods:
```kotlin
val content = QuoteParser.Builder()
        .deleteQuoteMarks(true)
        .recursive(false)
        .build()
        .parse(emlText.lines())
```
Kotlin-style builder is also supported:
```kotlin
val content = QuoteParser.Builder()
        .build {
            deleteQuoteMarks = true
            recursive = false
        }.parse(emlText.lines())
```
Complete code of the examples is placed [here](src/main/kotlin/examples).
