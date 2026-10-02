import { useState } from "react";
import { useNavigate } from "react-router-dom";
import ProductGrid from "../components/ProductGrid";
import OrderSummary from "../components/OrderSummary";
import { createOrder } from "../services/api";
import type { Product } from "../types/product";
import type { CreateOrderRequest } from "../types/order";

const products: Product[] = [
  { id: 1, name: "Mechanical Keyboard", price: 3999 },
  { id: 2, name: "Wireless Mouse", price: 1499 },
  { id: 3, name: "Monitor", price: 15999 },
  { id: 4, name: "Headphones", price: 7499 },
  { id: 5, name: "Mobile", price: 24999 },
  { id: 6, name: "Tablet", price: 29999 },
];

export default function CreateOrder() {
  const navigate = useNavigate();

  const [quantities, setQuantities] =
    useState<Record<number, number>>({});

  const addProduct = (id: number) => {
    setQuantities((old) => ({
      ...old,
      [id]: (old[id] || 0) + 1,
    }));
  };

  const removeProduct = (id: number) => {
    setQuantities((old) => {
      const copy = { ...old };

      if (!copy[id] || copy[id] === 1) {
        delete copy[id];
      } else {
        copy[id]--;
      }

      return copy;
    });
  };

  const placeOrder = async () => {
    const request: CreateOrderRequest = {
      items: Object.entries(quantities).map(
        ([productId, quantity]) => ({
          productId: Number(productId),
          quantity,
        })
      ),
    };

    try {
      const response = await createOrder(request);

      navigate(`/payment/${response.data.id}`);
    } catch (error) {
      console.error("Order creation failed:", error);
      alert("Could not create order.");
    }
  };

  return (
    <main className="store-page">
      <div className="hero">
        <p className="eyebrow"> EVENT-DRIVEN E-COMMERCE</p>

        <h1>INVENTORY</h1>

        <p>
          Create Your Order.
        </p>
      </div>

      <ProductGrid
        products={products}
        quantities={quantities}
        onAdd={addProduct}
        onRemove={removeProduct}
      />

      <OrderSummary
        products={products}
        quantities={quantities}
        onPlaceOrder={placeOrder}
      />
    </main>
  );
}