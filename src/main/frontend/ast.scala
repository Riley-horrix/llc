package frontend

import parsley.Parsley
import parsley.position.pos
import parsley.generic.{ErrorBridge, ParserBridge1}
import parsley.syntax.zipped.Zipped2
import parsley.generic
import javax.swing.text.html.HTMLEditorKit.Parser

object ast {

  case class LProgram(includes: List[Include])
  
  sealed trait Include
  case class LocalInclude(include: String) extends Include
  case class LibInclude(include: String) extends Include
  
  trait PosParserBridgeSingleton[+A] extends ErrorBridge {
    def con(pos: (Int, Int)): A
    def from(arg: Parsley[_]): Parsley[A] = pos.map(this.con(_)) <~ arg
    final def <#(op: Parsley[_]): Parsley[A] = this from op
  }
  
  trait PosParserBridge1[-A, +B] extends PosParserBridgeSingleton[A => B] {
    def apply(a: A)(pos: (Int, Int)): B
    def apply(a: Parsley[A]): Parsley[B] = pos <**> a.map(this(_) _)
    override final def con(pos: (Int, Int)): A => B = this.apply(_)(pos)
  }
  
  trait PosParserBridge2[-A, -B, +C]
  extends PosParserBridgeSingleton[(A, B) => C] {
    def apply(a: A, b: B)(pos: (Int, Int)): C
    def apply(a: Parsley[A], b: Parsley[B]): Parsley[C] =
      pos <**> (a, b).zipped(this.apply(_, _) _)
      override final def con(pos: (Int, Int)): (A, B) => C = this.apply(_, _)(pos)
    }
    
    object LProgram extends ParserBridge1[List[Include], LProgram]
    
    object LocalInclude extends ParserBridge1[String, Include]
    object LibInclude extends ParserBridge1[String, Include]
  }