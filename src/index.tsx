import React, { useCallback } from 'react';
import { StyleSheet, View, ViewStyle } from 'react-native';
import {
  NativeOnSettings,
  NativeSettingsView,
  SettingsBase,
  SettingsResult,
} from './NativeSettingsView';

export type { SettingsResult } from './NativeSettingsView';

export type SettingsViewProps<Settings extends SettingsBase> = {
  config: Settings;
  onChange: (event: SettingsResult<Settings>) => void;
  style: ViewStyle;
};

export function SettingsView<Settings extends SettingsBase>({
  config,
  onChange,
  style,
}: SettingsViewProps<Settings>) {
  const nativeOnChange = useCallback(
    (e: NativeOnSettings<Settings>) => onChange(e.nativeEvent.data),
    [onChange]
  );

  return (
    <View style={style}>
      <View style={Styles.container}>
        <NativeSettingsView
          config={config}
          // @ts-ignore - Not sure how properly type this.
          onChange={nativeOnChange}
          style={Styles.container}
        />
      </View>
    </View>
  );
}

const Styles = StyleSheet.create({
  container: { flex: 1 },
});
