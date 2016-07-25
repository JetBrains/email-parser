package practice.email.parser

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

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

        given("header with separate open angle brackets") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> On Mar 6, 2015., at 06:21, XXX YYY < xxx@zzz.com <
> mailto:xxx@zzz.com>> wrote:
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = "On Mar 6 2015 at 06:21 XXX YYY <xxx@zzz.com <mailto:xxx@zzz.com>> wrote:"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should not have separate open angle brackets") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with separate double open angle brackets") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> On Mar 6, 2015., at 06:21, XXX YYY << xxx@zzz.com <<
> mailto:xxx@zzz.com>> wrote:
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = "On Mar 6 2015 at 06:21 XXX YYY <<xxx@zzz.com <<mailto:xxx@zzz.com>> wrote:"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should not have separate open angle brackets") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with last open angle bracket") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> On Mar 6, 2015., at 06:21, XXX YYY <<
> mailto:xxx@zzz.com>> <
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = "On Mar 6 2015 at 06:21 XXX YYY <<mailto:xxx@zzz.com>> <"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should preserve it") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with separate double close angle bracket") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> On Mar 6, 2015., at 06:21, XXX YYY < xxx@zzz.com <
> mailto:xxx@zzz.com >> wrote:
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = "On Mar 6 2015 at 06:21 XXX YYY <xxx@zzz.com <mailto:xxx@zzz.com>> wrote:"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should not have separate angle brackets") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with separate close angle brackets") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> On Mar 6, 2015., at 06:21, XXX YYY << xxx@zzz.com
> > < mailto:xxx@zzz.com >> wrote:
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = "On Mar 6 2015 at 06:21 XXX YYY <<xxx@zzz.com> <mailto:xxx@zzz.com>> wrote:"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should not have separate angle brackets") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with starting close angle bracket") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> >> On Mar 6, 2015., at 06:21, XXX YYY <<
> mailto:xxx@zzz.com>> <
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = ">> On Mar 6 2015 at 06:21 XXX YYY <<mailto:xxx@zzz.com>> <"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should preserve it") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with ending close angle bracket with colon") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> On Mar 6, 2015., at 06:21, XXX YYY <
> mailto:xxx@zzz.com >:
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = "On Mar 6 2015 at 06:21 XXX YYY <mailto:xxx@zzz.com>:"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should join with previous word") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with open angle bracket at the line") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> >> On Mar 6, 2015., at 06:21, XXX YYY
> <<
> mailto:xxx@zzz.com>> <
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = ">> On Mar 6 2015 at 06:21 XXX YYY <<mailto:xxx@zzz.com>> <"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should join with the following word") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with close angle bracket at the line") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> >> On Mar 6, 2015., at 06:21, XXX YYY
> >
> << mailto:xxx@zzz.com >> <
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = ">> On Mar 6 2015 at 06:21 XXX YYY> <<mailto:xxx@zzz.com>> <"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should join with previous word") {
                    assertEquals(expected, actual)
                }
            }
        }

        given("header with separate colons") {
            val header: List<String>? = QuotesHeaderSuggestions.getQuoteHeader(
                    """
some text

> : On Mar 6, 2015., at 06:21, XXX : YYY
> :
> << mailto:xxx@zzz.com >> :
>
> text text,
> text text text text text text text text text text text xxx@zzz.com <mailto:xxx@zzz.com>."""
            )
            on("preprocessing") {
                val expected = ": On Mar 6 2015 at 06:21 XXX: YYY: <<mailto:xxx@zzz.com>>:"
                val actual   = preprocess(header ?: throw NullPointerException())
                it("should join with previous word") {
                    assertEquals(expected, actual)
                }
            }
        }

    }
}