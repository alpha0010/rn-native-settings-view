import AsyncStorage from '@react-native-async-storage/async-storage';

export const Config = {
  readBool: async (key: string, defValue: boolean) => {
    const value = await AsyncStorage.getItem(key);
    return value === '1' ? true : value === '0' ? false : defValue;
  },

  readString: async (key: string, defValue: string) => {
    return (await AsyncStorage.getItem(key)) ?? defValue;
  },

  writeBool: async (key: string, value: boolean | undefined) => {
    if (value != null) {
      await AsyncStorage.setItem(key, value ? '1' : '0');
    }
  },

  writeString: async (key: string, value: string | undefined) => {
    if (value != null) {
      await AsyncStorage.setItem(key, value);
    }
  },
};
