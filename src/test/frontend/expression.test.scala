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

    it should "be able to parse bracketed expressions" in {
        parseExpression.parse("(1+2)-3") should matchPattern {
            case Success(Subtraction(Addition(IntLiteral(1), IntLiteral(2)), IntLiteral(3))) =>
        }
        parseExpression.parse("1+(2-3)") should matchPattern {
            case Success(Addition(IntLiteral(1), Subtraction(IntLiteral(2), IntLiteral(3)))) =>
        }
    }

    it should "be able to parse non-decimal numbers" in {
        parseExpression.parse("0x1") should matchPattern {case Success(IntLiteral(1)) =>}
        parseExpression.parse("0X10") should matchPattern {case Success(IntLiteral(16)) =>}
        parseExpression.parse("0b1") should matchPattern {case Success(IntLiteral(1)) =>}
        parseExpression.parse("0b11") should matchPattern {case Success(IntLiteral(3)) =>}
        parseExpression.parse("0b11111111") should matchPattern {case Success(IntLiteral(255)) =>}
        parseExpression.parse("0o10") should matchPattern {case Success(IntLiteral(8)) =>}
        parseExpression.parse("0o13") should matchPattern {case Success(IntLiteral(11)) =>}
    }

    it should "be able to parse simple binary expressions" in {
        parseExpression.parse("1 + 2") should matchPattern {
            case Success(Addition(IntLiteral(1), IntLiteral(2))) =>
        }
        parseExpression.parse("1 + 2 + 3") should matchPattern {
            case Success(Addition(Addition(IntLiteral(1), IntLiteral(2)), IntLiteral(3))) =>
        }
        parseExpression.parse("1 - 2 - 3") should matchPattern {
            case Success(Subtraction(Subtraction(IntLiteral(1), IntLiteral(2)), IntLiteral(3))) =>
        }
        parseExpression.parse("1 - 2 + 3") should matchPattern {
            case Success(Addition(Subtraction(IntLiteral(1), IntLiteral(2)), IntLiteral(3))) =>
        }
        parseExpression.parse("1 - 2 + 3 - 4") should matchPattern {
            case Success(Subtraction(Addition(Subtraction(IntLiteral(1), IntLiteral(2)), IntLiteral(3)), IntLiteral(4))) =>
        }
        parseExpression.parse("1 * 4") should matchPattern {
            case Success(Multiplication(IntLiteral(1), IntLiteral(4))) =>
        }
        // Tests basic precedence too
        parseExpression.parse("1 * 2 + 3 * 4") should matchPattern {
            case Success(Addition(Multiplication(IntLiteral(1), IntLiteral(2)), Multiplication(IntLiteral(3), IntLiteral(4)))) =>
        }
    }

    it should "be able to parse simple unary operations" in {
        parseExpression.parse("- 10") should matchPattern {case Success(Negate(IntLiteral(10))) => }
        parseExpression.parse("- 10 + 2") should matchPattern {case Success(Addition(Negate(IntLiteral(10)), IntLiteral(2))) => }
        parseExpression.parse("-(10 + 2)") should matchPattern {case Success(Negate(Addition(IntLiteral(10), IntLiteral(2)))) => }
    }
}