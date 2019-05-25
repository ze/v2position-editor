grammar Climate;

/*
 * Handles "map/climate.txt". Define climate for a province.
 */

climate: climateData* EOF;

climateData: climateLandBlock | climateProvincesBlock;

climateLandBlock: IDENTIFIER ASSIGN L_BRACE landData* R_BRACE;
climateProvincesBlock: IDENTIFIER ASSIGN L_BRACE INT* R_BRACE;

landData:
	farmRGOSizeExpr
	| farmRGOEffExpr
	| mineRGOSizeExpr
	| mineRGOEffExpr
	| maxAttritionExpr;

farmRGOSizeExpr: FARM_RGO_SIZE ASSIGN (INT | FLOAT);
farmRGOEffExpr: FARM_RGO_EFF ASSIGN (INT | FLOAT);
mineRGOSizeExpr: MINE_RGO_SIZE ASSIGN (INT | FLOAT);
mineRGOEffExpr: MINE_RGO_EFF ASSIGN (INT | FLOAT);
maxAttritionExpr: MAX_ATTRITION ASSIGN (INT | FLOAT);

FARM_RGO_SIZE: 'farm_rgo_size';
FARM_RGO_EFF: 'farm_rgo_eff';
MINE_RGO_SIZE: 'mine_rgo_size';
MINE_RGO_EFF: 'mine_rgo_eff';
MAX_ATTRITION: 'max_attrition';

FLOAT: INT? '.' DIGIT+;

IDENTIFIER: LETTER (LETTER | DIGIT | '.')*;
fragment LETTER: ('a' .. 'z' | CAP_LETTER | '_');
fragment CAP_LETTER: ('A' .. 'Z');

INT: '-'? DIGIT+;
fragment DIGIT: ('0' .. '9');

ASSIGN: '=';
L_BRACE: '{';
R_BRACE: '}';

COMMENT: '#' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n] -> skip;
