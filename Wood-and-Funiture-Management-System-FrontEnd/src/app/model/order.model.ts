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
    quotationNumber: string;
    paidAmount: number;
    orderDate: string;
    status: string;
    createdById: number | null;
    orderDetails: OrderDetailDTO[];
}

export interface CustomerOrderResponseDTO {
    orderId: number;
    orderNumber: string;
    customerId: number;
    customerName: string;
    quotationNumber: string;
    totalAmount: number;
    paidAmount: number;
    balanceAmount: number;
    status: string;
    orderDate: string;
    orderDetails: OrderDetailDTO[];
}
