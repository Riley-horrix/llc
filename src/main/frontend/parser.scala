package frontend

import lexer._
import ast._

import java.io.File

import parsley.Parsley
import lexer.implicits.implicitSymbol
import parsley.Parsley.many

import scala.util.Failure
import scala.util.Success
import parsley.internal.deepembedding.backend.Local

object parser {

    type LLCError = String

    // def parseString(str: String): Either[LLCError, LProgram] = {

    // }

    def parseFile(file: File): Either[LLCError, LProgram] = {
        parser.parseFile(file) match {
            case Failure(exception) => Left(exception.toString())
            case Success(value) => value match {
                case err: parsley.Failure[_] => Left(err.toString())
                case parsley.Success(x) => Right(x)
            }
        }
    }

    private lazy val parser: Parsley[LProgram] =
        fully(parseProgram)

    private lazy val parseProgram: Parsley[LProgram] = 
        LProgram(parseIncludes)

    private lazy val parseIncludes: Parsley[List[Include]] = 
        many("#" ~> "include" ~> parseInclude)

    private lazy val parseInclude: Parsley[Include] = 
        "<" ~> LibInclude(parseIncl) <~ ">" | 
        "\"" ~> LocalInclude(includeFile) <~ "\"" 

    // private lazy val expression: Parsley[Expression] = {
    //     Add(Number(numb), Number(numb))
    // }
}
