import { renderNavbar } from "../components/navbar.js";
import { setHTML, setText, $ } from "../components/dom.js";
import { showToast } from "../components/toast.js";
import { monitoringApi } from "../api/monitoringApi.js";

console.log("monitoring.js loaded");
setHTML("navbar", renderNavbar("monitoring"));

function statusDot(status) {
  const color = status === "UP" ? "#5cb85c" : status === "DOWN" ? "#d9534f" : "#999";
  return `<span style="display:inline-block;width:10px;height:10px;border-radius:999px;background:${color};margin-right:6px;"></span>`;
}


// health check (actuator) : vérifie l'état des microservices 
async function loadHealth() {
  const data = await monitoringApi.health();
  const html = Object.entries(data)
    .map(([k, v]) => `<div>${statusDot(v)} <strong>${k}</strong>: ${v}</div>`)
    .join("");
  document.getElementById("health").innerHTML = html;
}

document.getElementById("refresh").addEventListener("click", async () => {
  await loadHealth();
  showToast("Health refreshed");
});

loadHealth();
