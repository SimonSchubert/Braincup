#import <Foundation/NSArray.h>
#import <Foundation/NSDictionary.h>
#import <Foundation/NSError.h>
#import <Foundation/NSObject.h>
#import <Foundation/NSSet.h>
#import <Foundation/NSString.h>
#import <Foundation/NSValue.h>

@class SharedGame, SharedColor, SharedShape, SharedKotlinEnum, SharedGameType, SharedMentalCalculationGameOperator, SharedAppState, SharedMentalCalculationGame, SharedColorConfusionGame, SharedSherlockCalculationGame, SharedChainCalculationGame;

@protocol SharedKotlinComparable, SharedAppInterface;

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

__attribute__((swift_name("Game")))
@interface SharedGame : KotlinBase
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("ChainCalculationGame")))
@interface SharedChainCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
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
- (instancetype)initWithName:(NSString *)name ordinal:(int32_t)ordinal __attribute__((swift_name("init(name:ordinal:)"))) __attribute__((objc_designated_initializer)) __attribute__((unavailable));
- (int32_t)compareToOther:(SharedGameType *)other __attribute__((swift_name("compareTo(other:)")));
@end;

__attribute__((objc_subclassing_restricted))
__attribute__((swift_name("MentalCalculationGame")))
@interface SharedMentalCalculationGame : SharedGame
- (instancetype)init __attribute__((swift_name("init()"))) __attribute__((objc_designated_initializer));
+ (instancetype)new __attribute__((availability(swift, unavailable, message="use object initializers instead")));
- (BOOL)isCorrectInput:(NSString *)input __attribute__((swift_name("isCorrect(input:)")));
- (void)nextRound __attribute__((swift_name("nextRound()")));
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
- (void)showMainMenuTitle:(NSString *)title description:(NSString *)description games:(NSArray<SharedGameType *> *)games callback:(void (^)(SharedGameType *))callback __attribute__((swift_name("showMainMenu(title:description:games:callback:)")));
- (void)showInstructionsTitle:(NSString *)title description:(NSString *)description start:(void (^)(void))start __attribute__((swift_name("showInstructions(title:description:start:)")));
- (void)showMentalCalculationGame:(SharedMentalCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showMentalCalculation(game:answer:next:)")));
- (void)showColorConfusionGame:(SharedColorConfusionGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showColorConfusion(game:answer:next:)")));
- (void)showSherlockCalculationGame:(SharedSherlockCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showSherlockCalculation(game:answer:next:)")));
- (void)showChainCalculationGame:(SharedChainCalculationGame *)game answer:(void (^)(NSString *))answer next:(void (^)(void))next __attribute__((swift_name("showChainCalculation(game:answer:next:)")));
- (void)showCorrectAnswerFeedback __attribute__((swift_name("showCorrectAnswerFeedback()")));
- (void)showWrongAnswerFeedback __attribute__((swift_name("showWrongAnswerFeedback()")));
- (void)showFinishFeedbackRank:(NSString *)rank plays:(int32_t)plays random:(void (^)(void))random __attribute__((swift_name("showFinishFeedback(rank:plays:random:)")));
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

#pragma clang diagnostic pop
NS_ASSUME_NONNULL_END
