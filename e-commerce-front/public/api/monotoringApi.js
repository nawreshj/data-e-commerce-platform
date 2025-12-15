import { request } from "./http.js";
import { BACK } from "../constants/back.js";

export const monitoringApi = {
  health: () => request(BACK.health),
};
