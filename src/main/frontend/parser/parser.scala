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
import parsley.errors.ErrorBuilder

object parser {

  /** Parse a file, returns either an error in which toString() can be called to
    * print to console, or returns a LinalFile ast node, containing all of the
    * elements of the program.
    */
  def parseFile(file: File): Either[LLCError, LinalFile] = {
    implicit val errorBuilder: ErrorBuilder[LLCError] = new ParserErrorBuilder()
    parser.parseFile(file) match {
      case Failure(exception) => Left(LLCError(FILE_IO_ERROR, file.getName()))
      case Success(value) =>
        value match {
          case parsley.Failure(error) =>
            Left(error) // TODO
          case parsley.Success(prog) => {
            Right(prog)
          }
        }
    }
  }

  /** The main parser entry point. */
  private lazy val parser: Parsley[LinalFile] =
    fully(parseProgram)

  /** Parses zero or more program elements. */
  private lazy val parseProgram: Parsley[LinalFile] =
    LinalFile(many(parseProgramElement))

  /** Parser for a single program element. */
  private lazy val parseProgramElement: Parsley[ProgramElement] =
    parseFunction |
      parseVariableDefinition <~ ";" |
      parseInclude

}
