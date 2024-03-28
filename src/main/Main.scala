package llc

import java.io.File

import frontend._
import llcerror._
import ast.LinalFile
import parser.parseFile

object Main {

  private final val Usage = "Usage : ./llc <filename>"

  private var filenameToCompile: Option[String] = None

  def main(args: Array[String]): Unit = {
    args.headOption match {
      case None => execute()

      case arg @ Some(filename: String) => {
        filenameToCompile = arg
        main(args.tail)
      }
    }
  }

  private def execute(): Unit = {
    filenameToCompile match {
      case None => println(Usage)
      case Some(filename) =>
        validateFile(filename) match {
          case Left(err) => println(err)
          case Right(file) =>
            frontend(file) match {
              case Left((err, retVal)) =>
                println(err.toString());
                println(s"Returned with exit code: ${retVal}")
              case Right(prog) => // TODO : Backend Here
            }
        }
    }
  }

  private def validateFile(filename: String): Either[LLCError, File] = {
    val file: File = new File(filename)
    file.exists() match {
      case true  => Right(file)
      case false => Left(FILE_NOT_FOUND(filename))
    }
  }

  private def frontend(file: File): Either[(LLCError, Int), LinalFile] =
    syntaxAnalysis(file) match {
      case err: Left[_, _] => err
      case Right(prog)     => Right(prog) // TODO : semantic analysis here
    }

  private def syntaxAnalysis(file: File): Either[(LLCError, Int), LinalFile] =
    parseFile(file) match {
      case Left(error) => Left(error, 100)
      case Right(prog) => Right(prog)
    }
}
