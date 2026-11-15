import axios from "axios";

const defaultApiUrl = `${window.location.protocol}//${window.location.hostname}:8080/api`;

const API = axios.create({
  baseURL: import.meta.env.VITE_API_URL ?? defaultApiUrl,
  auth: {
    username: "amadou",
    password: "1234"
  }
});

export function getApiErrorMessage(error, fallbackMessage) {
  const data = error?.response?.data;
  const targetApi = error?.config?.baseURL ?? defaultApiUrl;

  if (!error?.response) {
    return `Backend Avistock indisponible sur ${targetApi}. Demarrez Spring Boot puis rechargez la page.`;
  }

  return data?.detail || data?.message || data?.error || fallbackMessage;
}

export default API;
