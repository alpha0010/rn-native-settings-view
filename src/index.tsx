import React, { useCallback, useEffect, useRef, useState } from 'react';
import { StyleSheet, View, ViewStyle } from 'react-native';
import { dispatchEvent, Payload, subscribeEvents } from './event';
import {
  NativeOnDetails,
  NativeOnSettings,
  NativeSettingsView,
  SettingsBase,
  SettingsResult,
} from './NativeSettingsView';

export type { SettingsResult } from './NativeSettingsView';

export type SettingsViewProps<Settings extends SettingsBase> = {
  config: Settings;
  onChange?: (event: SettingsResult<Settings>) => void;
  onDetails?: (event: keyof Settings) => void;
  onSubViewRequest?: (
    key: keyof Settings,
    config: SettingsSubViewProps
  ) => void;
  style: ViewStyle;
};

export type SettingsSubViewProps = { id: number; settings: SettingsBase };

/**
 * Merge updates into the settings config.
 *
 * Creates a new object, unless updates result in no change.
 */
export function mergeChanges<Settings extends SettingsBase>(
  prev: Settings,
  updates: Payload
) {
  let next = prev;
  for (const [key, value] of Object.entries(updates)) {
    if (!(key in next)) {
      continue;
    }
    const element = next[key];
    if (element.type === 'list') {
      if (typeof value === 'string' && element.value !== value) {
        next = { ...next, [key]: { ...element, value } };
      }
    } else if (element.type === 'switch') {
      if (typeof value === 'boolean' && element.value !== value) {
        next = { ...next, [key]: { ...element, value } };
      }
    }
  }
  return next;
}

function useRequestsSubView<Settings extends SettingsBase>({
  config,
  onChange,
  onSubViewRequest,
}: SettingsViewProps<Settings>) {
  const [subView, setSubView] = useState<SettingsBase | null>(null);

  const curValuesRef = useRef<SettingsResult<Settings>>({});
  const onChangeRef = useRef(onChange);
  const onSubViewRequestRef = useRef(onSubViewRequest);

  onChangeRef.current = onChange;
  onSubViewRequestRef.current = onSubViewRequest;

  useEffect(() => {
    curValuesRef.current = {};
    for (const [key, value] of Object.entries(config)) {
      if (value.type !== 'details') {
        curValuesRef.current = { ...curValuesRef.current, [key]: value.value };
      }
    }
  }, [config, curValuesRef]);

  useEffect(() => {
    // Changing subView should be the only effect trigger, so pass
    // everything else by ref.
    if (subView == null) {
      return;
    }
    const { id, unsubscribe } = subscribeEvents((e) => {
      onChangeRef.current?.({ ...curValuesRef.current, ...e });
    });
    onSubViewRequestRef.current?.(Object.keys(subView)[0], {
      id,
      settings: subView,
    });
    return unsubscribe;
  }, [curValuesRef, onChangeRef, onSubViewRequestRef, subView]);

  return setSubView;
}

export function SettingsView<Settings extends SettingsBase>(
  props: SettingsViewProps<Settings>
) {
  const { config, onChange, onDetails, style } = props;

  const setSubView = useRequestsSubView(props);

  const nativeOnChange = useCallback(
    (e: NativeOnSettings<Settings>) => {
      if (onChange != null) {
        onChange(e.nativeEvent.data);
      }
    },
    [onChange]
  );

  const nativeOnDetails = useCallback(
    (e: NativeOnDetails<Settings>) => {
      const key = e.nativeEvent.data;
      const element = config[key];
      if (element.type === 'list') {
        setSubView({ [key]: element });
      } else if (onDetails != null) {
        onDetails(key);
      }
    },
    [config, onDetails, setSubView]
  );

  return (
    <View style={style}>
      <View style={Styles.container}>
        <NativeSettingsView
          config={config}
          // @ts-ignore - Not sure how properly type this.
          onChange={nativeOnChange}
          onDetails={nativeOnDetails}
          style={Styles.container}
        />
      </View>
    </View>
  );
}

export function SettingsSubView(props: SettingsSubViewProps) {
  const [settings, setSettings] = useState(props.settings);

  const onChange = useCallback(
    (e: Payload) => {
      setSettings((prev) => mergeChanges(prev, e));
      dispatchEvent(e, props.id);
    },
    [props.id, setSettings]
  );

  return (
    <SettingsView
      config={settings}
      onChange={onChange}
      style={Styles.container}
    />
  );
}

const Styles = StyleSheet.create({
  container: { flex: 1 },
});
