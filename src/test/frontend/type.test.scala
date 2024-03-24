package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.typeParser._
import frontend.ast._

import parsley.Success
import javax.print.attribute.standard.MediaSize.Other

object type_test extends AnyFlatSpec {
    behavior of "Type parser"

    it should "be able to parse primitive types" in {
        parseType.parse("int") should matchPattern {case Success(OtherType("int")) =>}
        parseType.parse("char") should matchPattern {case Success(OtherType("char")) =>}
      }

    ignore should "be able to parse matrix types" in {
      parseType.parse("int3x2") should matchPattern {case Success(MatrixType(OtherType("int"), 3, 2)) =>} 
    }
      
      it should "be able to parse user types" in {
        parseType.parse("myType_T") should matchPattern {case Success(OtherType("myType_T")) =>}
        parseType.parse("bool") should matchPattern {case Success(OtherType("bool")) =>}
    }
}