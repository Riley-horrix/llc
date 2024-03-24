package parser_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec

import frontend.includesParser._
import frontend.ast._

import parsley.Success

object inc_test extends AnyFlatSpec {

    behavior of "Include parser"

    it should "be able to parse simple include files" in {
        parseIncludes.parse("#include <stdio.h>") should matchPattern {case Success(LibInclude("stdio.h")) => }
        parseIncludes.parse("#  include  <stdlib.h>") should matchPattern {case Success(LibInclude("stdlib.h")) =>}
        
        parseIncludes.parse("#include \"my_file.h\"") should matchPattern {case Success(LocalInclude("my_file.h")) =>}
        parseIncludes.parse("  # include \"a-silly-name.h\"") shouldBe matchPattern {case Success(LocalInclude("a-silly-name.h")) =>}
    }
}