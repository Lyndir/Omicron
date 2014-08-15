#!/usr/local/bin/thrift -strict -v -r -gen html

/* === SIMPLE TYPES === */

namespace java com.lyndir.omicron.api.thrift

struct Color {
    /** The 1-byte red component of this RGB color. */
    1: required byte red,
    /** The 1-byte green component of this RGB color. */
    2: required byte green,
    /** The 1-byte blue component of this RGB color. */
    3: required byte blue,
}

struct Vec2 {
    /** The value of the first dimension of this two-dimensional vector. */
    1: required i16 x,
    /** The value of the second dimension of this two-dimensional vector. */
    2: required i16 y,
}

struct Size {
    /** The value of the first dimension of this two-dimensional size. */
    1: required i16 width,
    /** The value of the second dimension of this two-dimensional size. */
    2: required i16 height,
}

enum UnitType {
    /** An engineer is the base construction unit. */
    U_ENGINEER = 1,
    /** A scout is a cheap, fast, long-range but weak land-based scouting unit. */
    U_SCOUT = 2,
    /** An airship is a fast, long-range but weak airborne scouting unit. */
    U_AIRSHIP = 3,
    /** A container provides capacity for storing a variety of resources. */
    U_CONTAINER = 4,
    /** A quarry is a facility for mining metal resources. */
    U_QUARRY = 5,
    /** A drill is a facility for mining fuel resources. */
    U_DRILL = 6,
    /** A construction site is a temporary site while a unit is being constructed. */
    U_CONSTRUCTION = 7,
}

enum VictoryConditionType {
    /** The player that remains the last standing with live units ends the game a victor. */
    VC_SUPREMACY = 1,
    /** The player that successfully launches and defends a migration ship to Omicron ends the game a victor. */
    VC_MIGRATION = 2,
    /** The player that maintains a score of 10k more than that of all others for 10 turns ends the game a victor. */
    VC_MIGHT = 3,
    /** The player that repairs and activates a global insurgency device and successfully defends it for 30 turns ends the game a victor. */
    VC_CAPTURE = 4,
}

enum ModuleType {
    /** Makes a unit destructible and gives it the ability to observe its surroundings. */
    M_BASE = 1,
    /** Gives the unit the ability to move around. */
    M_MOBILITY = 2,
    /** Provides the unit with resource storage. */
    M_CONTAINER = 3,
    /** Adds the provisions for extracting resources to the unit. */
    M_EXTRACTOR = 4,
    /** Teaches the unit how to construct other units. */
    M_CONSTRUCTOR = 5,
    /** Installs a weapon system on the unit, allowing it to inflict damage onto other units. */
    M_WEAPON = 6,
}

enum ResourceType {
    /** This resource provides construction and framing capabilities. */
    R_METALS = 0,
    /** This resource provides operating energy to machinery. */
    R_FUEL = 1,
    /** This resource permits the construction of circuitry. */
    R_SILICON = 2,
    /** This resource provides extraordinary properties. */
    R_RARE_ELEMENTS = 3,
}

enum LevelType {
    /** This level is on top of the solid mass of a planetary object. */
    L_GROUND = 1,
    /** This level is the atmosphere above the ground around a planetary object. */
    L_SKY = 2,
    /** This level is the void above the atmosphere around a planetary object. */
    L_SPACE = 3,
}

/* === GAME === */

struct MaybeI16 {
    /** Determines whether the value for this container is known by the current player. */
    1: required bool known,
    /** Provides the value if it's known and present. */
    2: optional i16 value,
}

struct Turn {
    /** Each turn has a linearly incrementing identifying integer counter value. */
    1: required i32 number,
}

struct Tile {
    //1: optional GameObject contents,
    /** The position of a tile within its level among the other tiles. */
    2: required Vec2 position,
    //3: required Level level,
    /** The quantities of the remaining resources available on this tile mapped by their resource type. */
    4: map<ResourceType,MaybeI16> quantitiesByResourceType,
}

struct MaybeTile {
    /** Determines whether the value for this container is known by the current player. */
    1: required bool known,
    /** Provides the value if it's known and present. */
    2: optional Tile value,
}

struct Level {
    /** The maximum dimensions for the tiles in this level. */
    1: required Size size,
    /** The type of level this level's tiles represent. */
    2: required LevelType type,
    //3: required Game game,
    /** The tiles in this level mapped by their position. */
    4: map<Vec2,Tile> tilesByPosition,
}

struct ResourceCost {
    /** The amount of each type of resource required by this cost. */
    1: map<ResourceType,i16> quantitiesByResourceType,
}

struct Module {
    /** The resources required to construct this module. */
    1: required ResourceCost resourceCost,
    //2: required GameObject gameObject,
    /** The type that defines the behavior of this module. */
    3: required ModuleType type,
}

