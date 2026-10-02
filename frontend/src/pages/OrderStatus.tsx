import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { getOrderStatus } from "../services/api";

export default function OrderStatus() {
  const { orderId } = useParams();
  const [status, setStatus] = useState("CREATED");

  useEffect(() => {
    const id = Number(orderId);

    const checkStatus = async () => {
      try {
        const response = await getOrderStatus(id);
        setStatus(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    checkStatus();

    const interval = setInterval(checkStatus, 2000);

    return () => clearInterval(interval);
  }, [orderId]);

  const steps = [
    "CREATED",
    "PAYMENT_PENDING",
    "PAID",
    "SHIPPED",
  ];

  return (
    <main className="status-page">
      <div className="status-card">
        <p className="eyebrow">ORDER TRACKING</p>

        <h1>ORDER #{orderId}</h1>

        <div className="timeline">
          {steps.map((step) => {
            const active = steps.indexOf(step) <= steps.indexOf(status);

            return (
              <div
                key={step}
                className={`timeline-step ${
                  active ? "active" : ""
                }`}
              >
                <div className="timeline-dot">
                  {active ? "✓" : "○"}
                </div>

                <span>{step.replaceAll("_", " ")}</span>
              </div>
            );
          })}
        </div>

        <div className="current-status">
          CURRENT STATUS: {status}
        </div>
      </div>
    </main>
  );
}