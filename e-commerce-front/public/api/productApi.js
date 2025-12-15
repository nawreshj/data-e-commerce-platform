import { request } from "./http.js";
import { BACK } from "../constants/back.js";

export const productApi = {
  list: () => request(BACK.products),
  create: (payload) => request(BACK.products, { method: "POST", body: payload }),
  update: (id, payload) => request(`${BACK.products}/${id}`, { method: "PUT", body: payload }),
  remove: (id) => request(`${BACK.products}/${id}`, { method: "DELETE" }),
};
