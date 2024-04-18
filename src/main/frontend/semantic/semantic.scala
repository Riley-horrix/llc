package frontend

import llc.ast._
import llcerror._
import scope._

import functionSemantics.analyseFunction

import scala.annotation.tailrec

object semantic {

  /** Run semantic analysis on a program. Returns on error, an LLCError object,
    * on success will return a Verbose Linal File Ast Node.
    */
  def analyse(
      program: LinalFile
  ): Option[LLCError] = program match {
    case LinalFile(programElements) =>
      analyseElements(programElements, new Scope())(
        LLCError(SEMANTIC_ERROR, program.getFilename)
      )
  }

  /** Recursively analyse elements from the elements list, passing in the scope
    * for each.
    */
  @tailrec
  private def analyseElements(
      elements: List[ProgramElement],
      globalScope: Scope
  )(implicit errorBuilder: LLCError): Option[LLCError] = elements match {
    case Nil => None
    case head :: tail =>
      analyseElement(head, globalScope) match {
        case None      => analyseElements(tail, globalScope)
        case Some(err) => Some(err)
      }
  }

  /** Semantically analyse a single program element in a given scope. */
  private def analyseElement(
      element: ProgramElement,
      globalScope: Scope
  )(implicit errorBuilder: LLCError): Option[LLCError] = {
    element match {
      case f: FunctionDefinition => analyseFunction(f, globalScope)
      case d: VariableDefinition => ???
      case inc: Include          => ???
    }
    errorBuilder.reachedMaxErrors match {
      case true  => Some(errorBuilder)
      case false => None
    }
  }
}
