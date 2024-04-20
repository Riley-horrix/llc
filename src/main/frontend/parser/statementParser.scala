package frontend

import llc.ast._
import lexer.implicits.implicitSymbol
import lexer._
import typeParser._
import expressionParser._

import parsley.Parsley
import parsley.combinator.endBy
import parsley.errors.combinator._

object statementParser {

  /** Parses zero or more linal statements, separated and ended with a ';'. */
  lazy val parseStatements: Parsley[List[Statement]] =
    endBy(
      parseStatement,
      ";"
    )

  /** Parses a single statement, not ended with a ';'. */
  private lazy val parseStatement: Parsley[Statement] =
    parseVariableDefinition |
      "return" ~> Return(parseExpression)

  /** Parse a linal variable definition with form 'type var-name '='
    * expression'. This extra parser is needed since variable definitions are
    * program elements. To be used, ensure that a semi-colon is parsed
    * immediately after.
    */
  lazy val parseVariableDefinition: Parsley[VariableDefinition] =
    VariableDefinition(parseType, ident, "=" ~> parseExpression)
}
