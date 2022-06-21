import { NavigationContainer, useFocusEffect } from '@react-navigation/native';
import {
  createNativeStackNavigator,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import * as React from 'react';
import { StyleSheet, Text } from 'react-native';
import { SettingsView } from 'rn-native-settings-view';

import { Config } from './Config';

type RootStackParamList = {
  Settings: { page: string } | undefined;
};

type Props = NativeStackScreenProps<RootStackParamList, 'Settings'>;

const Stack = createNativeStackNavigator<RootStackParamList>();

const defaultSettings = {
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
};

function capitalize(str?: string) {
  if (str != null && str.length > 0) {
    return str[0].toUpperCase() + str.slice(1);
  }
  return str;
}

function SettingsScreen({ navigation, route }: Props) {
  const [settings, setSettings] = React.useState(defaultSettings);

  const settingsOpt = React.useMemo(
    () => ({ options: settings.options }),
    [settings.options]
  );

  React.useLayoutEffect(() => {
    navigation.setOptions({ title: capitalize(route.params?.page) });
  }, [navigation, route]);

  useFocusEffect(
    React.useCallback(() => {
      const state = { live: true };
      Promise.all([
        Config.readBool('switch', defaultSettings.switch.value),
        Config.readBool('is-false', defaultSettings['is-false'].value),
        Config.readString('options', defaultSettings.options.value),
      ]).then(([swVal, ifVal, opVal]) => {
        if (!state.live) {
          return;
        }
        setSettings((prev) => {
          return {
            'switch': { ...prev.switch, value: swVal },
            'is-false': { ...prev['is-false'], value: ifVal },
            'options': { ...prev.options, value: opVal },
            'account': prev.account,
          };
        });
      });
      return () => {
        state.live = false;
      };
    }, [setSettings])
  );

  const onSettingsChange = React.useCallback(
    (e: { 'switch': boolean; 'is-false': boolean; 'options': string }) => {
      Config.writeBool('switch', e.switch);
      Config.writeBool('is-false', e['is-false']);
      Config.writeString('options', e.options);
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

  const onOptionsChange = React.useCallback(
    (e: { options: string }) => {
      Config.writeString('options', e.options);
      setSettings((prev) => {
        return {
          'switch': prev.switch,
          'is-false': prev['is-false'],
          'options': { ...prev.options, value: e.options },
          'account': prev.account,
        };
      });
    },
    [setSettings]
  );

  const onDetails = React.useCallback(
    (page: string) => navigation.push('Settings', { page }),
    [navigation]
  );

  return route.params?.page === 'options' ? (
    <SettingsView
      config={settingsOpt}
      onChange={onOptionsChange}
      onDetails={onDetails}
      style={Styles.settings}
    />
  ) : route.params?.page === 'account' ? (
    <Text>{settings.account.details}</Text>
  ) : (
    <SettingsView
      config={settings}
      onChange={onSettingsChange}
      onDetails={onDetails}
      style={Styles.settings}
    />
  );
}

export function App() {
  return (
    <NavigationContainer>
      <Stack.Navigator>
        <Stack.Screen name="Settings" component={SettingsScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const Styles = StyleSheet.create({
  settings: { flex: 1 },
});
