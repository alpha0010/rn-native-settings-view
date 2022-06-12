import * as React from 'react';

import { StyleSheet } from 'react-native';
import { SettingsView } from 'rn-native-settings-view';

function onSettingsChange(event: { switch: boolean }) {
  console.log(event);
}

export function App() {
  return (
    <SettingsView
      config="TODO"
      onChange={onSettingsChange}
      style={Styles.settings}
    />
  );
}

const Styles = StyleSheet.create({
  settings: { flex: 1 },
});
