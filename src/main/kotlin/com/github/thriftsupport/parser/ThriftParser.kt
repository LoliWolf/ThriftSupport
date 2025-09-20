package com.github.thriftsupport.parser

import com.intellij.lang.ASTNode
import com.intellij.lang.PsiBuilder
import com.intellij.lang.PsiParser
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType

class ThriftParser : PsiParser {
    override fun parse(root: IElementType, builder: PsiBuilder): ASTNode {
        val rootMarker = builder.mark()
        builder.skipTrivialTokens()
        while (!builder.eof()) {
            if (!parseTopLevel(builder)) {
                builder.advanceLexer()
            }
            builder.skipTrivialTokens()
        }
        rootMarker.done(root)
        return builder.treeBuilt
    }

    private fun parseTopLevel(builder: PsiBuilder): Boolean = when (builder.tokenType) {
        ThriftTokenTypes.KW_NAMESPACE,
        ThriftTokenTypes.KW_CPP_NAMESPACE,
        ThriftTokenTypes.KW_PY_NAMESPACE,
        ThriftTokenTypes.KW_JAVA_PACKAGE,
        ThriftTokenTypes.KW_GO_PACKAGE -> parseNamespace(builder)
        ThriftTokenTypes.KW_INCLUDE,
        ThriftTokenTypes.KW_CPP_INCLUDE -> parseInclude(builder)
        ThriftTokenTypes.KW_CONST -> parseConst(builder)
        ThriftTokenTypes.KW_TYPEDEF -> parseTypedef(builder)
        ThriftTokenTypes.KW_STRUCT -> parseStructLike(builder, ThriftElementTypes.STRUCT)
        ThriftTokenTypes.KW_UNION -> parseStructLike(builder, ThriftElementTypes.UNION)
        ThriftTokenTypes.KW_EXCEPTION -> parseStructLike(builder, ThriftElementTypes.EXCEPTION)
        ThriftTokenTypes.KW_ENUM -> parseEnum(builder)
        ThriftTokenTypes.KW_SERVICE -> parseService(builder)
        else -> false
    }

    private fun parseNamespace(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        builder.skipTrivialTokens()
        if (builder.tokenType == ThriftTokenTypes.STAR || builder.tokenType == ThriftTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
            builder.skipTrivialTokens()
        }
        when (builder.tokenType) {
            ThriftTokenTypes.STRING_LITERAL -> builder.advanceLexer()
            else -> if (!consumeQualifiedName(builder)) {
                builder.error("Namespace identifier expected")
            }
        }
        builder.skipOptionalSemicolon()
        marker.done(ThriftElementTypes.NAMESPACE)
        return true
    }

    private fun parseInclude(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        builder.skipTrivialTokens()
        if (builder.tokenType == ThriftTokenTypes.STRING_LITERAL) {
            builder.advanceLexer()
        } else {
            builder.error("Include path must be a string literal")
        }
        builder.skipOptionalSemicolon()
        marker.done(ThriftElementTypes.INCLUDE)
        return true
    }

    private fun parseConst(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        builder.skipTrivialTokens()
        parseType(builder)
        builder.skipTrivialTokens()
        if (!consumeIdentifier(builder)) {
            builder.error("Constant name expected")
        }
        builder.skipTrivialTokens()
        if (builder.consumeToken(ThriftTokenTypes.EQUALS)) {
            builder.skipTrivialTokens()
            parseValue(builder)
        } else {
            builder.error("'=' expected in const definition")
        }
        builder.skipOptionalSemicolon()
        marker.done(ThriftElementTypes.CONST)
        return true
    }

    private fun parseTypedef(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        builder.skipTrivialTokens()
        parseType(builder)
        builder.skipTrivialTokens()
        if (!consumeIdentifier(builder)) {
            builder.error("Type alias name expected")
        }
        builder.skipOptionalSemicolon()
        marker.done(ThriftElementTypes.TYPEDEF)
        return true
    }

