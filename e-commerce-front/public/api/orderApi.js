import { request } from "./http.js";
import { BACK } from "../constants/back.js";

export const orderApi = {
  list: () => request(BACK.orders),
  create: (payload) => request(BACK.orders, { method: "POST", body: payload }),
  updateStatus: (id, status) =>
    request(`${BACK.orders}/${id}/status`, { method: "PUT", body: { status } }),
  cancel: (id) => request(`${BACK.orders}/${id}`, { method: "DELETE" }),
};
