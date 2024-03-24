package frontend

import ast._
import lexer.implicits.implicitSymbol

import parsley.Parsley
import parsley.combinator.sepBy1

object statementParser {
    lazy val parseStatements: Parsley[List[Statement]] = sepBy1(parseStatement, ";")
    lazy val parseStatement: Parsley[Statement] = ???
}