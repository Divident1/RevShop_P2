const localHosts = new Set(['localhost', '127.0.0.1']);

export const API_BASE_URL = localHosts.has(window.location.hostname)
  ? 'http://localhost:8080/api'
  : '/api';
