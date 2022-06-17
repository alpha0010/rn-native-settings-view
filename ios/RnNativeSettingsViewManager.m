#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(RnNativeSettingsViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(config, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(onChange, RCTBubblingEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onDetails, RCTBubblingEventBlock)

@end
