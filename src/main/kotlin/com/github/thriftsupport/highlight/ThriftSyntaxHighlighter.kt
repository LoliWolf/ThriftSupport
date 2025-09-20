package com.github.thriftsupport.highlight

import com.github.thriftsupport.lexer.ThriftLexer
import com.github.thriftsupport.parser.ThriftTokenSets
import com.github.thriftsupport.parser.ThriftTokenTypes
import com.intellij.lexer.Lexer
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ThriftSyntaxHighlighter : SyntaxHighlighterBase() {
    override fun getHighlightingLexer(): Lexer = ThriftLexer()

    override fun getTokenHighlights(tokenType: IElementType?): Array<TextAttributesKey> = when {
        tokenType == null -> emptyArray()
        tokenType == TokenType.BAD_CHARACTER -> pack(ThriftHighlighterColors.BAD_CHARACTER)
        tokenType == ThriftTokenTypes.DOC_COMMENT -> pack(ThriftHighlighterColors.DOC_COMMENT)
        ThriftTokenTypes.COMMENTS.contains(tokenType) -> pack(ThriftHighlighterColors.COMMENT)
        tokenType == ThriftTokenTypes.STRING_LITERAL -> pack(ThriftHighlighterColors.STRING)
        tokenType == ThriftTokenTypes.INTEGER_LITERAL || tokenType == ThriftTokenTypes.FLOAT_LITERAL -> pack(ThriftHighlighterColors.NUMBER)
        tokenType == ThriftTokenTypes.BOOLEAN_LITERAL -> pack(ThriftHighlighterColors.BOOLEAN)
        ThriftTokenSets.KEYWORDS.contains(tokenType) -> pack(ThriftHighlighterColors.KEYWORD)
        ThriftTokenSets.BUILTIN_TYPES.contains(tokenType) -> pack(ThriftHighlighterColors.BUILTIN_TYPE)
        tokenType == ThriftTokenTypes.AT_SIGN -> pack(ThriftHighlighterColors.ANNOTATION)
        tokenType == ThriftTokenTypes.IDENTIFIER -> pack(ThriftHighlighterColors.IDENTIFIER)
        else -> emptyArray()
    }
}
