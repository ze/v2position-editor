grammar Climate;

/*
 * I have no idea what this file does as well, but I'll try to parse it with what I know now.
 * Changes are likely.
 */

climate: climateData* EOF;

climateData:
	mildClimateLandBlock
	| mildClimateProvincesBlock
	| temperateClimateLandBlock
	| temperateClimateProvincesBlock
	| harshClimateLandBlock
	| harshClimateProvincesBlock
	| inhospitableClimateLandBlock
	| inhospitableClimateProvincesBlock;

mildClimateLandBlock:
	MILD_CLIMATE ASSIGN L_BRACE landData* R_BRACE;
mildClimateProvincesBlock:
	MILD_CLIMATE ASSIGN L_BRACE INT* R_BRACE;
temperateClimateLandBlock:
	TEMPERATE_CLIMATE ASSIGN L_BRACE landData* R_BRACE;
temperateClimateProvincesBlock:
	TEMPERATE_CLIMATE ASSIGN L_BRACE INT* R_BRACE;
harshClimateLandBlock:
	HARSH_CLIMATE ASSIGN L_BRACE landData* R_BRACE;
harshClimateProvincesBlock:
	HARSH_CLIMATE ASSIGN L_BRACE INT* R_BRACE;
inhospitableClimateLandBlock:
	INHOSPITABLE_CLIMATE ASSIGN L_BRACE landData* R_BRACE;
inhospitableClimateProvincesBlock:
	INHOSPITABLE_CLIMATE ASSIGN L_BRACE INT* R_BRACE;

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

MILD_CLIMATE: 'mild_climate';
TEMPERATE_CLIMATE: 'temperate_climate';
HARSH_CLIMATE: 'harsh_climate';
INHOSPITABLE_CLIMATE: 'inhospitable_climate';

FARM_RGO_SIZE: 'farm_rgo_size';
FARM_RGO_EFF: 'farm_rgo_eff';
MINE_RGO_SIZE: 'mine_rgo_size';
MINE_RGO_EFF: 'mine_rgo_eff';
MAX_ATTRITION: 'max_attrition';

FLOAT: INT? '.' DIGIT+;

INT: '-'? DIGIT+;
fragment DIGIT: ('0' .. '9');

ASSIGN: '=';
L_BRACE: '{';
R_BRACE: '}';

COMMENT: '#' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n] -> skip;
