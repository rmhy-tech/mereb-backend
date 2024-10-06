import {useSession} from "@/context/index";
import {useEffect, useState} from "react";

const fetchWithAuth = async (url: string, token: string, options = {}) => {
    const headers = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`, // Ensure you're passing the token correctly
    };

    const updatedOptions = {
        ...options,
        headers,
    };

    try {
        const response = await fetch(url, updatedOptions);

        if (!response.ok) {
            const errorBody = await response.text(); // Log the error response from the server
            throw new Error(`HTTP error! Status: ${response.status} - ${errorBody}`);
        }
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        } else {
            return await response.text(); // Handle non-JSON responses, e.g., for DELETE
        }
    } catch (error) {
        throw error;
    }
};

const postDataWithAuth = async (url: string, token: string, data: any) => {
    return fetchWithAuth(url, token, {
        method: 'POST',
        body: JSON.stringify(data),
    });
};

const deleteDataWithAuth = async (url: string, token: string) => {
    return fetchWithAuth(url, token, {
        method: 'DELETE',
    });
};

const formatDate = (date:Date) => {
    const pad = (num:number) => num.toString().padStart(2, '0');

    const year = date.getFullYear();
    const month = pad(date.getMonth() + 1); // Months are 0-based
    const day = pad(date.getDate());
    const hours = pad(date.getHours());
    const minutes = pad(date.getMinutes());

    return `${year}-${month}-${day}T${hours}:${minutes}`;
}

export interface Post {
    id?: number;
    content: string;
    userId?: number;
    username?: number;
    createdAt: string;
}

export interface PostDataState {
    posts: Post[];
    isLoading: boolean;
    addPost: (content:string) => Promise<void>;
    deletePost: (postId: number) => Promise<void>;
    error: string | null;
}

export interface CreatePost {
    // userId: number;
    content: string;
    createdAt: string;
}

const POST_URL = "http://192.168.1.109:8083/api/posts";

export default function usePostData():PostDataState {
    const {session} = useSession();
    const [posts, setPosts] = useState<any[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string|null>(null);

    useEffect(() => {
        const fetchPostData = async () => {
            if (session) {
                try {
                    const data = await fetchWithAuth(POST_URL, session);
                    console.log("API data:", data);
                    setPosts(data);
                    setIsLoading(false);
                } catch (error) {
                    setError("Error fetching data");
                    setIsLoading(false);
                    // console.error("Error fetching data:", error);
                }
            }
        }
        fetchPostData();
    }, [session, isLoading]);

    const addPost = async (content: string) => {
        if (content && session) {

            try {
                const postData:CreatePost = {content, createdAt: formatDate(new Date())};
                posts.push(postData);
                const response = await postDataWithAuth(POST_URL, session, postData);
                console.log("Response:", response);
                setIsLoading(true);
            } catch (error) {
                setError("Error posting data");
                // console.error("Error posting data:", error);
            }
        }
    }

    const deletePost = async (postId: number) => {
        if (postId && session) {
            const deleteUrl = `${POST_URL}/${postId}`;
            try {
                await deleteDataWithAuth(deleteUrl, session);
                setIsLoading(true);
            } catch (error) {
                // console.error("Error deleting post:", error);
                setError("Error deleting post");
            }
        }
    }

    return {isLoading, error, posts, addPost, deletePost}
}