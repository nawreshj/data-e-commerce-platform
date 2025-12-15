const express = require("express");
const axios = require("axios");
const path = require("path");

const app = express();
app.use(express.json());
app.use(express.static(path.join(__dirname, "public")));


app.get("/", (req, res) => {
    res.redirect("/pages/dashboard.html");
  });

// microservice base URL
const API = {
  membership: "http://localhost:8081",
  product: "http://localhost:8082",
  order: "http://localhost:8083",
};


/* =========================
   PROXY endpoints (/api/*)
   ========================= */

// USERS (Membership)
app.get("/api/users", async (req, res) => {
  try {
    const r = await axios.get(`${API.membership}/api/v1/users`);
    res.json(r.data);
  } catch (e) {
    res.status(500).json({ error: "Membership service down or endpoint mismatch" });
  }
});

app.post("/api/users", async (req, res) => {
  try {
    const r = await axios.post(`${API.membership}/api/v1/users`, req.body);
    res.json(r.data);
  } catch (e) {
    res.status(400).json({ error: "User creation failed" });
  }
});

// PRODUCTS
app.get("/api/products", async (req, res) => {
  try {
    const r = await axios.get(`${API.product}/api/v1/products`);
    res.json(r.data);
  } catch (e) {
    res.status(500).json({ error: "Product service down or endpoint mismatch" });
  }
});

app.post("/api/products", async (req, res) => {
  try {
    const r = await axios.post(`${API.product}/api/v1/products`, req.body);
    res.json(r.data);
  } catch (e) {
    res.status(400).json({ error: "Product creation failed" });
  }
});

app.put("/api/products/:id", async (req, res) => {
  try {
    const r = await axios.put(`${API.product}/api/v1/products/${req.params.id}`, req.body);
    res.json(r.data);
  } catch (e) {
    res.status(400).json({ error: "Product update failed" });
  }
});

app.delete("/api/products/:id", async (req, res) => {
  try {
    await axios.delete(`${API.product}/api/v1/products/${req.params.id}`);
    res.json({ ok: true });
  } catch (e) {
    res.status(400).json({ error: "Cannot delete product (maybe used in an order)" });
  }
});

// ORDERS
app.get("/api/orders", async (req, res) => {
  try {
    const r = await axios.get(`${API.order}/api/v1/orders`);
    res.json(r.data);
  } catch (e) {
    res.status(500).json({ error: "Order service down or endpoint mismatch" });
  }
});

app.post("/api/orders", async (req, res) => {
  try {
    const r = await axios.post(`${API.order}/api/v1/orders`, req.body);
    res.json(r.data);
  } catch (e) {
    res.status(400).json({ error: "Order creation failed (user/product/stock/service down)" });
  }
});

app.put("/api/orders/:id/status", async (req, res) => {
  try {
    const id = req.params.id;
    const { status } = req.body;

    if (!status) {
      return res.status(400).json({ error: "Missing status field" });
    }

    const r = await axios.patch(
      `${API.order}/api/v1/orders/${id}/status`,
      { status }, 
      { headers: { "Content-Type": "application/json" } }
    );

    res.json(r.data);
  } catch (e) {
    res.status(e.response?.status || 400).json(
      e.response?.data || { error: "Order status update failed" }
    );
  }
});
app.delete("/api/orders/:id", async (req, res) => {
  try {
    const r = await axios.delete(`${API.order}/api/v1/orders/${req.params.id}`);
    res.json(r.data ?? { ok: true });
  } catch (e) {
    res.status(400).json({ error: "Order cancel failed" });
  }
});

// HEALTH (actuator)
app.get("/api/health", async (req, res) => {
  const out = {};
  for (const [name, url] of Object.entries(API)) {
    try {
      const r = await axios.get(`${url}/actuator/health`);
      out[name] = r.data?.status || "UNKNOWN";
    } catch {
      out[name] = "DOWN";
    }
  }
  res.json(out);
});

app.listen(5173, () => console.log("Front JS running: http://localhost:5173"));
