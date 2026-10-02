import { useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import {
  completePayment,
  failPayment,
  duplicatePayment,
} from "../services/api";

export default function Payment() {
  const { orderId } = useParams();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("PAYMENT PENDING");

  const id = Number(orderId);

  const pay = async () => {
    try {
      setLoading(true);
      setMessage("PROCESSING PAYMENT...");

      await completePayment(id);

      setMessage("PAYMENT EVENT SENT ⚡");

      setTimeout(() => {
        navigate(`/order/${id}`);
      }, 800);
    } catch {
      setMessage("PAYMENT REQUEST FAILED");
    } finally {
      setLoading(false);
    }
  };

  const failure = async () => {
    try {
      setLoading(true);
      setMessage("SIMULATING FAILURE...");

      await failPayment(id);

      setMessage("FAILURE EVENT SENT → RETRY → DLT");
    } catch {
      setMessage("FAILURE SIMULATION SENT");
    } finally {
      setLoading(false);
    }
  };

  const duplicate = async () => {
    try {
      setLoading(true);
      setMessage("SENDING DUPLICATE EVENTS...");

      await duplicatePayment(id);

      setMessage("2 EVENTS SENT → IDEMPOTENCY TEST");
    } catch {
      setMessage("DUPLICATE TEST FAILED");
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="payment-page">
      <div className="payment-card">
        <p className="eyebrow">SECURE TRANSACTION</p>

        <h1>ORDER #{orderId}</h1>

        <div className="payment-status">
          <span className="status-dot" />
          {message}
        </div>

        <button
          className="main-button"
          onClick={pay}
          disabled={loading}
        >
          💳 PAY NOW
        </button>

        <div className="developer-panel">
          <p>DEVELOPER TESTING</p>

          <button onClick={failure}>
            💥 SIMULATE FAILURE
          </button>

          <button onClick={duplicate}>
            🔁 SEND DUPLICATE EVENT
          </button>
        </div>
      </div>
    </main>
  );
}