    private fun parseStructLike(builder: PsiBuilder, elementType: IElementType): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        builder.skipTrivialTokens()
        consumeIdentifier(builder)
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        builder.skipTrivialTokens()
        parseBlock(builder) { parseField(builder) }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        marker.done(elementType)
        return true
    }

    private fun parseEnum(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        builder.skipTrivialTokens()
        consumeIdentifier(builder)
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        builder.skipTrivialTokens()
        parseBlock(builder) { parseEnumField(builder) }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        marker.done(ThriftElementTypes.ENUM)
        return true
    }

    private fun parseService(builder: PsiBuilder): Boolean {
        val marker = builder.mark()
        builder.advanceLexer()
        builder.skipTrivialTokens()
        consumeIdentifier(builder)
        builder.skipTrivialTokens()
        if (builder.consumeToken(ThriftTokenTypes.KW_EXTENDS)) {
            builder.skipTrivialTokens()
            parseType(builder)
        }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        builder.skipTrivialTokens()
        parseBlock(builder) { parseFunction(builder) }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        marker.done(ThriftElementTypes.SERVICE)
        return true
    }

    private fun parseBlock(builder: PsiBuilder, bodyParser: () -> Unit) {
        val block = builder.mark()
        if (!builder.consumeToken(ThriftTokenTypes.LBRACE)) {
            builder.error("'{' expected")
            block.drop()
            return
        }
        builder.skipTrivialTokens()
        while (!builder.eof() && builder.tokenType != ThriftTokenTypes.RBRACE) {
            val before = builder.currentOffset
            bodyParser()
            builder.skipTrivialTokens()
            if (builder.currentOffset == before) {
                builder.advanceLexer()
                builder.skipTrivialTokens()
            }
        }
        if (!builder.consumeToken(ThriftTokenTypes.RBRACE)) {
            builder.error("'}' expected")
        }
        block.done(ThriftElementTypes.BLOCK)
    }

    private fun parseField(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.skipTrivialTokens()
        if (builder.tokenType == ThriftTokenTypes.INTEGER_LITERAL) {
            builder.advanceLexer()
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COLON)) {
                builder.error("':' expected after field id")
            }
        }
        builder.skipTrivialTokens()
        if (builder.tokenType == ThriftTokenTypes.KW_REQUIRED || builder.tokenType == ThriftTokenTypes.KW_OPTIONAL) {
            builder.advanceLexer()
            builder.skipTrivialTokens()
        }
        parseType(builder)
        builder.skipTrivialTokens()
        if (!consumeIdentifier(builder)) {
            builder.error("Field name expected")
        }
        builder.skipTrivialTokens()
        if (builder.consumeToken(ThriftTokenTypes.EQUALS)) {
            builder.skipTrivialTokens()
            parseValue(builder)
        }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        builder.skipOptionalDelimiter()
        marker.done(ThriftElementTypes.FIELD)
    }

    private fun parseEnumField(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.skipTrivialTokens()
        if (!consumeIdentifier(builder)) {
            builder.error("Enum member name expected")
        }
        builder.skipTrivialTokens()
        if (builder.consumeToken(ThriftTokenTypes.EQUALS)) {
            builder.skipTrivialTokens()
            parseValue(builder)
        }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        builder.skipOptionalDelimiter()
        marker.done(ThriftElementTypes.ENUM_FIELD)
    }

    private fun parseFunction(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.skipTrivialTokens()
        if (builder.tokenType == ThriftTokenTypes.KW_ONEWAY || builder.tokenType == ThriftTokenTypes.KW_ASYNC) {
            builder.advanceLexer()
            builder.skipTrivialTokens()
        }
        if (builder.tokenType == ThriftTokenTypes.KW_VOID) {
            builder.advanceLexer()
        } else {
            parseType(builder)
        }
        builder.skipTrivialTokens()
        if (!consumeIdentifier(builder)) {
            builder.error("Function name expected")
        }
        builder.skipTrivialTokens()
        parseParamList(builder)
        builder.skipTrivialTokens()
        if (builder.consumeToken(ThriftTokenTypes.KW_THROWS)) {
            builder.skipTrivialTokens()
            parseThrowsList(builder)
        }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        builder.skipOptionalDelimiter()
        marker.done(ThriftElementTypes.FUNCTION)
    }

    private fun parseParamList(builder: PsiBuilder) {
        if (!builder.consumeToken(ThriftTokenTypes.LPAREN)) {
            builder.error("'(' expected")
            return
        }
        val params = builder.mark()
        builder.skipTrivialTokens()
        while (!builder.eof() && builder.tokenType != ThriftTokenTypes.RPAREN) {
            parseParameter(builder)
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COMMA)) {
                break
            }
            builder.skipTrivialTokens()
        }
        if (!builder.consumeToken(ThriftTokenTypes.RPAREN)) {
            builder.error("')' expected")
        }
        params.done(ThriftElementTypes.PARAM_LIST)
    }

    private fun parseParameter(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.skipTrivialTokens()
        if (builder.tokenType == ThriftTokenTypes.INTEGER_LITERAL) {
            builder.advanceLexer()
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COLON)) {
                builder.error("':' expected after parameter id")
            }
            builder.skipTrivialTokens()
        }
        if (builder.tokenType == ThriftTokenTypes.KW_REQUIRED || builder.tokenType == ThriftTokenTypes.KW_OPTIONAL) {
            builder.advanceLexer()
            builder.skipTrivialTokens()
        }
        parseType(builder)
        builder.skipTrivialTokens()
        if (!consumeIdentifier(builder)) {
            builder.error("Parameter name expected")
        }
        builder.skipTrivialTokens()
        if (builder.consumeToken(ThriftTokenTypes.EQUALS)) {
            builder.skipTrivialTokens()
            parseValue(builder)
        }
        builder.skipTrivialTokens()
        parseAnnotations(builder)
        marker.done(ThriftElementTypes.PARAM)
    }

    private fun parseThrowsList(builder: PsiBuilder) {
        if (!builder.consumeToken(ThriftTokenTypes.LPAREN)) {
            builder.error("'(' expected after throws")
            return
        }
        val marker = builder.mark()
        builder.skipTrivialTokens()
        while (!builder.eof() && builder.tokenType != ThriftTokenTypes.RPAREN) {
            parseParameter(builder)
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COMMA)) {
                break
            }
            builder.skipTrivialTokens()
        }
        if (!builder.consumeToken(ThriftTokenTypes.RPAREN)) {
            builder.error("')' expected")
        }
        marker.done(ThriftElementTypes.THROWS)
    }

    private fun parseType(builder: PsiBuilder) {
        val marker = builder.mark()
        builder.skipTrivialTokens()
        when (builder.tokenType) {
            ThriftTokenTypes.KW_SET,
            ThriftTokenTypes.KW_LIST,
            ThriftTokenTypes.KW_MAP -> parseContainerType(builder)
            ThriftTokenTypes.IDENTIFIER,
            ThriftTokenTypes.KW_BOOL,
            ThriftTokenTypes.KW_BYTE,
            ThriftTokenTypes.KW_I8,
            ThriftTokenTypes.KW_I16,
            ThriftTokenTypes.KW_I32,
            ThriftTokenTypes.KW_I64,
            ThriftTokenTypes.KW_DOUBLE,
            ThriftTokenTypes.KW_STRING,
            ThriftTokenTypes.KW_BINARY,
            ThriftTokenTypes.KW_SLIST,
            ThriftTokenTypes.KW_VOID -> builder.advanceLexer()
            else -> builder.error("Type expected")
        }
        marker.done(ThriftElementTypes.TYPE)
    }

    private fun parseContainerType(builder: PsiBuilder) {
        val keyword = builder.tokenType
        builder.advanceLexer()
        builder.skipTrivialTokens()
        if (!builder.consumeToken(ThriftTokenTypes.LT)) {
            builder.error("'<' expected after container type")
            return
        }
        builder.skipTrivialTokens()
        parseType(builder)
        if (keyword == ThriftTokenTypes.KW_MAP) {
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COMMA)) {
                builder.error("',' expected in map type")
            }
            builder.skipTrivialTokens()
            parseType(builder)
        }
        builder.skipTrivialTokens()
        if (!builder.consumeToken(ThriftTokenTypes.GT)) {
            builder.error("'>' expected to close container type")
        }
    }

    private fun parseValue(builder: PsiBuilder) {
        val marker = builder.mark()
        when (builder.tokenType) {
            ThriftTokenTypes.STRING_LITERAL,
            ThriftTokenTypes.INTEGER_LITERAL,
            ThriftTokenTypes.FLOAT_LITERAL,
            ThriftTokenTypes.BOOLEAN_LITERAL,
            ThriftTokenTypes.IDENTIFIER -> builder.advanceLexer()
            ThriftTokenTypes.LBRACE -> parseInlineMap(builder)
            ThriftTokenTypes.LBRACKET -> parseInlineList(builder)
            else -> {
                builder.error("Value expected")
                builder.advanceLexer()
            }
        }
        marker.done(ThriftElementTypes.VALUE)
    }

    private fun parseInlineList(builder: PsiBuilder) {
        builder.consumeToken(ThriftTokenTypes.LBRACKET)
        builder.skipTrivialTokens()
        while (!builder.eof() && builder.tokenType != ThriftTokenTypes.RBRACKET) {
            parseValue(builder)
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COMMA)) {
                break
            }
            builder.skipTrivialTokens()
        }
        if (!builder.consumeToken(ThriftTokenTypes.RBRACKET)) {
            builder.error("] expected")
        }
    }

    private fun parseInlineMap(builder: PsiBuilder) {
        builder.consumeToken(ThriftTokenTypes.LBRACE)
        builder.skipTrivialTokens()
        while (!builder.eof() && builder.tokenType != ThriftTokenTypes.RBRACE) {
            parseValue(builder)
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COLON)) {
                builder.error("':' expected in map literal")
            }
            builder.skipTrivialTokens()
            parseValue(builder)
            builder.skipTrivialTokens()
            if (!builder.consumeToken(ThriftTokenTypes.COMMA)) {
                break
            }
            builder.skipTrivialTokens()
        }
        if (!builder.consumeToken(ThriftTokenTypes.RBRACE)) {
            builder.error("'}' expected")
        }
    }

    private fun parseAnnotations(builder: PsiBuilder) {
        while (builder.tokenType == ThriftTokenTypes.AT_SIGN) {
            val marker = builder.mark()
            builder.advanceLexer()
            builder.skipTrivialTokens()
            if (!consumeIdentifier(builder)) {
                builder.error("Annotation name expected")
            }
            builder.skipTrivialTokens()
            if (builder.consumeToken(ThriftTokenTypes.LPAREN)) {
                builder.skipTrivialTokens()
                if (builder.tokenType != ThriftTokenTypes.RPAREN) {
                    parseValue(builder)
                    builder.skipTrivialTokens()
                }
                if (!builder.consumeToken(ThriftTokenTypes.RPAREN)) {
                    builder.error("')' expected in annotation")
                }
            }
            builder.skipTrivialTokens()
            marker.done(ThriftElementTypes.ANNOTATION)
        }
    }

    private fun consumeQualifiedName(builder: PsiBuilder): Boolean {
        if (!consumeIdentifier(builder)) {
            return false
        }
        builder.skipTrivialTokens()
        while (builder.tokenType == ThriftTokenTypes.DOT) {
            builder.advanceLexer()
            builder.skipTrivialTokens()
            if (!consumeIdentifier(builder)) {
                builder.error("Identifier expected after '.'")
                break
            }
            builder.skipTrivialTokens()
        }
        return true
    }

    private fun consumeIdentifier(builder: PsiBuilder): Boolean {
        return if (builder.tokenType == ThriftTokenTypes.IDENTIFIER) {
            builder.advanceLexer()
            true
        } else {
            false
        }
    }

    private fun PsiBuilder.consumeToken(expected: IElementType): Boolean {
        return if (tokenType == expected) {
            advanceLexer()
            true
        } else {
            false
        }
    }

    private fun PsiBuilder.skipOptionalDelimiter() {
        if (tokenType == ThriftTokenTypes.SEMICOLON || tokenType == ThriftTokenTypes.COMMA) {
            advanceLexer()
        }
    }

    private fun PsiBuilder.skipOptionalSemicolon() {
        skipTrivialTokens()
        if (tokenType == ThriftTokenTypes.SEMICOLON) {
            advanceLexer()
        }
    }

    private fun PsiBuilder.skipTrivialTokens() {
        while (tokenType == TokenType.WHITE_SPACE || (tokenType != null && ThriftTokenTypes.COMMENTS.contains(tokenType))) {
            advanceLexer()
        }
    }
}
