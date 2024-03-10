package frontend

import java.io.File

import parsley.Parsley
import lexer._
import ast._

import lexer.implicits.implicitSymbol
import scala.util.Failure
import scala.util.Success

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

    private lazy val parser: Parsley[LProgram] = {
        fully(program)
    }

    private lazy val program: Parsley[LProgram] = {
        "main" ~> LProgram(expression)
    }

    private lazy val expression: Parsley[Expression] = {
        Add(Number(numb), Number(numb))
    }
}
