package com.github.thriftsupport.parser

import com.github.thriftsupport.lang.ThriftFile
import com.github.thriftsupport.lang.ThriftLanguage
import com.github.thriftsupport.lexer.ThriftLexer
import com.intellij.lang.ASTNode
import com.intellij.lang.ParserDefinition
import com.intellij.lang.PsiParser
import com.intellij.openapi.project.Project
import com.intellij.psi.FileViewProvider
import com.intellij.psi.PsiFile
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IFileElementType
import com.intellij.psi.tree.TokenSet

class ThriftParserDefinition : ParserDefinition {
    override fun createLexer(project: Project) = ThriftLexer()

    override fun createParser(project: Project): PsiParser = ThriftParser()

    override fun getFileNodeType(): IFileElementType = FILE

    override fun getCommentTokens(): TokenSet = ThriftTokenTypes.COMMENTS

    override fun getWhitespaceTokens(): TokenSet = TokenSet.create(TokenType.WHITE_SPACE)

    override fun getStringLiteralElements(): TokenSet = ThriftTokenTypes.STRINGS

    override fun createElement(node: ASTNode) = ThriftElementTypes.createElement(node)

    override fun createFile(viewProvider: FileViewProvider): PsiFile = ThriftFile(viewProvider)

    override fun spaceExistenceTypeBetweenTokens(left: ASTNode, right: ASTNode) = ParserDefinition.SpaceRequirements.MAY

    companion object {
        val FILE = IFileElementType(ThriftLanguage)
    }
}
