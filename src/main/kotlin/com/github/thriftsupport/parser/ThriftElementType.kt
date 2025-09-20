package com.github.thriftsupport.parser

import com.github.thriftsupport.lang.ThriftLanguage
import com.intellij.psi.tree.IElementType

class ThriftElementType(debugName: String) : IElementType(debugName, ThriftLanguage) {
    override fun toString(): String = "ThriftElement." + super.toString()
}
