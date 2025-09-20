package com.github.thriftsupport.highlight

import com.github.thriftsupport.lang.ThriftIcons
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import javax.swing.Icon

class ThriftColorSettingsPage : ColorSettingsPage {
    override fun getDisplayName(): String = "Thrift"

    override fun getIcon(): Icon = ThriftIcons.FILE

    override fun getHighlighter(): SyntaxHighlighter = ThriftSyntaxHighlighter()

    override fun getDemoText(): String = """
        namespace java com.example.demo

        include "shared.thrift"

        const i32 DEFAULT_TIMEOUT = 250

        @doc("Sample request payload")
        struct Request {
          1: required string traceId = "trace-1"
          2: optional map<string, string> headers
        }

        enum ResultCode {
          OK = 0,
          ERROR = 1
        }

        service DemoService extends shared.BaseService {
          i32 ping(1: string name) throws (1: shared.ApplicationException ex)
        }
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey>? = null

    override fun getAttributeDescriptors(): Array<AttributesDescriptor> = arrayOf(
        AttributesDescriptor("Keyword", ThriftHighlighterColors.KEYWORD),
        AttributesDescriptor("Builtin type", ThriftHighlighterColors.BUILTIN_TYPE),
        AttributesDescriptor("Identifier", ThriftHighlighterColors.IDENTIFIER),
        AttributesDescriptor("String", ThriftHighlighterColors.STRING),
        AttributesDescriptor("Number", ThriftHighlighterColors.NUMBER),
        AttributesDescriptor("Boolean", ThriftHighlighterColors.BOOLEAN),
        AttributesDescriptor("Comment", ThriftHighlighterColors.COMMENT),
        AttributesDescriptor("Doc comment", ThriftHighlighterColors.DOC_COMMENT),
        AttributesDescriptor("Annotation", ThriftHighlighterColors.ANNOTATION),
        AttributesDescriptor("Bad character", ThriftHighlighterColors.BAD_CHARACTER)
    )

    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY
}
