#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class SharedKotlinPair, SharedGameType, SharedGame, SharedColor, SharedShape, SharedKotlinEnum, SharedHeightComparisonGameType, SharedMentalCalculationGameOperator, SharedAppState, SharedMentalCalculationGame, SharedColorConfusionGame, SharedSherlockCalculationGame, SharedChainCalculationGame, SharedHeightComparisonGame, SharedFractionCalculationGame, SharedKotlinArray;

@protocol SharedKotlinComparable, SharedAppInterface, SharedKotlinIterator;

NS_ASSUME_NONNULL_BEGIN
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wunknown-warning-option"
#pragma clang diagnostic ignored "-Wnullability"

@interface KotlinBase : NSObject
- (instancetype)init __attribute__((unavailable));
+ (instancetype)new __attribute__((unavailable));
+ (void)initialize __attribute__((objc_requires_super));
@end;

@interface KotlinBase (KotlinBaseCopying) <NSCopying>
@end;

__attribute__((objc_runtime_name("KotlinMutableSet")))
__attribute__((swift_name("KotlinMutableSet")))
@interface SharedMutableSet<ObjectType> : NSMutableSet<ObjectType>
@end;

__attribute__((objc_runtime_name("KotlinMutableDictionary")))
__attribute__((swift_name("KotlinMutableDictionary")))
@interface SharedMutableDictionary<KeyType, ObjectType> : NSMutableDictionary<KeyType, ObjectType>
@end;

@interface NSError (NSErrorKotlinException)
@property (readonly) id _Nullable kotlinException;
@end;

