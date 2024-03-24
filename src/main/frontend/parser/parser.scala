package frontend

import ast._
import includesParser.parseIncludes
import lexer.implicits.implicitSymbol
import lexer.fully

import parsley.Parsley
import scala.util.Failure
import scala.util.Success
import java.io.File

object parser {

    type LLCError = String

    def curlyBraces[A](arg: Parsley[A]): Parsley[A] = "{" ~> arg <~ "}"

     def parseFile(file: File): Either[LLCError, LinalFile] = {
        parser.parseFile(file) match {
            case Failure(exception) => Left(exception.toString())
            case Success(value) => value match {
                case err: parsley.Failure[_] => Left(err.toString())
                case parsley.Success(prog) => Right(prog)
            }
        }
    }

    private lazy val parser: Parsley[LinalFile] =
        fully(parseProgram)

    private lazy val parseProgram: Parsley[LinalFile] = 
        LinalFile(parseIncludes)
}