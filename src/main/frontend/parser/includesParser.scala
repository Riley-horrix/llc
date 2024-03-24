package frontend

import lexer._
import ast._

import parsley.Parsley
import lexer.implicits.implicitSymbol
import parsley.Parsley.{many, some}
import parsley.character

object includesParser {

    lazy val parseIncludes: Parsley[List[Include]] = 
        many("#" ~> "include" ~> parseInclude)

    private lazy val parseInclude: Parsley[Include] = 
        "<" ~> LibInclude(parseIncludeFile) <~ ">" | 
        "\"" ~> LocalInclude(parseIncludeFile) <~ "\"" 

    private lazy val parseIncludeFile: Parsley[String] = 
        some(character.letterOrDigit | character.oneOf(',','-','.','_')).foldLeft("")(_+_)

}
