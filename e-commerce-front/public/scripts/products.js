import { renderNavbar } from "../components/navbar.js";
import { setHTML, setText, $ } from "../components/dom.js";
import { showToast } from "../components/toast.js";
import { PRODUCT_CATEGORIES } from "../constants/back.js";
import { productApi } from "../api/productApi.js";

setHTML("navbar", renderNavbar("products"));

function setError(msg) {
  setText("error", msg || "");
}

function renderCategories() {
  $("p_category").innerHTML = PRODUCT_CATEGORIES.map((c) => `<option>${c}</option>`).join("");
}

async function loadProducts() {
  const data = await productApi.list();
  $("table").innerHTML =
    `<tr>
      <th>ID</th><th>Name</th><th>Category</th><th>Price</th><th>Stock</th>
      <th>Quick Update</th><th>Actions</th>
    </tr>` +
    data
      .map(
        (p) => `
        <tr>
          <td>${p.id}</td>
          <td>${p.name}</td>
          <td>${p.category}</td>
          <td>${Number(p.price).toFixed(2)}</td>
          <td>${p.stock}</td>
          <td>
            <div class="row">
              <input data-price="${p.id}" type="number" step="0.01" value="${p.price}" style="width:120px" />
              <input data-stock="${p.id}" type="number" min="0" value="${p.stock}" style="width:110px" />
              <button data-save="${p.id}">Save</button>
            </div>
          </td>
          <td class="actions">
            <button data-del="${p.id}">Delete</button>
          </td>
        </tr>
      `
      )
      .join("");
}

$("productForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    setError("");
    await productApi.create({
      name: $("p_name").value,
      description: $("p_description").value,
      price: Number($("p_price").value),
      stock: Number($("p_stock").value),
      category: $("p_category").value,
      active: true,
    });
    showToast("Product created");
    e.target.reset();
    await loadProducts();
  } catch (err) {
    setError(err.message || "Create product failed");
  }
});

$("table").addEventListener("click", async (e) => {
  const delBtn = e.target.closest("button[data-del]");
  const saveBtn = e.target.closest("button[data-save]");

  try {
    if (delBtn) {
      const id = delBtn.getAttribute("data-del");
      setError("");
      await productApi.remove(id);
      showToast("Product deleted");
      await loadProducts();
    }

    if (saveBtn) {
      const id = saveBtn.getAttribute("data-save");
      const priceEl = document.querySelector(`input[data-price="${id}"]`);
      const stockEl = document.querySelector(`input[data-stock="${id}"]`);
      const payload = { price: Number(priceEl.value), stock: Number(stockEl.value) };

      setError("");
      await productApi.update(id, payload);
      showToast("Product updated");
      await loadProducts();
    }
  } catch (err) {
    setError(err.message || "Action failed");
  }
});

renderCategories();
loadProducts().catch((e) => setError(e.message));
