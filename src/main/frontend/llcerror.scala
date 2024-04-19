package frontend

import parsley.errors.Token
import parsley.syntax.extension
import scala.annotation.tailrec

object llcerror {

  def intersperce[A](
      coll: Seq[A],
      foreach: (A => _),
      between: => Any
  ): Any =
    coll match {
      case head :: (next :: Nil) =>
        foreach(head); between
      case head :: next => intersperce(next, foreach, between)
      case Nil          => null
    }

  final val INTERNAL_ERROR = -1
  final val FILE_IO_ERROR = -2
  final val FILE_NOT_FOUND = -3

  final val VALID_FILE = 0
  final val PARSER_ERROR = 100
  final val SEMANTIC_ERROR = 200

  private val errorStart: String = "LLC Compiler Error ::"
  private def errorIdent(
      errType: String,
      filename: String,
      pos: (Int, Int)
  ): StringBuilder =
    new StringBuilder(errType)
      .append(" error in file ")
      .append(filename)
      .append(" {line : ")
      .append(pos._1)
      .append(", col : ")
      .append(pos._2)
      .append("} -")

  private type ErrorT = StringBuilder

  abstract class LLCError {
    def reachedMaxErrors: Boolean = false;
    def result(): String
    def errorCode: Int
  }

  class DefaultError(errorMsg: ErrorT, code: Int) extends LLCError {
    override def result(): String = errorMsg.insert(0, errorStart).result()
    override def errorCode: Int = code
  }

  type Err = LLCError

  class ParserErrorBuilder extends parsley.errors.ErrorBuilder[LLCError] {

    override type UnexpectedLine = StringBuilder
    override type ExpectedLine = StringBuilder
    override type ExpectedItems = StringBuilder
    override type Messages = StringBuilder
    override type Message = String
    override type Source = String
    override type Position = (Int, Int)
    override type ErrorInfoLines = StringBuilder
    override type Item = String

    override def format(
        pos: Position,
        source: Source,
        lines: ErrorInfoLines
    ): LLCError =
      new DefaultError(
        new StringBuilder(errorStart)
          .append(" ")
          .append(errorIdent("syntax", source, pos))
          .append("\n")
          .append(lines),
        PARSER_ERROR
      )

    override def pos(line: Int, col: Int): Position = (line, col)

    override def source(sourceName: Option[String]): Source =
      sourceName.getOrElse(
        "stdin"
      ) // Assume that if source isn't a file, must be stdin

    override def vanillaError(
        unexpected: UnexpectedLine,
        expected: ExpectedLine,
        reasons: Messages,
        line: LineInfo
    ): ErrorInfoLines = new StringBuilder()
      .append(unexpected)
      .append("\n")
      .append(expected)
      .append("\n")
      .append(reasons)
      .append("\n")
      .append(line)

    override def specializedError(
        msgs: Messages,
        line: LineInfo
    ): ErrorInfoLines = msgs.append(lineSeparator).append(line)

    private final val expectedSeparator = " | "

    override def combineExpectedItems(alts: Set[Item]): ExpectedItems = {
      val sb: StringBuilder = new StringBuilder()
      val altsList: List[Item] = alts.toList
      altsList.headOption match {
        case None =>
        case Some(item) =>
          sb.append(item);
          altsList.tail.foreach((item: String) =>
            sb.append(expectedSeparator).append(item)
          )
      }
      sb
    }

    private final val messageSeparator = "\n\t"
    override def combineMessages(alts: Seq[Message]): Messages = {
      val sb: StringBuilder = new StringBuilder()
      alts.headOption match {
        case None =>
        case Some(item) =>
          sb.append(item);
          alts.tail.foreach((item: String) =>
            sb.append(messageSeparator).append(item)
          )
      }
      sb
    }

    private final val unexpectedStart = "  > Unexpected : "
    private final val expectedStart = "  > Expected   : "
    override def unexpected(item: Option[Item]): UnexpectedLine =
      new StringBuilder(unexpectedStart).append(item.getOrElse("unknown"))

    override def expected(alts: ExpectedItems): ExpectedLine =
      new StringBuilder(expectedStart).append(alts)

    override def reason(reason: String): Message = reason

    override def message(msg: String): Message = msg

    override type LineInfo = StringBuilder
    private final def lineSeparator = "\n"
    private final def lineStart = "> "
    private final def linePointerChar = "^"
    private final def lineGapChar = " "
    private def formatLinePointer(pointsAt: Int, width: Int): StringBuilder =
      formatLinePointerRec(pointsAt, width, new StringBuilder())
    @tailrec
    private def formatLinePointerRec(
        pointsAt: Int,
        width: Int,
        sb: StringBuilder
    ): StringBuilder = pointsAt match {
      case 0 =>
        width match {
          case 0 => sb
          case _ =>
            sb.append(linePointerChar);
            formatLinePointerRec(pointsAt, width - 1, sb)
        }
      case _ =>
        sb.append(lineGapChar); formatLinePointerRec(pointsAt - 1, width, sb)
    }
    override def lineInfo(
        line: String,
        linesBefore: Seq[String],
        linesAfter: Seq[String],
        errorPointsAt: Int,
        errorWidth: Int
    ): LineInfo = {
      val sb = new StringBuilder()
      linesBefore.foreach((line: String) =>
        sb.append(lineStart).append(line).append(lineSeparator)
      )
      sb.append(lineStart).append(line).append(lineSeparator)
      sb.append(lineStart.map(_ => ' '))
        .append(formatLinePointer(errorPointsAt, errorWidth))
        .append(lineSeparator)

      linesAfter.foreach((line: String) =>
        sb.append(lineStart).append(line).append(lineSeparator)
      )
      sb
    }

    override val numLinesBefore: Int = 2

    override val numLinesAfter: Int = 2

    override type Raw = String
    override type Named = String
    override type EndOfInput = String
    // type Token = String

    override def raw(item: String): Raw = item

    override def named(item: String): Named = item

    override val endOfInput: EndOfInput = "eof"

    override def unexpectedToken(
        cs: Iterable[Char],
        amountOfInputParserWanted: Int,
        lexicalError: Boolean
    ): Token = parsley.errors.tokenextractors.TillNextWhitespace
      .unexpectedToken(cs)

  }

  object LLCError {
    def apply(identifier: Int, filename: String): LLCError = identifier match {
      case FILE_IO_ERROR =>
        new DefaultError(
          new StringBuilder("A file IO error has occurred!"),
          identifier
        )
      case INTERNAL_ERROR =>
        new DefaultError(
          new StringBuilder("An internal error has occurred! (Apologies)"),
          identifier
        )
      case FILE_NOT_FOUND =>
        new DefaultError(
          new StringBuilder(s"The file '${filename}'' cannot be found!"),
          identifier
        )
    }
  }
}
