import { BrowserRouter, Routes, Route } from "react-router-dom";
import CreateOrder from "./pages/CreateOrder";
import Payment from "./pages/Payment";
import OrderStatus from "./pages/OrderStatus";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<CreateOrder />} />
        <Route path="/payment/:orderId" element={<Payment />} />
        <Route path="/order/:orderId" element={<OrderStatus />} />
      </Routes>
    </BrowserRouter>
  );
}