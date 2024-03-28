package frontend

import llc.ast._
import lexer.implicits.implicitSymbol
import lexer._
import typeParser._
import expressionParser._

import parsley.Parsley
import parsley.combinator.endBy

object statementParser {
  lazy val parseStatements: Parsley[List[Statement]] =
    endBy(parseStatement, ";")
  private lazy val parseStatement: Parsley[Statement] =
    parseVariableDefinition |
      "return" ~> Return(parseExpression)

  lazy val parseVariableDefinition: Parsley[VariableDefinition] =
    VariableDefinition(parseType, ident, "=" ~> parseExpression)
}
