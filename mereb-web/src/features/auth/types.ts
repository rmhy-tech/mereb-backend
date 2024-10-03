export interface AuthState {
    currentUser?: any;
    user: string | null;
    token: string | null;
    isAuthenticated: boolean;
    loading: boolean;
    error: string | null;
}

export interface LoginCredentials {
    username: string;
    password: string;
}

export interface RegisterCredentials {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
    role: string;
}