import axios from 'axios';

const AUTH_API_URL = 'http://localhost:9000/api/auth';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  password: string;
  email: string;
}

export interface AuthResponse {
  access_token: string;
  token_type: string;
  expires_in: number;
  username: string;
  email: string;
  userId: number;
  roles: string[];
}

export const authApi = axios.create({
  baseURL: AUTH_API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor para agregar el token a todas las peticiones
authApi.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('authToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

export const authService = {
  login: async (credentials: LoginRequest): Promise<AuthResponse> => {
    const response = await authApi.post<AuthResponse>('/login', credentials);
    if (response.data.access_token) {
      localStorage.setItem('authToken', response.data.access_token);
      localStorage.setItem('username', response.data.username);
      localStorage.setItem('email', response.data.email);
      localStorage.setItem('userId', response.data.userId.toString());
      localStorage.setItem('roles', JSON.stringify(response.data.roles));
    }
    return response.data;
  },

  register: async (userData: RegisterRequest): Promise<AuthResponse> => {
    const response = await authApi.post<AuthResponse>('/register', userData);
    if (response.data.access_token) {
      localStorage.setItem('authToken', response.data.access_token);
      localStorage.setItem('username', response.data.username);
      localStorage.setItem('email', response.data.email);
      localStorage.setItem('userId', response.data.userId.toString());
      localStorage.setItem('roles', JSON.stringify(response.data.roles));
    }
    return response.data;
  },

  logout: () => {
    localStorage.removeItem('authToken');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('userId');
    localStorage.removeItem('roles');
  },

  isAuthenticated: (): boolean => {
    return !!localStorage.getItem('authToken');
  },

  getToken: (): string | null => {
    return localStorage.getItem('authToken');
  },

  getUsername: (): string | null => {
    return localStorage.getItem('username');
  },

  getRoles: (): string[] => {
    const roles = localStorage.getItem('roles');
    return roles ? JSON.parse(roles) : [];
  },

  isAdmin: (): boolean => {
    const roles = authService.getRoles();
    return roles.includes('ADMIN');
  },

  isUser: (): boolean => {
    const roles = authService.getRoles();
    return roles.includes('USER') && !roles.includes('ADMIN');
  },

  canEdit: (): boolean => {
    return authService.isAdmin();
  },

  canView: (): boolean => {
    return authService.isAuthenticated();
  },
};
