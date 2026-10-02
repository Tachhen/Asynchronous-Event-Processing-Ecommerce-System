import type { Product } from "../types/product";

interface Props {
  products: Product[];
  quantities: Record<number, number>;
  onPlaceOrder: () => void;
}

export default function OrderSummary({
  products,
  quantities,
  onPlaceOrder,
}: Props) {
  const selected = products.filter(
    (p) => (quantities[p.id] || 0) > 0
  );

  const total = selected.reduce(
    (sum, p) => sum + p.price * quantities[p.id],
    0
  );

  return (
    <div className="order-summary">
      <div className="summary-title">Thank You For Your  MONEY</div>

      {selected.length === 0 ? (
        <p className="empty">No items selected</p>
      ) : (
        selected.map((p) => (
          <div className="summary-row" key={p.id}>
            <span>
              {p.name} × {quantities[p.id]}
            </span>
            <span>₹{(p.price * quantities[p.id]).toLocaleString()}</span>
          </div>
        ))
      )}

      <div className="summary-total">
        <span>TOTAL</span>
        <span>₹{total.toLocaleString()}</span>
      </div>

      <button
        className="main-button"
        disabled={selected.length === 0}
        onClick={onPlaceOrder}
      >
         PLACE ORDER
      </button>
    </div>
  );
}