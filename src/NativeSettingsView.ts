import {
  Platform,
  requireNativeComponent,
  UIManager,
  ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'rn-native-settings-view' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

export type SettingsViewProps = {
  config: string;
  style: ViewStyle;
};

const ComponentName = 'RnNativeSettingsView';

export const NativeSettingsView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<SettingsViewProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
