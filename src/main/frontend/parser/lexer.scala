package frontend

import parsley.token.Lexer
import parsley.token.descriptions._
import parsley.token.descriptions.numeric.PlusSignPresence
import parsley.token.descriptions.text.{EscapeDesc, TextDesc}
import parsley.token.predicate.Unicode
import parsley.Parsley
import parsley.Parsley.{notFollowedBy, atomic}
import parsley.character.{char => chr, digit}

object lexer {

  private val desc = LexicalDesc(
    NameDesc.plain.copy(
      identifierStart = Unicode(c => Character.isLetter(c) || c == '_'),
      identifierLetter = Unicode(c => Character.isLetterOrDigit(c) || c == '_')
    ),
    SymbolDesc.plain.copy(
      hardKeywords = Set(
        "int",
        "char",
        "const"
      ),
      hardOperators = Set(
        "+",
        "-",
        "*"
      )
    ),
    numeric.NumericDesc.plain.copy(
      integerNumbersCanBeBinary = true
    ),
    TextDesc.plain.copy(
      escapeSequences = EscapeDesc.plain.copy(
        escBegin = '\\',
        literals = Set('\'', '"', '\\'),
        mapping = Map(
          "0" -> 0x00,
          "b" -> 0x08,
          "t" -> 0x09,
          "n" -> 0x0a,
          "f" -> 0x0c,
          "r" -> 0x0d
        )
      ),
      graphicCharacter = Unicode(c =>
        c >= ' '.toInt && c <= '~'.toInt && c != '\''.toInt && c != '"'.toInt && c != '\\'.toInt
      )
    ),
    SpaceDesc.plain.copy(
      lineCommentStart = "/>",
      multiLineCommentStart = "/-",
      multiLineCommentEnd = "-/"
    )
  )
  private val lexer = new Lexer(desc)

  val implicits = lexer.lexeme.symbol.implicits
  val ident = lexer.lexeme.names.identifier

  // Use this lexer to parse a negate symbol, otherwise it treats (- int) as
  // integer(-int) rather than negate(int)
  val negateSymbol =
    lexer.lexeme(atomic(chr('-') <~ notFollowedBy(digit)))

  // Integer atom parser
  val integer64 = lexer.lexeme.integer.number64
  // Used in matrix type definitions
  val natural32 = lexer.lexeme.natural.number32

  val character = lexer.lexeme.character.ascii

  def fully[A](p: Parsley[A]): Parsley[A] = lexer.fully(p)
}
