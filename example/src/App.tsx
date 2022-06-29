import { NavigationContainer } from '@react-navigation/native';
import {
  createNativeStackNavigator,
  NativeStackScreenProps,
} from '@react-navigation/native-stack';
import * as React from 'react';
import { StyleSheet, Text } from 'react-native';
import {
  mergeChanges,
  SettingsResult,
  SettingsSubView,
  SettingsSubViewProps,
  SettingsView,
} from 'rn-native-settings-view';
import Icon from 'react-native-vector-icons/MaterialIcons';

import { Config } from './Config';

type RootStackParamList = {
  Settings:
    | { page: keyof Settings; subView?: SettingsSubViewProps; title: string }
    | undefined;
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
    icon: {
      font: Icon.getFontFamily(),
      char: Icon.getRawGlyphMap().person,
    },
    weight: 4,
  },
};

type Settings = typeof defaultSettings;

function SettingsScreen({ navigation, route }: Props) {
  const [settings, setSettings] = React.useState(defaultSettings);

  React.useLayoutEffect(() => {
    navigation.setOptions({ title: route.params?.title });
  }, [navigation, route]);

  React.useEffect(() => {
    const state = { live: true };
    Promise.all([
      Config.readBool('switch', defaultSettings.switch.value),
      Config.readBool('is-false', defaultSettings['is-false'].value),
      Config.readString('options', defaultSettings.options.value),
    ]).then(([swVal, ifVal, opVal]) => {
      if (!state.live) {
        return;
      }
      setSettings((prev) =>
        mergeChanges(prev, {
          'switch': swVal,
          'is-false': ifVal,
          'options': opVal,
        })
      );
    });
    return () => {
      state.live = false;
    };
  }, [setSettings]);

  const onSettingsChange = React.useCallback(
    (e: SettingsResult<Settings>) => {
      Config.writeBool('switch', e.switch);
      Config.writeBool('is-false', e['is-false']);
      Config.writeString('options', e.options);
      setSettings((prev) => mergeChanges(prev, e));
    },
    [setSettings]
  );

  const onDetails = React.useCallback(
    (page: keyof Settings) =>
      navigation.push('Settings', { page, title: defaultSettings[page].title }),
    [navigation]
  );

  const onSubViewRequest = React.useCallback(
    (page: keyof Settings, subView: SettingsSubViewProps) =>
      navigation.push('Settings', {
        page,
        subView,
        title: defaultSettings[page].title,
      }),
    [navigation]
  );

  return route.params?.subView != null ? (
    <SettingsSubView {...route.params.subView} />
  ) : route.params?.page === 'account' ? (
    <Text style={Styles.text}>{settings.account.details}</Text>
  ) : (
    <SettingsView
      config={settings}
      onChange={onSettingsChange}
      onDetails={onDetails}
      onSubViewRequest={onSubViewRequest}
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
  text: { fontSize: 18, padding: 20 },
});
