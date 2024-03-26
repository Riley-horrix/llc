package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.typeParser._
import frontend.ast._
import tests.testUtils._

import parsley.Success
import javax.print.attribute.standard.MediaSize.Other
import org.scalatest.Ignore

class type_test extends AnyFlatSpec {
  behavior of "Type parser"

  it should "be able to parse primitive types" in {
    parseType.parse("int") should matchPattern {case Success(IntType(le, Size32)) =>}
    parseType.parse("char") should matchPattern {case Success(CharType(le)) =>}
  }

  it should "be able to parse matrix types" in {
    parseType.parse("mat<int, 3,2>") should matchPattern {case Success(MatrixType(le, IntType(Nil, Size32), 3, 2)) =>} 
    parseType.parse("mat<int, 1, 5>") should matchPattern {case Success(MatrixType(le, IntType(Nil, Size32), 1, 5)) =>} 
    parseType.parse("mat<char, 3, 2>") should matchPattern {case Success(MatrixType(le, CharType(Nil), 3, 2)) =>} 
  }
}