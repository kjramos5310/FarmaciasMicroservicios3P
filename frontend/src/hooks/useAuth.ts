import { useState, useEffect } from 'react';
import { authService } from '../services/authService';

export const useAuth = () => {
  const [isAdmin, setIsAdmin] = useState(false);
  const [isUser, setIsUser] = useState(false);
  const [canEdit, setCanEdit] = useState(false);
  const [roles, setRoles] = useState<string[]>([]);
  const [username, setUsername] = useState<string | null>(null);

  useEffect(() => {
    const updateAuthState = () => {
      setIsAdmin(authService.isAdmin());
      setIsUser(authService.isUser());
      setCanEdit(authService.canEdit());
      setRoles(authService.getRoles());
      setUsername(authService.getUsername());
    };

    updateAuthState();

    // Escuchar cambios en localStorage
    window.addEventListener('storage', updateAuthState);
    
    return () => {
      window.removeEventListener('storage', updateAuthState);
    };
  }, []);

  return {
    isAdmin,
    isUser,
    canEdit,
    canView: authService.canView(),
    roles,
    username,
    isAuthenticated: authService.isAuthenticated(),
  };
};
