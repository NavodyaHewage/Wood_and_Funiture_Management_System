export interface EmployeeLoanDTO {
  loanId?: number;
  employeeId: number;
  loanAmount: number;
  issuedDate: string; // LocalDate as ISO string
  reason: string;
  totalDeducted: number;
  balance: number; // Read-only from backend
  status: string; // Enum as string
  createdById?: number;
  remarks: string;
}

export interface LoanDeductionRuleDTO {
  ruleId?: number;
  loanId: number;
  deductionAmount: number;
  startMonth: number;
  startYear: number;
  endMonth?: number;
  endYear?: number;
  isActive: boolean;
  createdById?: number;
  remarks: string;
}

export enum LoanStatus {
  ACTIVE = 'ACTIVE',
  PARTIALLY_PAID = 'PARTIALLY_PAID',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}
