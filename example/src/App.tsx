import * as React from 'react';

import { StyleSheet } from 'react-native';
import { SettingsView } from 'rn-native-settings-view';

export function App() {
  const [settings, setSettings] = React.useState({
    'switch': {
      value: true,
      title: 'Switch from js',
      type: 'switch' as const,
      weight: 0,
    },
    'is-false': {
      value: false,
      title: 'False switch',
      type: 'switch' as const,
      weight: 1,
    },
    'options': {
      value: 'b',
      title: 'Current letter',
      type: 'list' as const,
      labels: ['The letter A', 'The letter B', 'The letter C'],
      values: ['a', 'b', 'c'],
      weight: 3,
    },
    'account': {
      title: 'Account',
      type: 'details' as const,
      details: 'user@example.com',
      weight: 4,
    },
  });

  const onSettingsChange = React.useCallback(
    (e: { 'switch': boolean; 'is-false': boolean; 'options': string }) => {
      console.log('onChange', e);
      setSettings((prev) => {
        return {
          'switch': { ...prev.switch, value: e.switch },
          'is-false': { ...prev['is-false'], value: e['is-false'] },
          'options': { ...prev.options, value: e.options },
          'account': prev.account,
        };
      });
    },
    [setSettings]
  );

  return (
    <SettingsView
      config={settings}
      onChange={onSettingsChange}
      onDetails={console.log}
      style={Styles.settings}
    />
  );
}

const Styles = StyleSheet.create({
  settings: { flex: 1 },
});
