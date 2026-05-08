export interface LogEntry {
  id?: number;
  logNumber?: string;
  lengthFt: number;
  girthIn: number;
  girthFt: number;
  volumeCft: number;
  pricePerCft: number;
  lineTotal: number;
  woodType?: string;
  rmId?: number;
  isDuplicate?: boolean;
}

export interface WoodType {
  rmId: number;
  rmName: string;
  pricePerCft: number;
}
