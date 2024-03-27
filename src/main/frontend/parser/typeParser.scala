package frontend

import ast._
import lexer.implicits.implicitSymbol
import lexer.natural32
import llc.llcerror._

import parsley.Parsley.pure
import parsley.Parsley
import parsley.position

import parsley.debug, debug._
import parsley.generic
import parsley.expr.precedence
import parsley.expr.Postfix
import parsley.expr.Ops
import parsley.errors.combinator._

object typeParser {

  private sealed trait ParsedType

  private case class ParsedPointer(ptr: ParsedType) extends ParsedType
  private object ParsedPointer extends generic.ParserBridge1[ParsedType, ParsedPointer]

  private case class ParsedMatrix(matType: Type, rows: Int, cols: Int) extends ParsedType
  private object ParsedMatrix extends generic.ParserBridge3[Type, Int, Int, ParsedMatrix]

  private object ParsedInt extends ParsedType
  private object ParsedChar extends ParsedType

  private object TypeDisambiguator extends generic.ParserBridge1[ParsedType, SubType] {
    def apply(parsedType: ParsedType): SubType = parsedType match {
      case ptr:ParsedPointer => handleParsedPointer(ptr)
      case parsedType => translateParsedType(parsedType)
    }
  }

  private def translateParsedType(parsedType: ParsedType): BaseType = parsedType match {
    case ParsedMatrix(matType, rows, cols) => MatrixType(matType, rows, cols)
    case ParsedInt => IntType
    case ParsedChar => CharType
    case _:ParsedPointer => ???
  }

  private def handleParsedPointer(ptr: ParsedPointer, dim: Int = 1): PointerType = ptr match {
    case ParsedPointer(ptr) => ptr match {
      case ptr: ParsedPointer => handleParsedPointer(ptr, dim + 1)
      case ptrType => PointerType(translateParsedType(ptrType), dim)
    }
  }

  lazy val parseType: Parsley[Type] = Type(premodifiers, subType, postmodifiers)

  private lazy val premodifiers: Parsley[List[TypePreModifier]] = 
    ("const" as Constant) <::> premodifiers | pure(Nil)
  private lazy val postmodifiers: Parsley[List[TypePostModifier]] = 
    ("const" as Constant) <::> postmodifiers | pure(Nil)

  private lazy val subType: Parsley[SubType] = TypeDisambiguator(precedence(baseType)(
    Ops(Postfix)(ParsedPointer from "*")
  ))

  private lazy val baseType: Parsley[ParsedType] = 
    ("int" as ParsedInt) | 
    ("char" as ParsedChar) |
    ("mat<" ~> ParsedMatrix(parseType, "," ~> natural32, "," ~> natural32) <~ ">")
}
