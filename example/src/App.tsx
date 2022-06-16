import * as React from 'react';

import { StyleSheet } from 'react-native';
import { SettingsView } from 'rn-native-settings-view';

function onSettingsChange(event: { switch: boolean }) {
  console.log(event);
}

const settings = {
  'switch': {
    initialValue: true,
    title: 'Switch from js',
    type: 'switch',
    weight: 0,
  },
  'is-false': {
    initialValue: false,
    title: 'False switch',
    type: 'switch',
    weight: 1,
  },
  'options': {
    initialValue: 'b',
    title: 'Current letter',
    type: 'list',
    labels: ['The letter A', 'The letter B', 'The letter C'],
    values: ['a', 'b', 'c'],
    weight: 3,
  },
} as const;

export function App() {
  return (
    <SettingsView
      config={settings}
      onChange={onSettingsChange}
      style={Styles.settings}
    />
  );
}

const Styles = StyleSheet.create({
  settings: { flex: 1 },
});
