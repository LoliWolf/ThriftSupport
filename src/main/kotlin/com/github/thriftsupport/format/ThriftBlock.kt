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
            "ThriftElement.SERVICE" -> {
                // 为服务方法创建对齐
                try {
                    val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                    logFile.appendText("ThriftBlock: SERVICE branch - examining children\n")
                } catch (e: Exception) {
                    // 忽略日志错误
                }
                
                val functionNodes = mutableListOf<ASTNode>()
                
                // 查找BLOCK子节点，然后在BLOCK中查找FUNCTION节点
                var child = myNode.firstChildNode
                while (child != null) {
                    try {
                        val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                        logFile.appendText("ThriftBlock: SERVICE child type: ${child.elementType}, text: '${child.text.take(50)}'\n")
                    } catch (e: Exception) {
                        // 忽略日志错误
                    }
                    
                    if (child.elementType == ThriftElementTypes.BLOCK) {
                        // 在BLOCK中查找FUNCTION节点
                        var blockChild = child.firstChildNode
                        while (blockChild != null) {
                            try {
                                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                logFile.appendText("ThriftBlock: BLOCK child type: ${blockChild.elementType}, text: '${blockChild.text.take(50)}'\n")
                            } catch (e: Exception) {
                                // 忽略日志错误
                            }
                            
                            if (blockChild.elementType == ThriftElementTypes.FUNCTION) {
                                functionNodes.add(blockChild)
                                try {
                                    val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                    logFile.appendText("ThriftBlock: Found FUNCTION node in BLOCK: ${blockChild.text.take(50)}\n")
                                } catch (e: Exception) {
                                    // 忽略日志错误
                                }
                            }
                            blockChild = blockChild.treeNext
                        }
                    }
                    child = child.treeNext
                }
                
                if (functionNodes.isNotEmpty()) {
                    try {
                        val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                        logFile.appendText("ThriftBlock: Creating method alignments for ${functionNodes.size} methods\n")
                    } catch (e: Exception) {
                        // 忽略日志错误
                    }
                    
                    alignments["method_type"] = Alignment.createAlignment(true)
                    alignments["method_name"] = Alignment.createAlignment(true)
                    alignments["method_throws"] = Alignment.createAlignment(true)
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
            ThriftElementTypes.FUNCTION -> {
                when (childType) {
                    ThriftElementTypes.TYPE -> {
                        try {
                            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                            logFile.appendText("ThriftBlock: Aligning method type element: ${child.text}\n")
                        } catch (e: Exception) {
                            // 忽略日志错误
                        }
                        effectiveAlignments["method_type"]
                    }
                    ThriftTokenTypes.IDENTIFIER -> {
                        // 判断是返回类型还是方法名
                        if (isMethodName(child)) {
                            try {
                                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                logFile.appendText("ThriftBlock: Aligning method name: ${child.text}\n")
                            } catch (e: Exception) {
                                // 忽略日志错误
                            }
                            effectiveAlignments["method_name"]
                        } else {
                            try {
                                val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                                logFile.appendText("ThriftBlock: Aligning method type identifier: ${child.text}\n")
                            } catch (e: Exception) {
                                // 忽略日志错误
                            }
                            effectiveAlignments["method_type"]
                        }
                    }
                    ThriftTokenTypes.KW_THROWS -> {
                        try {
                            val logFile = java.io.File("D:\\Projects\\ThriftSupport\\formatter_debug.log")
                            logFile.appendText("ThriftBlock: Aligning method throws\n")
                        } catch (e: Exception) {
                            // 忽略日志错误
                        }
                        effectiveAlignments["method_throws"]
                    }
                    else -> null
                }
            }
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
        // 在方法定义中，方法名通常在返回类型之后
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
            else -> null
        }
    }

    private fun computeConstSpacing(parent: ASTNode, leftNode: ASTNode, rightNode: ASTNode): Spacing? {
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
        val blockNode = parent.treeParent ?: return null
        val widthInfo = blockNode.computeFieldWidths()

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

    @Suppress("UNUSED_PARAMETER")
    private fun computeFunctionSpacing(parent: ASTNode, leftNode: ASTNode, rightNode: ASTNode): Spacing? {
        if (parent.elementType != ThriftElementTypes.FUNCTION) return null
        val serviceBlock = parent.treeParent ?: return null

        if (rightNode.elementType == ThriftTokenTypes.IDENTIFIER && isMethodName(rightNode)) {
            val maxReturnWidth = serviceBlock.computeFunctionReturnWidth()
            val currentWidth = parent.functionReturnSegmentWidth()
            val spaces = (maxReturnWidth - currentWidth + 1).coerceAtLeast(1)
            return fixedSpacing(spaces)
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

    private fun ASTNode.computeFieldWidths(): FieldWidthInfo {
        var maxId = 0
        var maxWithQualifier = 0
        var maxWithoutQualifier = 0
        for (child in childrenSequence()) {
            if (child.elementType == ThriftElementTypes.FIELD) {
                child.findChildOfType(ThriftTokenTypes.INTEGER_LITERAL)?.let {
                    maxId = maxOf(maxId, it.normalizedLength())
                }
                val typeWidth = child.fieldTypeSegmentWidth()
                if (child.hasQualifier()) {
                    maxWithQualifier = maxOf(maxWithQualifier, typeWidth)
                } else {
                    maxWithoutQualifier = maxOf(maxWithoutQualifier, typeWidth)
                }
            }
        }
        return FieldWidthInfo(maxId, maxWithQualifier, maxWithoutQualifier)
    }

    private fun ASTNode.computeFunctionReturnWidth(): Int {
        var maxWidth = 0
        for (child in childrenSequence()) {
            if (child.elementType == ThriftElementTypes.FUNCTION) {
                maxWidth = maxOf(maxWidth, child.functionReturnSegmentWidth())
            }
        }
        return maxWidth
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

    private fun ASTNode.functionReturnSegmentWidth(): Int {
        val nameNode = childrenSequence()
            .firstOrNull { it.elementType == ThriftTokenTypes.IDENTIFIER && isMethodName(it) }
            ?: return 0

        var width = 0
        var previous: ASTNode? = null
        var child = firstChildNode
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
    }
}
