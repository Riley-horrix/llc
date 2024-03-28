package frontend

import llc.ast._
import includesParser._
import lexer.implicits.implicitSymbol
import lexer.fully
import llcerror._
import functionParser._
import statementParser._

import parsley.Parsley, Parsley.many
import scala.util.Failure
import scala.util.Success
import java.io.File

object parser {

  def parseFile(file: File): Either[LLCError, LinalFile] = {
    parser.parseFile(file) match {
      case Failure(exception) => Left(FILE_IO_ERROR)
      case Success(value) =>
        value match {
          case err: parsley.Failure[_] => Left(err.toString())
          case parsley.Success(prog)   => Right(prog)
        }
    }
  }

  private lazy val parser: Parsley[LinalFile] =
    fully(parseProgram)

  private lazy val parseProgram: Parsley[LinalFile] =
    LinalFile(many(parseProgramElement))

  // TODO : macros / defines
  private lazy val parseProgramElement: Parsley[ProgramElement] =
    parseFunction |
      parseVariableDefinition <~ ";" |
      parseInclude

}
