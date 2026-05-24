export interface ExpenseAccountDTO {
  expenseId?: number;
  date: string; // ISO string format
  amount: number;
  description: string;
  paidTo: string;
  remarks: string;
  expenseTypeId: number;
  grnId?: number; // Optional
  userId: number;
}

export interface ExpenseTypeDTO {
  expenseTypeId?: number;
  typeName: string;
  description: string;
}
