package frontend

import ast._
import typeParser._
import statementParser._
import lexer._

import parsley.Parsley
import parsley.Parsley.{many, pure}
import parsley.combinator.{sepBy1}
import lexer.implicits.implicitSymbol

object functionParser {
  lazy val parseFunction: Parsley[FunctionDefinition] =
    "def" ~> FunctionDefinition(
      ident,
      "(" ~> parameterList <~ ")",
      ":" ~> parseType,
      "{" ~> parseStatements <~ "}"
    )

  private lazy val parameterList: Parsley[List[Parameter]] =
    ("void" ~> pure(List(VoidParam))) |
      sepBy1(param, ",")

  private lazy val param: Parsley[FunctionParameter] =
    FunctionParameter(parseType, ident, ("*" ~> pure(true)) | pure(false))
}
