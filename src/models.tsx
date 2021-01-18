
type YenepayOrderedItem = {
  itemId: string;
  itemName: string;
  quantity: number;
  unitPrice: number;
};
type YenepayOrder = {
  merchantCode: string;
  merchantOrderId: string;
  ipnUrl: string;
  returnUrl: string;
  tax1: number;
  tax2: number;
  shippingFee: number;
  handlingFee: number;
  discount: number;
  isUseSandboxEnabled: boolean;
  items: YenepayOrderedItem[];
};

export { YenepayOrder, YenepayOrderedItem};
