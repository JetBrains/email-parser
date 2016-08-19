package quoteParser.features

/**
 * Created by Pavel.Zhuk on 16.08.2016.
 */
class DateFeature() : AbstractQuoteFeature() {
    override val name: String
        get() = "DATE"
    override fun getRegex() =
            Regex("(.*[\\s\\xA0])?((([0-3]?[0-9][\\.,]{0,2}[\\s\\xA0]+)(\\S+[\\s\\xA0]+)?(\\S+[\\s\\xA0]+)?(20\\d\\d[\\.,]{0,2}))|" + // full date
                    "((20\\d\\d[\\.,]{0,2}[\\s\\xA0]+)(\\S+[\\s\\xA0]+)?(\\S+[\\s\\xA0]+)?([0-3]?[0-9][\\.,]{0,2}))|" +

                    "((([0-3]?[0-9][/.-][0-3]?[0-9][/.-](?:[0-9]{2})?[0-9]{2})|" + // short date
                    "((?:[0-9]{2})?[0-9]{2}[/.-][0-3]?[0-9][/.-][0-3]?[0-9]))[,\\.]?:?" +
                    "))([\\s\\xA0].*)?")

}
