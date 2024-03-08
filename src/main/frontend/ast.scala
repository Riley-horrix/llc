package frontend

import parsley.Parsley
import parsley.position.pos
import parsley.generic.{ErrorBridge, ParserBridge1}
import parsley.syntax.zipped.Zipped2
import parsley.generic

object ast {

  
  case class LProgram(expr: Expression)
  
  object LProgram extends ParserBridge1[Expression, LProgram]
  
  sealed trait Expression 

  sealed trait Atom extends Expression
  case class Int32(numb: Int)(val pos: (Int, Int)) extends Atom
  object Int32 extends PosParserBridge1[Int, Atom]
  
  sealed trait BinaryOperator extends Expression
  case class Add(lhs: Expression, rhs: Expression)(val pos: (Int, Int)) extends BinaryOperator
  object Add extends PosParserBridge2[Expression, Expression, Add]


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

}