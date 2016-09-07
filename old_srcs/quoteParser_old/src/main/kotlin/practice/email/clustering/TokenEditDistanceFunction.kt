package practice.email.clustering

import practice.email.parser.*
import weka.core.Instances

class TokenEditDistanceFunction : AbstractStringDistanceFunction {
    constructor() {
    }

    constructor(data: Instances) : super(data) {
    }

    override fun globalInfo(): String? {
        return null
    }

    override fun getRevision(): String? {
        return null
    }

    override fun stringDistance(stringA: String, stringB: String): Double {
        return getEditDistance(stringA, stringB).toDouble()
    }
}
