package integration_test

import org.scalatest.matchers.should.Matchers._
import org.scalatest.flatspec.AnyFlatSpec
import java.io.File
import llc._

class frontendIntegration_test extends AnyFlatSpec {

  private final val testSuitePath = "./src/test/cases/"

  behavior of "LLC Frontend"

  it should "be able to fail on linal syntax errors" in {
    val testdir: File = new File(testSuitePath + "syntax")
    val testfiles: List[File] = testdir.listFiles().toList

    runTestSuite(testfiles, 100) shouldBe true

  }

  it should "pass on valid linal files" in {
    val testdir: File = new File(testSuitePath + "valid")
    val testfiles: List[File] = testdir.listFiles().toList

    runTestSuite(testfiles, 0) shouldBe true
  }

  ignore should "be able to fail on linal semantic errors" in {
    val testdir: File = new File(testSuitePath + "semantic")
    val testfiles: List[File] = testdir.listFiles().toList

    runTestSuite(testfiles, 0) shouldBe true
  }

  private def runTestSuite(files: List[File], expected: Int): Boolean = {
    files.forall((file: File) =>
      Main.frontend(file) match {
        case Left((err, retval)) if expected != 0 => retval == expected
        case Right(value) if expected == 0        => true
        case _                                    => false
      }
    )
  }
}
