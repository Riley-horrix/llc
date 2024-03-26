package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.statementParser._
import frontend.ast._
import tests.testUtils._

import parsley.Success

class statement_test extends AnyFlatSpec {
    behavior of "Statement parser"

    it should "be able to parse variable definitions" in {
        parseStatements.parse("int x = 10;") should matchPattern {
            case Success(List(VariableDefinition(IntType(le, Size32), "x", IntLiteral(10)))) =>
        }

        val expr: Expr = Multiplication(Subtraction(IntLiteral(10), IntLiteral(4)), IntLiteral(2))
        parseStatements.parse("int var = (10 - 4) * 2;") should matchPattern {
            case Success(List(VariableDefinition(IntType(le, Size32), "var", expr))) =>
        }
    }
}