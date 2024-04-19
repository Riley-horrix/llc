package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.functionParser._
import llc.ast._

import parsley.Success

class function_test extends AnyFlatSpec {
  behavior of "Function parser"

  it should "be able to parse simple functions" in {
    parseFunction.parse(
      "def add2(int x): int { return x + 2; }"
    ) should matchPattern {
      case Success(
            FunctionDefinition(
              "add2",
              List(FunctionParameter(Type(Nil, IntType, Nil), "x", false)),
              Type(Nil, IntType, Nil),
              List(Return(Addition(Ident("x", _), IntLiteral(2))))
            )
          ) =>
    }

    parseFunction.parse(
      "def add(int x, int y): int { return x + y; }"
    ) should matchPattern {
      case Success(
            FunctionDefinition(
              "add",
              List(
                FunctionParameter(Type(Nil, IntType, Nil), "x", false),
                FunctionParameter(Type(Nil, IntType, Nil), "y", false)
              ),
              Type(Nil, IntType, Nil),
              List(Return(Addition(Ident("x", _), Ident("y", _))))
            )
          ) =>
    }
  }

  it should "be able to parse vararg functions" in {
    parseFunction.parse(
      "def printf(char* string, void* args*): void {}"
    ) should matchPattern {
      case Success(
            FunctionDefinition(
              "printf",
              List(
                FunctionParameter(
                  Type(Nil, PointerType(CharType, 1), Nil),
                  "string",
                  false
                ),
                FunctionParameter(
                  Type(Nil, PointerType(VoidType, 1), Nil),
                  "args",
                  true
                )
              ),
              Type(Nil, VoidType, Nil),
              Nil
            )
          ) =>
    }
  }
}
