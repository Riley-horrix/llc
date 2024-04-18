package integration_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec
import java.io.File
import llc._

import frontend.llcerror._

class frontendIntegration_test extends AnyFlatSpec {

  private final val testSuitePath = "./src/test/cases/"

  behavior of "LLC Frontend"

  ignore should "be able to fail on linal syntax errors" in {
    val testdir: File = new File(testSuitePath + "syntax")
    val testfiles: List[File] = testdir.listFiles().toList

    runTestSuite(testfiles, PARSER_ERROR) shouldBe true

  }

  it should "pass on valid linal files" in {
    val testdir: File = new File(testSuitePath + "valid")
    val testfiles: List[File] = testdir.listFiles().toList

    runTestSuite(testfiles, VALID_FILE) shouldBe true
  }

  ignore should "be able to fail on linal semantic errors" in {
    val testdir: File = new File(testSuitePath + "semantic")
    val testfiles: List[File] = testdir.listFiles().toList

    runTestSuite(testfiles, SEMANTIC_ERROR) shouldBe true
  }

  private def runTestSuite(files: List[File], expected: Int): Boolean = {
    files.forall((file: File) =>
      Main.frontend(file) match {
        case Left(err) if expected != VALID_FILE    => err.errorCode == expected
        case Right(value) if expected == VALID_FILE => true
        case _                                      => false
      }
    )
  }
}
