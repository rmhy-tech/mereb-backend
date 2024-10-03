import {configureStore} from '@reduxjs/toolkit';
import authReducer from '../features/auth/authSlice.ts';
import postReducer from '../features/post/postSlice.ts';

export const store = configureStore({
    reducer: {
        auth: authReducer,
        post: postReducer,
    }
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;