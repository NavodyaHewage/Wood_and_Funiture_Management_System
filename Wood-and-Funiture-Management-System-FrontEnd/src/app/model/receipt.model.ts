export interface ReceiptDetailDTO {
    receiptDetailsId?: number;
    customerOrderDetailsId: number;
    productName?: string;
    amount: number;
}

export interface ReceiptRequestDTO {
    receiptNumber?: string;
    date: string;
    paymentMethod: string; // "CASH", "CARD", "CHEQUE", "BANK_TRANSFER"
    customerId: number;
    orderId: number;
    totalAmount: number;
    createdById: number | null;
    remarks: string;
    chequeNumber?: string;
    bankName?: string;
    cardType?: string; // "Credit", "Debit"
    cardLastDigits?: string;
    receiptDetails: ReceiptDetailDTO[];
}

export interface ReceiptResponseDTO {
    receiptId: number;
    receiptNumber: string;
    date: string;
    paymentMethod: string;
    customerId: number;
    customerName: string;
    orderId?: number;
    orderNumber?: string;
    totalAmount: number;
    createdById: number;
    remarks: string;
    chequeNumber?: string;
    bankName?: string;
    cardType?: string;
    cardLastDigits?: string;
    receiptDetails: ReceiptDetailDTO[];
}

export interface OutstandingLine {
    detailId: number;
    productCatId: number;
    name: string;
    quantity: number;
    price: number;
    lineTotal: number;
    paidAmount: number;
    outstanding: number;
}

export interface OutstandingOrderSummary {
    orderId: number;
    orderNumber: string;
    balanceAmount: number;
}

export interface OutstandingLinesResponseDTO {
    order: OutstandingOrderSummary;
    lines: OutstandingLine[];
}
