package com.github.thriftsupport.lang

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

object ThriftFileType : LanguageFileType(ThriftLanguage) {
    override fun getName(): String = "Thrift"

    override fun getDescription(): String = "Apache Thrift IDL file"

    override fun getDefaultExtension(): String = "thrift"

    override fun getIcon(): Icon = ThriftIcons.FILE
}
