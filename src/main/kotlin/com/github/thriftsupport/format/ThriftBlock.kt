package com.github.thriftsupport.format

import com.github.thriftsupport.parser.ThriftElementTypes
import com.github.thriftsupport.parser.ThriftTokenTypes
import com.github.thriftsupport.parser.ThriftTokenSets
import com.intellij.formatting.Alignment
import com.intellij.formatting.Block
import com.intellij.formatting.ChildAttributes
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.SpacingBuilder
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.psi.TokenType
import com.intellij.psi.tree.IElementType
import com.intellij.psi.formatter.common.AbstractBlock

class ThriftBlock(
    node: ASTNode,
    wrap: Wrap?,
    alignment: Alignment?,
    private val spacingBuilder: SpacingBuilder,
    private val parentAlignments: Map<String, Alignment?>? = null
) : AbstractBlock(node, wrap, alignment) {

    override fun buildChildren(): MutableList<Block> {
        val blocks = mutableListOf<Block>()
        var child = myNode.firstChildNode
        
        // 为当前容器的子元素创建对齐组
        val alignments = createAlignmentsForChildren()
        
        while (child != null) {
            if (child.elementType == TokenType.WHITE_SPACE || child.textLength == 0) {
                child = child.treeNext
                continue
            }
            
            // 确定子元素的对齐方式
            val childAlignment = determineChildAlignment(child, alignments)
            
            // 创建子块，传递对齐组给子元素
            val childBlock = ThriftBlock(
                child,
                null,
                childAlignment,
                spacingBuilder,
                alignments
            )
            
            blocks += childBlock
            child = child.treeNext
        }
        return blocks
    }
    
    private fun createAlignmentsForChildren(): Map<String, Alignment?> {
        val alignments = mutableMapOf<String, Alignment?>()
        val nodeType = myNode.elementType
        
        try {
            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
            logFile.appendText("ThriftBlock: createAlignmentsForChildren for ${nodeType}\n")
            logFile.appendText("ThriftBlock: nodeType == ThriftElementTypes.FILE: ${nodeType == ThriftElementTypes.FILE}\n")
            logFile.appendText("ThriftBlock: ThriftElementTypes.FILE: ${ThriftElementTypes.FILE}\n")
        } catch (e: Exception) {
            // 忽略日志错误
        }
        
        when (nodeType.toString()) {
            "FILE" -> {
                // 为文件级别的常量创建全局对齐
                val constNodes = mutableListOf<ASTNode>()
                var child = myNode.firstChildNode
                while (child != null) {
                    try {
                        val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                        logFile.appendText("ThriftBlock: FILE child type: ${child.elementType}, text: '${child.text}'\n")
                    } catch (e: Exception) {
                        // 忽略日志错误
                    }
                    
                    if (child.elementType == ThriftElementTypes.CONST) {
                        constNodes.add(child)
                        try {
                            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                            logFile.appendText("ThriftBlock: Found CONST node: '${child.text}'\n")
                        } catch (e: Exception) {
                            // 忽略日志错误
                        }
                    }
                    child = child.treeNext
                }
                
                try {
                    val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                    logFile.appendText("ThriftBlock: Found ${constNodes.size} const nodes\n")
                } catch (e: Exception) {
                    // 忽略日志错误
                }
                
                if (constNodes.isNotEmpty()) {
                    try {
                        val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                        logFile.appendText("ThriftBlock: Creating global alignments for ${constNodes.size} constants\n")
                    } catch (e: Exception) {
                        // 忽略日志错误
                    }
                    
                    alignments["const_type"] = Alignment.createAlignment(true)
                    alignments["const_name"] = Alignment.createAlignment(true)
                    alignments["const_equals"] = Alignment.createAlignment(true)
                }
            }
            "ThriftElement.STRUCT" -> {
                // 为结构体字段创建对齐
                val fieldNodes = mutableListOf<ASTNode>()
                var child = myNode.firstChildNode
                while (child != null) {
                    if (child.elementType == ThriftElementTypes.FIELD) {
                        fieldNodes.add(child)
                    }
                    child = child.treeNext
                }
                
                if (fieldNodes.isNotEmpty()) {
                    try {
                        val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                        logFile.appendText("ThriftBlock: Creating field alignments for ${fieldNodes.size} fields\n")
                    } catch (e: Exception) {
                        // 忽略日志错误
                    }
                    
                    alignments["field_id"] = Alignment.createAlignment(true)
                    alignments["field_type"] = Alignment.createAlignment(true)
                    alignments["field_name"] = Alignment.createAlignment(true)
                }
            }
        }
        
        return alignments
    }
    
    private fun hasChildrenOfType(elementType: com.intellij.psi.tree.IElementType): Boolean {
        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType == elementType) {
                return true
            }
            child = child.treeNext
        }
        return false
    }
    
    private fun getChildrenOfType(elementType: com.intellij.psi.tree.IElementType): List<ASTNode> {
        val result = mutableListOf<ASTNode>()
        var child = myNode.firstChildNode
        while (child != null) {
            if (child.elementType == elementType) {
                result.add(child)
            }
            child = child.treeNext
        }
        return result
    }
    
    private fun determineChildAlignment(child: ASTNode, alignments: Map<String, Alignment?>): Alignment? {
        val parentType = myNode.elementType
        val childType = child.elementType
        
        // 使用parentAlignments（从父级传递的对齐）而不是当前级别的alignments
        val effectiveAlignments = parentAlignments ?: alignments
        
        try {
            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
            logFile.appendText("ThriftBlock: determineChildAlignment - parent: ${parentType}, child: ${childType}, text: '${child.text}'\n")
            logFile.appendText("ThriftBlock: parentAlignments = $parentAlignments\n")
            logFile.appendText("ThriftBlock: alignments = $alignments\n")
            logFile.appendText("ThriftBlock: effectiveAlignments = $effectiveAlignments\n")
            logFile.appendText("ThriftBlock: effectiveAlignments keys = ${effectiveAlignments.keys}\n")
        } catch (e: Exception) {
            // 忽略日志错误
        }
        
        val alignment = when (parentType) {
            // 在文件级别，直接对常量、字段等进行对齐
            ThriftElementTypes.FILE -> {
                when (childType) {
                    ThriftElementTypes.CONST -> null // CONST块本身不对齐，其子元素使用对齐
                    else -> null
                }
            }
            // 在常量定义内部的元素对齐
            ThriftElementTypes.CONST -> {
                when (childType) {
                    ThriftElementTypes.TYPE -> {
                        try {
                            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                            logFile.appendText("ThriftBlock: Aligning const type element: ${child.text}\n")
                        } catch (e: Exception) {
                            // 忽略日志错误
                        }
                        // 从父级对齐中获取类型对齐
                        effectiveAlignments["const_type"]
                    }
                    ThriftTokenTypes.IDENTIFIER -> {
                        // 判断是类型名还是常量名
                        if (isConstantName(child)) {
                            try {
                                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                logFile.appendText("ThriftBlock: Aligning const name: ${child.text}\n")
                            } catch (e: Exception) {
                                // 忽略日志错误
                            }
                            effectiveAlignments["const_name"]
                        } else {
                            try {
                                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                logFile.appendText("ThriftBlock: Aligning const type identifier: ${child.text}\n")
                            } catch (e: Exception) {
                                // 忽略日志错误
                            }
                            effectiveAlignments["const_type"]
                        }
                    }
                    ThriftTokenTypes.EQUALS -> {
                        try {
                            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                            logFile.appendText("ThriftBlock: Aligning const equals\n")
                        } catch (e: Exception) {
                            // 忽略日志错误
                        }
                        effectiveAlignments["const_equals"]
                    }
                    else -> null
                }
            }
            // 在字段定义内部的元素对齐
            ThriftElementTypes.FIELD -> {
                when (childType) {
                    ThriftTokenTypes.INTEGER_LITERAL -> {
                        try {
                            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                            logFile.appendText("ThriftBlock: Aligning field id: ${child.text}\n")
                        } catch (e: Exception) {
                            // 忽略日志错误
                        }
                        effectiveAlignments["field_id"]
                    }
                    ThriftElementTypes.TYPE -> {
                        try {
                            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                            logFile.appendText("ThriftBlock: Aligning field type element: ${child.text}\n")
                        } catch (e: Exception) {
                            // 忽略日志错误
                        }
                        effectiveAlignments["field_type"]
                    }
                    ThriftTokenTypes.IDENTIFIER -> {
                        // 判断是类型名还是字段名
                        if (isFieldName(child)) {
                            try {
                                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                logFile.appendText("ThriftBlock: Aligning field name: ${child.text}\n")
                            } catch (e: Exception) {
                                // 忽略日志错误
                            }
                            effectiveAlignments["field_name"]
                        } else {
                            try {
                                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                logFile.appendText("ThriftBlock: Aligning field type identifier: ${child.text}\n")
                            } catch (e: Exception) {
                                // 忽略日志错误
                            }
                            effectiveAlignments["field_type"]
                        }
                    }
                    else -> null
                }
            }
            // 在方法定义内部的元素对齐
            else -> null
        }
        
        if (alignment != null) {
            try {
                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                logFile.appendText("ThriftBlock: Applied alignment for ${childType}\n")
            } catch (e: Exception) {
                // 忽略日志错误
            }
        }
        
        return alignment
    }
    
    private fun isConstantName(node: ASTNode): Boolean {
        // 在常量定义中，常量名是第三个非空白token（const type name）
        val parent = node.treeParent
        if (parent?.elementType != ThriftElementTypes.CONST) return false
        
        var tokenCount = 0
        var child = parent.firstChildNode
        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE) {
                tokenCount++
                if (child == node) {
                    return tokenCount == 3 // const(1) type(2) name(3)
                }
            }
            child = child.treeNext
        }
        return false
    }
    
    private fun isFieldName(node: ASTNode): Boolean {
        // 在字段定义中，字段名通常在类型之后
        val parent = node.treeParent
        if (parent?.elementType != ThriftElementTypes.FIELD) return false
        
        var foundType = false
        var child = parent.firstChildNode
        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE) {
                if (!foundType && (child.elementType == ThriftElementTypes.TYPE ||
                        child.elementType in ThriftTokenSets.BUILTIN_TYPES ||
                        child.elementType == ThriftTokenTypes.IDENTIFIER)) {
                    foundType = true
                } else if (foundType && child == node && child.elementType == ThriftTokenTypes.IDENTIFIER) {
                    return true
                }
            }
            child = child.treeNext
        }
        return false
    }
    

    private fun isMethodName(node: ASTNode): Boolean {
        val parent = node.treeParent
        if (parent?.elementType != ThriftElementTypes.FUNCTION) return false

        var foundType = false
        var child = parent.firstChildNode
        while (child != null) {
            if (child.elementType != TokenType.WHITE_SPACE) {
                if (!foundType && (child.elementType == ThriftElementTypes.TYPE ||
                        child.elementType in ThriftTokenSets.BUILTIN_TYPES ||
                        child.elementType == ThriftTokenTypes.IDENTIFIER)) {
                    foundType = true
                } else if (foundType && child == node && child.elementType == ThriftTokenTypes.IDENTIFIER) {
                    return true
                }
            }
            child = child.treeNext
        }
        return false
    }

    override fun getSpacing(child1: Block?, child2: Block): Spacing? {
        val preSpacing = computePreSpacing(child1, child2)
        if (preSpacing != null) {
            return preSpacing
        }

        val alignmentSpacing = computeAlignmentSpacing(child1, child2)
        return alignmentSpacing ?: spacingBuilder.getSpacing(this, child1, child2)
    }


    private fun computeAlignmentSpacing(child1: Block?, child2: Block): Spacing? {
        val leftBlock = child1 as? ThriftBlock ?: return null
        val rightBlock = child2 as? ThriftBlock ?: return null
        val leftNode = leftBlock.node
        val rightNode = rightBlock.node
        val parent = rightNode.treeParent ?: return null

        return when (parent.elementType) {
            ThriftElementTypes.CONST -> computeConstSpacing(parent, leftNode, rightNode)
            ThriftElementTypes.FIELD -> computeFieldSpacing(parent, leftNode, rightNode)
            ThriftElementTypes.FUNCTION -> computeFunctionSpacing(parent, leftNode, rightNode)
            ThriftElementTypes.ENUM_FIELD -> computeEnumSpacing(parent, leftNode, rightNode)
            else -> null
        }
    }

    private fun computePreSpacing(child1: Block?, child2: Block): Spacing? {
        val rightBlock = child2 as? ThriftBlock ?: return null
        val leftBlock = child1 as? ThriftBlock
        val leftNode = leftBlock?.node
        val rightNode = rightBlock.node

        computeTopLevelBlankLineSpacing(leftNode, rightNode)?.let { return it }
        return computeGenericSpacing(leftNode, rightNode)
    }

    private fun computeTopLevelBlankLineSpacing(leftNode: ASTNode?, rightNode: ASTNode): Spacing? {
        val braceParent = leftNode?.takeIf { it.elementType == ThriftTokenTypes.RBRACE }?.treeParent ?: return null
        if (braceParent.elementType !in CONTAINER_TYPES) {
            return null
        }
        val fileNode = braceParent.treeParent
        if (fileNode?.elementType != ThriftElementTypes.FILE) {
            return null
        }

        if (!shouldForceTopLevelBlankLine(rightNode)) {
            return null
        }

        return Spacing.createSpacing(0, 0, 2, false, 1)
    }

    private fun shouldForceTopLevelBlankLine(rightNode: ASTNode): Boolean {
        val type = rightNode.elementType
        if (type in TOP_LEVEL_DECLARATION_ELEMENT_TYPES) {
            return true
        }
        if (type in TOP_LEVEL_DECLARATION_START_TOKENS) {
            return true
        }
        if (type == ThriftTokenTypes.LINE_COMMENT || type == ThriftTokenTypes.BLOCK_COMMENT) {
            return rightNode.treeParent?.elementType == ThriftElementTypes.FILE
        }
        return false
    }

    private fun computeGenericSpacing(child1: Block?, child2: Block): Spacing? {
        val rightBlock = child2 as? ThriftBlock ?: return null
        val leftBlock = child1 as? ThriftBlock
        return computeGenericSpacing(leftBlock?.node, rightBlock.node)
    }

    private fun computeGenericSpacing(leftNode: ASTNode?, rightNode: ASTNode): Spacing? {
        if (findGenericContainer(rightNode) == null && findGenericContainer(leftNode) == null) {
            return null
        }

        return when {
            rightNode.elementType == ThriftTokenTypes.LT -> zeroSpacing()
            leftNode?.elementType == ThriftTokenTypes.LT -> zeroSpacing()
            rightNode.elementType == ThriftTokenTypes.GT -> zeroSpacing()
            leftNode?.elementType == ThriftTokenTypes.COMMA -> zeroSpacing()
            else -> null
        }
    }

    private fun findGenericContainer(node: ASTNode?): ASTNode? {
        var current = node
        while (current != null) {
            if (current.elementType == ThriftElementTypes.TYPE && current.findChildOfType(ThriftTokenTypes.LT) != null) {
                return current
            }
            current = current.treeParent
        }
        return null
    }

    private fun zeroSpacing(): Spacing = Spacing.createSpacing(0, 0, 0, false, 0)

    private fun computeConstSpacing(parent: ASTNode, leftNode: ASTNode, rightNode: ASTNode): Spacing? {
        if (rightNode.isComment()) {
            return fixedSpacing(1)
        }

        if (leftNode.elementType == ThriftElementTypes.TYPE &&
            rightNode.elementType == ThriftTokenTypes.IDENTIFIER &&
            isConstantName(rightNode)
        ) {
            val fileNode = parent.treeParent ?: return null
            val (maxTypeWidth, _) = fileNode.computeConstWidths()
            val currentWidth = leftNode.normalizedLength()
            val spaces = (maxTypeWidth - currentWidth + 1).coerceAtLeast(1)
            return fixedSpacing(spaces)
        }

        if (leftNode.elementType == ThriftTokenTypes.IDENTIFIER &&
            isConstantName(leftNode) &&
            rightNode.elementType == ThriftTokenTypes.EQUALS
        ) {
            val fileNode = parent.treeParent ?: return null
            val (_, maxNameWidth) = fileNode.computeConstWidths()
            val currentWidth = leftNode.normalizedLength()
            val spaces = (maxNameWidth - currentWidth + 2).coerceAtLeast(2)
            return fixedSpacing(spaces)
        }

        return null
    }

    private fun computeFieldSpacing(parent: ASTNode, leftNode: ASTNode, rightNode: ASTNode): Spacing? {
        if (parent.elementType != ThriftElementTypes.FIELD) return null

        if (rightNode.isComment()) {
            return fixedSpacing(1)
        }

        val containerNode = parent.treeParent ?: return null

        if (containerNode.elementType == ThriftElementTypes.THROWS) {
            if (leftNode.elementType == ThriftTokenTypes.COLON &&
                rightNode.elementType != TokenType.WHITE_SPACE &&
                rightNode.elementType != ThriftTokenTypes.COMMA
            ) {
                return fixedSpacing(1)
            }

            if (rightNode.elementType == ThriftTokenTypes.IDENTIFIER && isFieldName(rightNode)) {
                return fixedSpacing(1)
            }

            if (leftNode.elementType == ThriftTokenTypes.EQUALS &&
                rightNode.elementType != TokenType.WHITE_SPACE &&
                !rightNode.isComment()
            ) {
                return fixedSpacing(1)
            }

            return null
        }

        val widthInfo = containerNode.computeFieldGroupWidths(parent)

        if (leftNode.elementType == ThriftTokenTypes.COLON &&
            rightNode.elementType != TokenType.WHITE_SPACE &&
            rightNode.elementType != ThriftTokenTypes.COMMA
        ) {
            val idNode = parent.findChildOfType(ThriftTokenTypes.INTEGER_LITERAL)
            val currentWidth = idNode?.normalizedLength() ?: 0
            val spaces = (widthInfo.maxIdWidth - currentWidth + 1).coerceAtLeast(1)
            return fixedSpacing(spaces)
        }

        if (rightNode.elementType == ThriftTokenTypes.IDENTIFIER && isFieldName(rightNode)) {
            val currentWidth = parent.fieldTypeSegmentWidth()
            val hasQualifier = parent.hasQualifier()
            val targetWidth = if (hasQualifier) widthInfo.maxTypeWithQualifier else widthInfo.maxTypeWithoutQualifier
            if (targetWidth == 0) return null
            val baseSpacing = if (hasQualifier) 2 else 3
            val spaces = (targetWidth - currentWidth + baseSpacing).coerceAtLeast(baseSpacing)
            return fixedSpacing(spaces)
        }

        return null
    }

    private fun computeFunctionSpacing(parent: ASTNode, leftNode: ASTNode, rightNode: ASTNode): Spacing? {
        if (parent.elementType != ThriftElementTypes.FUNCTION) return null

        if (rightNode.elementType == ThriftTokenTypes.IDENTIFIER && isMethodName(rightNode)) {
            return fixedSpacing(1)
        }

        if ((leftNode.elementType == ThriftTokenTypes.RPAREN || leftNode.elementType == ThriftElementTypes.PARAM_LIST) &&
            rightNode.elementType == ThriftTokenTypes.KW_THROWS
        ) {
            return fixedSpacing(1)
        }

        return null
    }

    private fun computeEnumSpacing(parent: ASTNode, leftNode: ASTNode, rightNode: ASTNode): Spacing? {
        if (parent.elementType != ThriftElementTypes.ENUM_FIELD) return null
        val enumNode = parent.treeParent ?: return null
        val widthInfo = enumNode.computeEnumWidthInfo(parent)

        if (leftNode.elementType == ThriftTokenTypes.IDENTIFIER && rightNode.elementType == ThriftTokenTypes.EQUALS) {
            val nameLength = leftNode.normalizedLength()
            val spaces = (widthInfo.maxNameWidth - nameLength + 1).coerceAtLeast(1)
            return fixedSpacing(spaces)
        }

        if (rightNode.isComment()) {
            val nameNode = parent.findChildOfType(ThriftTokenTypes.IDENTIFIER) ?: return null
            val nameLength = nameNode.normalizedLength()
            val hasEquals = parent.findChildOfType(ThriftTokenTypes.EQUALS) != null
            val valueWidth = parent.enumValueSegmentWidth()
            val baseSpacing = 2

            return if (hasEquals) {
                val spaces = (widthInfo.maxValueWidth - valueWidth + baseSpacing).coerceAtLeast(baseSpacing)
                fixedSpacing(spaces)
            } else {
                val namePadding = widthInfo.maxNameWidth - nameLength + 1
                val equalsPadding = if (widthInfo.hasEquals) 1 else 0
                val spaces = namePadding + equalsPadding + (widthInfo.maxValueWidth - valueWidth) + baseSpacing
                fixedSpacing(spaces.coerceAtLeast(baseSpacing))
            }
        }

        if (leftNode.elementType == ThriftTokenTypes.EQUALS && !rightNode.isComment() && rightNode.elementType != TokenType.WHITE_SPACE) {
            return fixedSpacing(1)
        }

        return null
    }

    private fun fixedSpacing(spaces: Int): Spacing = Spacing.createSpacing(spaces, spaces, 0, false, 0)

    private fun ASTNode.computeConstWidths(): Pair<Int, Int> {
        var maxType = 0
        var maxName = 0
        for (child in childrenSequence()) {
            if (child.elementType == ThriftElementTypes.CONST) {
                child.findChildOfType(ThriftElementTypes.TYPE)?.let {
                    maxType = maxOf(maxType, it.normalizedLength())
                }
                val nameNode = child.childrenSequence()
                    .firstOrNull { it.elementType == ThriftTokenTypes.IDENTIFIER && isConstantName(it) }
                if (nameNode != null) {
                    maxName = maxOf(maxName, nameNode.normalizedLength())
                }
            }
        }
        return maxType to maxName
    }

    private data class FieldWidthInfo(
        val maxIdWidth: Int,
        val maxTypeWithQualifier: Int,
        val maxTypeWithoutQualifier: Int
    )

    private fun ASTNode.computeFieldGroupWidths(targetField: ASTNode): FieldWidthInfo {
        val group = collectGroupForChild(targetField, ThriftElementTypes.FIELD)
        var maxId = 0
        var maxWithQualifier = 0
        var maxWithoutQualifier = 0
        for (field in group) {
            field.findChildOfType(ThriftTokenTypes.INTEGER_LITERAL)?.let {
                maxId = maxOf(maxId, it.normalizedLength())
            }
            val typeWidth = field.fieldTypeSegmentWidth()
            if (field.hasQualifier()) {
                maxWithQualifier = maxOf(maxWithQualifier, typeWidth)
            } else {
                maxWithoutQualifier = maxOf(maxWithoutQualifier, typeWidth)
            }
        }
        return FieldWidthInfo(maxId, maxWithQualifier, maxWithoutQualifier)
    }

    private data class EnumWidthInfo(
        val maxNameWidth: Int,
        val maxValueWidth: Int,
        val hasEquals: Boolean
    )

    private fun ASTNode.computeEnumWidthInfo(targetField: ASTNode): EnumWidthInfo {
        val group = collectGroupForChild(targetField, ThriftElementTypes.ENUM_FIELD)
        var maxName = 0
        var maxValue = 0
        var hasEquals = false
        for (field in group) {
            field.findChildOfType(ThriftTokenTypes.IDENTIFIER)?.let {
                maxName = maxOf(maxName, it.normalizedLength())
            }
            if (!hasEquals && field.findChildOfType(ThriftTokenTypes.EQUALS) != null) {
                hasEquals = true
            }
            maxValue = maxOf(maxValue, field.enumValueSegmentWidth())
        }
        return EnumWidthInfo(maxName, maxValue, hasEquals)
    }

    private fun ASTNode.collectGroupForChild(target: ASTNode, childType: IElementType): List<ASTNode> {
        val result = mutableListOf<ASTNode>()
        var currentGroup = mutableListOf<ASTNode>()
        var child = firstChildNode
        while (child != null) {
            if (child.elementType == childType) {
                currentGroup.add(child)
            }
            if (child.isGroupSeparator()) {
                if (currentGroup.contains(target)) {
                    result.addAll(currentGroup)
                    break
                }
                currentGroup = mutableListOf()
            }
            child = child.treeNext
        }
        if (result.isEmpty() && currentGroup.contains(target)) {
            result.addAll(currentGroup)
        }
        if (result.isEmpty() && target.elementType == childType) {
            result.add(target)
        }
        return result
    }

    private fun ASTNode.isGroupSeparator(): Boolean {
        if (elementType != TokenType.WHITE_SPACE) return false
        var newlineCount = 0
        for (ch in text) {
            if (ch.code == 10) {
                newlineCount++
                if (newlineCount >= 2) {
                    return true
                }
            }
        }
        return false
    }

    private fun ASTNode.enumValueSegmentWidth(): Int {
        val equalsNode = findChildOfType(ThriftTokenTypes.EQUALS)
        val anchor = equalsNode ?: findChildOfType(ThriftTokenTypes.IDENTIFIER) ?: return 0
        var width = 0
        var previous: ASTNode? = null
        var hasValueToken = false
        var child = anchor.treeNext
        while (child != null) {
            when (child.elementType) {
                TokenType.WHITE_SPACE -> {
                    if (child.text.any { it.code == 10 }) {
                        break
                    }
                }
                ThriftTokenTypes.LINE_COMMENT,
                ThriftTokenTypes.BLOCK_COMMENT -> break
                else -> {
                    if (previous != null && requiresSpaceBetween(previous, child)) {
                        width += 1
                    }
                    width += child.normalizedLength()
                    previous = child
                    hasValueToken = true
                }
            }
            child = child.treeNext
        }
        if (equalsNode != null && hasValueToken) {
            width += 1 // space after '='
        }
        return width
    }

    private fun ASTNode.fieldTypeSegmentWidth(): Int {
        val colonNode = findChildOfType(ThriftTokenTypes.COLON) ?: return 0
        val nameNode = childrenSequence()
            .firstOrNull { it.elementType == ThriftTokenTypes.IDENTIFIER && isFieldName(it) }
            ?: return 0

        var width = 0
        var previous: ASTNode? = null
        var child = colonNode.treeNext
        while (child != null && child != nameNode) {
            if (child.elementType != TokenType.WHITE_SPACE) {
                if (previous != null && requiresSpaceBetween(previous, child)) {
                    width += 1
                }
                width += child.normalizedLength()
                previous = child
            }
            child = child.treeNext
        }
        return width
    }

    private fun ASTNode.hasQualifier(): Boolean =
        childrenSequence().any {
            it.elementType == ThriftTokenTypes.KW_OPTIONAL ||
            it.elementType == ThriftTokenTypes.KW_REQUIRED
        }

    private fun ASTNode.isComment(): Boolean =
        elementType == ThriftTokenTypes.LINE_COMMENT || elementType == ThriftTokenTypes.BLOCK_COMMENT

    private fun requiresSpaceBetween(previous: ASTNode, next: ASTNode): Boolean {
        return when (previous.elementType) {
            ThriftTokenTypes.KW_OPTIONAL,
            ThriftTokenTypes.KW_REQUIRED,
            ThriftTokenTypes.KW_ONEWAY,
            ThriftTokenTypes.KW_ASYNC,
            ThriftTokenTypes.KW_VOID -> true
            ThriftElementTypes.TYPE -> next.elementType == ThriftTokenTypes.IDENTIFIER || next.elementType == ThriftTokenTypes.KW_THROWS
            else -> false
        }
    }

    private fun ASTNode.childrenSequence(): Sequence<ASTNode> =
        generateSequence(firstChildNode) { it.treeNext }

    private fun ASTNode.findChildOfType(type: IElementType): ASTNode? =
        childrenSequence().firstOrNull { it.elementType == type }

    private fun ASTNode.normalizedLength(): Int {
        var length = 0
        for (ch in text) {
            if (!ch.isWhitespace()) {
                length++
            }
        }
        return length
    }

    override fun getIndent(): Indent? {
        val parentType = myNode.treeParent?.elementType
        val elementType = myNode.elementType
        
        return when {
            // 根级别的定义不缩进（namespace, const, enum, struct, service 等）
            parentType == null || parentType == ThriftElementTypes.FILE -> Indent.getNoneIndent()
            
            // 代码块内的元素需要缩进
            parentType == ThriftElementTypes.BLOCK && elementType !in BRACES -> Indent.getNormalIndent()
            
            // 参数列表内的元素需要缩进
            parentType == ThriftElementTypes.PARAM_LIST && elementType !in PARENS -> Indent.getNormalIndent()
            
            // throws 子句内的元素需要缩进
            parentType == ThriftElementTypes.THROWS && elementType !in PARENS -> Indent.getNormalIndent()
            
            // 结构体、联合体、异常、枚举、服务内的字段需要缩进
            parentType in CONTAINER_TYPES && myNode.isComment() -> Indent.getNormalIndent()
            parentType in CONTAINER_TYPES && elementType == ThriftElementTypes.FIELD -> Indent.getNormalIndent()
            parentType in CONTAINER_TYPES && elementType == ThriftElementTypes.ENUM_FIELD -> Indent.getNormalIndent()
            parentType in CONTAINER_TYPES && elementType == ThriftElementTypes.FUNCTION -> Indent.getNormalIndent()
            
            // 大括号本身不缩进
            elementType in BRACES -> Indent.getNoneIndent()
            
            // 其他情况不缩进
            else -> Indent.getNoneIndent()
        }
    }

    override fun getChildAttributes(newChildIndex: Int): ChildAttributes = when (myNode.elementType) {
        ThriftElementTypes.BLOCK,
        ThriftElementTypes.PARAM_LIST,
        ThriftElementTypes.THROWS -> ChildAttributes(Indent.getNormalIndent(), null)
        in CONTAINER_TYPES -> ChildAttributes(Indent.getNormalIndent(), null)
        else -> ChildAttributes(Indent.getNoneIndent(), null)
    }

    override fun isLeaf(): Boolean = myNode.firstChildNode == null

    companion object {
        private val BRACES = setOf(ThriftTokenTypes.LBRACE, ThriftTokenTypes.RBRACE)
        private val PARENS = setOf(ThriftTokenTypes.LPAREN, ThriftTokenTypes.RPAREN)
        private val CONTAINER_TYPES = setOf(
            ThriftElementTypes.STRUCT,
            ThriftElementTypes.UNION,
            ThriftElementTypes.EXCEPTION,
            ThriftElementTypes.ENUM,
            ThriftElementTypes.SERVICE
        )
        private val TOP_LEVEL_DECLARATION_ELEMENT_TYPES = setOf(
            ThriftElementTypes.ENUM,
            ThriftElementTypes.STRUCT,
            ThriftElementTypes.UNION,
            ThriftElementTypes.EXCEPTION,
            ThriftElementTypes.SERVICE
        )

        private val TOP_LEVEL_DECLARATION_START_TOKENS = setOf(
            ThriftTokenTypes.KW_ENUM,
            ThriftTokenTypes.KW_STRUCT,
            ThriftTokenTypes.KW_EXCEPTION,
            ThriftTokenTypes.KW_SERVICE,
            ThriftTokenTypes.KW_UNION
        )
    }
}
