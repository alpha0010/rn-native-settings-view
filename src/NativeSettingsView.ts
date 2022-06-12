import {
  NativeSyntheticEvent,
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

export type NativeOnSettings = NativeSyntheticEvent<Record<string, boolean>>;

export type NativeSettingsViewProps = {
  config: string;
  onChange: (event: NativeOnSettings) => void;
  style: ViewStyle;
};

const ComponentName = 'RnNativeSettingsView';

export const NativeSettingsView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<NativeSettingsViewProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
