package frontend

import llc.ast._
import typeParser._
import statementParser._
import lexer._

import parsley.Parsley
import parsley.Parsley.{many, pure}
import parsley.combinator.{sepBy1}
import lexer.implicits.implicitSymbol

object functionParser {

  /** Parses a single linal function, including the statements, until the
    * closing brace.
    */
  lazy val parseFunction: Parsley[FunctionDefinition] =
    "def" ~> FunctionDefinition(
      ident,
      "(" ~> parameterList <~ ")",
      ":" ~> parseType,
      "{" ~> parseStatements <~ "}"
    )

  /** Parses the parameters contained within the '(' ')' characters. Can either
    * be void, or a list of typed parameters.
    */
  private lazy val parameterList: Parsley[List[Parameter]] =
    ("void" ~> pure(List(VoidParam))) |
      sepBy1(param, ",")

  /** Parses a single, non-void parameter. */
  private lazy val param: Parsley[FunctionParameter] =
    FunctionParameter(parseType, ident, ("*" ~> pure(true)) | pure(false))
}
