package practice.email.parser

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class HeaderPreprocessingSpecs : Spek() {
    init {
        given("header") {
           val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                   """
some text

> Begin forwarded message:
>
> From: X Y <xxx@yyy.it>
> Subject: Re: Place,. your., subject here,
> Date: 15 Apr., 2013,. 21:39:00 GMT+3
> To: zzz@rrr.com
>
> text text text text text text text text text text text text
>
>> On Mar 6, 2015, at 06:21, XXX YYY <xxx@zzz.com <mailto:xxx@zzz.com>> wrote:
>>
>> text text,
>>  text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
           )
            on("preprocessing") {
                val expected = "From: X Y <xxx@yyy.it> Subject: Re: Place your subject here Date: 15 Apr 2013 21:39:00 GMT+3 To: zzz@rrr.com"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should not have starting angle brackets") {
                    assertEquals(expected, actual)
                }
            }
        }
    }
}