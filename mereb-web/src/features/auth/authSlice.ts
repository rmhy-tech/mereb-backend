import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import {AuthState, LoginCredentials, RegisterCredentials} from './types';
import axios from "axios";
import {jwtDecode} from 'jwt-decode';

const baseUrl = "http://13.83.41.233:8082/api/users";

const initialState: AuthState = {
    currentUser: null,
    user: localStorage.getItem("user"),
    token: localStorage.getItem('token') || null,
    isAuthenticated: !!localStorage.getItem('token'),
    loading: false,
    error: null,
};

const extractUsernameFromToken = (token: string): string | null => {
    try {
        const decodedToken = jwtDecode(token);
        console.log(decodedToken)
        return decodedToken?.sub || null; // Adjust the key based on your token structure
    } catch (error) {
        console.error("Failed to decode token", error);
        return null;
    }
};

export const login = createAsyncThunk(
    'auth/loginUser',
    async (credentials: LoginCredentials, thunkAPI) => {
        try {
            const response = await axios.post(`${baseUrl}/login`, credentials);
            const {token} = response.data;
            localStorage.setItem('token', token);
            return token;
        } catch (error) {
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            return thunkAPI.rejectWithValue(error.response.data.message);
        }
    }
);

export const getUser = createAsyncThunk(
    'auth/getUser',
    async (thunkAPI) => {
        try {
            const username = localStorage.getItem('user');
            const response = await axios.get(`${baseUrl}/${username}`);
            const user = response.data;
            return user;
        } catch (error) {
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            return thunkAPI.rejectWithValue(error.response.data.message);
        }
    }
);

export const register = createAsyncThunk(
    'auth/register',
    async (credentials: RegisterCredentials, thunkAPI) => {
        try {
            const response = await axios.post(`${baseUrl}/register`, credentials);
            const {token} = response.data;
            localStorage.setItem('token', token);
            return token;
        } catch (error) {
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            return thunkAPI.rejectWithValue(error.response.data.message);
        }
    }
);

const authSlice = createSlice({
    name: 'auth',
    initialState,
    reducers: {
        logout: (state) => {
            localStorage.removeItem('token');
            state.token = null;
            state.isAuthenticated = false;
        },
    },
    extraReducers: (builder) => {
        builder.addCase(login.pending, (state) => {
            state.loading = true;
        });
        builder.addCase(login.fulfilled, (state, action) => {
            state.loading = false;
            state.isAuthenticated = true;
            const username = extractUsernameFromToken(action.payload);
            state.user = username;
            if (username) {
                localStorage.setItem('user', username);
            }
            state.token = action.payload;
        });
        builder.addCase(login.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload as string;
        });
        builder.addCase(register.pending, (state) => {
            state.loading = true;
        });
        builder.addCase(register.fulfilled, (state, action) => {
            state.loading = false;
            state.isAuthenticated = true;
            const username = extractUsernameFromToken(action.payload);
            state.user = username;
            if (username) {
                localStorage.setItem('user', username);
            }
            state.token = action.payload;
        });
        builder.addCase(register.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload as string;
        });
        builder.addCase(getUser.pending, (state) => {
            state.loading = true;
        });
        builder.addCase(getUser.fulfilled, (state, action) => {
            state.loading = false;
            state.currentUser = action.payload;
        });
        builder.addCase(getUser.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload as string;
        });
    }
});

export const {logout} = authSlice.actions;
export default authSlice.reducer;
