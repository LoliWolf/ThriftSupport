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
        // 写入日志文件以确认格式化器被调用
        try {
            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
            logFile.appendText("ThriftFormattingModelBuilder: createModel called for file: ${formattingContext.containingFile.name}\n")
            logFile.appendText("Node type: ${formattingContext.node.elementType}\n")
            logFile.appendText("Timestamp: ${System.currentTimeMillis()}\n\n")
        } catch (e: Exception) {
            // 忽略日志错误
        }
        
        val file = formattingContext.containingFile
        val rootNode = formattingContext.node
        val settings = formattingContext.codeStyleSettings
        val spacingBuilder = createSpacingBuilder(settings)
        
        val block = ThriftBlock(rootNode, Wrap.createWrap(WrapType.NONE, false), null, spacingBuilder, emptyMap())
        return FormattingModelProvider.createFormattingModelForPsiFile(file, block, settings)
    }

    private fun createSpacingBuilder(settings: CodeStyleSettings): SpacingBuilder = SpacingBuilder(settings, ThriftLanguage)
        // 空行处理 - 只在定义前添加空行，不影响关键字与标识符的间距
        .before(ThriftTokenTypes.KW_NAMESPACE).blankLines(1)
        .before(ThriftTokenTypes.KW_CONST).blankLines(1)
        .before(ThriftTokenTypes.KW_ENUM).blankLines(1)
        .before(ThriftTokenTypes.KW_STRUCT).blankLines(1)
        .before(ThriftTokenTypes.KW_EXCEPTION).blankLines(1)
        .before(ThriftTokenTypes.KW_SERVICE).blankLines(1)
        
        // 基本标点符号间距
        .around(ThriftTokenTypes.COMMA).spacing(0, 1, 0, true, 0)
        .around(ThriftTokenTypes.SEMICOLON).spacing(0, 1, 0, true, 0)
        .before(ThriftTokenTypes.COLON).spacing(0, 0, 0, true, 0)
        .after(ThriftTokenTypes.COLON).spacing(1, Int.MAX_VALUE, 0, true, 0)
        // EQUALS的间距：最小1个空格，但允许对齐增加更多空格
        .around(ThriftTokenTypes.EQUALS).spacing(1, Int.MAX_VALUE, 0, true, 0)
        
        // 括号间距
        .after(ThriftTokenTypes.LPAREN).spacing(0, 0, 0, true, 0)
        .before(ThriftTokenTypes.RPAREN).spacing(0, 0, 0, true, 0)
        .before(ThriftTokenTypes.LPAREN).spacing(0, 0, 0, true, 0)
        .before(ThriftTokenTypes.LBRACE).spacing(1, 1, 0, true, 0)
        .after(ThriftTokenTypes.LBRACE).lineBreakInCode()
        .before(ThriftTokenTypes.RBRACE).lineBreakInCode()
        .after(ThriftTokenTypes.RBRACE).lineBreakInCode()
        
        // 特定关键字后的间距（不包括const、类型关键字等需要对齐的）
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
        .after(ThriftTokenTypes.KW_VOID).spacing(1, Int.MAX_VALUE, 0, true, 0)
        
        // 注意：不为const关键字和类型关键字设置固定间距，让对齐控制间距
        
        // throws 关键字的特殊间距
        .between(ThriftTokenTypes.RPAREN, ThriftTokenTypes.KW_THROWS).spacing(1, 1, 0, true, 0)
        .between(ThriftTokenTypes.KW_THROWS, ThriftTokenTypes.LPAREN).spacing(1, 1, 0, true, 0)
        
        // throws 子句内的参数间距
        .between(ThriftTokenTypes.INTEGER_LITERAL, ThriftTokenTypes.COLON).spacing(0, 0, 0, true, 0)
        .between(ThriftTokenTypes.COLON, ThriftTokenTypes.IDENTIFIER).spacing(1, Int.MAX_VALUE, 0, true, 0)
        .between(ThriftTokenTypes.COMMA, ThriftTokenTypes.INTEGER_LITERAL).spacing(1, 1, 0, true, 0)
        
        // extends 关键字间距
        .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.KW_EXTENDS).spacing(1, 1, 0, true, 0)
        .between(ThriftTokenTypes.KW_EXTENDS, ThriftTokenTypes.IDENTIFIER).spacing(1, 1, 0, true, 0)
        
        // 注释前的间距
        .before(ThriftTokenTypes.LINE_COMMENT).spacing(1, 1, 0, true, 0)
        .before(ThriftTokenTypes.BLOCK_COMMENT).spacing(1, 1, 0, true, 0)
        
        // 点号和尖括号不要空格
        .around(ThriftTokenTypes.DOT).spacing(0, 0, 0, true, 0)
        .after(ThriftTokenTypes.LT).spacing(0, 0, 0, true, 0)
        .before(ThriftTokenTypes.GT).spacing(0, 0, 0, true, 0)
        .between(ThriftTokenTypes.LT, ThriftTokenTypes.IDENTIFIER).spacing(0, 0, 0, true, 0)
        .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.GT).spacing(0, 0, 0, true, 0)
        .between(ThriftTokenTypes.COMMA, ThriftTokenTypes.IDENTIFIER).spacing(0, 1, 0, true, 0)
        
        // 修复泛型类型间距：list<string>、map<string,string>、set<i32>
        .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.LT).spacing(0, 0, 0, true, 0)
        
        // 修复关键字与大括号间距：确保只有一个空格
        .between(ThriftTokenTypes.IDENTIFIER, ThriftTokenTypes.LBRACE).spacing(1, 1, 0, true, 0)
}
