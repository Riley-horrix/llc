package llc

import llc.ast._
import frontend.llcerror.LLCError
import frontend.llcerror.INTERNAL_ERROR

object typeError extends Enumeration {
  type typeError = Value
  val TYPE_MISMATCH = Value
}

import typeError._

object typeSemantics {

  sealed trait TypeSize
  object Size8 extends TypeSize
  object Size32 extends TypeSize
  object Size64 extends TypeSize
  case class SizeN(numBytes: Int) extends TypeSize

  private def numericArithAllowedTypes(aType: ActualType): Boolean =
    aType match {
      case CharType      => true
      case IntType       => true
      case _: MatrixType => true
      case _             => false
    }

  private def arithAllowedTypes(aType: ActualType): Boolean =
    numericArithAllowedTypes(aType) || (aType match {
      case _: PointerType => true
      case _              => false
    })
  // private val

  /** Check the compatability of two types, and returns the casted to type. Else
    * returns Error type.
    */
  // Note for future self : this should probably take in Type rather than
  // actual type to allow for checking the const modifiers, could also return an error
  def typeCheck(
      aType: Type,
      bType: Type,
      arith: ArithBinop
  ): Either[typeError, Type] = arith match {
    case _: Addition       => ???
    case _: Multiplication => ???
    case _: Subtraction    => ???
  }

  /** Type Checker for addition and subtraction. */
  def checkSimpleArith(
      aType: ActualType,
      bType: ActualType
  ): Option[ActualType] = aType.equals(bType) match {
    case true if !aType.isInstanceOf[PointerType] =>
      ??? // All good if the types same and not pointers or structures
    case false => ???
    case _     => ???
  }

  private def matrixTypeCheck(
      matA: MatrixType,
      matB: MatrixType,
      arith: ArithBinop
  ): Option[ActualType] = matA match {
    case MatrixType(matrixTypeA, rowsA, colsA) =>
      matB match {
        case MatrixType(matrixTypeB, rowsB, colsB) =>
          arith match {
            case _: Addition if rowsA == rowsB && colsA == colsB =>
              typeCheck(matrixTypeA, matrixTypeB, arith)
              Some(matA)
            case _: Multiplication => ???
            case _: Subtraction    => ???
          }
      }
  }

  private implicit val typeOrd: Ordering[ActualType] = ???

  def typeCheck(varType: ActualType, unop: Unop): Option[ActualType] =
    unop match {
      case _: Negate if numericArithAllowedTypes(varType) =>
        Some(varType)
      case _ => None // Semantic Error
    }

  def commonType(aType: ActualType, bType: ActualType): ActualType = ???

  /** Returns the storage size related to a specific type. */
  def getSize(varType: ActualType): TypeSize = varType match {
    case CharType           => Size8
    case IntType            => Size32
    case VoidType           => LLCError.exitGracefully(INTERNAL_ERROR)
    case matrix: MatrixType => getMatrixSize(matrix)
    case _: PointerType     => Size64
  }

  /** Gets the storage size of a matrix. */
  private def getMatrixSize(matrix: MatrixType): TypeSize = matrix match {
    case MatrixType(matrixType, rows, cols) =>
      numBytes(getSize(matrixType.baseType)) * rows * cols match {
        case numb if numb <= 1 => Size8
        case numb if numb <= 4 => Size32
        case numb if numb <= 8 => Size64
        case numb              => SizeN(numb)
      }
  }

  private def numBytes(typeSize: TypeSize): Int = typeSize match {
    case Size8           => 1
    case Size32          => 4
    case Size64          => 8
    case SizeN(numBytes) => numBytes
  }
}
