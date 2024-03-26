package frontend

import ast._
import disambiguators._
import lexer.implicits.implicitSymbol
import lexer.natural32

import parsley.Parsley.pure
import parsley.Parsley

object typeParser {

    lazy val parseType: Parsley[Type] = 
        matrixType | primitive

    private lazy val matrixType: Parsley[MatrixType] = 
        MatrixType(modifiers, "mat<" ~> primitive, "," ~> natural32, "," ~> natural32) <~ ">"

    private lazy val primitive: Parsley[Primitive] = 
        PrimitiveTypeDisambiguator(modifiers, 
            (
            ("int" as ParseInt32) | 
            ("char" as ParseChar)
            ),
            modifiers)
    private lazy val modifiers: Parsley[List[TypeModifier]] = 
        ("const" as Constant) <::> modifiers | pure(Nil)
}