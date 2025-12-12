import { request } from "./http.js";
import { BACK } from "../constants/back.js";

export const membershipApi = {
  list: () => request(BACK.users),
  create: (payload) => request(BACK.users, { method: "POST", body: payload }),
};
