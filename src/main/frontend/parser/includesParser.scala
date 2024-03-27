package frontend

import lexer._
import ast._

import parsley.Parsley
import lexer.implicits.implicitSymbol
import parsley.Parsley.{many, some}
import parsley.character
import parsley.Success
import parsley.Failure

object includesParser {

  lazy val parseInclude: Parsley[Include] =
    "include" ~> (
      "<" ~> LibInclude(parseIncludeFile) <~ ">" |
        "\"" ~> LocalInclude(parseIncludeFile) <~ "\""
    )

  private lazy val parseIncludeFile: Parsley[String] =
    some(character.letterOrDigit | character.oneOf(',', '-', '.', '_')).map(
      (list: List[Char]) => list.foldLeft("")(_ + _)
    )

}
