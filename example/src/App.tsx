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
  Account: { account: string };
  Settings: { subView?: SettingsSubViewProps; title: string } | undefined;
};
type AccountProps = NativeStackScreenProps<RootStackParamList, 'Account'>;
type SettingsProps = NativeStackScreenProps<RootStackParamList, 'Settings'>;

const Stack = createNativeStackNavigator<RootStackParamList>();

const defaultSettings = {
  'account': {
    title: 'Account',
    type: 'details' as const,
    details: 'user@example.com',
    icon: {
      font: Icon.getFontFamily(),
      char: Icon.getRawGlyphMap().person,
    },
    weight: 0,
  },
  'search': {
    value: 'google',
    title: 'Search Engine',
    type: 'list' as const,
    labels: ['Google', 'Yahoo', 'Bing', 'DuckDuckGo'],
    values: ['google', 'yahoo', 'bing', 'duckduckgo'],
    icon: {
      fg: 'white',
      bg: '#43b0ef',
      font: Icon.getFontFamily(),
      char: Icon.getRawGlyphMap().search,
    },
    weight: 1,
  },
  'notifications': {
    value: true,
    title: 'Notifications',
    type: 'switch' as const,
    weight: 2,
  },
  'targeted-ads': {
    value: false,
    title: 'Targeted Ads',
    type: 'switch' as const,
    weight: 3,
  },
};

type Settings = typeof defaultSettings;

function AccountScreen({ route }: AccountProps) {
  return <Text style={Styles.text}>{route.params.account}</Text>;
}

function SettingsScreen({ navigation, route }: SettingsProps) {
  const [settings, setSettings] = React.useState(defaultSettings);

  React.useLayoutEffect(() => {
    navigation.setOptions({ title: route.params?.title });
  }, [navigation, route]);

  React.useEffect(() => {
    const state = { live: true };
    Promise.all([
      Config.readString('search', defaultSettings.search.value),
      Config.readBool('notifications', defaultSettings.notifications.value),
      Config.readBool('targeted-ads', defaultSettings['targeted-ads'].value),
    ]).then(([search, notifications, tAds]) => {
      if (!state.live) {
        return;
      }
      setSettings((prev) =>
        mergeChanges(prev, {
          search,
          notifications,
          'targeted-ads': tAds,
        })
      );
    });
    return () => {
      state.live = false;
    };
  }, [setSettings]);

  const onSettingsChange = React.useCallback(
    (e: SettingsResult<Settings>) => {
      Config.writeString('search', e.search);
      Config.writeBool('notifications', e.notifications);
      Config.writeBool('targeted-ads', e['targeted-ads']);
      setSettings((prev) => mergeChanges(prev, e));
    },
    [setSettings]
  );

  const onDetails = React.useCallback(
    (page: keyof Settings) => {
      if (page === 'account') {
        navigation.push('Account', { account: settings.account.details });
      }
    },
    [navigation, settings.account.details]
  );

  const onSubViewRequest = React.useCallback(
    (page: keyof Settings, subView: SettingsSubViewProps) =>
      navigation.push('Settings', {
        subView,
        title: defaultSettings[page].title,
      }),
    [navigation]
  );

  return route.params?.subView != null ? (
    <SettingsSubView {...route.params.subView} />
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
      <Stack.Navigator initialRouteName="Settings">
        <Stack.Screen name="Account" component={AccountScreen} />
        <Stack.Screen name="Settings" component={SettingsScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
}

const Styles = StyleSheet.create({
  settings: { flex: 1 },
  text: { fontSize: 18, padding: 20 },
});
