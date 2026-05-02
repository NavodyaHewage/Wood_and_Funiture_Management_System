export enum UnitOfMeasurement {
    SQUARE_FEET = 'SQUARE_FEET',
    LENGTH_FEET = 'LENGTH_FEET'
}

export interface ProductCategory {
    productCatId?: number;
    description: string;
    materialCategory: string;
    unitOfMeasurement: UnitOfMeasurement;
    unitPrice: number;
}