__attribute__((objc_runtime_name("KotlinNumber")))
__attribute__((swift_name("KotlinNumber")))
@interface SharedNumber : NSNumber
- (instancetype)initWithChar:(char)value __attribute__((unavailable));
- (instancetype)initWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
- (instancetype)initWithShort:(short)value __attribute__((unavailable));
- (instancetype)initWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
- (instancetype)initWithInt:(int)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
- (instancetype)initWithLong:(long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
- (instancetype)initWithLongLong:(long long)value __attribute__((unavailable));
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
- (instancetype)initWithFloat:(float)value __attribute__((unavailable));
- (instancetype)initWithDouble:(double)value __attribute__((unavailable));
- (instancetype)initWithBool:(BOOL)value __attribute__((unavailable));
- (instancetype)initWithInteger:(NSInteger)value __attribute__((unavailable));
- (instancetype)initWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
+ (instancetype)numberWithChar:(char)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedChar:(unsigned char)value __attribute__((unavailable));
+ (instancetype)numberWithShort:(short)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedShort:(unsigned short)value __attribute__((unavailable));
+ (instancetype)numberWithInt:(int)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInt:(unsigned int)value __attribute__((unavailable));
+ (instancetype)numberWithLong:(long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLong:(unsigned long)value __attribute__((unavailable));
+ (instancetype)numberWithLongLong:(long long)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value __attribute__((unavailable));
+ (instancetype)numberWithFloat:(float)value __attribute__((unavailable));
+ (instancetype)numberWithDouble:(double)value __attribute__((unavailable));
+ (instancetype)numberWithBool:(BOOL)value __attribute__((unavailable));
+ (instancetype)numberWithInteger:(NSInteger)value __attribute__((unavailable));
+ (instancetype)numberWithUnsignedInteger:(NSUInteger)value __attribute__((unavailable));
@end;

__attribute__((objc_runtime_name("KotlinByte")))
__attribute__((swift_name("KotlinByte")))
@interface SharedByte : SharedNumber
- (instancetype)initWithChar:(char)value;
+ (instancetype)numberWithChar:(char)value;
@end;

__attribute__((objc_runtime_name("KotlinUByte")))
__attribute__((swift_name("KotlinUByte")))
@interface SharedUByte : SharedNumber
- (instancetype)initWithUnsignedChar:(unsigned char)value;
+ (instancetype)numberWithUnsignedChar:(unsigned char)value;
@end;

__attribute__((objc_runtime_name("KotlinShort")))
__attribute__((swift_name("KotlinShort")))
@interface SharedShort : SharedNumber
- (instancetype)initWithShort:(short)value;
+ (instancetype)numberWithShort:(short)value;
@end;

__attribute__((objc_runtime_name("KotlinUShort")))
__attribute__((swift_name("KotlinUShort")))
@interface SharedUShort : SharedNumber
- (instancetype)initWithUnsignedShort:(unsigned short)value;
+ (instancetype)numberWithUnsignedShort:(unsigned short)value;
@end;

__attribute__((objc_runtime_name("KotlinInt")))
__attribute__((swift_name("KotlinInt")))
@interface SharedInt : SharedNumber
- (instancetype)initWithInt:(int)value;
+ (instancetype)numberWithInt:(int)value;
@end;

__attribute__((objc_runtime_name("KotlinUInt")))
__attribute__((swift_name("KotlinUInt")))
@interface SharedUInt : SharedNumber
- (instancetype)initWithUnsignedInt:(unsigned int)value;
+ (instancetype)numberWithUnsignedInt:(unsigned int)value;
@end;

__attribute__((objc_runtime_name("KotlinLong")))
__attribute__((swift_name("KotlinLong")))
@interface SharedLong : SharedNumber
- (instancetype)initWithLongLong:(long long)value;
+ (instancetype)numberWithLongLong:(long long)value;
@end;

__attribute__((objc_runtime_name("KotlinULong")))
__attribute__((swift_name("KotlinULong")))
@interface SharedULong : SharedNumber
- (instancetype)initWithUnsignedLongLong:(unsigned long long)value;
+ (instancetype)numberWithUnsignedLongLong:(unsigned long long)value;
@end;

__attribute__((objc_runtime_name("KotlinFloat")))
__attribute__((swift_name("KotlinFloat")))
@interface SharedFloat : SharedNumber
- (instancetype)initWithFloat:(float)value;
+ (instancetype)numberWithFloat:(float)value;
@end;

__attribute__((objc_runtime_name("KotlinDouble")))
__attribute__((swift_name("KotlinDouble")))
@interface SharedDouble : SharedNumber
- (instancetype)initWithDouble:(double)value;
+ (instancetype)numberWithDouble:(double)value;
@end;

__attribute__((objc_runtime_name("KotlinBoolean")))
__attribute__((swift_name("KotlinBoolean")))
@interface SharedBoolean : SharedNumber
- (instancetype)initWithBool:(BOOL)value;
+ (instancetype)numberWithBool:(BOOL)value;
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Api")))
@interface SharedApi : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)api __attribute__((swift_name("init()")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("UserStorage")))
@interface SharedUserStorage : KotlinBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (int32_t)getHighScoreGameId:(NSString *)gameId __attribute__((swift_name("getHighScore(gameId:)")));
- (BOOL)putScoreGameId:(NSString *)gameId score:(int32_t)score __attribute__((swift_name("putScore(gameId:score:)")));
- (NSArray<SharedKotlinPair *> *)getScoresGameId:(NSString *)gameId __attribute__((swift_name("getScores(gameId:)")));
@end;

__attribute__((swift_name("Game")))
@interface SharedGame : KotlinBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (NSString *)solution __attribute__((swift_name("solution()")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ChainCalculationGame")))
@interface SharedChainCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (NSString *)solution __attribute__((swift_name("solution()")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
@property NSString *calculation __attribute__((swift_name("calculation")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ColorConfusionGame")))
@interface SharedColorConfusionGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)points __attribute__((swift_name("points()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
@property SharedColor *answerColor __attribute__((swift_name("answerColor")));
@property SharedColor *displayedColor __attribute__((swift_name("displayedColor")));
@property SharedColor *stringColor __attribute__((swift_name("stringColor")));
@property SharedShape *answerShape __attribute__((swift_name("answerShape")));
@property SharedShape *displayedShape __attribute__((swift_name("displayedShape")));
@property int32_t colorPoints __attribute__((swift_name("colorPoints")));
@property int32_t shapePoints __attribute__((swift_name("shapePoints")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ColorConfusionGame.Companion")))
@interface SharedColorConfusionGameCompanion : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (readonly) NSArray<SharedColor *> *colors __attribute__((swift_name("colors")));
@property (readonly) NSArray<SharedShape *> *shapes __attribute__((swift_name("shapes")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FractionCalculationGame")))
@interface SharedFractionCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (NSString *)solution __attribute__((swift_name("solution()")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
@property int32_t result __attribute__((swift_name("result")));
@property NSString *calculation __attribute__((swift_name("calculation")));
@end;

__attribute__((swift_name("KotlinComparable")))
@protocol SharedKotlinComparable
@required
- (int32_t)compareToOther:(id _Nullable)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((swift_name("KotlinEnum")))
@interface SharedKotlinEnum : KotlinBase <SharedKotlinComparable>
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer));
- (int32_t)compareToOther:(SharedKotlinEnum *)other __attribute__((swift_name("compareTo(other:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@property (readonly) int32_t ordinal __attribute__((swift_name("ordinal")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("GameType")))
@interface SharedGameType : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
@property (class, readonly) SharedGameType *mentalCalculation __attribute__((swift_name("mentalCalculation")));
@property (class, readonly) SharedGameType *colorConfusion __attribute__((swift_name("colorConfusion")));
@property (class, readonly) SharedGameType *sherlockCalculation __attribute__((swift_name("sherlockCalculation")));
@property (class, readonly) SharedGameType *chainCalculation __attribute__((swift_name("chainCalculation")));
@property (class, readonly) SharedGameType *fractionCalculation __attribute__((swift_name("fractionCalculation")));
@property (class, readonly) SharedGameType *heightComparison __attribute__((swift_name("heightComparison")));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (int32_t)compareToOther:(SharedGameType *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HeightComparisonGame")))
@interface SharedHeightComparisonGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (NSString *)solution __attribute__((swift_name("solution()")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
@property int32_t round __attribute__((swift_name("round")));
@property NSMutableArray<NSString *> *answers __attribute__((swift_name("answers")));
@property NSArray<SharedHeightComparisonGameType *> *types __attribute__((swift_name("types")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HeightComparisonGame.Type")))
@interface SharedHeightComparisonGameType : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
@property (class, readonly) SharedHeightComparisonGameType *addition __attribute__((swift_name("addition")));
@property (class, readonly) SharedHeightComparisonGameType *fraction __attribute__((swift_name("fraction")));
@property (class, readonly) SharedHeightComparisonGameType *multiplication __attribute__((swift_name("multiplication")));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (int32_t)compareToOther:(SharedHeightComparisonGameType *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MentalCalculationGame")))
@interface SharedMentalCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (int32_t)getNumberLength __attribute__((swift_name("getNumberLength()")));
@property NSString *calculation __attribute__((swift_name("calculation")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MentalCalculationGame.Operator")))
@interface SharedMentalCalculationGameOperator : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
@property (class, readonly) SharedMentalCalculationGameOperator *plus __attribute__((swift_name("plus")));
@property (class, readonly) SharedMentalCalculationGameOperator *minus __attribute__((swift_name("minus")));
@property (class, readonly) SharedMentalCalculationGameOperator *multiply __attribute__((swift_name("multiply")));
@property (class, readonly) SharedMentalCalculationGameOperator *divide __attribute__((swift_name("divide")));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (int32_t)compareToOther:(SharedMentalCalculationGameOperator *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SherlockCalculationGame")))
@interface SharedSherlockCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString *)getNumbersString __attribute__((swift_name("getNumbersString()")));
@property int32_t result __attribute__((swift_name("result")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Calculator")))
@interface SharedCalculator : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)calculator __attribute__((swift_name("init()")));
- (double)calcInput:(NSString *)input __attribute__((swift_name("calc(input:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Color")))
@interface SharedColor : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
@property (class, readonly) SharedColor *red __attribute__((swift_name("red")));
@property (class, readonly) SharedColor *green __attribute__((swift_name("green")));
@property (class, readonly) SharedColor *blue __attribute__((swift_name("blue")));
@property (class, readonly) SharedColor *purple __attribute__((swift_name("purple")));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (int32_t)compareToOther:(SharedColor *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Shape")))
@interface SharedShape : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
@property (class, readonly) SharedShape *square __attribute__((swift_name("square")));
@property (class, readonly) SharedShape *triangle __attribute__((swift_name("triangle")));
@property (class, readonly) SharedShape *circle __attribute__((swift_name("circle")));
@property (class, readonly) SharedShape *heart __attribute__((swift_name("heart")));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (int32_t)compareToOther:(SharedShape *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AppController")))
@interface SharedAppController : KotlinBase
- (instancetype)initWithApp:(id<SharedAppInterface>)app __attribute__((swift_name("init(app:)"))) __attribute__((objc_designated_initializer));
- (void)start __attribute__((swift_name("start()")));
@property double startTime __attribute__((swift_name("startTime")));
@property int32_t points __attribute__((swift_name("points")));
@property BOOL isCorrect __attribute__((swift_name("isCorrect")));
@property int32_t plays __attribute__((swift_name("plays")));
@property SharedAppState *state __attribute__((swift_name("state")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AppController.Companion")))
@interface SharedAppControllerCompanion : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (readonly) NSArray<SharedGameType *> *games __attribute__((swift_name("games")));
@end;

__attribute__((swift_name("AppInterface")))
@protocol SharedAppInterface
@required
- (void)showMainMenuTitle:(NSString *)title description:(NSString *)description games:(NSArray<SharedGameType *> *)games instructions:(void (^)(SharedGameType *))instructions score:(void (^)(SharedGameType *))score __attribute__((swift_name("showMainMenu(title:description:games:instructions:score:)")));
- (void)showInstructionsTitle:(NSString *)title description:(NSString *)description start:(void (^)(void))start __attribute__((swift_name("showInstructions(title:description:start:)")));
- (void)showMentalCalculationGame:(SharedMentalCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showMentalCalculation(game:answer:next:)")));
- (void)showColorConfusionGame:(SharedColorConfusionGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showColorConfusion(game:answer:next:)")));
- (void)showSherlockCalculationGame:(SharedSherlockCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showSherlockCalculation(game:answer:next:)")));
- (void)showChainCalculationGame:(SharedChainCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showChainCalculation(game:answer:next:)")));
- (void)showHeightComparisonGame:(SharedHeightComparisonGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showHeightComparison(game:answer:next:)")));
- (void)showFractionCalculationGame:(SharedFractionCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showFractionCalculation(game:answer:next:)")));
- (void)showCorrectAnswerFeedback __attribute__((swift_name("showCorrectAnswerFeedback()")));
- (void)showWrongAnswerFeedbackSolution:(NSString *)solution __attribute__((swift_name("showWrongAnswerFeedback(solution:)")));
- (void)showFinishFeedbackRank:(NSString *)rank newHighscore:(BOOL)newHighscore plays:(int32_t)plays random:(void (^)(void))random __attribute__((swift_name("showFinishFeedback(rank:newHighscore:plays:random:)")));
- (void)showScoreboardGame:(SharedGameType *)game highscore:(int32_t)highscore scores:(NSArray<SharedKotlinPair *> *)scores __attribute__((swift_name("showScoreboard(game:highscore:scores:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AppState")))
@interface SharedAppState : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
@property (class, readonly) SharedAppState *start __attribute__((swift_name("start")));
@property (class, readonly) SharedAppState *game __attribute__((swift_name("game")));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (int32_t)compareToOther:(SharedAppState *)other __attribute__((swift_name("compareTo(other:)")));
@end;

@interface SharedGameType (Extensions)
- (NSString *)getName __attribute__((swift_name("getName()")));
- (NSString *)getId __attribute__((swift_name("getId()")));
- (SharedKotlinArray *)getScoreTable __attribute__((swift_name("getScoreTable()")));
- (NSString *)getMedalResourceScore:(int32_t)score __attribute__((swift_name("getMedalResource(score:)")));
- (NSString *)getDescription __attribute__((swift_name("getDescription()")));
- (NSString *)getImageResource __attribute__((swift_name("getImageResource()")));
@end;

@interface SharedColor (Extensions)
- (NSString *)getName __attribute__((swift_name("getName()")));
- (NSString *)getHex __attribute__((swift_name("getHex()")));
@end;

@interface SharedShape (Extensions)
- (NSString *)getName __attribute__((swift_name("getName()")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ExtensionFunctionsKt")))
@interface SharedExtensionFunctionsKt : KotlinBase
+ (NSString *)addString:(NSString *)receiver part:(NSString *)part position:(int32_t)position __attribute__((swift_name("addString(_:part:position:)")));
+ (NSString *)removeWhitespaces:(NSString *)receiver __attribute__((swift_name("removeWhitespaces(_:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("GameTypeKt")))
@interface SharedGameTypeKt : KotlinBase
@property (class, readonly) NSString *MEDAL_FIRST_RESOURCE __attribute__((swift_name("MEDAL_FIRST_RESOURCE")));
@property (class, readonly) NSString *MEDAL_SECOND_RESOURCE __attribute__((swift_name("MEDAL_SECOND_RESOURCE")));
@property (class, readonly) NSString *MEDAL_THIRD_RESOURCE __attribute__((swift_name("MEDAL_THIRD_RESOURCE")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinPair")))
@interface SharedKotlinPair : KotlinBase
- (instancetype)initWithFirst:(id _Nullable)first second:(id _Nullable)second __attribute__((swift_name("init(first:second:)"))) __attribute__((objc_designated_initializer));
- (id _Nullable)component1 __attribute__((swift_name("component1()")));
- (id _Nullable)component2 __attribute__((swift_name("component2()")));
- (SharedKotlinPair *)doCopyFirst:(id _Nullable)first second:(id _Nullable)second __attribute__((swift_name("doCopy(first:second:)")));
- (BOOL)equalsOther:(id _Nullable)other __attribute__((swift_name("equals(other:)")));
- (int32_t)hashCode __attribute__((swift_name("hashCode()")));
- (NSString *)toString __attribute__((swift_name("toString()")));
@property (readonly) id _Nullable first __attribute__((swift_name("first")));
@property (readonly) id _Nullable second __attribute__((swift_name("second")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinArray")))
@interface SharedKotlinArray : KotlinBase
+ (instancetype)arrayWithSize:(int32_t)size init:(id _Nullable (^)(SharedInt *))init __attribute__((swift_name("init(size:init:)")));
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (id _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
- (id<SharedKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
- (void)setIndex:(int32_t)index value:(id _Nullable)value __attribute__((swift_name("set(index:value:)")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end;

__attribute__((swift_name("KotlinIterator")))
@protocol SharedKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end;

#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
