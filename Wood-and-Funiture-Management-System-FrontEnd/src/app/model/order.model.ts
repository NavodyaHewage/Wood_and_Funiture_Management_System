export interface OrderDetailDTO {
    productCatId: number;
    productCatName?: string;
    name: string;
    quantity: number;
    price: number;
    lineTotal?: number;
}

export interface CustomerOrderRequestDTO {
    customerId: number;
    receiptNumber: string;
    paidAmount: number;
    orderDate: string;
    status: string;
    createdById: number | null;
    orderDetails: OrderDetailDTO[];
}

export interface CustomerOrderResponseDTO {
    orderId: number;
    customerId: number;
    customerName: string;
    receiptNumber: string;
    totalAmount: number;
    paidAmount: number;
    balanceAmount: number;
    status: string;
    orderDate: string;
    orderDetails: OrderDetailDTO[];
}
