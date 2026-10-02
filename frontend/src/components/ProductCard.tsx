import type { Product } from "../types/product";

import keyboardImg from "../assets/funnykeyboards_1.jpg";
import mouseImg from "../assets/mousse.jpg";
import monitorImg from "../assets/monitor.webp";
import headphoneImg from "../assets/headphone.jpg";
import mobileImg from "../assets/mobile.jpg";
import tabletImg from "../assets/tablet.jpg";

interface Props {
  product: Product;
  quantity: number;
  onAdd: () => void;
  onRemove: () => void;
}

export default function ProductCard({
  product,
  quantity,
  onAdd,
  onRemove,
}: Props) {
  return (
    <div className="product-card">

      <div className="product-icon">
        {product.name === "Mechanical Keyboard" && (
          <img src={keyboardImg} alt={product.name} />
        )}

        {product.name === "Wireless Mouse" && (
          <img src={mouseImg} alt={product.name} />
        )}

        {product.name === "Monitor" && (
          <img src={monitorImg} alt={product.name} />
        )}

        {product.name === "Headphones" && (
          <img src={headphoneImg} alt={product.name} />
        )}

        {product.name === "Mobile" && (
          <img src={mobileImg} alt={product.name} />
        )}

        {product.name === "Tablet" && (
          <img src={tabletImg} alt={product.name} />
        )}
      </div>

      <h3>{product.name}</h3>

      <p className="price">
        ₹{product.price.toLocaleString()}
      </p>

      <div className="quantity">
        <button onClick={onRemove}>−</button>
        <span>{quantity}</span>
        <button onClick={onAdd}>+</button>
      </div>

    </div>
  );
}