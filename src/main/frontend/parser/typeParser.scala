package frontend

import ast._
import lexer.implicits.implicitSymbol
import lexer.natural32

import parsley.Parsley

object typeParser {

    lazy val parseType: Parsley[Type] = 
        matrixType | primitive

    private lazy val matrixType: Parsley[MatrixType] = 
        "mat<" ~> MatrixType(primitive, "," ~> natural32, "," ~> natural32) <~ ">"

    private lazy val primitive: Parsley[Primitive] = 
        ("int32" as Int32Type) |
        ("char" as CharType)
}