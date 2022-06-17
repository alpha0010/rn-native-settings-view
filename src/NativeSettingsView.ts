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

type DetailsRow = {
  title: string;
  type: 'details';
  details: string;
  weight: number;
};

type ListSetting = {
  initialValue: string;
  title: string;
  type: 'list';
  labels: ReadonlyArray<string>;
  values: ReadonlyArray<string>;
  weight: number;
};

type SwitchSetting = {
  initialValue: boolean;
  title: string;
  type: 'switch';
  weight: number;
};

type SettingsRow = DetailsRow | ListSetting | SwitchSetting;

export type SettingsBase = Record<string, SettingsRow>;

export type SettingsResult<Settings extends SettingsBase> = {
  [key in keyof Settings]: Settings[key]['type'] extends 'switch'
    ? boolean
    : Settings[key]['type'] extends 'list'
    ? string
    : never;
};

export type NativeOnSettings<Settings extends SettingsBase> =
  NativeSyntheticEvent<{ data: SettingsResult<Settings> }>;

export type NativeOnDetails<Settings extends SettingsBase> =
  NativeSyntheticEvent<{ data: keyof Settings }>;

type NativeSettingsViewProps<Settings extends SettingsBase> = {
  config: Settings;
  onChange: (event: NativeOnSettings<Settings>) => void;
  onDetails: (event: NativeOnDetails<Settings>) => void;
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
