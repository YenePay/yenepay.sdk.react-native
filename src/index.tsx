import { NativeModules } from 'react-native';
import type { YenepayOrder } from './models'


type YenepaySdkReactNativeType = {
  multiply(a: number, b: number): Promise<number>;
  requestPayment(paymentOrder: YenepayOrder): Promise<number>;
  getConstants(): any
};

const { YenepaySdkReactNative } = NativeModules;

export default YenepaySdkReactNative as YenepaySdkReactNativeType;
