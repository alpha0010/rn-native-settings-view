import React from 'react';
import { StyleSheet, View } from 'react-native';
import { NativeSettingsView, SettingsViewProps } from './NativeSettingsView';

export function SettingsView({ config, style }: SettingsViewProps) {
  return (
    <View style={style}>
      <View style={Styles.container}>
        <NativeSettingsView config={config} style={Styles.container} />
      </View>
    </View>
  );
}

const Styles = StyleSheet.create({
  container: { flex: 1 },
});
