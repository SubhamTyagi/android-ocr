package io.github.subhamtyagi.ocr.data.model

//JavaChineseModifier State class
data class JCMState(
    var preserveInterWordSpaces: String = "0",
    var chopEnable: String = "T",
    var newStateCost: String = "F",
    var segmentSegCostRating: String = "F",
    var newSegSearch: String = "0",
    var languageNgramOn: String = "F",
    var textortForceMakePropWords: String = "F",
    var edgeMaxChildrenPerOutline: String = "40"
)