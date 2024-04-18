package llc

import java.io.File

import frontend._
import llcerror._
import ast.LinalFile

object Main {

  private final val Usage = "Usage : ./llc <filename>"

  private var filenameToCompile: Option[String] = None

  /** Main entrance function for the llc compiler. */
  def main(args: Array[String]): Unit = {
    args.headOption match {
      case None => execute()

      case arg @ Some(filename: String) => {
        filenameToCompile = arg
        main(args.tail)
      }
    }
  }

  /** Starts program execution. */
  private def execute(): Unit = {
    filenameToCompile match {
      case None => println(Usage)
      case Some(filename) =>
        validateFile(filename) match {
          case Left(err) => println(err)
          case Right(file) =>
            frontend(file) match {
              case Left(err) =>
                println(err.toString());
                println(s"Returned with exit code: ${err.errorCode}")
              case Right(prog) => // TODO : Backend Here
            }
        }
    }
  }

  /** Validate a given filename, returning either an error, or the file object.
    */
  private def validateFile(filename: String): Either[String, File] = {
    val file: File = new File(filename)
    file.exists() match {
      case true  => Right(file)
      case false => Left(LLCError(FILE_NOT_FOUND, filename).result())
    }
  }

  /** Run the frontend on a file, on failure, returns an error and an exit code,
    * on success, returns the ast of the file.
    */
  def frontend(file: File): Either[LLCError, LinalFile] =
    syntaxAnalysis(file) match {
      case err: Left[_, _] => err
      case Right(prog)     => Right(prog) // TODO : semantic analysis here
    }

  private def semanticAnalysis(
      program: LinalFile
  ): Either[LLCError, LinalFile] =
    semantic.analyse(program) match {
      case Some(err) => Left(err)
      case None      => Right(program)
    }

  /** Performs syntactic analysis on a file, returns an error and an exit code,
    * on success, returns the parsed ast of the file.
    */
  private def syntaxAnalysis(file: File): Either[LLCError, LinalFile] =
    parser.parseFile(file)
}
