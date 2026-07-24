package io.github.subhamtyagi.ocr.data.model

//JavaChineseModifier State class
data class JCMState(
    val preserveInterWordSpaces: String = "0",
    val chopEnable: String = "T",
    val languageNgramOn: String = "F",
    val textortForceMakePropWords: String = "F",
    val edgeMaxChildrenPerOutline: String = "40"
) {

    fun getParameters(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["preserve_interword_spaces"] = preserveInterWordSpaces
        map["chop_enable"] = chopEnable
        map["textord_force_make_prop_words"] = textortForceMakePropWords
        map["language_model_ngram_on"] = languageNgramOn
        map["edges_max_children_per_outline"] = edgeMaxChildrenPerOutline

        return map
    }
}
