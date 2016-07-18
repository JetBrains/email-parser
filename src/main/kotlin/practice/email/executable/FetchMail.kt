package practice.email.executable

import practice.email.parser.EmailParser.ContentType
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import javax.mail.Folder
import javax.mail.Message
import javax.mail.Session

private class GmailMailbox(val login: String, val pass: String) {
    fun readInbox() {

        println("Connecting to mailbox...")

        val session = Session.getDefaultInstance(Properties())
        val store = session.getStore("imaps")
        store.connect("imap.gmail.com", login, pass)

        println("Connected.")
        println("Opening 'Inbox' folder...")

        val inbox: Folder = store.getFolder("inbox");
        if (inbox.exists()) {
            inbox.open(Folder.READ_ONLY);

            println("Total messages: " + inbox.messageCount);
            println("Filtering...");

            val filteredMessages = 
                    filterMessages(inbox.messages.asList())

            println("Total messages after filtration: " + filteredMessages.size);

            if (filteredMessages.size == 0) {
                println("Nothing to save on the disk.")
            } else {
                saveMessages(filteredMessages)
                
                println("All messages saved.");
            }            

            inbox.close(false);
        } else {
            println("Can't open 'Inbox' folder.")
        }
        
        println("Done.")
        
        store.close();
    }


    /**
     * Фультрует сообщения только с подходящим ContentType
     */
    private fun filterMessages(messages: List<Message>): List<Message> =
            messages.filterIndexed { i, message ->
                if (i % 50 == 0)
                    println("$i messages filtered.")

                val contentType = message.contentType.split(";")[0].trim().toLowerCase()
                contentType.equals(ContentType.TEXT_PLAIN) ||
                        contentType.equals(ContentType.MULTIPART_ALT)
            }


    private fun saveMessages(messages: List<Message>) {
        val path = ".${File.separator}src${File.separator}main${File.separator}" +
                "resources${File.separator}YT${File.separator}"

        messages.forEachIndexed { i, message ->
            if (i % 50 == 0)
                println("$i messages saved.")

            message.writeTo(FileOutputStream(File("$path$i.eml")))
        }
    }
}

fun main(args: Array<String>) {
    val properties = Properties()

    try {
        properties.load(FileInputStream(File(
                ".${File.separator}src${File.separator}main${File.separator}" +
                        "resources${File.separator}mail.properties"
        )))

        val gmail = GmailMailbox(
                properties.getProperty("loginTest"),
                properties.getProperty("passwordTest")
        )

        gmail.readInbox()

    } catch(e: IllegalArgumentException) {
        print("Couldn't find properties file: mail.properties.")
        System.exit(-1)
    } catch(e: IllegalStateException) {
        print("One or more necessary properties are missing.")
        System.exit(-1)
    } catch(e: IOException) {
        print("Properties file is incorrect.")
        System.exit(-1)
    }
}
