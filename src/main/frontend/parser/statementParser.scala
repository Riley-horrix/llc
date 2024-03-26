package frontend

import ast._
import lexer.implicits.implicitSymbol
import lexer._
import typeParser._
import expressionParser._

import parsley.Parsley
import parsley.combinator.endBy

object statementParser {
  lazy val parseStatements: Parsley[List[Statement]] = endBy(parseStatement, ";")
  private lazy val parseStatement: Parsley[Statement] = 
    VariableDefinition(parseType, ident, "=" ~> parseExpression)
}