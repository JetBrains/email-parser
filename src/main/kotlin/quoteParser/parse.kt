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

package quoteParser

import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties
import javax.mail.Session
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.internet.MimePart
import javax.mail.internet.ParseException

/**
 * Check weather given message contains *In-Reply-To* header or 
 * *References* header.
 * @param msg 
 * @return true if message contains at least one of the headers specified above
 * @see getMimeMessage
 */
fun containInReplyToHeader(msg: MimeMessage) =
        msg.getHeader("In-Reply-To") != null || msg.getHeader("References") != null

/**
 * Construct *MimeMessage* from File object
 * @param emlFile 
 * @return MimeMessage object
 */
fun getMimeMessage(emlFile: File): MimeMessage {
    val source: InputStream = FileInputStream(emlFile)
    val props: Properties = System.getProperties()
    val session: Session = Session.getDefaultInstance(props)
    val msg: MimeMessage = MimeMessage(session, source)
    return msg
}

/**
 * Construct MimeMessage and then get the content of the text/plain part 
 * of this message. So if you already have MimeMessage object it is better 
 * to call **getEmailText(MimePart)** function in order to avoid additional 
 * costs.
 * @param emlFile
 * @return plain text content of the message as a string 
 * @throws ParseException if message does not contain text/plain part
 */
fun getEmailText(emlFile: File): String {
    val msg: MimeMessage = getMimeMessage(emlFile)
    return getEmailText(msg)
}

/**
 * Get the content of the text/plain part of the message.
 * @param part MimePart to get plain text from.
 * @return plain text content of the message as a string
 * @exception ParseException if message does not contain text/plain part
 */
fun getEmailText(part: MimePart): String {
   val text = searchForContent(part)
    return text ?: throw ParseException("Could not find text/plain part.")
}

private fun searchForContent(part: MimePart): String? {
    val content = part.content ?: throw ParseException("Could not find content of this email.")

    if (part.isMimeType("text/plain")) {
        return content as String
    }
    if (content is MimeMultipart) {
        for (i in 0..content.count - 1) {
            val c = searchForContent(content.getBodyPart(i) as MimePart)
            return c ?: continue
        }
    }
    return null
}