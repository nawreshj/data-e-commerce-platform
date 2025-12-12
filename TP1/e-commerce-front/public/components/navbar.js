export function renderNavbar(active) {
    const items = [
      ["dashboard", "/pages/dashboard.html"],
      ["users", "/pages/users.html"],
      ["products", "/pages/products.html"],
      ["orders", "/pages/orders.html"],
      ["monitoring", "/pages/monitoring.html"],
    ];
  
    return `
      <div class="topbar">
        <div class="brand">TP1 Front</div>
        <nav class="nav">
          ${items
            .map(([name, href]) => {
              const cls = name === active ? "nav__link nav__link--active" : "nav__link";
              return `<a class="${cls}" href="${href}">${name}</a>`;
            })
            .join("")}
        </nav>
      </div>
    `;
  }
  