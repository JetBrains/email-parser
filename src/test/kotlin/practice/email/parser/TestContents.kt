package practice.email.parser

import java.io.File

object TestContents {
    private val path = 
            ".${File.separator}src${File.separator}test${File.separator}" + 
            "resources${File.separator}testFiles${File.separator}content${File.separator}"
    
    val simple_sig = Content(
            TestEmails.readFile(File(
                    "${path}simple_sig${File.separator}body.txt"
            )),
            null,
            TestEmails.readFile(File(
                    "${path}simple_sig${File.separator}signature.txt"
            ))
    )

    val simple_sig_deep = simple_sig

    val simple_sig_2 = Content(
            TestEmails.readFile(File(
                    "${path}simple_sig_2${File.separator}body.txt"
            )),
            null,
            TestEmails.readFile(File(
                    "${path}simple_sig_2${File.separator}signature.txt"
            ))
    )

    val simple_sig_2_deep = simple_sig_2

    val simple_quote_koi8 = Content(
            TestEmails.readFile(File(
                    "${path}simple_quote_koi8${File.separator}body.txt"
            )),
            Content(
                    TestEmails.readFile(File(
                            "${path}simple_quote_koi8${File.separator}quote.txt"
                    )),
                    null,
                    null
            ),
            null
    )

    val simple_quote_koi8_deep = simple_quote_koi8

    val quote_plus_inner_sig = Content(
            TestEmails.readFile(File(
                    "${path}quote_plus_inner_sig${File.separator}body.txt"
            )),
            Content(
                    TestEmails.readFile(File(
                            "${path}quote_plus_inner_sig${File.separator}quote.txt"
                    )),
                    null,
                    null
            ),
            null
    )

    val quote_plus_inner_sig_deep = Content(
            TestEmails.readFile(File(
                    "${path}quote_plus_inner_sig${File.separator}body.txt"
            )),
            Content(
                    TestEmails.readFile(File(
                            "${path}quote_plus_inner_sig${File.separator}body1.txt"
                    )),
                    null,
                    TestEmails.readFile(File(
                            "${path}quote_plus_inner_sig${File.separator}signature1.txt"
                    ))
            ),
            null
    )

    val only_nested_quotes = Content(
            TestEmails.readFile(File(
                    "${path}only_nested_quotes${File.separator}body.txt"
            )),
            Content(
                    TestEmails.readFile(File(
                            "${path}only_nested_quotes${File.separator}quote.txt"
                    )),
                    null,
                    null
            ),
            null
    )

    val only_nested_quotes_deep = Content(
            TestEmails.readFile(File(
                    "${path}only_nested_quotes${File.separator}body0.txt"
            )),
            Content(
                    TestEmails.readFile(File(
                            "${path}only_nested_quotes${File.separator}body1.txt"
                    )),
                    Content(
                            TestEmails.readFile(File(
                                    "${path}only_nested_quotes${File.separator}body2.txt"
                            )),
                            null,
                            null
                    ),
                    null
            ),
            null
    )

    val just_body = Content(
            TestEmails.readFile(File(
                    "${path}just_body${File.separator}body.txt"
            )),
            null,
            null
    )

    val just_body_deep = just_body

    val quotes_signs_5 = Content(
            TestEmails.readFile(File(
                    "${path}5_quotes_plus_signs${File.separator}body.txt"
            )),
            Content(
                    TestEmails.readFile(File(
                            "${path}5_quotes_plus_signs${File.separator}quote.txt"
                    )),
                    null,
                    null
            ),
            TestEmails.readFile(File(
                    "${path}5_quotes_plus_signs${File.separator}signature.txt"
            ))
    )

    val quotes_signs_5_deep = Content(
            TestEmails.readFile(File(
                    "${path}5_quotes_plus_signs${File.separator}body0.txt"
            )),
            Content(
                    TestEmails.readFile(File(
                            "${path}5_quotes_plus_signs${File.separator}body1.txt"
                    )),
                    Content(
                            TestEmails.readFile(File(
                                    "${path}5_quotes_plus_signs${File.separator}body2.txt"
                            )),
                            Content(
                                    TestEmails.readFile(File(
                                            "${path}5_quotes_plus_signs${File.separator}body3.txt"
                                    )),
                                    Content(
                                            TestEmails.readFile(File(
                                                    "${path}5_quotes_plus_signs${File.separator}body4.txt"
                                            )),
                                            Content(
                                                    TestEmails.readFile(File(
                                                            "${path}5_quotes_plus_signs${File.separator}body5.txt"
                                                    )),
                                                    null,
                                                    null
                                            ),
                                            TestEmails.readFile(File(
                                                    "${path}5_quotes_plus_signs${File.separator}signature4.txt"
                                            ))
                                    ),
                                    TestEmails.readFile(File(
                                            "${path}5_quotes_plus_signs${File.separator}signature3.txt"
                                    ))
                            ),
                            null
                    ),
                    null
            ),
            TestEmails.readFile(File(
                    "${path}5_quotes_plus_signs${File.separator}signature0.txt"
            ))
    )
}