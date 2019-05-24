grammar Region;

/*
 * A very simple region file definition. I don't know what region_sea.txt does for now, though.
 */

region: regionData* EOF;
regionData: IDENTIFIER ASSIGN L_BRACE INT* R_BRACE;

INT: '-'? DIGIT+;
fragment DIGIT: ('0' .. '9');

IDENTIFIER: LETTERS (LETTERS | '0' .. '9')*;
fragment LETTERS: ('a' .. 'z' | 'A' .. 'Z' | '_');

ASSIGN: '=';
L_BRACE: '{';
R_BRACE: '}';

COMMENT: '#' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n] -> skip;