struct GameObject {
    /** The unique identifier of this unit in the game. */
    1: required i64 objectID,
    /** The type that defines the behavior of this unit. */
    2: required UnitType type,
    //3: required Game game,
    /** The modules that implement the behavior of this unit. */
    4: map<ModuleType, list<Module>> modulesByType,
    //5: optional Player owner,
    /** The tile that this unit currently occupies in the game. */
    6: required MaybeTile location,
}

struct Player {
    /** The unique identifier of the player in the game. */
    1: required i64 playerID,
    /** The visible name of this player. */
    2: required string name,
    /** The primary color for this player's units. */
    3: required Color primaryColor,
    /** The secondary color for this player's units. */
    4: required Color secondaryColor,
    /** The live objects this player currently controls mapped by object identifier. */
    5: map<i64,GameObject> objectsByID,
    /** This player's current score. */
    6: required i32 score,
    /** Determines whether this is the currently authenticated player. */
    7: required bool curentPlayer,
}

struct Game {
    /** The turns that have so-far occurred in this game.  The final entry is the currently active turn. */
    1: list<Turn> turns,
    /** The maximum dimensions to create the levels of this game in. */
    2: required Size levelSize,
    /** The levels in this game. */
    3: list<Level> levels,
    /** The players in this game, in order of sequence when simultaneous turns are not enabled. */
    4: list<Player> players,
    /** The set of players that have marked themselves as ready to advance to the next turn. */
    5: set<Player> readyPlayers,
    /** Determines whether this game is currently ongoing or not. */
    6: required bool running,
}


/* === MODULES === */

struct BaseModule {
    /** The module's base properties. */
    255: required Module zuper,
    /** The unit's maximum health represents the total amount of damage a unit can receive before it is destroyed. */
    1: required i16 maxHealth,
    /** The protection this unit has against incoming damage. */
    2: required i16 armor,
    /** The distance from the unit's current tile that the unit can observe the activity on other tiles. */
    3: required i16 viewRange,
    /** The type of levels on which this unit is able to exist. */
    4: set<LevelType> supportedLayers,
    /** The total amount of damage this unit has incurred so far. */
    5: required i16 damage,
}

struct MobilityModule {
    /** The module's base properties. */
    255: required Module zuper,
    /** The amount of movement power this unit is able to spend in a turn. */
    1: required i16 movementSpeed,
    /** The amount of movement power remaining for this unit in the current turn. */
    2: required double remainingSpeed,
    /** The costs for moving a single tile mapped by the type of level in which the movement will occur. */
    3: map<LevelType,double> costForMovementInLevelType,
    /** The costs for leveling to an adjacent level mapped by the type of level in which the leveling will occur. */
    4: map<LevelType,double> costForLevelingToLevelType,
}

struct ContainerModule {
    /** The module's base properties. */
    255: required Module zuper,
    /** The type of resources this container is able to store. */
    1: required ResourceType resourceType,
    /** The total amount of resources this container is able to hold. */
    2: required i16 capacity,
    /** The current stock of resources currently present in this container. */
    3: required i16 stock,
}

struct ExtractorModule {
    /** The module's base properties. */
    255: required Module zuper,
    /** The type of resources this extractor is able to mine. */
    1: required ResourceType resourceType,
    /** The speed at which the extractor is able to mine resources in a single turn. */
    2: required i16 speed,
}

struct ConstructorModule {
    /** The module's base properties. */
    255: required Module zuper,
    /** The type of module this constructor is able to build. */
    1: required ModuleType buildsModule,
    /** The speed at which the constructor is able to contribute to unit construction. */
    2: required i16 buildSpeed,

    /** Specifies whether the constructor was constrained during its previous construction effort due to insufficient resources. */
    3: required bool resourceConstrained,
    /** The amount of construction speed this unit is still able to contribute to unit construction in the current turn. */
    4: required i16 remainingSpeed,

    /** The unit that this constructor is currently targetting for contribution to construction. */
    5: optional GameObject target,

    /** The unit types this constructor can create a new construction site for. */
    6: required list<UnitType> blueprints,
}

struct WeaponModule {
    /** The module's base properties. */
    255: required Module zuper,
    /** The amount of damaging power this weapon provides in a single shot. */
    1: required i16 firePower,
    /** The amount of extra damaging power this weapon is able to contribute. */
    2: required i16 variance,
    /** The distance to the farthest tile from this unit's current location this weapon is able to strike. */
    3: required i16 range,
    /** The amount of times in a single turn this weapon is able to take a shot. */
    4: required i16 repeat,
    /** The total amount of ammunition this weapon is able to be loaded with. */
    5: required i16 ammunitionLoad,
    /** The type of levels this weapon is able to strike at. */
    6: set<LevelType> supportedLayers,
    /** The amount of times this weapon has fired in the current turn. */
    7: required i16 repeated,
    /** The amount of remaining ammunition available to the weapon. */
    8: required i16 ammunition,
}
