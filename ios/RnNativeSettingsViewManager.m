#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(RnNativeSettingsViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(config, NSString)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)

@end
