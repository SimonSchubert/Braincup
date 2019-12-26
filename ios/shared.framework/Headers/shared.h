#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class SharedKotlinPair, SharedUserStorageAchievements, SharedKotlinEnum, SharedGameType, SharedChallengeData, SharedChallengeDataParseError, SharedChallengeUrlResult, SharedChallengeUrl, SharedChallengeUrlError, SharedRiddleChallengeData, SharedSherlockCalculationChallengeData, SharedGame, SharedFigure, SharedAnomalyPuzzleGamePuzzle, SharedOperator, SharedColor, SharedShape, SharedHeightComparisonGameType, SharedDirection, SharedAppState, SharedUserStorage, SharedAnomalyPuzzleGame, SharedChainCalculationGame, SharedColorConfusionGame, SharedFractionCalculationGame, SharedHeightComparisonGame, SharedMentalCalculationGame, SharedPathFinderGame, SharedRiddleGame, SharedSherlockCalculationGame, SharedKotlinArray, SharedKotlinRegex, SharedKotlinRegexOption, SharedKotlinMatchResultDestructured, SharedKotlinIntRange, SharedKotlinMatchGroup, SharedKotlinIntIterator, SharedKotlinIntProgression;

@protocol SharedKotlinComparable, SharedNavigationInterface, SharedKotlinIterator, SharedKotlinMatchResult, SharedKotlinSequence, SharedKotlinMatchGroupCollection, SharedKotlinIterable, SharedKotlinCollection, SharedKotlinClosedRange;

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
- (int32_t)getAppOpenCount __attribute__((swift_name("getAppOpenCount()")));
- (int32_t)getHighScoreGameId:(NSString *)gameId __attribute__((swift_name("getHighScore(gameId:)")));
- (NSArray<SharedKotlinPair *> *)getScoresGameId:(NSString *)gameId __attribute__((swift_name("getScores(gameId:)")));
- (int32_t)getTotalScore __attribute__((swift_name("getTotalScore()")));
- (NSMutableArray<SharedUserStorageAchievements *> *)getUnlockedAchievements __attribute__((swift_name("getUnlockedAchievements()")));
- (BOOL)hasAppOpenAchievementAchievement:(SharedUserStorageAchievements *)achievement appOpenDay:(int32_t)appOpenDay __attribute__((swift_name("hasAppOpenAchievement(achievement:appOpenDay:)")));
- (void)putAppOpen __attribute__((swift_name("putAppOpen()")));
- (BOOL)putScoreGameId:(NSString *)gameId score:(int32_t)score __attribute__((swift_name("putScore(gameId:score:)")));
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
__attribute__((swift_name("UserStorage.Achievements")))
@interface SharedUserStorageAchievements : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedUserStorageAchievements *medalBronze __attribute__((swift_name("medalBronze")));
@property (class, readonly) SharedUserStorageAchievements *medalSilver __attribute__((swift_name("medalSilver")));
@property (class, readonly) SharedUserStorageAchievements *medalGold __attribute__((swift_name("medalGold")));
@property (class, readonly) SharedUserStorageAchievements *scores10 __attribute__((swift_name("scores10")));
@property (class, readonly) SharedUserStorageAchievements *scores100 __attribute__((swift_name("scores100")));
@property (class, readonly) SharedUserStorageAchievements *scores1000 __attribute__((swift_name("scores1000")));
@property (class, readonly) SharedUserStorageAchievements *scores10000 __attribute__((swift_name("scores10000")));
@property (class, readonly) SharedUserStorageAchievements *appOpen3 __attribute__((swift_name("appOpen3")));
@property (class, readonly) SharedUserStorageAchievements *appOpen7 __attribute__((swift_name("appOpen7")));
@property (class, readonly) SharedUserStorageAchievements *appOpen30 __attribute__((swift_name("appOpen30")));
- (int32_t)compareToOther:(SharedUserStorageAchievements *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("UserStorage.Companion")))
@interface SharedUserStorageCompanion : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (readonly) NSString *KEY_APP_OPEN_COMBO __attribute__((swift_name("KEY_APP_OPEN_COMBO")));
@property (readonly) NSString *KEY_APP_OPEN_DAY __attribute__((swift_name("KEY_APP_OPEN_DAY")));
@property (readonly) NSString *KEY_TOTAL_SCORE __attribute__((swift_name("KEY_TOTAL_SCORE")));
@property (readonly) NSString *KEY_UNLOCKED_ACHIEVEMENTS __attribute__((swift_name("KEY_UNLOCKED_ACHIEVEMENTS")));
@end;

