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

type SwitchSetting = {
  initialValue: boolean;
  title: string;
  type: 'switch';
  weight: number;
};

export type SettingsBase = Record<string, SwitchSetting>;

export type SettingsResult<Settings extends SettingsBase> = {
  [key in keyof Settings]: Settings[key]['type'] extends 'switch'
    ? boolean
    : never;
};

export type NativeOnSettings<Settings extends SettingsBase> =
  NativeSyntheticEvent<{ data: SettingsResult<Settings> }>;

type NativeSettingsViewProps<Settings extends SettingsBase> = {
  config: Settings;
  onChange: (event: NativeOnSettings<Settings>) => void;
  style: ViewStyle;
};

const ComponentName = 'RnNativeSettingsView';

export const NativeSettingsView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? // TODO: Can `SettingsBase` be forwarded as generic?
      requireNativeComponent<NativeSettingsViewProps<SettingsBase>>(
        ComponentName
      )
    : () => {
        throw new Error(LINKING_ERROR);
      };
