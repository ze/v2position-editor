grammar Terrain;

/*
 * Manages land textures and terrain types.
 */

terrain: (terrainExpr | terrainTGAData | categoriesBlock)* EOF;

terrainTGAData:
	IDENTIFIER ASSIGN L_BRACE (
		typeExpr
		| colorBlock
		| modifierExpr
	)* R_BRACE;

terrainExpr: TERRAIN ASSIGN INT;

categoriesBlock:
	CATEGORIES ASSIGN L_BRACE categoriesData* R_BRACE;
categoriesData:
	IDENTIFIER ASSIGN L_BRACE (categoriesExpr | colorBlock)* R_BRACE;
categoriesExpr:
	movementCostExpr
	| isWaterExpr
	| defenceExpr
	| farmRGOSizeExpr
	| farmRGOEffExpr
	| mineRGOSizeExpr
	| mineRGOEffExpr
	| supplyLimitExpr
	| minBuildNavalBaseExpr
	| minBuildRailroadExpr
	| minBuildFortExpr
	| attritionExpr
	| combatWidthExpr
	| immigrantAttractExpr
	| assimilationRateExpr;

movementCostExpr: MOVEMENT_COST ASSIGN (INT | FLOAT);
isWaterExpr: IS_WATER ASSIGN affirmative;
defenceExpr:
	DEFENCE ASSIGN (INT | FLOAT); // why is it spelled defence????????
farmRGOSizeExpr: FARM_RGO_SIZE ASSIGN (INT | FLOAT);
farmRGOEffExpr: FARM_RGO_EFF ASSIGN (INT | FLOAT);
mineRGOSizeExpr: MINE_RGO_SIZE ASSIGN (INT | FLOAT);
mineRGOEffExpr: MINE_RGO_EFF ASSIGN (INT | FLOAT);
supplyLimitExpr: SUPPLY_LIMIT ASSIGN INT;
minBuildNavalBaseExpr: MIN_BUILD_NAVAL_BASE ASSIGN INT;
minBuildRailroadExpr: MIN_BUILD_RAILROAD ASSIGN INT;
minBuildFortExpr: MIN_BUILD_FORT ASSIGN INT;
attritionExpr: ATTRITION ASSIGN (INT | FLOAT);
combatWidthExpr: COMBAT_WIDTH ASSIGN (INT | FLOAT);
immigrantAttractExpr: IMMIGRANT_ATTRACT ASSIGN (INT | FLOAT);
assimilationRateExpr: ASSIMILATION_RATE ASSIGN (INT | FLOAT);

colorBlock: COLOR ASSIGN L_BRACE INT* R_BRACE;

modifierExpr: priorityExpr | hasTextureExpr | supplyLimitExpr;

typeExpr: TYPE ASSIGN IDENTIFIER;
hasTextureExpr: HAS_TEXTURE ASSIGN affirmative;

priorityExpr: PRIORITY ASSIGN INT;

TERRAIN: 'terrain';
CATEGORIES: 'categories';
MOVEMENT_COST: 'movement_cost';
IS_WATER: 'is_water';
DEFENCE: 'defence'; // nice grammar pdx!!!
FARM_RGO_SIZE: 'farm_rgo_size';
FARM_RGO_EFF: 'farm_rgo_eff';
MINE_RGO_SIZE: 'mine_rgo_size';
MINE_RGO_EFF: 'mine_rgo_eff';
SUPPLY_LIMIT: 'supply_limit';
MIN_BUILD_NAVAL_BASE: 'min_build_naval_base';
MIN_BUILD_RAILROAD: 'min_build_railroad';
MIN_BUILD_FORT: 'min_build_fort';
ATTRITION: 'attrition';
COMBAT_WIDTH: 'combat_width';
IMMIGRANT_ATTRACT: 'immigrant_attract';
ASSIMILATION_RATE: 'assimilation_rate';

COLOR: 'color';

TYPE: 'type';
HAS_TEXTURE: 'has_texture';
PRIORITY: 'priority';

affirmative: YES | NO;
YES: 'yes';
NO: 'no';

FLOAT: INT? '.' DIGIT+;

INT: '-'? DIGIT+;
fragment DIGIT: ('0' .. '9');

IDENTIFIER: LETTERS (LETTERS | '0' .. '9')*;
fragment LETTERS: ('a' .. 'z' | 'A' .. 'Z' | '_');

ASSIGN: '=';
L_BRACE: '{';
R_BRACE: '}';

COMMENT: '#' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n] -> skip;
