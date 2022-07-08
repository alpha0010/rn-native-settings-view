# rn-native-settings-view

Native component based settings view for React Native. "It looks native
because it is native."

| Android | iOS |
|:-------:|:---:|
| <img src="https://user-images.githubusercontent.com/4990928/178061061-310c88ac-3b25-42b7-b1ca-189431602d82.png" width="50%" height="50%"> | <img src="https://user-images.githubusercontent.com/4990928/178061553-f69e521f-95f5-4b4b-8003-736916c99844.png" width="60%" height="60%"> |

## Installation

```sh
npm install rn-native-settings-view
cd ios && pod install
```

## Usage

`SettingsView` is a controlled component, so results from `onChange` must be
applied to the config.

Icons (optional) are from icon fonts. The example below retrieves fonts from
`react-native-vector-icons`, but there is no dependency on said library.
Adding an icon font is sufficient for usage.

Some settings may require multiple screens to use. When this occurs,
`SettingsView` will call `onSubViewRequest` with an opaque object. The app
should render `<SettingsSubView {...subViewOpaqueObject} />` on a separate
screen. (There is no dependency on a specific navigation library; use
whichever library best suits the app.)

```ts
import {
  mergeChanges,
  SettingsResult,
  SettingsSubView,
  SettingsView
} from 'rn-native-settings-view';

// ...

const [settings, setSettings] = useState({
  // Generic pressable row.
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

  // Radio option selector.
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

  // Boolean toggle.
  'notifications': {
    value: true,
    title: 'Notifications',
    type: 'switch' as const,
    weight: 2,
  },
});

const onChange = useCallback(
  (e: SettingsResult<typeof settings>) => {
    // saveSettingsToStorage(e);
    setSettings((prev) => mergeChanges(prev, e));
  },
  [setSettings]
);

const onDetails = useCallback(
  (page: keyof typeof settings) => {
    console.log('User pressed: ' + page);
  },
  []
);

const onSubViewRequest = useCallback(
  (page: keyof typeof settings, subView: SettingsSubViewProps) =>
    // Replace with preferred navigation library.
    // Pushed screen should render <SettingsSubView {...subView} />.
    navigation.push('Settings', {
      subView,
      title: settings[page].title,
    }),
  [navigation, settings]
);

<SettingsView
  config={settings}
  onChange={onChange}
  onDetails={onDetails}
  onSubViewRequest={onSubViewRequest}
  style={Styles.settings}
/>
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to
the repository and the development workflow.

## License

MIT
