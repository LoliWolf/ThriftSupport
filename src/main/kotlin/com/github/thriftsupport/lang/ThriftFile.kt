package com.github.thriftsupport.lang

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

class ThriftFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, ThriftLanguage) {
    override fun getFileType() = ThriftFileType

    override fun toString(): String = "Thrift File"
}
