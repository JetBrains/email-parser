package practice.email.executable

import practice.email.parser.Content
import practice.email.parser.parseEml
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.util.Properties

import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

private val incorrectFileMsg = "Given file is not exists."
private val noFilePathMsg = "There isn't any path to target file. Try again and input path as a first command-line argument."

fun main(args: Array<String>) {
    if (args.size > 0) {
        val file = File(args[0])
        if (file.exists()) {
//            val text =
//"""Привет,
//
//В принципе, я более-менее живая и в офисе. Если не напишу до 15-00,
//встречаемся в 17-00 в офисе.
//
//Скажи девочкам на ресепшене, что ты ко мне на практику, они либо
//пропустят, либо позовут меня. Я сижу в 508 (кабинет прямо напротив
//лифтовой площадки).
//
//On 03/02/2016 12:10 AM, Павел Жук wrote:
//> Почта с телефона доступна почти всегда, желательно где-то до 15:00 -
//> 15:30 меня предупредить. Если завтра не придет письма, считать что
//> встреча состоится?
//> Желаю поправиться.
//>
//> 1 марта 2016 г., 19:15 пользователь Mariya Davydova
//> <mariya.davydova@jetbrains.com> написал:
//>> Паша,
//>>
//>> Я приболела. Я очень надеюсь, что завтра смогу выйти, но не уверена. Как часто ты читаешь почту? Иными словами, когда крайний срок, чтобы предупредить тебя, если я все-таки разболеюсь?
//>>
//>> Sent from my iPad
//>>
//>>> On Mar 1, 2016, at 13:35, Павел Жук <ppzhuk@gmail.com> wrote:
//>>>
//>>> Нотификации получил, сегодня вечером займусь исправлением.
//>>>
//>>> 29 февраля 2016 г., 17:56 пользователь Mariya Davydova
//>>> <mariya.davydova@jetbrains.com> написал:
//>>>> Привет,
//>>>>
//>>>> Сделала глобальное ревью на весь код, оставила кучу комментов (если я
//>>>> правильно понимаю гитхаб, он уже должен забросать тебя нотификациями на эту
//>>>> тему). Если нотификаций вдруг нет, то просто почитай коммиты, комментарии
//>>>> аттачатся именно к ним.
//>>>>
//>>>> В целом хорошо, но много мелочей, на которые стоит обратить внимание. Мне бы
//>>>> хотелось, чтобы ты пофиксил указанные замечания.
//>>>>
//>>>>> On 02/27/2016 03:29 AM, Павел Жук wrote:
//>>>>>
//>>>>> Привет.
//>>>>>
//>>>>> Я сделал вторую задачу, все в репозитории, посмотри, пожалуйста.
//>>>>> Подойдет ли такой вариант запуска gradle? (то что запуск задачи run и
//>>>>> передача числа происходят не вместе).
//>>>>>
//>>>>> Id задачи указан не во всех коммитах (поздно вспомнил), но там они все
//>>>>> относятся ко второй задаче.
//>>>>>
//>>>> --
//>>>> Best regards,
//>>>> Mariya Davydova
//>>>> Software Developer
//>>>> JetBrains
//>>>> http://www.jetbrains.com
//>>>> The Drive to Develop
//>>>>
//>>>
//>>>
//>>> --
//>>> Жук Павел.
//>
//>
//
//--
//Best regards,
//Mariya Davydova
//Software Developer
//JetBrains
//http://www.jetbrains.com
//The Drive to Develop
//
//""".replace("\n", "\r\n")
            val email = parseEml(file)
            println(email)
//            println(text)
//            println("${text.length} = ${email.content.body.length}")
//            for ((i, ch) in email.content.body.withIndex()) {
//                if (text[i] != ch) {
//                    println("$i => ${text[i]} - $ch")
//                }
//            }
//            val arrText = text.toCharArray()
//            val bodyText = email.content.body.toCharArray()
//            val i = 10
        } else {
            println(incorrectFileMsg)
        }
    } else {
        println(noFilePathMsg)
    }


}

// just some func for testing
fun helperFun() {
    val props: Properties = System.getProperties()
    val mailSession: Session = Session.getDefaultInstance(props)
    val source: InputStream = FileInputStream(File("D:\\IDEA workspace\\email-parser\\src\\test\\resources\\yamail-yamail\\wc_test.eml"))
    val message: MimeMessage = MimeMessage(mailSession, source)

    //    val sc: Scanner = Scanner(FileInputStream(File("src\\main\\resources\\1_1_send_2_q0.eml")))
    //    var sb = StringBuffer()
    //    while (sc.hasNextLine()) {
    //        sb.append(sc.nextLine()+ "\n")
    //    }
    //    val text = sb.toString()
    //    println(text)
    //    val source: InputStream = ByteArrayInputStream(text.toByteArray(Charset.defaultCharset()))
    //    val message: MimeMessage = MimeMessage(mailSession, source)

    println("Date : " + message.sentDate)
    for (a in message.from) {
        val addr = a as InternetAddress
        println("From : ${addr.address}")
    }
    for (t in message.getRecipients(Message.RecipientType.TO)) {
        val addr = t as InternetAddress
        println("To : ${addr.address}")
    }
    println("Subject : " + message.subject)
    println("--------------")
    val content = message.content
    if (content is MimeMultipart) {
        println(content.getBodyPart(0).content)
    } else {
        println(content)
    }


    //    val headers =  message.allHeaders
    //    while (headers.hasMoreElements()) {
    //        val h: Header = headers.nextElement() as Header
    //        println("${h.name}: ${h.value}")
    //    }
}
