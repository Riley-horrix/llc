package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.typeParser._
import llc.ast._

import parsley.Success
import javax.print.attribute.standard.MediaSize.Other
import org.scalatest.Ignore
import java.awt.Point

class type_test extends AnyFlatSpec {
  behavior of "Type parser"

  it should "be able to parse primitive types" in {
    parseType.parse("int") should matchPattern {
      case Success(Type(Nil, IntType, Nil)) =>
    }
    parseType.parse("char") should matchPattern {
      case Success(Type(Nil, CharType, Nil)) =>
    }
  }

  it should "be able to parse matrix types" in {
    parseType.parse("mat<int, 3,2>") should matchPattern {
      case Success(Type(Nil, MatrixType(Type(Nil, IntType, Nil), 3, 2), Nil)) =>
    }
    parseType.parse("mat<int, 1, 5>") should matchPattern {
      case Success(Type(Nil, MatrixType(Type(Nil, IntType, Nil), 1, 5), Nil)) =>
    }
    parseType.parse("mat<char, 3, 2>") should matchPattern {
      case Success(
            Type(Nil, MatrixType(Type(Nil, CharType, Nil), 3, 2), Nil)
          ) =>
    }
  }

  it should "be able to parse vector types" in {
    parseType.parse("vec<int, 5>") should matchPattern {
      case Success(Type(Nil, MatrixType(Type(Nil, IntType, Nil), 5, 1), Nil)) =>
    }
    parseType.parse("vec<vec<int, 5>, 3>") should matchPattern {
      case Success(
            Type(
              Nil,
              MatrixType(
                Type(Nil, MatrixType(Type(Nil, IntType, Nil), 5, 1), Nil),
                3,
                1
              ),
              Nil
            )
          ) =>
    }
    parseType.parse("vecT<int, 3>") should matchPattern {
      case Success(Type(Nil, MatrixType(Type(Nil, IntType, Nil), 1, 3), Nil)) =>
    }
    parseType.parse("vecT<vec<int, 5>, 3>") should matchPattern {
      case Success(
            Type(
              Nil,
              MatrixType(
                Type(Nil, MatrixType(Type(Nil, IntType, Nil), 5, 1), Nil),
                1,
                3
              ),
              Nil
            )
          ) =>
    }
  }

  it should "be able to parse modifiers" in {
    parseType.parse("const int") should matchPattern {
      case Success(Type(List(Constant), IntType, Nil)) =>
    }
    parseType.parse("const char") should matchPattern {
      case Success(Type(List(Constant), CharType, Nil)) =>
    }
    parseType.parse("const mat<const int, 6, 6>") should matchPattern {
      case Success(
            Type(
              List(Constant),
              MatrixType(Type(List(Constant), IntType, Nil), 6, 6),
              Nil
            )
          ) =>
    }
  }

  it should "be able to parse pointer types" in {
    parseType.parse("int*") should matchPattern {
      case Success(Type(Nil, PointerType(IntType, 1), Nil)) =>
    }
    parseType.parse("int *") should matchPattern {
      case Success(Type(Nil, PointerType(IntType, 1), Nil)) =>
    }
    parseType.parse("int **") should matchPattern {
      case Success(Type(Nil, PointerType(IntType, 2), Nil)) =>
    }
    parseType.parse("mat<int*, 3, 2>") should matchPattern {
      case Success(
            Type(
              Nil,
              MatrixType(Type(Nil, PointerType(IntType, 1), Nil), 3, 2),
              Nil
            )
          ) =>
    }
  }

  it should "be able to parse post modifiers" in {
    parseType.parse("int const") should matchPattern {
      case Success(Type(Nil, IntType, List(Constant))) =>
    }
    parseType.parse("char const") should matchPattern {
      case Success(Type(Nil, CharType, List(Constant))) =>
    }
  }
}
