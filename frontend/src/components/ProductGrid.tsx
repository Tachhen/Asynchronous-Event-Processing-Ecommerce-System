import ProductCard from "./ProductCard";
import type { Product } from "../types/product";

interface Props {
  products: Product[];
  quantities: Record<number, number>;
  onAdd: (id: number) => void;
  onRemove: (id: number) => void;
}

export default function ProductGrid({
  products,
  quantities,
  onAdd,
  onRemove,
}: Props) {
  return (
    <div className="product-grid">
      {products.map((product) => (
        <ProductCard
          key={product.id}
          product={product}
          quantity={quantities[product.id] || 0}
          onAdd={() => onAdd(product.id)}
          onRemove={() => onRemove(product.id)}
        />
      ))}
    </div>
  );
}