grammar Default;

/*
 * Handles the default.map file in directory `map`.
 */

defaults: defaultData* EOF;

defaultData:
	maxProvincesExpr
	| seaStartsBlock
	| definitionsExpr
	| provincesExpr
	| positionsExpr
	| terrainExpr
	| riversExpr
	| terrainDefinitionExpr
	| treeDefinitionExpr
	| continentExpr
	| adjacenciesExpr
	| regionExpr
	| regionSeaExpr
	| provinceFlagSpriteExpr
	| borderHeightsBlock
	| terrainSheetHeighstBlock
	| treeExpr
	| borderCutoffExpr;

maxProvincesExpr: MAX_PROVINCES ASSIGN INT;
seaStartsBlock: SEA_STARTS ASSIGN L_BRACE INT* R_BRACE;
definitionsExpr: DEFINITIONS ASSIGN STRING;
provincesExpr: PROVINCES ASSIGN STRING;
positionsExpr: POSITIONS ASSIGN STRING;
terrainExpr: TERRAIN ASSIGN STRING;
riversExpr: RIVERS ASSIGN STRING;
terrainDefinitionExpr: TERRAIN_DEFINITION ASSIGN STRING;
treeDefinitionExpr: TREE_DEFINITION ASSIGN STRING;
continentExpr: CONTINENT ASSIGN STRING;
adjacenciesExpr: ADJACENCIES ASSIGN STRING;
regionExpr: REGION ASSIGN STRING;
regionSeaExpr: REGION_SEA ASSIGN STRING;
provinceFlagSpriteExpr: PROVINCE_FLAG_SPRITE ASSIGN STRING;

borderHeightsBlock:
	BORDER_HEIGHTS ASSIGN L_BRACE INT INT R_BRACE;
terrainSheetHeighstBlock:
	TERRAIN_SHEET_HEIGHTS ASSIGN L_BRACE INT R_BRACE;
treeExpr: TREE ASSIGN INT;
borderCutoffExpr: BORDER_CUTOFF ASSIGN (INT | FLOAT);

MAX_PROVINCES: 'max_provinces';
SEA_STARTS: 'sea_starts';
DEFINITIONS: 'definitions';
PROVINCES: 'provinces';
POSITIONS: 'positions';
TERRAIN: 'terrain';
RIVERS: 'rivers';
TERRAIN_DEFINITION: 'terrain_definition';
TREE_DEFINITION: 'tree_definition';
CONTINENT: 'continent';
ADJACENCIES: 'adjacencies';
REGION: 'region';
REGION_SEA: 'region_sea';
PROVINCE_FLAG_SPRITE: 'province_flag_sprite';
BORDER_HEIGHTS: 'border_heights';
TERRAIN_SHEET_HEIGHTS: 'terrain_sheet_heights';
TREE: 'tree';
BORDER_CUTOFF: 'border_cutoff';

FLOAT: INT? '.' DIGIT+;

INT: '-'? DIGIT+;
fragment DIGIT: ('0' .. '9');

ASSIGN: '=';
L_BRACE: '{';
R_BRACE: '}';

STRING: '"' (~'"' | '\\"')* '"';

COMMENT: '#' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n] -> skip;
