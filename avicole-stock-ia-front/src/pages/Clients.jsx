import { useEffect, useState } from "react";
import API from "../api/api";

function Clients() {
  const [clients, setClients] = useState([]);

  useEffect(() => {
    API.get("/clients")
      .then(res => setClients(res.data))
      .catch(err => console.error(err));
  }, []);

  return (
    <div style={{ padding: 20 }}>
      <h2>Clients 👥</h2>

      <ul>
        {clients.map(c => (
          <li key={c.id}>
            {c.nom} {c.prenom}
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Clients;