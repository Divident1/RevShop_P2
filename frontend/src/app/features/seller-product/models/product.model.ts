export interface Product {

    id?: number;
    name: string;
    description: string;
    price: number;
    mrp: number;
    discountPercentage?: number;
    category: string;
    quantity: number;
    sellerId: number;
    isActive?: boolean;
    stockThreshold: number;
}
