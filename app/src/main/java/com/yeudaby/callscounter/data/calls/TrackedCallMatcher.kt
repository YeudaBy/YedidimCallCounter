package com.yeudaby.callscounter.data.calls

object TrackedCallMatcher {

    private const val MIN_SUFFIX_MATCH_DIGITS = 7
    private const val ISRAEL_COUNTRY_CODE = "972"

    fun matches(number: String, trackedNumbers: Set<String>): Boolean {
        val candidateDigits = digitsOnly(number)
        if (candidateDigits.isEmpty()) return false

        return trackedNumbers.any { trackedNumber ->
            matchesCandidate(candidateDigits, trackedNumber)
        }
    }

    fun comparableKey(number: String): String {
        val digits = digitsOnly(number)
        if (digits.isEmpty()) return ""

        return buildVariants(digits).minByOrNull(String::length) ?: digits
    }

    private fun matchesCandidate(candidateDigits: String, trackedNumber: String): Boolean {
        val trackedDigits = digitsOnly(trackedNumber)
        if (trackedDigits.isEmpty()) return false

        val candidateVariants = buildVariants(candidateDigits)
        val trackedVariants = buildVariants(trackedDigits)

        return candidateVariants.any { candidateVariant ->
            trackedVariants.any { trackedVariant ->
                candidateVariant == trackedVariant || canSuffixMatch(candidateVariant, trackedVariant)
            }
        }
    }

    private fun buildVariants(digits: String): Set<String> {
        val variants = linkedSetOf(digits)

        if (digits.startsWith(ISRAEL_COUNTRY_CODE) && digits.length > ISRAEL_COUNTRY_CODE.length) {
            variants += "0${digits.removePrefix(ISRAEL_COUNTRY_CODE)}"
        }

        if (digits.startsWith("0") && digits.length > 1) {
            variants += "$ISRAEL_COUNTRY_CODE${digits.drop(1)}"
        }

        return variants
    }

    private fun canSuffixMatch(first: String, second: String): Boolean {
        if (first.length < MIN_SUFFIX_MATCH_DIGITS || second.length < MIN_SUFFIX_MATCH_DIGITS) {
            return false
        }

        return first.endsWith(second) || second.endsWith(first)
    }

    private fun digitsOnly(value: String): String = value.filter(Char::isDigit)
}
