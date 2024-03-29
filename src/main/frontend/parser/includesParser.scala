package frontend

import lexer._
import llc.ast._

import parsley.Parsley
import lexer.implicits.implicitSymbol
import parsley.Parsley.{many, some}
import parsley.character
import parsley.Success
import parsley.Failure

object includesParser {

  /** Parse a single include (import) statement. Lib files are specified with
    * '<filename>', local files are specified with '"path-to-file"'.
    */
  lazy val parseInclude: Parsley[Include] =
    "include" ~> (
      "<" ~> LibInclude(parseIncludeFile) <~ ">" |
        "\"" ~> LocalInclude(parseIncludeFile) <~ "\""
    )

  /** Parse an include file name into a list of characters, and then fold them
    * into a string.
    */
  private lazy val parseIncludeFile: Parsley[String] =
    some(character.noneOf('>', '"'))
      .map((list: List[Char]) => list.foldLeft("")(_ + _))

}
