package com.github.thriftsupport.format

import com.github.thriftsupport.parser.ThriftElementTypes
import com.github.thriftsupport.parser.ThriftTokenTypes
import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.ChildAttributes
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.formatter.common.AbstractBlock

class ThriftBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    private val spacingBuilder: SpacingBuilder
) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): MutableList<Block> {
        val blocks = mutableListOf<Block>()
        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType == TokenType.WHITE_SPACE || child.textLength == 0) {
                child = child.treeNext
                continue
            }
            blocks += ThriftBlock(child, null, null, spacingBuilder)
            child = child.treeNext
        }
        return blocks
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? = spacingBuilder.getSpacing(this, child1, child2)

    override fun getIndent(): Indent? {
        val parentType = myNode.treeParent?.elementType
        val elementType = myNode.elementType
        return when {
            parentType == ThriftElementTypes.BLOCK && elementType !in BRACES -> Indent.getNormalIndent()
            parentType == ThriftElementTypes.PARAM_LIST && elementType !in PARENS -> Indent.getNormalIndent()
            parentType == ThriftElementTypes.THROWS && elementType !in PARENS -> Indent.getNormalIndent()
            else -> Indent.getNoneIndent()
        }
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes = when (myNode.elementType) {
        ThriftElementTypes.BLOCK,
        ThriftElementTypes.PARAM_LIST,
        ThriftElementTypes.THROWS -> ChildAttributes(Indent.getNormalIndent(), null)
        else -> ChildAttributes(Indent.getNoneIndent(), null)
    }

    override fun isLeaf(): Boolean = myNode.firstChildNode == null

    companion object {
        private val BRACES = setOf(ThriftTokenTypes.LBRACE, ThriftTokenTypes.RBRACE)
        private val PARENS = setOf(ThriftTokenTypes.LPAREN, ThriftTokenTypes.RPAREN)
    }
}
