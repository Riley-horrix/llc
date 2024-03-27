package frontend

import parsley.Parsley
import parsley.position.pos
import parsley.generic.{ErrorBridge, ParserBridge1, ParserBridge2, ParserBridge3, ParserBridge4}
import parsley.syntax.zipped.{Zipped2, Zipped3, Zipped4}
import parsley.generic

object ast {

  private type Position = (Int, Int)

  // Program elements include any top level Linal program elements, like 
  // includes, #defines, functions etc
  sealed trait ProgramElement
  case class LinalFile(elements: List[ProgramElement])

  // Include files
  sealed trait Include extends ProgramElement
  case class LocalInclude(include: String) extends Include
  case class LibInclude(include: String) extends Include

  sealed trait Definition extends ProgramElement
  // Functions
  case class FunctionParameter(paramType: Type, name: String)
  case class FunctionDefinition(funcType: Type, name: String, params: List[FunctionParameter], body: List[Statement]) extends Definition
  // Definitions
  case class VariableDefinition(varType: Type, name: String, value: Expr) extends Definition with Statement

  // Types
  case class Type(premodifiers: List[TypePreModifier], baseType: SubType, postModifiers: List[TypePostModifier])
  sealed trait SubType
  sealed trait BaseType extends SubType
  object IntType extends BaseType
  object CharType extends BaseType
  case class MatrixType(matrixType: Type, rows: Int, cols: Int) extends BaseType
  case class PointerType(ptr: BaseType, dimension: Int) extends SubType

  // Int sizes
  sealed trait IntSize
  object Size32 extends IntSize

  // Type modifiers
  sealed trait TypePreModifier
  sealed trait TypePostModifier
  object Constant extends TypePreModifier with TypePostModifier

  sealed trait Statement

  // Expressions
  sealed trait Expr
  sealed trait ArithBinop extends Expr
  // TODO : Simplify names
  case class Addition(exprL: Expr, exprR: Expr) extends ArithBinop
  case class Subtraction(exprL: Expr, exprR: Expr) extends ArithBinop
  case class Multiplication(exprL: Expr, exprR: Expr) extends ArithBinop

  sealed trait Unop extends Expr
  case class Negate(expr: Expr) extends Unop

  // Atoms
  sealed trait Atom extends Expr
  case class IntLiteral(value: Long) extends Atom
  
  // -------------------------- Companion Objects -------------------------- //
  
  // File object
  object LinalFile extends ParserBridge1[List[ProgramElement], LinalFile]
  
  // Include files
  object LocalInclude extends ParserBridge1[String, LocalInclude]
  object LibInclude extends ParserBridge1[String, LibInclude]

  // Functions
  object FunctionParameter extends ParserBridge2[Type, String, FunctionParameter]
  object FunctionDefinition extends ParserBridge4[Type, String, List[FunctionParameter], List[Statement], FunctionDefinition]
  // Definitions
  object VariableDefinition extends ParserBridge3[Type, String, Expr, VariableDefinition]

  // Types
  object Type extends ParserBridge3[List[TypePreModifier], SubType, List[TypePostModifier], Type]

  // ----- Expressions -----
  // Binops
  object Addition extends ParserBridge2[Expr, Expr, Addition]
  object Subtraction extends ParserBridge2[Expr, Expr, Subtraction]
  object Multiplication extends ParserBridge2[Expr, Expr, Multiplication]
  // Unops
  object Negate extends ParserBridge1[Expr, Negate]
  // Atoms
  object IntLiteral extends ParserBridge1[Long, IntLiteral]

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