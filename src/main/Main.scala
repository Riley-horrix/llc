package llc

import java.io.File

import frontend._

object Main {

    private final val Usage = "./llc <filename>"

    def main(args: Array[String]): Unit = {
        args.headOption match {
            case None => println(Usage)
            case Some(filename: String) => parser.parseFile(new File(filename)) match {
                case Left(err) => println(err)
                case Right(prog) => println(prog)
            }
        }
    }
}