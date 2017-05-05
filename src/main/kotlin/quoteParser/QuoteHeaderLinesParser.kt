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

import quoteParser.features.*

internal class QuoteHeaderLinesParser(val headerLinesCount: Int = 3,
                             val multiLIneHeaderLinesCount: Int = 6,
                             keyPhrases: List<String> = KeyPhrases.default) {

    // For single line headers
    private val featureSet: Array<AbstractQuoteFeature>
    private val sufficientFeatureCount: Int
    private val maxFeatureCount: Int
    private var foundFeatureMap: MutableMap<String, Int> = mutableMapOf()
    // ------

    // For multi line and FWD headers
    private var middleColonCount = 0
    private val middleColonFeature = MiddleColonFeature()
    private var lineMatchesMiddleColon = false
    private var firstMiddleColonLineIndex = -1
    // ------

    // For phraseFeature
    private var foundPhraseFeature = false
    private var phraseFeatureLineIndex = -1
    private val phraseFeature: PhraseFeature
    // ------

    private var lines: List<String> = listOf()

    init {
        this.phraseFeature = PhraseFeature(keyPhrases)
        this.featureSet = arrayOf(
                DateFeature(),
                TimeFeature(),
                EmailFeature(),
                LastColonFeature()
        )
        this.maxFeatureCount = this.featureSet.size

        this.sufficientFeatureCount = 2
    }

    private fun prepare() {
        this.foundFeatureMap.clear()

        this.middleColonCount = 0
        this.lineMatchesMiddleColon = false
        this.firstMiddleColonLineIndex = -1

        this.foundPhraseFeature = false
        this.phraseFeatureLineIndex = -1
    }

    internal fun parse(lines: List<String>,
              matchedLinesQuoteMark: List<QuoteMarkMatchingResult> =
              QuoteMarkFeature().matchLines(lines)): Pair<Int, Int>? {
        this.prepare()
        this.lines = lines

        this.lines.forEachIndexed { lineIndex, line ->

            this.resetSingleLineFeatures(oldLineIndex = lineIndex - this.headerLinesCount, all = false)

            var anyFeatureMatches = false

            this.featureSet.forEach { feature ->
                if (feature.matches(line)) {
                    this.updateSingleLineFeature(lineIndex, feature)
                    anyFeatureMatches = true
                }
            }

            if (this.middleColonFeature.matches(line)) {
                this.updateMultiLineFeature(lineIndex)
                anyFeatureMatches = true
            }

            if (this.phraseFeature.matches(line)) {
                this.updatePhraseFeature(lineIndex)
            }

            if (anyFeatureMatches) {
                this.resetFeatures(oldLineIndex = lineIndex - this.headerLinesCount)
            } else {
                this.resetFeatures(all = true)
            }

            if (this.headerFound(matchedLinesQuoteMark)) {
                return@parse this.identifyHeader()
            }
        }

        return null
    }

    private fun updatePhraseFeature(lineIndex: Int) {
        this.foundPhraseFeature = true
        this.phraseFeatureLineIndex = lineIndex
    }

    private fun headerFound(matchedLinesQuoteMark: List<QuoteMarkMatchingResult>) =
            when {
                this.foundPhraseFeature -> true
                this.foundFeatureMap.size > this.sufficientFeatureCount -> true
                this.foundFeatureMap.size == this.sufficientFeatureCount
                        && this.checkForSecondaryFeatures(matchedLinesQuoteMark) -> true
                else -> false
            }

    private fun checkForSecondaryFeatures(matchedLinesQuoteMark: List<QuoteMarkMatchingResult>): Boolean {
        val sortedIndexes = this.foundFeatureMap.values.sorted()
        val startIdx = sortedIndexes.first()
        val endIdx = sortedIndexes.last()

        return when {
            checkMiddleColonSuggestion(startIdx, endIdx, this.lines, this.middleColonFeature) -> true
            checkQuoteMarkSuggestion(endIdx, this.lines, matchedLinesQuoteMark) -> true
            else -> false
        }
    }

    private fun updateSingleLineFeature(lineIndex: Int, feature: AbstractQuoteFeature) {
        if (feature is LastColonFeature) {

            // LastColonFeature cannot be the first feature of the quote.
            if (this.foundFeatureMap.size == 0) {
                return
            }

            val sortedIndexes = this.foundFeatureMap.values.sorted()
            val startIdx = sortedIndexes.first()
            val endIdx = sortedIndexes.last()

            // LastColonFeature cannot be in multi line header.
            if (checkMiddleColonSuggestion(startIdx, endIdx, this.lines, this.middleColonFeature)) {
                val headerLinesCount = endIdx - startIdx + 1

                // It seems like MiddleColonFeature instead LastColonFeature.
                if (this.middleColonCount <= headerLinesCount) {
                    this.updateMultiLineFeature(lineIndex)

                    // If line contains the real MiddleColonFeature
                    // then we should decrease its count.
                    if (this.middleColonFeature.matches(this.lines[lineIndex])) {
                        this.middleColonCount--
                    }
                }
                return
            }
        }
        this.foundFeatureMap[feature.name] = lineIndex
    }

    private fun updateMultiLineFeature(lineIndex: Int) {
        this.lineMatchesMiddleColon = true
        if (this.middleColonCount == this.multiLIneHeaderLinesCount) {
            this.firstMiddleColonLineIndex++
        } else {
            if (this.middleColonCount == 0) {
                this.firstMiddleColonLineIndex = lineIndex
            }
            this.middleColonCount++
        }
    }

    private fun resetFeatures(oldLineIndex: Int = -1, all: Boolean = false) {
        this.resetSingleLineFeatures(oldLineIndex, all)
        this.resetMultiLineFeatures()
    }

    private fun resetMultiLineFeatures(shouldReset: Boolean = true) {
        if (!this.lineMatchesMiddleColon && shouldReset) {
            this.middleColonCount = 0

        }
        this.lineMatchesMiddleColon = false
    }

    private fun resetSingleLineFeatures(oldLineIndex: Int, all: Boolean) {
        if (all) {
            this.foundFeatureMap.clear()
        } else {
            val temp: MutableMap<String, Int> = mutableMapOf()
            this.foundFeatureMap.filterTo(temp) {
                it.value > oldLineIndex
            }
            this.foundFeatureMap = temp
        }
    }

    private fun identifyHeader(): Pair<Int, Int>? {
        var fromIndex = Int.MAX_VALUE
        var toIndex = Int.MIN_VALUE

        if (this.foundPhraseFeature) {
            fromIndex = this.phraseFeatureLineIndex
            toIndex = this.phraseFeatureLineIndex
        } else {
            this.foundFeatureMap.forEach {
                fromIndex = Math.min(fromIndex, it.value)
                toIndex = Math.max(toIndex, it.value)
            }

            while (this.checkForAllRemainingFeatures(fromIndex, toIndex)) {
                toIndex++
            }

            if (this.checkForMultiLineHeader(fromIndex, toIndex)) {
                fromIndex = this.firstMiddleColonLineIndex
                toIndex = this.firstMiddleColonLineIndex + this.middleColonCount - 1
            }
        }

        return Pair(fromIndex, toIndex)
    }

    // If sufficient count of features had been found in less then HEADER_LINES_COUNT
    // lines try to check others not found features in the following line.
    private fun checkForAllRemainingFeatures(fromIndex: Int, toIndex: Int): Boolean {
        val remainingFeatures = this.featureSet.filter { !this.foundFeatureMap.containsKey(it.name) }
        if (remainingFeatures.isNotEmpty() && // One suggestion is not found.
                toIndex < this.lines.size - 1 && // There is the following line to check.
                toIndex - fromIndex + 1 < this.headerLinesCount) {  // Found suggestions are placed in less than HEADER_LINES_COUNT lines.

            val lineIndex = toIndex + 1

            var anyFeatureMatches = false
            remainingFeatures.forEach { feature ->
                if (feature.matches(this.lines[lineIndex])) {
                    this.updateSingleLineFeature(lineIndex, feature)
                    anyFeatureMatches = true
                }
            }

            if (anyFeatureMatches) {

                if (this.middleColonFeature.matches(this.lines[lineIndex])) {
                    this.updateMultiLineFeature(lineIndex)
                }
                this.resetMultiLineFeatures()

                return@checkForAllRemainingFeatures true
            }
        }
        return false
    }

    // Check if there is a multi line header or FWD
    private fun checkForMultiLineHeader(fromIndex: Int, toIndex: Int): Boolean {
        if (this.middleColonCount >= toIndex - fromIndex + 1) {
            var cnt = this.multiLIneHeaderLinesCount - this.middleColonCount
            var lineIndex = toIndex + 1
            while (cnt > 0 && lineIndex < this.lines.size) {

                if (this.middleColonFeature.matches(this.lines[lineIndex])) {
                    this.updateMultiLineFeature(lineIndex)
                } else {
                    break
                }

                lineIndex++
                cnt--
            }

            return this.firstMiddleColonLineIndex <= fromIndex &&
                    this.firstMiddleColonLineIndex + this.middleColonCount - 1 >= toIndex
        }
        return false
    }
}