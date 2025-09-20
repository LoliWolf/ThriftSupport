package com.github.thriftsupport.lexer

import com.github.thriftsupport.parser.ThriftTokenTypes
import com.intellij.lexer.LexerBase
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import kotlin.math.max

class ThriftLexer : LexerBase() {
    private var buffer: CharSequence = ""
    private var startOffset: Int = 0
    private var endOffset: Int = 0
    private var tokenStart: Int = 0
    private var tokenEnd: Int = 0
    private var tokenType: IElementType? = null

    override fun getState(): Int = 0

    override fun getTokenType(): IElementType? = tokenType

    override fun getTokenStart(): Int = tokenStart

    override fun getTokenEnd(): Int = tokenEnd

    override fun getBufferSequence(): CharSequence = buffer

    override fun getBufferEnd(): Int = endOffset

    override fun start(buffer: CharSequence, startOffset: Int, endOffset: Int, initialState: Int) {
        this.buffer = buffer
        this.startOffset = startOffset
        this.endOffset = endOffset
        this.tokenStart = startOffset
        this.tokenEnd = startOffset
        this.tokenType = null
        advance()
    }

    override fun advance() {
        tokenStart = tokenEnd
        if (tokenStart >= endOffset) {
            tokenType = null
            return
        }

        val currentChar = buffer[tokenStart]
        when {
            currentChar.isWhitespace() -> lexWhitespace()
            currentChar == '/' -> lexSlash()
            currentChar == '#' -> lexLineComment()
            currentChar == '"' || currentChar == '\'' -> lexString()
            currentChar == '-' && tokenStart + 1 < endOffset && buffer[tokenStart + 1].isDigit() -> lexNumber(true)
            currentChar.isDigit() -> lexNumber(false)
            isIdentifierStart(currentChar) -> lexIdentifier()
            else -> lexSymbol()
        }
    }

    private fun lexWhitespace() {
        var index = tokenStart + 1
        while (index < endOffset && buffer[index].isWhitespace()) {
            index++
        }
        tokenType = TokenType.WHITE_SPACE
        tokenEnd = index
    }

    private fun lexSlash() {
        val next = if (tokenStart + 1 < endOffset) buffer[tokenStart + 1] else '\u0000'
        if (next == '/') {
            lexLineComment()
            return
        }
        if (next == '*') {
            lexBlockComment()
            return
        }
        tokenType = ThriftTokenTypes.SLASH
        tokenEnd = tokenStart + 1
    }

    private fun lexLineComment() {
        var index = tokenStart + 1
        if (buffer[tokenStart] == '/' && index < endOffset && buffer[index] == '/') {
            index++
        }
        while (index < endOffset) {
            val c = buffer[index]
            if (c == '\r' || c == '\n') break
            index++
        }
        tokenType = ThriftTokenTypes.LINE_COMMENT
        tokenEnd = index
    }

    private fun lexBlockComment() {
        val isDocComment = tokenStart + 2 < endOffset && buffer[tokenStart + 2] == '*'
        var index = tokenStart + 2
        while (index < endOffset - 1) {
            if (buffer[index] == '*' && buffer[index + 1] == '/') {
                index += 2
                tokenType = if (isDocComment) ThriftTokenTypes.DOC_COMMENT else ThriftTokenTypes.BLOCK_COMMENT
                tokenEnd = index
                return
            }
            index++
        }
        tokenType = if (isDocComment) ThriftTokenTypes.DOC_COMMENT else ThriftTokenTypes.BLOCK_COMMENT
        tokenEnd = endOffset
    }

    private fun lexString() {
        val quote = buffer[tokenStart]
        var index = tokenStart + 1
        var escaped = false
        while (index < endOffset) {
            val c = buffer[index]
            if (escaped) {
                escaped = false
            } else if (c == '\\') {
                escaped = true
            } else if (c == quote) {
                index++
                tokenType = ThriftTokenTypes.STRING_LITERAL
                tokenEnd = index
                return
            }
            index++
        }
        tokenType = ThriftTokenTypes.STRING_LITERAL
        tokenEnd = endOffset
    }

    private fun lexNumber(signed: Boolean) {
        var index = tokenStart + if (signed) 1 else 0
        var hasDot = false
        var hasExponent = false
        while (index < endOffset) {
            val c = buffer[index]
            when {
                c.isDigit() -> index++
                c == '.' && !hasDot && !hasExponent -> {
                    hasDot = true
                    index++
                }
                (c == 'e' || c == 'E') && !hasExponent -> {
                    hasExponent = true
                    index++
                    if (index < endOffset && (buffer[index] == '+' || buffer[index] == '-')) {
                        index++
                    }
                }
                else -> break
            }
        }
        tokenType = if (hasDot || hasExponent) ThriftTokenTypes.FLOAT_LITERAL else ThriftTokenTypes.INTEGER_LITERAL
        tokenEnd = max(index, tokenStart + 1)
    }

    private fun lexIdentifier() {
        var index = tokenStart + 1
        while (index < endOffset && isIdentifierPart(buffer[index])) {
            index++
        }
        val text = buffer.subSequence(tokenStart, index).toString()
        val keywordType = ThriftTokenTypes.KEYWORD_LOOKUP[text]
        tokenType = when (keywordType) {
            ThriftTokenTypes.KW_TRUE, ThriftTokenTypes.KW_FALSE -> ThriftTokenTypes.BOOLEAN_LITERAL
            null -> ThriftTokenTypes.IDENTIFIER
            else -> keywordType
        }
        tokenEnd = index
    }

    private fun lexSymbol() {
        val c = buffer[tokenStart]
        tokenType = when (c) {
            '{' -> ThriftTokenTypes.LBRACE
            '}' -> ThriftTokenTypes.RBRACE
            '(' -> ThriftTokenTypes.LPAREN
            ')' -> ThriftTokenTypes.RPAREN
            '[' -> ThriftTokenTypes.LBRACKET
            ']' -> ThriftTokenTypes.RBRACKET
            ',' -> ThriftTokenTypes.COMMA
            ';' -> ThriftTokenTypes.SEMICOLON
            ':' -> ThriftTokenTypes.COLON
            '=' -> ThriftTokenTypes.EQUALS
            '<' -> ThriftTokenTypes.LT
            '>' -> ThriftTokenTypes.GT
            '@' -> ThriftTokenTypes.AT_SIGN
            '?' -> ThriftTokenTypes.QUESTION
            '*' -> ThriftTokenTypes.STAR
            '+' -> ThriftTokenTypes.PLUS
            '-' -> ThriftTokenTypes.MINUS
            '%' -> ThriftTokenTypes.PERCENT
            '|' -> ThriftTokenTypes.PIPE
            '&' -> ThriftTokenTypes.AMPERSAND
            '^' -> ThriftTokenTypes.CARET
            '.' -> ThriftTokenTypes.DOT
            else -> TokenType.BAD_CHARACTER
        }
        tokenEnd = tokenStart + 1
    }

    private fun Char.isWhitespace(): Boolean = Character.isWhitespace(this)

    private fun isIdentifierStart(c: Char): Boolean = c == '_' || c == '$' || c.isLetter()

    private fun isIdentifierPart(c: Char): Boolean = c == '_' || c == '$' || c == '.' || c.isLetterOrDigit()
}
