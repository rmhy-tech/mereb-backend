// src/components/PostItem.tsx
import React, {useState} from 'react';// Assuming you have a types directory for TypeScript types
import '../styles/PostItem.scss';
import {Post} from "../features/post/types.ts";
import {useAppDispatch} from "../hooks/useAppDispatch.ts";
import {deletePost} from "../features/post/postSlice.ts";

interface PostItemProps {
    post: Post;
}

const PostItem: React.FC<PostItemProps> = ({ post }) => {

    const dispatch = useAppDispatch();
    const [isDeleting, setIsDeleting] = useState(false); // Local state for individual post

    const handleDelete = async () => {
        setIsDeleting(true);  // Set the local state to true when deletion starts
        await dispatch(deletePost(post.id));  // Wait for the deletePost action to complete
        setIsDeleting(false);  // Reset the local state when deletion is finished
    };

    return (
        <li className="post-item">
            <p>{post.content}</p>
            <small>Posted by user {post.userId} on {new Date(post.createdAt).toLocaleString()}</small>
            <div className="post-actions">
                <button onClick={handleDelete} disabled={isDeleting}>
                    {isDeleting ? 'Deleting...' : 'Delete'}
                </button>
            </div>
        </li>
    );
};

export default PostItem;
