grammar Continent;

/*
 * Similar to terrain, I don't know if I've covered the entire breadth of continent data, but this
 * works for now.
 */

continents: continentData* EOF;

continentData:
	IDENTIFIER ASSIGN L_BRACE (provincesBlock | continentExpr)* R_BRACE;

provincesBlock: PROVINCES ASSIGN L_BRACE INT* R_BRACE;
continentExpr:
	farmRGOSizeExpr
	| mineRGOSizeExpr
	| assimilationRateExpr;

farmRGOSizeExpr: FARM_RGO_SIZE ASSIGN (INT | FLOAT);
mineRGOSizeExpr: MINE_RGO_SIZE ASSIGN (INT | FLOAT);
assimilationRateExpr: ASSIMILATION_RATE ASSIGN (INT | FLOAT);

PROVINCES: 'provinces';
FARM_RGO_SIZE: 'farm_rgo_size';
MINE_RGO_SIZE: 'mine_rgo_size';
ASSIMILATION_RATE: 'assimilation_rate';

FLOAT: INT? '.' DIGIT+;

INT: '-'? DIGIT+;
fragment DIGIT: ('0' .. '9');

ASSIGN: '=';
L_BRACE: '{';
R_BRACE: '}';

IDENTIFIER: LETTERS (LETTERS | '0' .. '9')*;
fragment LETTERS: ('a' .. 'z' | 'A' .. 'Z' | '_');

COMMENT: '#' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n] -> skip;
