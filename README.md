# email-parser
Clone project via git and change directory to `email-parser`.

### To parse .eml file:
Enter `gradlew runProcessing -PemlFile="path"` in the console, where `path` is path to eml file.

**Output format**
Header of the quotation is in uppercase.
Quotation is marked with '>' symbol beginning with the first line of the header of the quotation till the end of the message.
Working time is also provided.

### To test:
Enter `gradlew test` in the console.

