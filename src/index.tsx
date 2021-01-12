import { NativeModules } from 'react-native';

type YenepaySdkReactNativeType = {
  multiply(a: number, b: number): Promise<number>;
};

const { YenepaySdkReactNative } = NativeModules;

export default YenepaySdkReactNative as YenepaySdkReactNativeType;
