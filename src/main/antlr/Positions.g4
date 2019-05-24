grammar Positions;

/*
 * This file holds all of the graphical information (and where boats are docked). Personally, this
 * is one of the ugliest files to read without some formatting.
 */

positions: positionData* EOF;
positionData: INT ASSIGN L_BRACE positionExpr* R_BRACE;

positionExpr:
	unitPositionBlock
	| textPositionBlock
	| buildingPositionBlock
	| buildingConstructionBlock
	| buildingNudgeBlock // is it just naval bases?
	| militaryConstructionBlock
	| spawnRailwayTrackBlock
	| railroadVisibilityBlock
	| factoryBlock
	| buildingRotationBlock
	| textRotationExpr
	| textScaleExpr
	| cityBlock
	| townBlock;

unitPositionBlock: UNIT ASSIGN L_BRACE coordinate R_BRACE;
textPositionBlock:
	TEXT_POSITION ASSIGN L_BRACE coordinate R_BRACE;
buildingPositionBlock:
	BUILDING_POSITION ASSIGN L_BRACE objectPositionBlock* R_BRACE;
objectPositionBlock:
	fortPositionBlock
	| navalBasePositionBlock
	| railroadPositionBlock;
fortPositionBlock: FORT ASSIGN L_BRACE coordinate R_BRACE;
navalBasePositionBlock:
	NAVAL_BASE ASSIGN L_BRACE coordinate R_BRACE;
railroadPositionBlock:
	RAILROAD ASSIGN L_BRACE coordinate R_BRACE;

buildingConstructionBlock:
	BUILDING_CONSTRUCTION ASSIGN L_BRACE coordinate R_BRACE;
buildingNudgeBlock:
	BUILDING_NUDGE ASSIGN L_BRACE objectValueExpr* R_BRACE;
militaryConstructionBlock:
	MILITARY_CONSTRUCTION ASSIGN L_BRACE coordinate R_BRACE;
spawnRailwayTrackBlock:
	SPAWN_RAILWAY_TRACK ASSIGN L_BRACE railwayTrackData* R_BRACE;
railwayTrackData: L_BRACE coordinate R_BRACE;

railroadVisibilityBlock:
	RAILROAD_VISIBILITY ASSIGN L_BRACE INT* R_BRACE;

factoryBlock: FACTORY ASSIGN L_BRACE coordinate R_BRACE;
buildingRotationBlock:
	BUILDING_ROTATION ASSIGN L_BRACE objectValueExpr* R_BRACE;

objectValueExpr:
	fortValueExpr
	| navalBaseValueExpr
	| railroadValueExpr
	| aeroplaneFactoryValueExpr;
fortValueExpr: FORT ASSIGN (INT | FLOAT);
navalBaseValueExpr: NAVAL_BASE ASSIGN (INT | FLOAT);
railroadValueExpr: RAILROAD ASSIGN (INT | FLOAT);
aeroplaneFactoryValueExpr:
	AEROPLANE_FACTORY ASSIGN (INT | FLOAT);

textRotationExpr: TEXT_ROTATION ASSIGN (INT | FLOAT);
textScaleExpr: TEXT_SCALE ASSIGN (INT | FLOAT);

cityBlock: CITY ASSIGN L_BRACE coordinate R_BRACE;
townBlock: TOWN ASSIGN L_BRACE coordinate R_BRACE;

coordinate: (xPosition | yPosition)*;
xPosition: 'x' ASSIGN (INT | FLOAT);
yPosition: 'y' ASSIGN (INT | FLOAT);

UNIT: 'unit';
TEXT_POSITION: 'text_position';
BUILDING_POSITION: 'building_position';
FORT: 'fort';
NAVAL_BASE: 'naval_base';
RAILROAD: 'railroad';
BUILDING_CONSTRUCTION: 'building_construction';
BUILDING_NUDGE: 'building_nudge';
MILITARY_CONSTRUCTION: 'military_construction';
SPAWN_RAILWAY_TRACK: 'spawn_railway_track';
RAILROAD_VISIBILITY: 'railroad_visibility';
FACTORY: 'factory';
BUILDING_ROTATION: 'building_rotation';
AEROPLANE_FACTORY: 'aeroplane_factory';
TEXT_ROTATION: 'text_rotation';
TEXT_SCALE: 'text_scale';
CITY: 'city';
TOWN: 'town';

FLOAT: INT? '.' DIGIT+;

INT: '-'? DIGIT+;
fragment DIGIT: ('0' .. '9');

ASSIGN: '=';
L_BRACE: '{';
R_BRACE: '}';

COMMENT: '#' ~('\r' | '\n')* -> skip;
WS: [ \t\r\n] -> skip;
