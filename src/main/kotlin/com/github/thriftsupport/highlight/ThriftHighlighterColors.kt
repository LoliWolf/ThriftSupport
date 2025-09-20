package com.github.thriftsupport.highlight

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey

object ThriftHighlighterColors {
    val KEYWORD: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_KEYWORD",
        DefaultLanguageHighlighterColors.KEYWORD
    )
    val BUILTIN_TYPE: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_BUILTIN_TYPE",
        DefaultLanguageHighlighterColors.KEYWORD
    )
    val IDENTIFIER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_IDENTIFIER",
        DefaultLanguageHighlighterColors.IDENTIFIER
    )
    val STRING: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_STRING",
        DefaultLanguageHighlighterColors.STRING
    )
    val NUMBER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_NUMBER",
        DefaultLanguageHighlighterColors.NUMBER
    )
    val BOOLEAN: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_BOOLEAN",
        DefaultLanguageHighlighterColors.KEYWORD
    )
    val COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_COMMENT",
        DefaultLanguageHighlighterColors.LINE_COMMENT
    )
    val DOC_COMMENT: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_DOC_COMMENT",
        DefaultLanguageHighlighterColors.DOC_COMMENT
    )
    val ANNOTATION: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_ANNOTATION",
        DefaultLanguageHighlighterColors.METADATA
    )
    val BAD_CHARACTER: TextAttributesKey = TextAttributesKey.createTextAttributesKey(
        "THRIFT_BAD_CHARACTER",
        DefaultLanguageHighlighterColors.INVALID_STRING_ESCAPE
    )
}
