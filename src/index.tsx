import React, { useCallback } from 'react';
import { StyleSheet, View, ViewStyle } from 'react-native';
import { NativeOnSettings, NativeSettingsView } from './NativeSettingsView';

export type SettingsViewProps<T extends string> = {
  config: string;
  onChange: (event: Record<T, boolean>) => void;
  style: ViewStyle;
};

export function SettingsView<T extends string>({
  config,
  onChange,
  style,
}: SettingsViewProps<T>) {
  const nativeOnChange = useCallback(
    ({ nativeEvent }: NativeOnSettings) => onChange(nativeEvent),
    [onChange]
  );

  return (
    <View style={style}>
      <View style={Styles.container}>
        <NativeSettingsView
          config={config}
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
