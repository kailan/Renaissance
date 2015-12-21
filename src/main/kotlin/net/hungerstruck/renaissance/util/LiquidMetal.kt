package net.hungerstruck.renaissance.util

/**
 * Port of LiquidMetal to Kotlin.
 *
 * Created by molenzwiebel on 21-12-15.
 */
class LiquidMetal {
    companion object {
        final val SCORE_NO_MATCH = 0.0
        final val SCORE_MATCH = 1.0
        final val SCORE_TRAILING = 0.8
        final val SCORE_TRAILING_BUT_STARTED = 0.9

        final val SCORE_BUFFER = 0.85

        private fun buildScoreArray(string: String, abbreviation: String): DoubleArray? {
            val scores = DoubleArray(string.length)
            val lower = string.toLowerCase()
            val chars = abbreviation.toLowerCase()

            var lastIndex = -1
            var started = false
            for (i in 0..chars.length - 1) {
                val c = chars[i]
                val index = lower.indexOf(c, lastIndex + 1)

                if (index == -1) return null // signal no match
                if (index == 0) started = true

                if (isNewWord(string, index)) {
                    scores[index - 1] = 1.0
                    scores.fill(SCORE_BUFFER, lastIndex + 1, index - 1)
                } else if (isUpperCase(string, index)) {
                    scores.fill(SCORE_BUFFER, lastIndex + 1, index)
                } else {
                    scores.fill(SCORE_NO_MATCH, lastIndex + 1, index)
                }

                scores[index] = SCORE_MATCH
                lastIndex = index
            }

            val trailingScore = if (started) SCORE_TRAILING_BUT_STARTED else SCORE_TRAILING
            scores.fill(trailingScore, lastIndex + 1, scores.size)
            return scores
        }

        /**
         * Gets closest match from a query against a Collection of data
         * @param query    String to find closest match to
         * @param coll     Collection to search from
         * @param minScore The minimum score needed to have any match at all (defines closest)
         * @return closest match, null if not found
         */
        fun <E> fuzzyMatch(coll: Iterable<E>, query: String, provider: (E) -> String, minScore: Double): E? {
            var closestMatch: E? = null
            var closestMatchScore = 0.0

            for (obj in coll) {
                val itemString = provider(obj)
                if (LiquidMetal.score(itemString, query) > closestMatchScore) {
                    closestMatch = obj
                    closestMatchScore = LiquidMetal.score(itemString, query)
                } else if (LiquidMetal.score(itemString, query) == closestMatchScore) closestMatch = null
            }

            return if (closestMatchScore < minScore) null else closestMatch
        }

        private fun isNewWord(string: String, index: Int) = if (index == 0) false else string[index - 1] == ' ' || string[index - 1] == '\t'
        private fun isUpperCase(string: String, index: Int) = 'A' <= string[index] && string[index] <= 'Z'

        fun score(string: String, abbreviation: String): Double {
            if (abbreviation.length == 0) return SCORE_TRAILING
            if (abbreviation.length > string.length) return SCORE_NO_MATCH

            // complete miss:
            val scores = buildScoreArray(string, abbreviation) ?: return 0.0

            var sum = 0.0
            for (score in scores) {
                sum += score
            }

            return (sum / scores.size)
        }
    }
}