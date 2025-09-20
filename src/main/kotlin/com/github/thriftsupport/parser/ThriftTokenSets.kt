package com.github.thriftsupport.parser

import com.intellij.psi.tree.TokenSet

object ThriftTokenSets {
    val KEYWORDS: TokenSet = TokenSet.create(
        ThriftTokenTypes.KW_NAMESPACE,
        ThriftTokenTypes.KW_CPP_NAMESPACE,
        ThriftTokenTypes.KW_PY_NAMESPACE,
        ThriftTokenTypes.KW_JAVA_PACKAGE,
        ThriftTokenTypes.KW_GO_PACKAGE,
        ThriftTokenTypes.KW_INCLUDE,
        ThriftTokenTypes.KW_CPP_INCLUDE,
        ThriftTokenTypes.KW_CONST,
        ThriftTokenTypes.KW_TYPEDEF,
        ThriftTokenTypes.KW_STRUCT,
        ThriftTokenTypes.KW_UNION,
        ThriftTokenTypes.KW_EXCEPTION,
        ThriftTokenTypes.KW_ENUM,
        ThriftTokenTypes.KW_SERVICE,
        ThriftTokenTypes.KW_EXTENDS,
        ThriftTokenTypes.KW_THROWS,
        ThriftTokenTypes.KW_OPTIONAL,
        ThriftTokenTypes.KW_REQUIRED,
        ThriftTokenTypes.KW_ONEWAY,
        ThriftTokenTypes.KW_ASYNC
    )

    val BUILTIN_TYPES: TokenSet = TokenSet.create(
        ThriftTokenTypes.KW_BOOL,
        ThriftTokenTypes.KW_BYTE,
        ThriftTokenTypes.KW_I8,
        ThriftTokenTypes.KW_I16,
        ThriftTokenTypes.KW_I32,
        ThriftTokenTypes.KW_I64,
        ThriftTokenTypes.KW_DOUBLE,
        ThriftTokenTypes.KW_STRING,
        ThriftTokenTypes.KW_BINARY,
        ThriftTokenTypes.KW_SLIST,
        ThriftTokenTypes.KW_SET,
        ThriftTokenTypes.KW_LIST,
        ThriftTokenTypes.KW_MAP,
        ThriftTokenTypes.KW_VOID
    )

    val BOOLEAN_LITERALS: TokenSet = TokenSet.create(ThriftTokenTypes.BOOLEAN_LITERAL)
}
