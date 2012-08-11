grammar OpenCL;

options {
  language = 'Java';
  output=AST;
} 


@parser::header {
package edu.syr.pcpratts.compressor;
}

@lexer::header {
package edu.syr.pcpratts.compressor;
}

@rulecatch {
    catch (RecognitionException e) {
        throw e;
    }
}

program returns [List<String> ret, List<Boolean> modify, List<Boolean> string]
@init {
  retval.ret = new ArrayList<String>();
  retval.modify = new ArrayList<Boolean>();
  retval.string = new ArrayList<Boolean>();
}
  : ( (e1=(SPECIAL | SEP | END_OF_STATEMENT | AMP | POINT | INT_LITERAL | WHITESPACE) { retval.ret.add($e1.text); retval.modify.add(false); retval.string.add(false); })
    | e3 = STRING_DELIM { retval.ret.add($e3.text); retval.modify.add(false); retval.string.add(true); }
    | e2=VARIABLE { retval.ret.add($e2.text); retval.modify.add(true); retval.string.add(false); } )* EOF;
/*
 * Lexer Rules
 */

SPECIAL: ('&&' | '||' | '==' | '=' | '>' | '>=' | '<=' | '<' | '/' | '?' | '+' | '-' | ':' | '//' | '/*' | '\\*' | '+=' | '-=' | '++' 
         | '%' | '--' | '>>' | '<<' | '\\' | '!=' | '\\"' );
SEP: ('{' | '}' | ')' | '(' | ',' | '[' | ']' | '!' | '@' | '.');
END_OF_STATEMENT: ';';
AMP: '&';
POINT: '*';
STRING_DELIM: ('"' | '\'');
VARIABLE: ('a'..'z' | 'A'..'Z' | '_' | '$' | '#') ('a'..'z' | 'A'..'Z' | '_' | '$' | '#' | '0' .. '9' )*;
INT_LITERAL: ('-'? ('0'..'9' | '.' )+ 'L'?) | ('0x' ('0'..'9' | 'a'..'f' | 'A'..'F')+ 'L'?) ;
WHITESPACE : (' ' | '\t' | '\n' | '\r' | '\u000C' | '\uffff')+ {$channel = HIDDEN; };
