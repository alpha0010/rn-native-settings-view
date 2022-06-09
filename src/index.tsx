import {
  requireNativeComponent,
  UIManager,
  Platform,
  ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'rn-native-settings-view' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

type RnNativeSettingsViewProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'RnNativeSettingsView';

export const RnNativeSettingsView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<RnNativeSettingsViewProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
