package com.github.thriftsupport.parser

import com.intellij.psi.tree.TokenSet

object ThriftTokenTypes {
    val LINE_COMMENT = ThriftTokenType("LINE_COMMENT")
    val BLOCK_COMMENT = ThriftTokenType("BLOCK_COMMENT")
    val DOC_COMMENT = ThriftTokenType("DOC_COMMENT")

    val STRING_LITERAL = ThriftTokenType("STRING_LITERAL")
    val INTEGER_LITERAL = ThriftTokenType("INTEGER_LITERAL")
    val FLOAT_LITERAL = ThriftTokenType("FLOAT_LITERAL")
    val BOOLEAN_LITERAL = ThriftTokenType("BOOLEAN_LITERAL")

    val IDENTIFIER = ThriftTokenType("IDENTIFIER")

    val KW_INCLUDE = ThriftTokenType("KW_INCLUDE")
    val KW_CPP_INCLUDE = ThriftTokenType("KW_CPP_INCLUDE")
    val KW_NAMESPACE = ThriftTokenType("KW_NAMESPACE")
    val KW_CPP_NAMESPACE = ThriftTokenType("KW_CPP_NAMESPACE")
    val KW_PY_NAMESPACE = ThriftTokenType("KW_PY_NAMESPACE")
    val KW_JAVA_PACKAGE = ThriftTokenType("KW_JAVA_PACKAGE")
    val KW_GO_PACKAGE = ThriftTokenType("KW_GO_PACKAGE")

    val KW_CONST = ThriftTokenType("KW_CONST")
    val KW_TYPEDEF = ThriftTokenType("KW_TYPEDEF")
    val KW_STRUCT = ThriftTokenType("KW_STRUCT")
    val KW_UNION = ThriftTokenType("KW_UNION")
    val KW_EXCEPTION = ThriftTokenType("KW_EXCEPTION")
    val KW_ENUM = ThriftTokenType("KW_ENUM")
    val KW_SERVICE = ThriftTokenType("KW_SERVICE")
    val KW_EXTENDS = ThriftTokenType("KW_EXTENDS")
    val KW_THROWS = ThriftTokenType("KW_THROWS")
    val KW_OPTIONAL = ThriftTokenType("KW_OPTIONAL")
    val KW_REQUIRED = ThriftTokenType("KW_REQUIRED")
    val KW_ONEWAY = ThriftTokenType("KW_ONEWAY")
    val KW_ASYNC = ThriftTokenType("KW_ASYNC")
    val KW_VOID = ThriftTokenType("KW_VOID")

    val KW_TRUE = ThriftTokenType("KW_TRUE")
    val KW_FALSE = ThriftTokenType("KW_FALSE")

    val KW_SET = ThriftTokenType("KW_SET")
    val KW_LIST = ThriftTokenType("KW_LIST")
    val KW_MAP = ThriftTokenType("KW_MAP")

    val KW_BOOL = ThriftTokenType("KW_BOOL")
    val KW_BYTE = ThriftTokenType("KW_BYTE")
    val KW_I8 = ThriftTokenType("KW_I8")
    val KW_I16 = ThriftTokenType("KW_I16")
    val KW_I32 = ThriftTokenType("KW_I32")
    val KW_I64 = ThriftTokenType("KW_I64")
    val KW_DOUBLE = ThriftTokenType("KW_DOUBLE")
    val KW_STRING = ThriftTokenType("KW_STRING")
    val KW_BINARY = ThriftTokenType("KW_BINARY")
    val KW_SLIST = ThriftTokenType("KW_SLIST")

    val AT_SIGN = ThriftTokenType("AT_SIGN")
    val COMMA = ThriftTokenType("COMMA")
    val SEMICOLON = ThriftTokenType("SEMICOLON")
    val COLON = ThriftTokenType("COLON")
    val EQUALS = ThriftTokenType("EQUALS")
    val LT = ThriftTokenType("LT")
    val GT = ThriftTokenType("GT")
    val LPAREN = ThriftTokenType("LPAREN")
    val RPAREN = ThriftTokenType("RPAREN")
    val LBRACE = ThriftTokenType("LBRACE")
    val RBRACE = ThriftTokenType("RBRACE")
    val LBRACKET = ThriftTokenType("LBRACKET")
    val RBRACKET = ThriftTokenType("RBRACKET")
    val QUESTION = ThriftTokenType("QUESTION")
    val STAR = ThriftTokenType("STAR")
    val SLASH = ThriftTokenType("SLASH")
    val PLUS = ThriftTokenType("PLUS")
    val MINUS = ThriftTokenType("MINUS")
    val PERCENT = ThriftTokenType("PERCENT")
    val PIPE = ThriftTokenType("PIPE")
    val AMPERSAND = ThriftTokenType("AMPERSAND")
    val CARET = ThriftTokenType("CARET")
    val DOT = ThriftTokenType("DOT")

    val BAD_CHARACTER = ThriftTokenType("BAD_CHARACTER")

    val KEYWORD_LOOKUP: Map<String, ThriftTokenType> = mapOf(
        "include" to KW_INCLUDE,
        "cpp_include" to KW_CPP_INCLUDE,
        "namespace" to KW_NAMESPACE,
        "cpp_namespace" to KW_CPP_NAMESPACE,
        "py_namespace" to KW_PY_NAMESPACE,
        "java_package" to KW_JAVA_PACKAGE,
        "go_package" to KW_GO_PACKAGE,
        "const" to KW_CONST,
        "typedef" to KW_TYPEDEF,
        "struct" to KW_STRUCT,
        "union" to KW_UNION,
        "exception" to KW_EXCEPTION,
        "enum" to KW_ENUM,
        "service" to KW_SERVICE,
        "extends" to KW_EXTENDS,
        "throws" to KW_THROWS,
        "optional" to KW_OPTIONAL,
        "required" to KW_REQUIRED,
        "oneway" to KW_ONEWAY,
        "async" to KW_ASYNC,
        "void" to KW_VOID,
        "true" to KW_TRUE,
        "false" to KW_FALSE,
        "set" to KW_SET,
        "list" to KW_LIST,
        "map" to KW_MAP,
        "bool" to KW_BOOL,
        "byte" to KW_BYTE,
        "i8" to KW_I8,
        "i16" to KW_I16,
        "i32" to KW_I32,
        "i64" to KW_I64,
        "double" to KW_DOUBLE,
        "string" to KW_STRING,
        "binary" to KW_BINARY,
        "slist" to KW_SLIST
    )

    val COMMENTS: TokenSet = TokenSet.create(LINE_COMMENT, BLOCK_COMMENT, DOC_COMMENT)
    val STRINGS: TokenSet = TokenSet.create(STRING_LITERAL)
}
