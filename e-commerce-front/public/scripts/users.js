import { renderNavbar } from "../components/navbar.js";
import { setHTML, setText, $ } from "../components/dom.js";
import { showToast } from "../components/toast.js";
import { membershipApi } from "../api/membershipApi.js";

setHTML("navbar", renderNavbar("users"));

function setError(msg) {
  setText("error", msg || "");
}

async function loadUsers() {
  const users = await membershipApi.list();

  $("table").innerHTML =
    `<tr><th>ID</th><th>First name</th><th>Last name</th><th>Email</th></tr>` +
    users
      .map(
        (u) =>
          `<tr>
            <td>${u.id}</td>
            <td>${u.firstName ?? ""}</td>
            <td>${u.lastName ?? ""}</td>
            <td>${u.email ?? ""}</td>
          </tr>`
      )
      .join("");
}


// gestion de la soumission du formulaire de création de client
$("userForm").addEventListener("submit", async (e) => {
  e.preventDefault();
  try {
    setError("");
    await membershipApi.create({
      firstName: $("firstName").value,
      lastName: $("lastName").value,
      email: $("email").value,
    });
    showToast("User created");
    e.target.reset();
    await loadUsers();
  } catch (err) {
    setError(err.message || "Create user failed");
  }
});

loadUsers().catch((e) => setError(e.message));
