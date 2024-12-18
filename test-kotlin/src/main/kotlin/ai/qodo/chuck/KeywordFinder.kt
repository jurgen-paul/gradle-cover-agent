package ai.qodo.chuck
class KeywordFinder {
    fun findKeywords(text: String, keywords: Set<String>): List<String> {
        val normalizedText = text.lowercase()

        return normalizedText.split(Regex("\\s+"))
            .filter { word ->
                keywords.any { keyword ->
                    word.contains(keyword.lowercase())
                }
            }
    }
}