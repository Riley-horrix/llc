package frontend

import ast._
import typeParser._
import statementParser._
import lexer._
import parser.curlyBraces

import parsley.Parsley
import parsley.Parsley.many
import parsley.combinator.sepBy1
import lexer.implicits.implicitSymbol

object functionParser {
    lazy val parseFunction: Parsley[FunctionDefinition] = 
        FunctionDefinition(parseType, ident, sepBy1(param, ","), curlyBraces(parseStatements))
        
    private lazy val param: Parsley[FunctionParameter] = FunctionParameter(parseType, ident)
}