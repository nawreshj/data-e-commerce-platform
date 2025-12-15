export async function request(url, { method = "GET", body } = {}) {
    const options = { method, headers: {} };
  
    if (body !== undefined) {
      options.headers["Content-Type"] = "application/json";
      options.body = JSON.stringify(body);
    }
  
    const res = await fetch(url, options);
    const raw = await res.text();

    const ct = res.headers.get("content-type") || "";
    let data = raw;
  

    if (ct.includes("application/json") && raw) {
      try {
        data = JSON.parse(raw);
      } catch {
        data = raw; 
      }
    }
  
// on vérife le formattage soit string soit json. String pour les gestions d'exception
    if (!res.ok) {
      const msg =
        typeof data === "string"
          ? data
          : data?.message || data?.error || data?.code || JSON.stringify(data);
  
      throw new Error(msg || `HTTP ${res.status}`);
    }
  
    return data;
  }
  