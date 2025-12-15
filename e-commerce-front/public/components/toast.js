import { setText } from "./dom.js";

let timer = null;

export function showToast(message) {
  setText("toast", message);
  const el = document.getElementById("toast");
  el.classList.add("toast--show");

  if (timer) clearTimeout(timer);
  timer = setTimeout(() => {
    el.classList.remove("toast--show");
    setText("toast", "");
  }, 2500);
}
