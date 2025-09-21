package com.github.thriftsupport.format

import com.github.thriftsupport.lang.ThriftLanguage
import com.github.thriftsupport.parser.ThriftTokenSets
import com.github.thriftsupport.parser.ThriftTokenTypes
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.formatting.WrapType
import com.intellij.psi.codeStyle.CodeStyleSettings

class ThriftFormattingModelBuilder : FormattingModelBuilder {
    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val file = formattingContext.containingFile
        val rootNode = formattingContext.node
        val settings = formattingContext.codeStyleSettings
        val spacingBuilder = createSpacingBuilder(settings)

        val block = ThriftBlock(rootNode, Wrap.createWrap(WrapType.NONE, false), null, spacingBuilder)
        return FormattingModelProvider.createFormattingModelForPsiFile(file, block, settings)
    }

    private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder {
        val builder = SpacingBuilder(settings, ThriftLanguage)
            // blank lines between top-level declarations
            .before(ThriftTokenTypes.KW_NAMESPACE).blankLines(1)
            .before(ThriftTokenTypes.KW_INCLUDE).blankLines(1)
            .before(ThriftTokenTypes.KW_CONST).blankLines(1)
            .before(ThriftTokenTypes.KW_ENUM).blankLines(1)
            .before(ThriftTokenTypes.KW_STRUCT).blankLines(1)
            .before(ThriftTokenTypes.KW_EXCEPTION).blankLines(1)
            .before(ThriftTokenTypes.KW_SERVICE).blankLines(1)

            // punctuation
            .around(ThriftTokenTypes.COMMA).spacing(0, 1, 0, true, 0)
            .around(ThriftTokenTypes.SEMICOLON).spacing(0, 1, 0, true, 0)
            .before(ThriftTokenTypes.COLON).spacing(0, 0, 0, true, 0)
            .after(ThriftTokenTypes.COLON).spacing(1, Int.MAX_VALUE, 0, true, 0)
            .around(ThriftTokenTypes.EQUALS).spacing(1, Int.MAX_VALUE, 0, true, 0)

            // parentheses and braces
            .after(ThriftTokenTypes.LPAREN).spacing(0, 0, 0, true, 0)
            .before(ThriftTokenTypes.RPAREN).spacing(0, 0, 0, true, 0)
            .before(ThriftTokenTypes.LPAREN).spacing(0, 0, 0, true, 0)
            .before(ThriftTokenTypes.LBRACE).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.LBRACE).lineBreakInCode()
            .before(ThriftTokenTypes.RBRACE).lineBreakInCode()
            .after(ThriftTokenTypes.RBRACE).lineBreakInCode()

            // keywords following other tokens
            .after(ThriftTokenTypes.KW_NAMESPACE).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_INCLUDE).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_TYPEDEF).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_STRUCT).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_UNION).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_EXCEPTION).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_ENUM).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_SERVICE).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_EXTENDS).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_OPTIONAL).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_REQUIRED).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_ONEWAY).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_ASYNC).spacing(1, 1, 0, true, 0)
            .after(ThriftTokenTypes.KW_VOID).spacing(1, 1, 0, true, 0)

            // throws clause
            .between(ThriftTokenTypes.RPAREN, ThriftTokenTypes.KW_THROWS).spacing(1, 1, 0, true, 0)
            .between(ThriftTokenTypes.KW_THROWS, ThriftTokenTypes.LPAREN).spacing(0, 0, 0, true, 0)

            // throws parameters
            .between(ThriftTokenTypes.INTEGER_LITERAL, ThriftTokenTypes.COLON).spacing(0, 0, 0, true, 0)
            .between(ThriftTokenTypes.COLON, ThriftTokenTypes.IDENTIFIER).spacing(1, 1, 0, true, 0)
            .between(ThriftTokenTypes.COMMA, ThriftTokenTypes.IDENTIFIER).spacing(1, 1, 0, true, 0)

            // extends keyword
            .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.KW_EXTENDS).spacing(1, 1, 0, true, 0)
            .between(ThriftTokenTypes.KW_EXTENDS, ThriftTokenTypes.IDENTIFIER).spacing(1, 1, 0, true, 0)

            // comments
            .before(ThriftTokenTypes.LINE_COMMENT).spacing(1, 1, 0, true, 0)
            .before(ThriftTokenTypes.BLOCK_COMMENT).spacing(1, 1, 0, true, 0)

            // generics and dotted identifiers
            .around(ThriftTokenTypes.DOT).spacing(0, 0, 0, true, 0)
            .after(ThriftTokenTypes.LT).spacing(0, 0, 0, true, 0)
            .before(ThriftTokenTypes.GT).spacing(0, 0, 0, true, 0)
            .between(ThriftTokenTypes.LT, ThriftTokenTypes.IDENTIFIER).spacing(0, 0, 0, true, 0)
            .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.GT).spacing(0, 0, 0, true, 0)
            .between(ThriftTokenTypes.COMMA, ThriftTokenTypes.IDENTIFIER).spacing(0, 1, 0, true, 0)
            .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.LT).spacing(0, 0, 0, true, 0)

            // identifier followed by block
            .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.LBRACE).spacing(1, 1, 0, true, 0)

        listOf(
            ThriftTokenTypes.KW_LIST,
            ThriftTokenTypes.KW_SET,
            ThriftTokenTypes.KW_MAP
        ).forEach { keyword ->
            builder.between(keyword, ThriftTokenTypes.LT).spacing(0, 0, 0, true, 0)
        }

        ThriftTokenSets.BUILTIN_TYPES.types.forEach { builtin ->
            builder.between(ThriftTokenTypes.LT, builtin).spacing(0, 0, 0, true, 0)
            builder.between(builtin, ThriftTokenTypes.GT).spacing(0, 0, 0, true, 0)
            builder.between(ThriftTokenTypes.COMMA, builtin).spacing(0, 0, 0, true, 0)
        }

        return builder
    }
}
