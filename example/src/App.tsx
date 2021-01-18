import * as React from 'react';

import { StyleSheet, View, Text, Button, NativeEventEmitter } from 'react-native';
import YenepaySdkReactNative from 'yenepay.sdk.react-native';
import type {YenePayOrder} from 'yenepay.sdk.react-native'
import type { YenepayOrderedItem } from 'yenepay.sdk.react-native';
class App extends React.Component {
  state = {result: null};
  paymentResponseListener: any;
  paymentErrorListener: any;
  componentDidMount() {
    const eventEmitter = new NativeEventEmitter(YenepaySdkReactNative);
    const { PAYMENT_RESPONSE_EVENT_NAME , PAYMENT_ERROR_EVENT_NAME } = YenepaySdkReactNative.getConstants();
    this.paymentResponseListener = eventEmitter.addListener(PAYMENT_RESPONSE_EVENT_NAME, (event: any) => {
      this.setState({result: JSON.stringify(event, null, 2)}) 
      console.log(event)
    });
    this.paymentErrorListener = eventEmitter.addListener(PAYMENT_ERROR_EVENT_NAME, (event: any) => {
      this.setState({result: JSON.stringify(event, null, 2)}) 
      console.log(event)
    });
  }
 
  componentWillUnmount() {
    this.paymentResponseListener.remove(); //Removes the listener
    this.paymentErrorListener.remove();
  }
  getOrder = () => {
    let item: YenepayOrderedItem = {
      itemId: "0001",
      itemName: "ReactNative Test Item",
      unitPrice: 1.0,
      quantity: 2
    };
    let order:YenePayOrder = {
      merchantCode: "0008",
      merchantOrderId: "79879987987",
      ipnUrl: "",
      returnUrl: "com.yenepay.reactnativeexample:/payment2return",
      items: [item]
    };
    return order;
  }

  render() {    

    // React.useEffect(() => {
    //   YenepaySdkReactNative.multiply(3, 7).then(setResult);
    // }, []);

    return (
      <View style={styles.container}>
        <Button
          onPress={() => {
            let order = this.getOrder();
            console.log(order);          
            YenepaySdkReactNative.requestPayment(order)
            // .then(response => {
            //   this.setState({result: JSON.stringify(response, null, 2)})
            // })
            .catch(err => this.setState({result: err.message}));
          }}
          title="Pay Now"
        />
        <Text>Result: {this.state.result}</Text>
      </View>
    );
  }
}
const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});

export default App

