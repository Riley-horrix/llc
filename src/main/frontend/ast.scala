package frontend

import parsley.Parsley
import parsley.position.pos
import parsley.generic.{ErrorBridge, ParserBridge1, ParserBridge3}
import parsley.syntax.zipped.{Zipped2, Zipped3, Zipped4}
import parsley.generic

object ast {

  private type Position = (Int, Int)

  // Program elements include any top level Linal program elements, like 
  // includes, #defines, functions etc
  sealed trait ProgramElement
  case class LinalFile(elements: List[ProgramElement])(val pos: Position)

  sealed trait Include extends ProgramElement

  case class LocalInclude(include: String)(val pos: Position) extends Include
  case class LibInclude(include: String)(val pos: Position) extends Include


  sealed trait Definition extends ProgramElement

  case class FunctionParameter(paramType: Type, name: String)(val pos: Position)
  case class FunctionDefinition(funcType: Type, name: String, params: List[FunctionParameter], body: List[Statement])(val pos: Position) extends Definition

  sealed trait Type
  sealed trait Primitive extends Type

  case class MatrixType(matrixType: Primitive, rows: Int, cols: Int) extends Type
  object Int32Type extends Primitive
  object CharType extends Primitive

  sealed trait Statement
  
  // -------------------------- Companion Objects -------------------------- //
  
  object LinalFile extends PosParserBridge1[List[ProgramElement], LinalFile]
  
  object LocalInclude extends PosParserBridge1[String, Include]
  object LibInclude extends PosParserBridge1[String, Include]

  object FunctionParameter extends PosParserBridge2[Type, String, FunctionParameter]
  object FunctionDefinition extends PosParserBridge4[Type, String, List[FunctionParameter], List[Statement], FunctionDefinition]

  object MatrixType extends ParserBridge3[Primitive, Int, Int, MatrixType]

  // ----------------- Position Parser Bridge Definitions ----------------- //
  
  trait PosParserBridgeSingleton[+A] extends ErrorBridge {
    def con(pos: Position): A
    def from(arg: Parsley[_]): Parsley[A] = pos.map(this.con(_)) <~ arg
    final def <#(op: Parsley[_]): Parsley[A] = this from op
  }
  
  trait PosParserBridge1[-A, +B] extends PosParserBridgeSingleton[A => B] {
    def apply(a: A)(pos: Position): B
    def apply(a: Parsley[A]): Parsley[B] = pos <**> a.map(this(_) _)
    override final def con(pos: Position): A => B = this.apply(_)(pos)
  }
  
  trait PosParserBridge2[-A, -B, +C]
  extends PosParserBridgeSingleton[(A, B) => C] {
    def apply(a: A, b: B)(pos: Position): C
    def apply(a: Parsley[A], b: Parsley[B]): Parsley[C] =
      pos <**> (a, b).zipped(this.apply(_, _) _)
      override final def con(pos: Position): (A, B) => C = this.apply(_, _)(pos)
    }

  trait PosParserBridge3[-A, -B, -C, +D]
  extends PosParserBridgeSingleton[(A, B, C) => D] {
    def apply(a: A, b: B, c: C)(pos: Position): D
    def apply(a: Parsley[A], b: Parsley[B], c: Parsley[C]): Parsley[D] =
      pos <**> (a, b, c).zipped(this.apply(_, _, _) _)
      override final def con(pos: Position): (A, B, C) => D = this.apply(_, _, _)(pos)
    }

  trait PosParserBridge4[-A, -B, -C, -D, +E]
  extends PosParserBridgeSingleton[(A, B, C, D) => E] {
    def apply(a: A, b: B, c: C, d: D)(pos: Position): E
    def apply(a: Parsley[A], b: Parsley[B], c: Parsley[C], d: Parsley[D]): Parsley[E] =
      pos <**> (a, b, c, d).zipped(this.apply(_, _, _, _) _)
      override final def con(pos: Position): (A, B, C, D) => E = this.apply(_, _, _, _)(pos)
    }
  }