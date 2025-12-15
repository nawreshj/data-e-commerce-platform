import { renderNavbar } from "../components/navbar.js";
import { setHTML } from "../components/dom.js";

setHTML("navbar", renderNavbar("dashboard"));
