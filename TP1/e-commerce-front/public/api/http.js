export async function request(url, { method = "GET", body } = {}) {
    const options = { method, headers: {} };
  
    if (body !== undefined) {
      options.headers["Content-Type"] = "application/json";
      options.body = JSON.stringify(body);
    }
  
    const res = await fetch(url, options);
    const text = await res.text();
    const data = text ? JSON.parse(text) : null;
  
    if (!res.ok) {
      throw new Error(data?.error || `HTTP ${res.status}`);
    }
    return data;
  }
  