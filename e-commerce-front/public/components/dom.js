export const $ = (id) => document.getElementById(id);

export function setHTML(id, html) {
  $(id).innerHTML = html;
}

export function setText(id, text) {
  $(id).textContent = text || "";
}
