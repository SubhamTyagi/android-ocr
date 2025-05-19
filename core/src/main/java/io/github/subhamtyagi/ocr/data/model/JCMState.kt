package io.github.subhamtyagi.ocr.data.model

//JavaChineseModifier State class
data class JCMState(
    var preserveInterWordSpaces: String = "0",
    var chopEnable: String = "T",
    var languageNgramOn: String = "F",
    var textortForceMakePropWords: String = "F",
    var edgeMaxChildrenPerOutline: String = "40"
) {

    fun getParameters(): Map<String, String> {
        var map = mutableMapOf<String, String>()
        map.put("preserve_interword_spaces", preserveInterWordSpaces)
        map.put("chop_enable", chopEnable)
        map.put("textord_force_make_prop_words", textortForceMakePropWords)
        map.put("language_model_ngram_on", languageNgramOn)
        map.put("edges_max_children_per_outline", edgeMaxChildrenPerOutline)

        return map
    }
}