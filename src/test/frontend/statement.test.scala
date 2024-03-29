package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.statementParser._
import llc.ast._
import tests.testUtils._

import parsley.Success

class statement_test extends AnyFlatSpec {
  behavior of "Statement parser"

  it should "be able to parse variable definitions" in {
    parseStatements.parse("int x = 10;") should matchPattern {
      case Success(
            List(
              VariableDefinition(Type(Nil, IntType, Nil), "x", IntLiteral(10))
            )
          ) =>
    }

    val expr: Expr =
      Multiplication(Subtraction(IntLiteral(10), IntLiteral(4)), IntLiteral(2))
    parseStatements.parse("int var = (10 - 4) * 2;") should matchPattern {
      case Success(
            List(VariableDefinition(Type(Nil, IntType, Nil), "var", expr))
          ) =>
    }

    parseStatements.parse("char chr = c;") should matchPattern {
      case Success(
            List(
              VariableDefinition(Type(Nil, CharType, Nil), "chr", Ident("c"))
            )
          ) =>
    }
  }

  it should "be able to parse return statements" in {
    parseStatements.parse("return 0;") should matchPattern {
      case Success(List(Return(IntLiteral(0)))) =>
    }
  }

  it should "be able to parse multiple statements" in {
    parseStatements.parse("int x = 10 - y; return x;") should matchPattern {
      case Success(
            List(
              VariableDefinition(
                Type(Nil, IntType, Nil),
                "x",
                Subtraction(IntLiteral(10), Ident("y"))
              ),
              Return(Ident("x"))
            )
          ) =>
    }
  }
}
