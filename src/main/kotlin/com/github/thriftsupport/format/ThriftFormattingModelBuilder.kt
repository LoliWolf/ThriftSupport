package com.github.thriftsupport.format

import com.github.thriftsupport.lang.ThriftLanguage
import com.github.thriftsupport.parser.ThriftTokenTypes
import com.intellij.formatting.Alignment
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
        val block = ThriftBlock(rootNode, Wrap.createWrap(WrapType.NONE, false), Alignment.createAlignment(), spacingBuilder)
        return FormattingModelProvider.createFormattingModelForPsiFile(file, block, settings)
    }

    private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder = SpacingBuilder(settings, ThriftLanguage)
        .before(ThriftTokenTypes.COMMA).spaces(0)
        .after(ThriftTokenTypes.COMMA).spaces(1)
        .before(ThriftTokenTypes.COLON).spaces(0)
        .after(ThriftTokenTypes.COLON).spaces(1)
        .around(ThriftTokenTypes.EQUALS).spaces(1)
        .withinPair(ThriftTokenTypes.LPAREN, ThriftTokenTypes.RPAREN).spaces(0)
        .withinPair(ThriftTokenTypes.LBRACE, ThriftTokenTypes.RBRACE).spaces(1)
        .withinPair(ThriftTokenTypes.LT, ThriftTokenTypes.GT).spaces(0)
        .withinPair(ThriftTokenTypes.LBRACKET, ThriftTokenTypes.RBRACKET).spaces(0)
        .before(ThriftTokenTypes.SEMICOLON).spaces(0)
}
