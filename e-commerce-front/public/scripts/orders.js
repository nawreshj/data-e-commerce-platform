import { renderNavbar } from "../components/navbar.js";
import { setHTML, setText, $ } from "../components/dom.js";
import { ORDER_STATUSES } from "../constants/back.js";
import { showToast } from "../components/toast.js";
import { orderApi } from "../api/orderApi.js";

setHTML("navbar", renderNavbar("orders"));

function setError(msg) {
  setText("error", msg || "");
}

function renderItems(order) {
  if (!order.items || !order.items.length) return "";
  return order.items
    .map((it) => `${it.productName ?? it.productId} x ${it.quantity} (${Number(it.subtotal).toFixed(2)})`)
    .join("<br/>");
}

async function loadOrders() {
  const orders = await orderApi.list();
  $("table").innerHTML =
    `<tr>
      <th>ID</th><th>UserId</th><th>Status</th><th>Total</th><th>Address</th><th>Items</th><th>Actions</th>
    </tr>` +
    orders
      .map((o) => {
        const immutable = o.status === "DELIVERED" || o.status === "CANCELLED";
        return `
          <tr>
            <td>${o.id}</td>
            <td>${o.userId}</td>
            <td><span class="badge">${o.status}</span></td>
            <td>${Number(o.totalAmount).toFixed(2)}</td>
            <td>${o.shippingAddress}</td>
            <td>${renderItems(o)}</td>
            <<td class="actions">
              <select data-status="${o.id}" ${immutable ? "disabled" : ""}>
                  ${ORDER_STATUSES.map((st) => `<option ${st === o.status ? "selected" : ""}>${st}</option>`).join("")}
              </select>
               <button data-update="${o.id}" ${immutable ? "disabled" : ""}>Update</button>
              <button data-cancel="${o.id}" ${immutable ? "disabled" : ""}>Cancel</button>
            </td>

          </tr>
        `;
      })
      .join("");
}

// mutiple items
function addItemRow({ productId = "", qty = 1 } = {}) {
  const row = document.createElement("div");
  row.className = "item-row";
  row.innerHTML = `
    <input class="o_product" type="number" min="1" placeholder="productId" value="${productId}" required />
    <input class="o_qty" type="number" min="1" placeholder="qty" value="${qty}" required />
    <button type="button" class="removeItemBtn">Remove</button>
  `;
  $("itemsContainer").appendChild(row);
}

function getItemsFromForm() {
  const rows = Array.from(document.querySelectorAll("#itemsContainer .item-row"));
  const items = rows.map((row) => {
    const pid = Number(row.querySelector(".o_product")?.value);
    const q = Number(row.querySelector(".o_qty")?.value);
    return { productId: pid, quantity: q };
  });

  // at least one product for order
  if (!items.length) throw new Error("At least one item is required");
  for (const it of items) {
    if (!Number.isFinite(it.productId) || it.productId <= 0) throw new Error("Invalid productId");
    if (!Number.isFinite(it.quantity) || it.quantity <= 0) throw new Error("Invalid quantity");
  }
  return items;
}

// Add item
$("addItemBtn").addEventListener("click", () => addItemRow());

// Remove item 
$("itemsContainer").addEventListener("click", (e) => {
  const btn = e.target.closest(".removeItemBtn");
  if (!btn) return;
  const row = btn.closest(".item-row");
  row?.remove();
});

// submit
$("orderForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    setError("");

    const userId = Number($("o_user").value);
    const shippingAddress = $("o_address").value?.trim();
    const items = getItemsFromForm();

    if (!Number.isFinite(userId) || userId <= 0) throw new Error("Invalid userId");
    if (!shippingAddress) throw new Error("Shipping address is required");

    const payload = { userId, shippingAddress, items };

    await orderApi.create(payload);

    showToast("Order created");
    e.target.reset();

    
    $("itemsContainer").innerHTML = "";
    addItemRow();

    await loadOrders();
  } catch (err) {
    setError(err.message || "Create order failed");
  }
});

// Cancel + Update status
$("table").addEventListener("click", async (e) => {
  const cancelBtn = e.target.closest("button[data-cancel]");
  const updateBtn = e.target.closest("button[data-update]");

  try {
    if (cancelBtn) {
      const id = cancelBtn.getAttribute("data-cancel");
      await orderApi.cancel(id);
      showToast("Order cancelled");
      await loadOrders();
    }

    if (updateBtn) {
      const id = updateBtn.getAttribute("data-update");
      const sel = document.querySelector(`select[data-status="${id}"]`);
      const newStatus = sel?.value;
      if (!newStatus) throw new Error("Select a status");
      await orderApi.updateStatus(id, newStatus);
      showToast("Status updated");
      await loadOrders();
    }
  } catch (err) {
    setError(err.message || "Action failed");
  }
});



Promise.resolve()
  .then(async () => {
    addItemRow();     // 1 produit basique
    await loadOrders();
  })
  .catch((e) => setError(e.message));
