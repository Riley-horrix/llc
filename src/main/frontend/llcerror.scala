package frontend

import parsley.errors.Token

object llcerror {
  final val INTERNAL_ERROR = -1
  final val FILE_IO_ERROR = -2
  final val FILE_NOT_FOUND = -3

  final val VALID_FILE = 0
  final val PARSER_ERROR = 100
  final val SEMANTIC_ERROR = 200

  private val errorStart: String = "LLC Compiler Error ::"

  abstract class LLCError {
    def reachedMaxErrors: Boolean
    def result(): String
    def errorCode: Int
  }

  class DefaultError(errorMsg: String, code: Int) extends LLCError {
    override def reachedMaxErrors: Boolean = true
    override def result(): String = errorStart + errorMsg
    override def errorCode: Int = code
  }

  type Err = String

  class ParserError
      extends LLCError
      with parsley.errors.ErrorBuilder[LLCError] {

    override def reachedMaxErrors: Boolean = false
    override def result(): String = "TODO"
    override def errorCode: Int = PARSER_ERROR

    override def format(
        pos: Position,
        source: Source,
        lines: ErrorInfoLines
    ): LLCError = ???

    override def pos(line: Int, col: Int): Position = ???

    override def source(sourceName: Option[String]): Source = ???

    override def vanillaError(
        unexpected: UnexpectedLine,
        expected: ExpectedLine,
        reasons: Messages,
        line: LineInfo
    ): ErrorInfoLines = ???

    override def specializedError(
        msgs: Messages,
        line: LineInfo
    ): ErrorInfoLines = ???

    override def combineExpectedItems(alts: Set[Item]): ExpectedItems = ???

    override def combineMessages(alts: Seq[Message]): Messages = ???

    override def unexpected(item: Option[Item]): UnexpectedLine = ???

    override def expected(alts: ExpectedItems): ExpectedLine = ???

    override def reason(reason: String): Message = ???

    override def message(msg: String): Message = ???

    override def lineInfo(
        line: String,
        linesBefore: Seq[String],
        linesAfter: Seq[String],
        errorPointsAt: Int,
        errorWidth: Int
    ): LineInfo = ???

    override val numLinesBefore: Int = ???

    override val numLinesAfter: Int = ???

    override def raw(item: String): Raw = ???

    override def named(item: String): Named = ???

    override val endOfInput: EndOfInput = ???

    override def unexpectedToken(
        cs: Iterable[Char],
        amountOfInputParserWanted: Int,
        lexicalError: Boolean
    ): Token = ???

  }

  object LLCError {
    def apply(identifier: Int, filename: String): LLCError = identifier match {
      case FILE_IO_ERROR =>
        new DefaultError("A file IO error has occurred!", identifier)
      case INTERNAL_ERROR =>
        new DefaultError(
          "An internal error has occurred! (Apologies)",
          identifier
        )
      case FILE_NOT_FOUND =>
        new DefaultError(
          s"The file '${filename}'' cannot be found!",
          identifier
        )
      case PARSER_ERROR   => new ParserError()
      case SEMANTIC_ERROR => ???
    }
  }
}
