import * as React from 'react';

import { StyleSheet } from 'react-native';
import { SettingsView } from 'rn-native-settings-view';

export function App() {
  return <SettingsView config="TODO" style={Styles.settings} />;
}

const Styles = StyleSheet.create({
  settings: { flex: 1 },
});
