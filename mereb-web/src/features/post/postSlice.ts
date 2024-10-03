import {createAsyncThunk, createSlice} from '@reduxjs/toolkit';
import apiClient from '../../services/api';
import {CreatePost, PostState} from './types';

const initialState: PostState = {
    posts: [],
    loading: false,
    error: null,
};

export const fetchPosts = createAsyncThunk('posts/fetch', async () => {
    const response = await apiClient.get('/posts');
    return response.data;
});

export const createPost = createAsyncThunk(
    'posts/create',
    async (newPost: CreatePost, thunkAPI) => {
        try {
            const response = await apiClient.post('/posts', newPost);
            return response.data;
        } catch (error: unknown) {
            // eslint-disable-next-line @typescript-eslint/ban-ts-comment
            // @ts-expect-error
            return thunkAPI.rejectWithValue(error.response.data.message);
        }
    }
);

export const deletePost = createAsyncThunk(
    'post/deletePost',
    async (postId: number, thunkAPI) => {
        try {
            await apiClient.delete(`/posts/${postId}`);
            return postId;
            // eslint-disable-next-line @typescript-eslint/no-unused-vars
        } catch (error) {
            return thunkAPI.rejectWithValue('Failed to delete post');
        }
    }
);

const postSlice = createSlice({
    name: 'posts',
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder.addCase(fetchPosts.pending, (state) => {
            state.loading = true;
        });
        builder.addCase(fetchPosts.fulfilled, (state, action) => {
            state.loading = false;
            state.posts = action.payload;
        });
        builder.addCase(fetchPosts.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload as string;
        });
        builder.addCase(createPost.pending, (state) => {
            state.loading = true;
        });
        builder.addCase(createPost.fulfilled, (state, action) => {
            state.loading = false;
            state.posts.push(action.payload);
        });
        builder.addCase(createPost.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload as string;
        });
        builder.addCase(deletePost.pending, (state) => {
            state.loading = true;
        });
        builder.addCase(deletePost.fulfilled, (state, action) => {
            state.loading = false;
            state.posts = state.posts.filter(post => post.id !== action.payload);
        });
        builder.addCase(deletePost.rejected, (state, action) => {
            state.loading = false;
            state.error = action.payload as string;
        });
    },
});

export default postSlice.reducer;