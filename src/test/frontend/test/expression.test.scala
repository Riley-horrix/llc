package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.expressionParser._
import frontend.ast._

import parsley.Success

class expression_test extends AnyFlatSpec {
    behavior of "Expression parser"

    it should "be able to parse integer atoms" in {
        parseExpression.parse("152") should matchPattern {case Success(IntLiteral(152)) =>}
        parseExpression.parse("-373") should matchPattern {case Success(IntLiteral(-373)) =>}
        parseExpression.parse("0") should matchPattern {case Success(IntLiteral(0)) =>}
        parseExpression.parse("2147483647") should matchPattern {case Success(IntLiteral(2147483647)) =>}
        parseExpression.parse("2147483648") should matchPattern {case Success(IntLiteral(2147483648L)) =>}
    }

    it should "be able to parse simple binary expressions" in {
        parseExpression.parse("1 + 2") should matchPattern {
            case Success(Addition(IntLiteral(1), IntLiteral(2))) =>
        }
        parseExpression.parse("1 + 2 + 3") should matchPattern {
            case Success(Addition(Addition(IntLiteral(1), IntLiteral(2)), IntLiteral(3))) =>
        }
    }
}