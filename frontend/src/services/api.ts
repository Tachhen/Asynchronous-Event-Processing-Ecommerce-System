import axios from "axios";
import type { CreateOrderRequest, OrderResponse, OrderStatus } from "../types/order";

const api = axios.create({
  baseURL: "http://localhost:8080/api",
});

export const createOrder = (data: CreateOrderRequest) =>
  api.post<OrderResponse>("/orders", data);

export const getOrderStatus = (orderId: number) =>
  api.get<OrderStatus>(`/orders/status/${orderId}`);

export const completePayment = (orderId: number) =>
  api.post(`/payments/${orderId}/complete`);

export const failPayment = (orderId: number) =>
  api.post(`/payments/${orderId}/fail`);

export const duplicatePayment = (orderId: number) =>
  api.post(`/payments/${orderId}/duplicate`);

export default api;