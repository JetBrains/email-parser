package practice.email.parser

import java.io.File

object TestContents {
    val simple_sig = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\simple_sig\body.txt"""
            )),
            null,
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\simple_sig\signature.txt"""
            ))
    )

    val simple_sig_deep = simple_sig

    val simple_sig_2 = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\simple_sig_2\body.txt"""
            )),
            null,
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\simple_sig_2\signature.txt"""
            ))
    )

    val simple_sig_2_deep = simple_sig_2

    val simple_quote_koi8 = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\simple_quote_koi8\body.txt"""
            )),
            Content(
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\simple_quote_koi8\quote.txt"""
                    )),
                    null,
                    null
            ),
            null
    )

    val simple_quote_koi8_deep = simple_quote_koi8

    val quote_plus_inner_sig = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\quote_plus_inner_sig\body.txt"""
            )),
            Content(
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\quote_plus_inner_sig\quote.txt"""
                    )),
                    null,
                    null
            ),
            null
    )

    val quote_plus_inner_sig_deep = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\quote_plus_inner_sig\body.txt"""
            )),
            Content(
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\quote_plus_inner_sig\body1.txt"""
                    )),
                    null,
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\quote_plus_inner_sig\signature1.txt"""
                    ))
            ),
            null
    )

    val only_nested_quotes = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\only_nested_quotes\body.txt"""
            )),
            Content(
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\only_nested_quotes\quote.txt"""
                    )),
                    null,
                    null
            ),
            null
    )

    val only_nested_quotes_deep = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\only_nested_quotes\body0.txt"""
            )),
            Content(
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\only_nested_quotes\body1.txt"""
                    )),
                    Content(
                            TestEmails.readFile(File(
                                    """.\src\test\resources\testFiles\content\only_nested_quotes\body2.txt"""
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
                    """.\src\test\resources\testFiles\content\just_body\body.txt"""
            )),
            null,
            null
    )

    val just_body_deep = just_body

    val quotes_signs_5 = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\5_quotes_plus_signs\body.txt"""
            )),
            Content(
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\5_quotes_plus_signs\quote.txt"""
                    )),
                    null,
                    null
            ),
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\5_quotes_plus_signs\signature.txt"""
            ))
    )

    val quotes_signs_5_deep = Content(
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\5_quotes_plus_signs\body0.txt"""
            )),
            Content(
                    TestEmails.readFile(File(
                            """.\src\test\resources\testFiles\content\5_quotes_plus_signs\body1.txt"""
                    )),
                    Content(
                            TestEmails.readFile(File(
                                    """.\src\test\resources\testFiles\content\5_quotes_plus_signs\body2.txt"""
                            )),
                            Content(
                                    TestEmails.readFile(File(
                                            """.\src\test\resources\testFiles\content\5_quotes_plus_signs\body3.txt"""
                                    )),
                                    Content(
                                            TestEmails.readFile(File(
                                                    """.\src\test\resources\testFiles\content\5_quotes_plus_signs\body4.txt"""
                                            )),
                                            Content(
                                                    TestEmails.readFile(File(
                                                            """.\src\test\resources\testFiles\content\5_quotes_plus_signs\body5.txt"""
                                                    )),
                                                    null,
                                                    null
                                            ),
                                            TestEmails.readFile(File(
                                                    """.\src\test\resources\testFiles\content\5_quotes_plus_signs\signature4.txt"""
                                            ))
                                    ),
                                    TestEmails.readFile(File(
                                            """.\src\test\resources\testFiles\content\5_quotes_plus_signs\signature3.txt"""
                                    ))
                            ),
                            null
                    ),
                    null
            ),
            TestEmails.readFile(File(
                    """.\src\test\resources\testFiles\content\5_quotes_plus_signs\signature0.txt"""
            ))
    )
}