__attribute__((swift_name("ChallengeData")))
@interface SharedChallengeData : KotlinBase
- (NSString *)getTitle __attribute__((swift_name("getTitle()")));
@property (readonly) NSString *challengeSecret __attribute__((swift_name("challengeSecret")));
@property (readonly) SharedGameType *gameType __attribute__((swift_name("gameType")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ChallengeData.Companion")))
@interface SharedChallengeDataCompanion : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
- (SharedChallengeData *)parseUrl:(NSString *)url data:(NSString *)data __attribute__((swift_name("parse(url:data:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ChallengeDataParseError")))
@interface SharedChallengeDataParseError : SharedChallengeData
- (instancetype)initWithMsg:(NSString *)msg __attribute__((swift_name("init(msg:)"))) __attribute__((objc_designated_initializer));
- (NSString *)component1 __attribute__((swift_name("component1()")));
- (SharedChallengeDataParseError *)doCopyMsg:(NSString *)msg __attribute__((swift_name("doCopy(msg:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *msg __attribute__((swift_name("msg")));
@end;

__attribute__((swift_name("ChallengeUrlResult")))
@interface SharedChallengeUrlResult : KotlinBase
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ChallengeUrl")))
@interface SharedChallengeUrl : SharedChallengeUrlResult
- (instancetype)initWithUrl:(NSString *)url __attribute__((swift_name("init(url:)"))) __attribute__((objc_designated_initializer));
- (NSString *)component1 __attribute__((swift_name("component1()")));
- (SharedChallengeUrl *)doCopyUrl:(NSString *)url __attribute__((swift_name("doCopy(url:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *url __attribute__((swift_name("url")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ChallengeUrlError")))
@interface SharedChallengeUrlError : SharedChallengeUrlResult
- (instancetype)initWithErrorMessage:(NSString *)errorMessage __attribute__((swift_name("init(errorMessage:)"))) __attribute__((objc_designated_initializer));
- (NSString *)component1 __attribute__((swift_name("component1()")));
- (SharedChallengeUrlError *)doCopyErrorMessage:(NSString *)errorMessage __attribute__((swift_name("doCopy(errorMessage:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSString *errorMessage __attribute__((swift_name("errorMessage")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("RiddleChallengeData")))
@interface SharedRiddleChallengeData : SharedChallengeData
- (instancetype)initWithUrl:(NSString *)url t:(NSString *)t secret:(NSString *)secret description:(NSString *)description answers:(NSArray<NSString *> *)answers __attribute__((swift_name("init(url:t:secret:description:answers:)"))) __attribute__((objc_designated_initializer));
- (NSString *)component1 __attribute__((swift_name("component1()")));
- (NSString *)component2 __attribute__((swift_name("component2()")));
- (NSString *)component3 __attribute__((swift_name("component3()")));
- (NSString *)component4 __attribute__((swift_name("component4()")));
- (NSArray<NSString *> *)component5 __attribute__((swift_name("component5()")));
- (SharedRiddleChallengeData *)doCopyUrl:(NSString *)url t:(NSString *)t secret:(NSString *)secret description:(NSString *)description answers:(NSArray<NSString *> *)answers __attribute__((swift_name("doCopy(url:t:secret:description:answers:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSArray<NSString *> *answers __attribute__((swift_name("answers")));
@property (readonly, getter=description_) NSString *description __attribute__((swift_name("description")));
@property (readonly) NSString *secret __attribute__((swift_name("secret")));
@property (readonly) NSString *t __attribute__((swift_name("t")));
@property (readonly) NSString *url __attribute__((swift_name("url")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SherlockCalculationChallengeData")))
@interface SharedSherlockCalculationChallengeData : SharedChallengeData
- (instancetype)initWithUrl:(NSString *)url t:(NSString *)t secret:(NSString *)secret goal:(int32_t)goal numbers:(NSArray<SharedInt *> *)numbers __attribute__((swift_name("init(url:t:secret:goal:numbers:)"))) __attribute__((objc_designated_initializer));
- (NSString *)component1 __attribute__((swift_name("component1()")));
- (NSString *)component2 __attribute__((swift_name("component2()")));
- (NSString *)component3 __attribute__((swift_name("component3()")));
- (int32_t)component4 __attribute__((swift_name("component4()")));
- (NSArray<SharedInt *> *)component5 __attribute__((swift_name("component5()")));
- (SharedSherlockCalculationChallengeData *)doCopyUrl:(NSString *)url t:(NSString *)t secret:(NSString *)secret goal:(int32_t)goal numbers:(NSArray<SharedInt *> *)numbers __attribute__((swift_name("doCopy(url:t:secret:goal:numbers:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t goal __attribute__((swift_name("goal")));
@property (readonly) NSArray<SharedInt *> *numbers __attribute__((swift_name("numbers")));
@property (readonly) NSString *secret __attribute__((swift_name("secret")));
@property (readonly) NSString *t __attribute__((swift_name("t")));
@property (readonly) NSString *url __attribute__((swift_name("url")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("UrlBuilder")))
@interface SharedUrlBuilder : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)urlBuilder __attribute__((swift_name("init()")));
- (SharedChallengeUrlResult *)buildRiddleChallengeUrlTitle:(NSString *)title secret:(NSString *)secret description:(NSString *)description answersInput:(NSString *)answersInput __attribute__((swift_name("buildRiddleChallengeUrl(title:secret:description:answersInput:)")));
- (SharedChallengeUrlResult *)buildSherlockCalculationChallengeUrlTitle:(NSString *)title secret:(NSString *)secret goalInput:(NSString *)goalInput numbersInput:(NSString *)numbersInput __attribute__((swift_name("buildSherlockCalculationChallengeUrl(title:secret:goalInput:numbersInput:)")));
@end;

__attribute__((swift_name("Game")))
@interface SharedGame : KotlinBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString *)getName __attribute__((swift_name("getName()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property BOOL answeredAllCorrect __attribute__((swift_name("answeredAllCorrect")));
@property int32_t round __attribute__((swift_name("round")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AnomalyPuzzleGame")))
@interface SharedAnomalyPuzzleGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property (readonly) NSMutableArray<SharedFigure *> *figures __attribute__((swift_name("figures")));
@property int32_t resultIndex __attribute__((swift_name("resultIndex")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AnomalyPuzzleGame.Puzzle")))
@interface SharedAnomalyPuzzleGamePuzzle : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedAnomalyPuzzleGamePuzzle *randomColorAndShape __attribute__((swift_name("randomColorAndShape")));
@property (class, readonly) SharedAnomalyPuzzleGamePuzzle *sameColor __attribute__((swift_name("sameColor")));
@property (class, readonly) SharedAnomalyPuzzleGamePuzzle *sameShape __attribute__((swift_name("sameShape")));
@property (class, readonly) SharedAnomalyPuzzleGamePuzzle *sameShapeMaxColor __attribute__((swift_name("sameShapeMaxColor")));
@property (class, readonly) SharedAnomalyPuzzleGamePuzzle *triangleRotation __attribute__((swift_name("triangleRotation")));
@property (class, readonly) SharedAnomalyPuzzleGamePuzzle *rectangleVariation __attribute__((swift_name("rectangleVariation")));
@property (class, readonly) SharedAnomalyPuzzleGamePuzzle *lRotation __attribute__((swift_name("lRotation")));
- (int32_t)compareToOther:(SharedAnomalyPuzzleGamePuzzle *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ChainCalculationGame")))
@interface SharedChainCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property (readonly) NSArray<SharedOperator *> *availableOperators __attribute__((swift_name("availableOperators")));
@property NSString *calculation __attribute__((swift_name("calculation")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ColorConfusionGame")))
@interface SharedColorConfusionGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSArray<NSString *> *)getPossibleAnswers __attribute__((swift_name("getPossibleAnswers()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)points __attribute__((swift_name("points()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property SharedColor *answerColor __attribute__((swift_name("answerColor")));
@property SharedShape *answerShape __attribute__((swift_name("answerShape")));
@property int32_t colorPoints __attribute__((swift_name("colorPoints")));
@property SharedColor *displayedColor __attribute__((swift_name("displayedColor")));
@property SharedShape *displayedShape __attribute__((swift_name("displayedShape")));
@property int32_t shapePoints __attribute__((swift_name("shapePoints")));
@property SharedColor *stringColor __attribute__((swift_name("stringColor")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("FractionCalculationGame")))
@interface SharedFractionCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property NSString *calculation __attribute__((swift_name("calculation")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("GameType")))
@interface SharedGameType : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedGameType *anomalyPuzzle __attribute__((swift_name("anomalyPuzzle")));
@property (class, readonly) SharedGameType *mentalCalculation __attribute__((swift_name("mentalCalculation")));
@property (class, readonly) SharedGameType *colorConfusion __attribute__((swift_name("colorConfusion")));
@property (class, readonly) SharedGameType *sherlockCalculation __attribute__((swift_name("sherlockCalculation")));
@property (class, readonly) SharedGameType *chainCalculation __attribute__((swift_name("chainCalculation")));
@property (class, readonly) SharedGameType *fractionCalculation __attribute__((swift_name("fractionCalculation")));
@property (class, readonly) SharedGameType *heightComparison __attribute__((swift_name("heightComparison")));
@property (class, readonly) SharedGameType *riddle __attribute__((swift_name("riddle")));
@property (class, readonly) SharedGameType *pathFinder __attribute__((swift_name("pathFinder")));
- (int32_t)compareToOther:(SharedGameType *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HeightComparisonGame")))
@interface SharedHeightComparisonGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property NSMutableArray<NSString *> *answers __attribute__((swift_name("answers")));
@property NSArray<SharedHeightComparisonGameType *> *types __attribute__((swift_name("types")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("HeightComparisonGame.Type_")))
@interface SharedHeightComparisonGameType : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedHeightComparisonGameType *addition __attribute__((swift_name("addition")));
@property (class, readonly) SharedHeightComparisonGameType *fraction __attribute__((swift_name("fraction")));
@property (class, readonly) SharedHeightComparisonGameType *multiplication __attribute__((swift_name("multiplication")));
- (int32_t)compareToOther:(SharedHeightComparisonGameType *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MentalCalculationGame")))
@interface SharedMentalCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (int32_t)getNumberLength __attribute__((swift_name("getNumberLength()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property NSString *calculation __attribute__((swift_name("calculation")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("PathFinderGame")))
@interface SharedPathFinderGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property int32_t currentX __attribute__((swift_name("currentX")));
@property int32_t currentY __attribute__((swift_name("currentY")));
@property (readonly) NSMutableArray<SharedDirection *> *directions __attribute__((swift_name("directions")));
@property (readonly) int32_t gridSize __attribute__((swift_name("gridSize")));
@property SharedDirection *lastDirection __attribute__((swift_name("lastDirection")));
@property int32_t startX __attribute__((swift_name("startX")));
@property (readonly) int32_t startY __attribute__((swift_name("startY")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("RiddleGame")))
@interface SharedRiddleGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property NSString *quest __attribute__((swift_name("quest")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("SherlockCalculationGame")))
@interface SharedSherlockCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedGameType *)getGameType __attribute__((swift_name("getGameType()")));
- (NSString *)getNumbersString __attribute__((swift_name("getNumbersString()")));
- (NSString * _Nullable)hint __attribute__((swift_name("hint()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (NSString *)solution __attribute__((swift_name("solution()")));
@property (readonly) NSMutableArray<SharedInt *> *numbers __attribute__((swift_name("numbers")));
@property int32_t result __attribute__((swift_name("result")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Calculator")))
@interface SharedCalculator : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)calculator __attribute__((swift_name("init()")));
- (double)calculateInput:(NSString *)input __attribute__((swift_name("calculate(input:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Color")))
@interface SharedColor : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedColor *red __attribute__((swift_name("red")));
@property (class, readonly) SharedColor *green __attribute__((swift_name("green")));
@property (class, readonly) SharedColor *blue __attribute__((swift_name("blue")));
@property (class, readonly) SharedColor *purple __attribute__((swift_name("purple")));
@property (class, readonly) SharedColor *yellow __attribute__((swift_name("yellow")));
@property (class, readonly) SharedColor *orange __attribute__((swift_name("orange")));
@property (class, readonly) SharedColor *turkies __attribute__((swift_name("turkies")));
@property (class, readonly) SharedColor *rosa __attribute__((swift_name("rosa")));
@property (class, readonly) SharedColor *greyDark __attribute__((swift_name("greyDark")));
@property (class, readonly) SharedColor *greyLight __attribute__((swift_name("greyLight")));
- (int32_t)compareToOther:(SharedColor *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Direction")))
@interface SharedDirection : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedDirection *up __attribute__((swift_name("up")));
@property (class, readonly) SharedDirection *right __attribute__((swift_name("right")));
@property (class, readonly) SharedDirection *down __attribute__((swift_name("down")));
@property (class, readonly) SharedDirection *left __attribute__((swift_name("left")));
- (int32_t)compareToOther:(SharedDirection *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Figure")))
@interface SharedFigure : KotlinBase
- (instancetype)initWithShape:(SharedShape *)shape color:(SharedColor *)color rotation:(int32_t)rotation __attribute__((swift_name("init(shape:color:rotation:)"))) __attribute__((objc_designated_initializer));
- (NSString *)getRotationString __attribute__((swift_name("getRotationString()")));
@property SharedColor *color __attribute__((swift_name("color")));
@property int32_t rotation __attribute__((swift_name("rotation")));
@property SharedShape *shape __attribute__((swift_name("shape")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Operator")))
@interface SharedOperator : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedOperator *plus __attribute__((swift_name("plus")));
@property (class, readonly) SharedOperator *minus __attribute__((swift_name("minus")));
@property (class, readonly) SharedOperator *multiply __attribute__((swift_name("multiply")));
@property (class, readonly) SharedOperator *divide __attribute__((swift_name("divide")));
- (int32_t)compareToOther:(SharedOperator *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Shape")))
@interface SharedShape : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedShape *square __attribute__((swift_name("square")));
@property (class, readonly) SharedShape *triangle __attribute__((swift_name("triangle")));
@property (class, readonly) SharedShape *circle __attribute__((swift_name("circle")));
@property (class, readonly) SharedShape *heart __attribute__((swift_name("heart")));
@property (class, readonly) SharedShape *star __attribute__((swift_name("star")));
@property (class, readonly) SharedShape *t __attribute__((swift_name("t")));
@property (class, readonly) SharedShape *l __attribute__((swift_name("l")));
@property (class, readonly) SharedShape *diamond __attribute__((swift_name("diamond")));
@property (class, readonly) SharedShape *house __attribute__((swift_name("house")));
@property (class, readonly) SharedShape *abstractTriangle __attribute__((swift_name("abstractTriangle")));
@property (class, readonly) SharedShape *arrow __attribute__((swift_name("arrow")));
- (int32_t)compareToOther:(SharedShape *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("AppState")))
@interface SharedAppState : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedAppState *start __attribute__((swift_name("start")));
@property (class, readonly) SharedAppState *game __attribute__((swift_name("game")));
@property (class, readonly) SharedAppState *instructions __attribute__((swift_name("instructions")));
@property (class, readonly) SharedAppState *achievements __attribute__((swift_name("achievements")));
@property (class, readonly) SharedAppState *scoreboard __attribute__((swift_name("scoreboard")));
@property (class, readonly) SharedAppState *createChallenge __attribute__((swift_name("createChallenge")));
@property (class, readonly) SharedAppState *challenge __attribute__((swift_name("challenge")));
- (int32_t)compareToOther:(SharedAppState *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NavigationController")))
@interface SharedNavigationController : KotlinBase
- (instancetype)initWithApp:(id<SharedNavigationInterface>)app __attribute__((swift_name("init(app:)"))) __attribute__((objc_designated_initializer));
- (void)startState:(SharedAppState *)state gameType:(SharedGameType * _Nullable)gameType challengeData:(SharedChallengeData * _Nullable)challengeData __attribute__((swift_name("start(state:gameType:challengeData:)")));
@property int32_t plays __attribute__((swift_name("plays")));
@property int32_t points __attribute__((swift_name("points")));
@property double startTime __attribute__((swift_name("startTime")));
@property SharedAppState *state __attribute__((swift_name("state")));
@property (readonly) SharedUserStorage *storage __attribute__((swift_name("storage")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("NavigationController.Companion")))
@interface SharedNavigationControllerCompanion : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)companion __attribute__((swift_name("init()")));
@property (readonly) NSArray<SharedGameType *> *games __attribute__((swift_name("games")));
@end;

__attribute__((swift_name("NavigationInterface")))
@protocol SharedNavigationInterface
@required
- (void)showAchievementsAllAchievements:(NSArray<SharedUserStorageAchievements *> *)allAchievements unlockedAchievements:(NSArray<SharedUserStorageAchievements *> *)unlockedAchievements __attribute__((swift_name("showAchievements(allAchievements:unlockedAchievements:)")));
- (void)showAnomalyPuzzleGame:(SharedAnomalyPuzzleGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showAnomalyPuzzle(game:answer:next:)")));
- (void)showChainCalculationGame:(SharedChainCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showChainCalculation(game:answer:next:)")));
- (void)showColorConfusionGame:(SharedColorConfusionGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showColorConfusion(game:answer:next:)")));
- (void)showCorrectAnswerFeedbackGameType:(SharedGameType *)gameType hint:(NSString * _Nullable)hint __attribute__((swift_name("showCorrectAnswerFeedback(gameType:hint:)")));
- (void)showCorrectChallengeAnswerFeedbackSolution:(NSString *)solution secret:(NSString *)secret url:(NSString *)url __attribute__((swift_name("showCorrectChallengeAnswerFeedback(solution:secret:url:)")));
- (void)showCreateChallengeMenuGames:(NSArray<SharedGameType *> *)games answer:(void (^)(SharedGameType *))answer __attribute__((swift_name("showCreateChallengeMenu(games:answer:)")));
- (void)showCreateRiddleChallengeTitle:(NSString *)title __attribute__((swift_name("showCreateRiddleChallenge(title:)")));
- (void)showCreateSherlockCalculationChallengeTitle:(NSString *)title description:(NSString *)description __attribute__((swift_name("showCreateSherlockCalculationChallenge(title:description:)")));
- (void)showFinishFeedbackGameType:(SharedGameType *)gameType rank:(NSString *)rank newHighscore:(BOOL)newHighscore answeredAllCorrect:(BOOL)answeredAllCorrect plays:(int32_t)plays random:(void (^)(void))random again:(void (^)(void))again __attribute__((swift_name("showFinishFeedback(gameType:rank:newHighscore:answeredAllCorrect:plays:random:again:)")));
- (void)showFractionCalculationGame:(SharedFractionCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showFractionCalculation(game:answer:next:)")));
- (void)showHeightComparisonGame:(SharedHeightComparisonGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showHeightComparison(game:answer:next:)")));
- (void)showInstructionsGameType:(SharedGameType *)gameType title:(NSString *)title description:(NSString *)description showChallengeInfo:(BOOL)showChallengeInfo hasSecret:(BOOL)hasSecret start:(void (^)(void))start __attribute__((swift_name("showInstructions(gameType:title:description:showChallengeInfo:hasSecret:start:)")));
- (void)showMainMenuTitle:(NSString *)title description:(NSString *)description games:(NSArray<SharedGameType *> *)games showInstructions:(void (^)(SharedGameType *))showInstructions showScore:(void (^)(SharedGameType *))showScore showAchievements:(void (^)(void))showAchievements createChallenge:(void (^)(void))createChallenge storage:(SharedUserStorage *)storage totalScore:(int32_t)totalScore appOpenCount:(int32_t)appOpenCount __attribute__((swift_name("showMainMenu(title:description:games:showInstructions:showScore:showAchievements:createChallenge:storage:totalScore:appOpenCount:)")));
- (void)showMentalCalculationGame:(SharedMentalCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showMentalCalculation(game:answer:next:)")));
- (void)showPathFinderGame:(SharedPathFinderGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showPathFinder(game:answer:next:)")));
- (void)showRiddleGame:(SharedRiddleGame *)game title:(NSString *)title answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showRiddle(game:title:answer:next:)")));
- (void)showScoreboardGameType:(SharedGameType *)gameType highscore:(int32_t)highscore scores:(NSArray<SharedKotlinPair *> *)scores __attribute__((swift_name("showScoreboard(gameType:highscore:scores:)")));
- (void)showSherlockCalculationGame:(SharedSherlockCalculationGame *)game title:(NSString *)title answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showSherlockCalculation(game:title:answer:next:)")));
- (void)showWrongAnswerFeedbackGameType:(SharedGameType *)gameType solution:(NSString *)solution __attribute__((swift_name("showWrongAnswerFeedback(gameType:solution:)")));
- (void)showWrongChallengeAnswerFeedbackUrl:(NSString *)url __attribute__((swift_name("showWrongChallengeAnswerFeedback(url:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("Version")))
@interface SharedVersion : KotlinBase
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
+ (instancetype)version_ __attribute__((swift_name("init()")));
@property (readonly) NSString *name __attribute__((swift_name("name")));
@end;

@interface SharedUserStorageAchievements (Extensions)
- (NSString *)getDescription __attribute__((swift_name("getDescription()")));
@end;

@interface SharedGameType (Extensions)
- (NSString *)getDescriptionAddTimeLimit:(BOOL)addTimeLimit __attribute__((swift_name("getDescription(addTimeLimit:)")));
- (NSString *)getId __attribute__((swift_name("getId()")));
- (NSString *)getImageResource __attribute__((swift_name("getImageResource()")));
- (NSString *)getName __attribute__((swift_name("getName()")));
- (SharedKotlinArray *)getScoreTable __attribute__((swift_name("getScoreTable()")));
@end;

@interface SharedDirection (Extensions)
- (SharedFigure *)getFigure __attribute__((swift_name("getFigure()")));
@end;

@interface SharedColor (Extensions)
- (NSString *)getHex __attribute__((swift_name("getHex()")));
- (NSString *)getName __attribute__((swift_name("getName()")));
@end;

@interface SharedShape (Extensions)
- (NSString *)getName __attribute__((swift_name("getName()")));
- (NSArray<SharedKotlinPair *> *)getPaths __attribute__((swift_name("getPaths()")));
@end;

@interface SharedOperator (Extensions)
- (unichar)toChar __attribute__((swift_name("toChar()")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("RegexKt")))
@interface SharedRegexKt : KotlinBase
@property (class, readonly) SharedKotlinRegex *numbersRegex __attribute__((swift_name("numbersRegex")));
@property (class, readonly) SharedKotlinRegex *validCalculationRegex __attribute__((swift_name("validCalculationRegex")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ExtensionFunctionsKt")))
@interface SharedExtensionFunctionsKt : KotlinBase
+ (NSString *)addString:(NSString *)receiver part:(NSString *)part position:(int32_t)position __attribute__((swift_name("addString(_:part:position:)")));
+ (NSString *)removeWhitespaces:(NSString *)receiver __attribute__((swift_name("removeWhitespaces(_:)")));
+ (NSArray<SharedInt *> *)splitToIntList:(NSString *)receiver __attribute__((swift_name("splitToIntList(_:)")));
+ (NSArray<NSString *> *)splitToStringList:(NSString *)receiver __attribute__((swift_name("splitToStringList(_:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ShapeKt")))
@interface SharedShapeKt : KotlinBase
@property (class, readonly) NSArray<SharedKotlinPair *> *abstractTrianglePath __attribute__((swift_name("abstractTrianglePath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *arrowPath __attribute__((swift_name("arrowPath")));
@property (class, readonly) NSMutableArray<SharedKotlinPair *> *circlePath __attribute__((swift_name("circlePath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *diamondPath __attribute__((swift_name("diamondPath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *heartPath __attribute__((swift_name("heartPath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *housePath __attribute__((swift_name("housePath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *lPath __attribute__((swift_name("lPath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *squarePath __attribute__((swift_name("squarePath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *starPath __attribute__((swift_name("starPath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *tPath __attribute__((swift_name("tPath")));
@property (class, readonly) NSArray<SharedKotlinPair *> *trianglePath __attribute__((swift_name("trianglePath")));
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

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinRegex")))
@interface SharedKotlinRegex : KotlinBase
- (instancetype)initWithPattern:(NSString *)pattern __attribute__((swift_name("init(pattern:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithPattern:(NSString *)pattern option:(SharedKotlinRegexOption *)option __attribute__((swift_name("init(pattern:option:)"))) __attribute__((objc_designated_initializer));
- (instancetype)initWithPattern:(NSString *)pattern options:(NSSet<SharedKotlinRegexOption *> *)options __attribute__((swift_name("init(pattern:options:)"))) __attribute__((objc_designated_initializer));
- (BOOL)containsMatchInInput:(id)input __attribute__((swift_name("containsMatchIn(input:)")));
- (id<SharedKotlinMatchResult> _Nullable)findInput:(id)input startIndex:(int32_t)startIndex __attribute__((swift_name("find(input:startIndex:)")));
- (id<SharedKotlinSequence>)findAllInput:(id)input startIndex:(int32_t)startIndex __attribute__((swift_name("findAll(input:startIndex:)")));
- (id<SharedKotlinMatchResult> _Nullable)matchEntireInput:(id)input __attribute__((swift_name("matchEntire(input:)")));
- (BOOL)matchesInput:(id)input __attribute__((swift_name("matches(input:)")));
- (NSString *)replaceInput:(id)input transform:(id (^)(id<SharedKotlinMatchResult>))transform __attribute__((swift_name("replace(input:transform:)")));
- (NSString *)replaceInput:(id)input replacement:(NSString *)replacement __attribute__((swift_name("replace(input:replacement:)")));
- (NSString *)replaceFirstInput:(id)input replacement:(NSString *)replacement __attribute__((swift_name("replaceFirst(input:replacement:)")));
- (NSArray<NSString *> *)splitInput:(id)input limit:(int32_t)limit __attribute__((swift_name("split(input:limit:)")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) NSSet<SharedKotlinRegexOption *> *options __attribute__((swift_name("options")));
@property (readonly) NSString *pattern __attribute__((swift_name("pattern")));
@end;

__attribute__((swift_name("KotlinIterator")))
@protocol SharedKotlinIterator
@required
- (BOOL)hasNext __attribute__((swift_name("hasNext()")));
- (id _Nullable)next __attribute__((swift_name("next()")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinRegexOption")))
@interface SharedKotlinRegexOption : SharedKotlinEnum
+ (instancetype)alloc __attribute__((unavailable));
+ (instancetype)allocWithZone:(struct _NSZone *)zone __attribute__((unavailable));
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
@property (class, readonly) SharedKotlinRegexOption *ignoreCase __attribute__((swift_name("ignoreCase")));
@property (class, readonly) SharedKotlinRegexOption *multiline __attribute__((swift_name("multiline")));
@property (class, readonly) SharedKotlinRegexOption *literal __attribute__((swift_name("literal")));
@property (class, readonly) SharedKotlinRegexOption *unixLines __attribute__((swift_name("unixLines")));
@property (class, readonly) SharedKotlinRegexOption *comments __attribute__((swift_name("comments")));
@property (class, readonly) SharedKotlinRegexOption *dotMatchesAll __attribute__((swift_name("dotMatchesAll")));
@property (class, readonly) SharedKotlinRegexOption *canonEq __attribute__((swift_name("canonEq")));
- (int32_t)compareToOther:(SharedKotlinRegexOption *)other __attribute__((swift_name("compareTo(other:)")));
@property (readonly) int32_t mask __attribute__((swift_name("mask")));
@property (readonly) int32_t value __attribute__((swift_name("value")));
@end;

__attribute__((swift_name("KotlinMatchResult")))
@protocol SharedKotlinMatchResult
@required
- (id<SharedKotlinMatchResult> _Nullable)next __attribute__((swift_name("next()")));
@property (readonly) SharedKotlinMatchResultDestructured *destructured __attribute__((swift_name("destructured")));
@property (readonly) NSArray<NSString *> *groupValues __attribute__((swift_name("groupValues")));
@property (readonly) id<SharedKotlinMatchGroupCollection> groups __attribute__((swift_name("groups")));
@property (readonly) SharedKotlinIntRange *range __attribute__((swift_name("range")));
@property (readonly) NSString *value_ __attribute__((swift_name("value_")));
@end;

__attribute__((swift_name("KotlinSequence")))
@protocol SharedKotlinSequence
@required
- (id<SharedKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinMatchResultDestructured")))
@interface SharedKotlinMatchResultDestructured : KotlinBase
- (NSString *)component1 __attribute__((swift_name("component1()")));
- (NSString *)component10 __attribute__((swift_name("component10()")));
- (NSString *)component2 __attribute__((swift_name("component2()")));
- (NSString *)component3 __attribute__((swift_name("component3()")));
- (NSString *)component4 __attribute__((swift_name("component4()")));
- (NSString *)component5 __attribute__((swift_name("component5()")));
- (NSString *)component6 __attribute__((swift_name("component6()")));
- (NSString *)component7 __attribute__((swift_name("component7()")));
- (NSString *)component8 __attribute__((swift_name("component8()")));
- (NSString *)component9 __attribute__((swift_name("component9()")));
- (NSArray<NSString *> *)toList __attribute__((swift_name("toList()")));
@property (readonly) id<SharedKotlinMatchResult> match __attribute__((swift_name("match")));
@end;

__attribute__((swift_name("KotlinIterable")))
@protocol SharedKotlinIterable
@required
- (id<SharedKotlinIterator>)iterator __attribute__((swift_name("iterator()")));
@end;

__attribute__((swift_name("KotlinCollection")))
@protocol SharedKotlinCollection <SharedKotlinIterable>
@required
- (BOOL)containsElement:(id _Nullable)element __attribute__((swift_name("contains(element:)")));
- (BOOL)containsAllElements:(id)elements __attribute__((swift_name("containsAll(elements:)")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
@property (readonly) int32_t size __attribute__((swift_name("size")));
@end;

__attribute__((swift_name("KotlinMatchGroupCollection")))
@protocol SharedKotlinMatchGroupCollection <SharedKotlinCollection>
@required
- (SharedKotlinMatchGroup * _Nullable)getIndex:(int32_t)index __attribute__((swift_name("get(index:)")));
@end;

__attribute__((swift_name("KotlinIntProgression")))
@interface SharedKotlinIntProgression : KotlinBase <SharedKotlinIterable>
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
- (SharedKotlinIntIterator *)iterator __attribute__((swift_name("iterator()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) int32_t first __attribute__((swift_name("first")));
@property (readonly) int32_t last __attribute__((swift_name("last")));
@property (readonly) int32_t step __attribute__((swift_name("step")));
@end;

__attribute__((swift_name("KotlinClosedRange")))
@protocol SharedKotlinClosedRange
@required
- (BOOL)containsValue:(id)value __attribute__((swift_name("contains(value:)")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
@property (readonly) id endInclusive __attribute__((swift_name("endInclusive")));
@property (readonly) id start __attribute__((swift_name("start")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinIntRange")))
@interface SharedKotlinIntRange : SharedKotlinIntProgression <SharedKotlinClosedRange>
- (instancetype)initWithStart:(int32_t)start endInclusive:(int32_t)endInclusive __attribute__((swift_name("init(start:endInclusive:)"))) __attribute__((objc_designated_initializer));
- (BOOL)containsValue:(SharedInt *)value __attribute__((swift_name("contains(value:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (BOOL)isEmpty __attribute__((swift_name("isEmpty()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedInt *endInclusive __attribute__((swift_name("endInclusive")));
@property (readonly) SharedInt *start __attribute__((swift_name("start")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("KotlinMatchGroup")))
@interface SharedKotlinMatchGroup : KotlinBase
- (instancetype)initWithValue:(NSString *)value range:(SharedKotlinIntRange *)range __attribute__((swift_name("init(value:range:)"))) __attribute__((objc_designated_initializer));
- (NSString *)component1 __attribute__((swift_name("component1()")));
- (SharedKotlinIntRange *)component2 __attribute__((swift_name("component2()")));
- (SharedKotlinMatchGroup *)doCopyValue:(NSString *)value range:(SharedKotlinIntRange *)range __attribute__((swift_name("doCopy(value:range:)")));
- (BOOL)isEqual:(id _Nullable)other __attribute__((swift_name("isEqual(_:)")));
- (NSUInteger)hash __attribute__((swift_name("hash()")));
- (NSString *)description __attribute__((swift_name("description()")));
@property (readonly) SharedKotlinIntRange *range __attribute__((swift_name("range")));
@property (readonly) NSString *value __attribute__((swift_name("value")));
@end;

__attribute__((swift_name("KotlinIntIterator")))
@interface SharedKotlinIntIterator : KotlinBase <SharedKotlinIterator>
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (SharedInt *)next __attribute__((swift_name("next()")));
- (int32_t)nextInt __attribute__((swift_name("nextInt()")));
@end;

#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
