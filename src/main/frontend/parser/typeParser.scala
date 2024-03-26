package frontend

import ast._
import disambiguators._
import lexer.implicits.implicitSymbol
import lexer.natural32

import parsley.Parsley.pure
import parsley.Parsley
import parsley.position

object typeParser {

    lazy val parseType: Parsley[Type] = 
        matrixType | primitive

    private lazy val matrixType: Parsley[MatrixType] = 
        MatrixType(premodifiers, "mat<" ~> primitive, "," ~> natural32, "," ~> natural32) <~ ">"

    private lazy val primitive: Parsley[Primitive] = 
        PrimitiveTypeDisambiguator(premodifiers, 
            (
            ("int" as ParseInt32) | 
            ("char" as ParseChar)
            ),
            postmodifiers)
    private lazy val premodifiers: Parsley[List[TypeModifier]] = 
        ("const" as Constant) <::> premodifiers | pure(Nil)

    private lazy val postmodifiers: Parsley[List[TypeModifier]] = 
        ("const" as Constant) <::> postmodifiers | pure(Nil)
}