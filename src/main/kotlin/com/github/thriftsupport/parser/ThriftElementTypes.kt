package com.github.thriftsupport.parser

import com.github.thriftsupport.psi.ThriftPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement

object ThriftElementTypes {
    val FILE = ThriftElementType("FILE")
    val NAMESPACE = ThriftElementType("NAMESPACE")
    val INCLUDE = ThriftElementType("INCLUDE")
    val CONST = ThriftElementType("CONST")
    val TYPEDEF = ThriftElementType("TYPEDEF")
    val STRUCT = ThriftElementType("STRUCT")
    val UNION = ThriftElementType("UNION")
    val EXCEPTION = ThriftElementType("EXCEPTION")
    val ENUM = ThriftElementType("ENUM")
    val SERVICE = ThriftElementType("SERVICE")
    val ENUM_FIELD = ThriftElementType("ENUM_FIELD")
    val FIELD = ThriftElementType("FIELD")
    val FUNCTION = ThriftElementType("FUNCTION")
    val FUNCTION_RETURNS = ThriftElementType("FUNCTION_RETURNS")
    val THROWS = ThriftElementType("THROWS")
    val PARAM_LIST = ThriftElementType("PARAM_LIST")
    val PARAM = ThriftElementType("PARAM")
    val BLOCK = ThriftElementType("BLOCK")
    val VALUE = ThriftElementType("VALUE")
    val TYPE = ThriftElementType("TYPE")
    val ANNOTATION = ThriftElementType("ANNOTATION")

    fun createElement(node: ASTNode): PsiElement = ThriftPsiElement(node)
}
