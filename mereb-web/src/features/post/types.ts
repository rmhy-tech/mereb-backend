export interface Post {
    id: number;
    content: string;
    userId: number;
    createdAt: string;
}

export interface PostState {
    posts: Post[];
    loading: boolean;
    error: string | null;
}

export interface CreatePost {
    // userId: number;
    content: string;
    createdAt: string;
}
