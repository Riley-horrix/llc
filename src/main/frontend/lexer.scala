package frontend

import parsley.token.Lexer
import parsley.token.descriptions._
import parsley.token.descriptions.numeric.PlusSignPresence
import parsley.token.descriptions.text.EscapeDesc
import parsley.token.descriptions.text.TextDesc
import parsley.token.predicate.Unicode
import parsley.Parsley

object lexer {

  private val desc = LexicalDesc(
    NameDesc.plain.copy(
      identifierStart = Unicode(c => Character.isLetter(c) || c == '_'),
      identifierLetter = Unicode(c => Character.isLetterOrDigit(c) || c == '_')
    ),
    SymbolDesc.plain.copy(
      hardKeywords = Set(
        "int"
      ),
      hardOperators = Set(
        "+"
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
      lineCommentStart = "<>",
      multiLineCommentStart = "<<",
      multiLineCommentEnd = ">>"
    )
  )
  private val lexer = new Lexer(desc)

  val int32 = lexer.lexeme.integer.decimal32
  val implicits = lexer.lexeme.symbol.implicits
  val ident = lexer.lexeme.names.identifier
  def fully[A](p: Parsley[A]): Parsley[A] = lexer.fully(p)
}