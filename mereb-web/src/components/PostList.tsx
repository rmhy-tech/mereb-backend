import React, { useEffect, useState } from 'react';
import { useAppDispatch } from "../hooks/useAppDispatch";
import { useAppSelector } from '../hooks/useAppSelector';
import { fetchPosts, createPost } from '../features/post/postSlice';
import {formatDate} from "../utils";
import '../styles/Posts.scss';
import PostItem from "./PostItem.tsx";

const PostList = () => {
    const dispatch = useAppDispatch();
    const posts = useAppSelector((state) => state.post.posts);
    const [content, setContent] = useState('');
    const [error, setError] = useState('');

    useEffect(() => {
        dispatch(fetchPosts());
    }, [dispatch]);

    const handleCreatePost = (e: React.FormEvent) => {
        e.preventDefault();
        if (content.trim() === '') {
            setError('Post content cannot be empty');
            return;
        }
        setError('');
        const createdAt = formatDate(new Date());
        dispatch(createPost({ content, createdAt }));
        setContent('');
    };

    return (
        <div className="posts-page">
            <h2>Posts</h2>
            <form onSubmit={handleCreatePost}>
                <input
                    type="text"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    placeholder="What's on your mind?"
                />
                {error && <p className="error">{error}</p>}
                <button type="submit">Create Post</button>
            </form>
            <ul>
                {posts.length > 0 ? (
                    posts.map((post) => (
                        <PostItem key={post.id} post={post}/>
                    ))
                ) : (
                    <p>No posts available</p>
                )}
            </ul>
        </div>
    );
};

export default PostList;
