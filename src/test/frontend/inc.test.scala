package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.includesParser._
import llc.ast._

import parsley.Success
import frontend.lexer.fully

class inc_test extends AnyFlatSpec {

  behavior of "Include parser"

  it should "be able to parse simple include files" in {
    parseInclude.parse("include <stdio.h>") should matchPattern {
      case Success(LibInclude("stdio.h")) =>
    }
    parseInclude.parse("include  <stdlib.h>") should matchPattern {
      case Success(LibInclude("stdlib.h")) =>
    }

    parseInclude.parse("include \"my_file.h\"") should matchPattern {
      case Success(LocalInclude("my_file.h")) =>
    }
    fully(parseInclude).parse(
      "  include \"a-silly-name.h\""
    ) should matchPattern { case Success(LocalInclude("a-silly-name.h")) => }
  }
}
