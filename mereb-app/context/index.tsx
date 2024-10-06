import {createContext, type PropsWithChildren, useContext, useEffect, useState} from 'react';
import {useStorageState} from "@/hooks/useStorageState";

import {jwtDecode} from "jwt-decode";

const extractUsernameFromToken = (token: string): string | null => {
    try {
        const decodedToken: any = jwtDecode(token); // Adjust type as needed
        console.log(decodedToken); // You can also use console.log for debugging in React Native
        return decodedToken?.sub || null; // Adjust based on the structure of your JWT
    } catch (error) {
        console.error("Failed to decode token", error);
        return null;
    }
};

const AUTH_URL = "http://192.168.1.109:8082/api/users";


export interface RegisterCredentials {
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    password: string;
    role: string;
}

const AuthContext = createContext<{
    signUp: (registerCredentials: RegisterCredentials) => void;
    signIn: (username: string, password: string) => void;
    signOut: () => void;
    session?: string | null;
    error?: string | null;
    isLoading: boolean;
    username: string | null;
}>({
    signUp: () => null,
    signIn: () => null,
    signOut: () => null,
    session: null,
    error: null,
    isLoading: false,
    username: null,
});

export function useSession() {
    const value = useContext(AuthContext);
    if (process.env.NODE_ENV !== 'production') {
        if (!value) {
            throw new Error('useSession must be wrapped in a <SessionProvider />');
        }
    }

    return value;
}

function useUsername() {
    const [username, setUsername] = useState<string | null>(null);
    const [[_isLoading, session], _setSession] = useStorageState('session');

    useEffect(() => {
        if (session) {
            setUsername(extractUsernameFromToken(session));
        }
    }, [session]);

    return {username};
}

export function SessionProvider({children}: PropsWithChildren) {
    const [[isLoading, session], setSession] = useStorageState('session');
    const [error, setError] = useState<string | null>(null);
    const {username} = useUsername()

    const handlePostFetch = (endpoint: string, body: string) => {
        fetch(`${AUTH_URL}/${endpoint}`, {
            method: "POST",
            headers: {
                'Content-Type': 'application/json',
            },
            body,
        }).then(res => res.json())
            .then((data) => {
                if (data?.error) throw new Error(data?.error);
                if (data) {
                    setSession(data.token);
                    setError(null);
                }
            })
            .catch((_err) => {
                setError("Error logging in! try again!");
            });
    }

    const handleLogin = (username: string, password: string) => {
        if (!session && username && password) {
            handlePostFetch("login", JSON.stringify({username, password}))
        }
    }

    const handleRegister = (registerCredentials: RegisterCredentials) => {
        if (!session && registerCredentials) {
            handlePostFetch("register", JSON.stringify(registerCredentials))
        }
    }


    return (
        <AuthContext.Provider
            value={{
                signUp: handleRegister,
                signIn: handleLogin,
                signOut: () => {
                    setSession(null);
                },
                error,
                session,
                isLoading,
                username
            }}>
            {children}
        </AuthContext.Provider>
    );
}