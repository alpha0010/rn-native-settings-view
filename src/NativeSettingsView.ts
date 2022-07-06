import {
  Appearance,
  ColorValue,
  NativeSyntheticEvent,
  Platform,
  processColor,
  ProcessedColorValue,
  requireNativeComponent,
  UIManager,
  ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'rn-native-settings-view' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

type FontIcon = {
  font: string;
  char: number;
  fg?: number | ColorValue;
  bg?: number | ColorValue;

  /** For internal use. */
  fgP?: ProcessedColorValue | null;
  /** For internal use. */
  bgP?: ProcessedColorValue | null;
};

type DetailsRow = {
  title: string;
  type: 'details';
  details: string;
  icon?: FontIcon;
  weight: number;
};

type ListSetting = {
  value: string;
  title: string;
  type: 'list';
  labels: ReadonlyArray<string>;
  values: ReadonlyArray<string>;
  icon?: FontIcon;
  weight: number;
};

type SwitchSetting = {
  value: boolean;
  title: string;
  type: 'switch';
  icon?: FontIcon;
  weight: number;
};

type SettingsRow = DetailsRow | ListSetting | SwitchSetting;

export type SettingsBase = Record<string, SettingsRow>;

type SettingsResultStrict<Settings extends SettingsBase> = {
  [key in keyof Settings]: Settings[key]['type'] extends 'switch'
    ? boolean
    : Settings[key]['type'] extends 'list'
    ? string
    : never;
};

export type SettingsResult<Settings extends SettingsBase> = Partial<
  SettingsResultStrict<Settings>
>;

export type NativeOnSettings<Settings extends SettingsBase> =
  NativeSyntheticEvent<{ data: SettingsResultStrict<Settings> }>;

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

// Note: This does not handle runtime theme changes.
export const defaultColors = {
  fg: processColor(
    Platform.OS === 'android'
      ? Appearance.getColorScheme() === 'dark'
        ? '#c6c5d0'
        : '#737373'
      : 'white'
  ),
  bg: processColor(Platform.OS === 'android' ? 'transparent' : '#8e8e93'),
};
