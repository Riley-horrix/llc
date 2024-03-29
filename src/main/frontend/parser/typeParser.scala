package frontend

import llc.ast._
import lexer.implicits.implicitSymbol
import lexer.natural32
import llcerror._

import parsley.Parsley.pure
import parsley.Parsley
import parsley.position

import parsley.generic._
import parsley.expr.precedence
import parsley.expr.Postfix
import parsley.expr.Ops
import parsley.errors.combinator._
import parsley.Parsley.atomic

object typeParser {

  private sealed trait ParsedType

  private case class ParsedPointer(ptr: ParsedType) extends ParsedType
  private object ParsedPointer extends ParserBridge1[ParsedType, ParsedPointer]

  private case class ParsedMatrix(matType: Type, rows: Int, cols: Int)
      extends ParsedType
  private object ParsedMatrix
      extends ParserBridge3[Type, Int, Int, ParsedMatrix]

  private object ParsedInt extends ParsedType
  private object ParsedChar extends ParsedType
  private object ParsedVoidType extends ParsedType

  private object TypeDisambiguator
      extends ParserBridge1[ParsedType, ActualType] {
    def apply(parsedType: ParsedType): ActualType = parsedType match {
      case ptr: ParsedPointer => handleParsedPointer(ptr)
      case parsedType         => translateParsedType(parsedType)
    }
  }

  private def translateParsedType(parsedType: ParsedType): BaseType =
    parsedType match {
      case ParsedMatrix(matType, rows, cols) => MatrixType(matType, rows, cols)
      case ParsedInt                         => IntType
      case ParsedChar                        => CharType
      case ParsedVoidType                    => VoidType
      case _: ParsedPointer =>
        println(INTERNAL_ERROR); sys.exit(INTERNAL_ERROR_TRANSLATE_POINTER)
    }

  private def translatePointerType(ptrType: ParsedType): PtrType =
    ptrType match {
      case ParsedMatrix(matType, rows, cols) => MatrixType(matType, rows, cols)
      case ParsedInt                         => IntType
      case ParsedChar                        => CharType
      case ParsedVoidType                    => VoidType
      case _: ParsedPointer =>
        println(INTERNAL_ERROR); sys.exit(INTERNAL_ERROR_TRANSLATE_POINTER)
    }

  private def handleParsedPointer(
      ptr: ParsedPointer,
      dim: Int = 1
  ): PointerType = ptr match {
    case ParsedPointer(ptr) =>
      ptr match {
        case ptr: ParsedPointer => handleParsedPointer(ptr, dim + 1)
        case ptrType => PointerType(translatePointerType(ptrType), dim)
      }
  }

  /** Parse a single linal type definition. */
  lazy val parseType: Parsley[Type] =
    Type(premodifiers, actualType, postmodifiers)

  // Pre and post type modifier keywords.
  private lazy val premodifiers: Parsley[List[TypePreModifier]] =
    ("const" as Constant) <::> premodifiers | pure(Nil)
  private lazy val postmodifiers: Parsley[List[TypePostModifier]] =
    ("const" as Constant) <::> postmodifiers | pure(Nil)

  /** Parser for the actual type of a variable, this is either a base type, or a
    * pointer to a base type, or a pointer to a pointer.
    */
  private lazy val actualType: Parsley[ActualType] = TypeDisambiguator(
    precedence(baseType)(
      Ops(Postfix)(ParsedPointer from "*")
    )
  )

  /** The parser for a linal base type, i.e. int, char, matrices, etc... */
  private lazy val baseType: Parsley[ParsedType] =
    ("void" as ParsedVoidType) |
      ("int" as ParsedInt) |
      ("char" as ParsedChar) |
      ("mat<" ~> ParsedMatrix(
        parseType,
        "," ~> natural32,
        "," ~> natural32
      ) <~ ">") |
      (atomic("vec<") ~> ParsedMatrix(
        parseType,
        "," ~> natural32,
        pure(1)
      ) <~ ">") |
      ("vecT<" ~> ParsedMatrix(parseType, pure(1), "," ~> natural32) <~ ">")
}
