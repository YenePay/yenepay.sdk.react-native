# yenepay.sdk.react-native

YenePay sdk for react native. Currently Android implementation only. IOS implemenation will be added soon.

## Installation

```sh
npm install yenepay.sdk.react-native
```
## Setup
### Android
Set manifest return uri scheme for your application in your build.gradle file
```groove
android {
    ...
    defaultConfig {
        ...
        manifestPlaceholders = [
          yenepayReturnScheme: "com.yenepay.reactnativeexample"
        ]
    }
}
```
### IOS
coming soon, not implemented yet

## Usage
### Requesting Payment
```js
import YenepaySdkReactNative from "yenepay.sdk.react-native";
// ...
let item = {
      itemId: "0001",
      itemName: "ReactNative Test Item",
      unitPrice: 1.0,
      quantity: 2
    };
    let order = {
      merchantCode: "Your YenePay Merchant Code",
      merchantOrderId: "79879987987",
      ipnUrl: "",
      returnUrl: "com.yenepay.reactnativeexample:/payment2return",
      items: [item]
    };
YenepaySdkReactNative.requestPayment(order)
            .catch(err => "Payment failed log here...");
```
### Handling Payment Events
```js
class App extends Component {
    ...
  paymentResponseListener: any;
  paymentErrorListener: any;
  componentDidMount() {
    const eventEmitter = new NativeEventEmitter(YenepaySdkReactNative);
    const { PAYMENT_RESPONSE_EVENT_NAME , PAYMENT_ERROR_EVENT_NAME } = YenepaySdkReactNative.getConstants();
    this.paymentResponseListener = eventEmitter.addListener(PAYMENT_RESPONSE_EVENT_NAME, (event: any) => {
      //event is a payment response json here.
      //Check status of the payment here and update your server
    });
    this.paymentErrorListener = eventEmitter.addListener(PAYMENT_ERROR_EVENT_NAME, (event: any) => {
        // event.message will have the error message
    });
  }
 
  componentWillUnmount() {
    this.paymentResponseListener.remove(); //Removes the listener
    this.paymentErrorListener.remove();
  }
  ...
}
```
## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
