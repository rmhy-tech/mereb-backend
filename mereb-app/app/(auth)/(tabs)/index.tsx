import {
    ActivityIndicator,
    Alert,
    Button,
    FlatList,
    StyleSheet,
    Text,
    TextInput,
    TouchableOpacity,
    View
} from 'react-native';
import usePostData, {Post} from "@/context/usePostData";
import React, {useState} from "react";
import moment from 'moment';
import {Avatar, Card, IconButton, Menu} from 'react-native-paper';


interface PostItemProps {
    post: Post;
    deletePost: (postId: number) => void;
}

const PostItem: React.FC<PostItemProps> = ({post, deletePost}) => {
    const [menuVisible, setMenuVisible] = React.useState(false);

    // Confirm before deleting
    const handleDeleteConfirmation = () => {
        const postId = post.id;
        if (postId) {
            Alert.alert(
                "Delete Post",
                "Are you sure you want to delete this post?",
                [
                    {text: "Cancel", style: "cancel"},
                    {text: "Delete", style: "destructive", onPress: () => deletePost(postId)}
                ]
            );
        }
    }

    return (
        <TouchableOpacity>
            <Card style={styles.card}>
                <Card.Title
                    titleStyle={{color: "#000"}}
                    subtitleStyle={{color: "#ccc", fontSize: 10}}
                    title={post.userId}
                    subtitle={moment(post.createdAt).fromNow()}
                    left={(props) => (
                        <Avatar.Text
                            {...props}
                            label={"username"[0].toUpperCase()}
                            size={50} // Avatar size
                            style={styles.avatar}
                        />
                    )}
                    right={(props) => (
                        <Menu
                            visible={menuVisible}
                            onDismiss={() => setMenuVisible(false)}
                            anchor={
                                <IconButton
                                    {...props}
                                    icon="dots-vertical"
                                    size={24}
                                    onPress={() => setMenuVisible(true)}
                                />
                            }
                        >
                            <Menu.Item onPress={handleDeleteConfirmation} title="Delete"/>
                        </Menu>
                    )}
                />
                <Card.Content>
                    <Text style={styles.postContent}>{post.content}</Text>
                </Card.Content>
            </Card>
        </TouchableOpacity>
    );
}

export default function HomeScreen() {

    const {posts, addPost, deletePost, isLoading} = usePostData();
    const [content, setContent] = useState("");

    const handleAddPost = async () => {
        if (content) {
            await addPost(content);
            setContent("");  // Clear input after posting
        }
    };

    return (
        <View style={styles.container}>
            <TextInput
                style={styles.input}
                placeholder="Write something here"
                value={content}
                onChangeText={setContent}
                autoCapitalize="none"
                onSubmitEditing={handleAddPost}
                returnKeyType="done"
                editable={!isLoading}  // Disable input while loading
            />
            <Button
                title="Add Post"
                onPress={handleAddPost}
                disabled={!content || isLoading}  // Disable button if no content or loading
            />
            {isLoading && (
                <ActivityIndicator size="large" color="#00ff00" style={{marginVertical: 10}}/>
            )}
            <FlatList
                data={posts}
                contentContainerStyle={{padding: 10, gap: 10}}
                keyExtractor={(item) => item.id?.toString() || Math.random().toString()} // Generate unique key if no ID
                renderItem={({item}) => <PostItem post={item} deletePost={deletePost}/>}
                ListEmptyComponent={() => (
                    <Text style={styles.emptyMessage}>No posts available</Text>
                )}
            />

        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        paddingVertical: 20,
        paddingHorizontal: 10,
        backgroundColor: '#f4f4f4',
    },
    input: {
        padding: 10,
        marginBottom: 10,
        borderWidth: 1,
        borderColor: "#ccc",
        borderRadius: 5,
        backgroundColor: '#fff',
    },
    emptyMessage: {
        textAlign: 'center',
        marginTop: 20,
        color: "#777",
    },
    // Post Item
    card: {
        // marginVertical: 10,
        // marginHorizontal: 5,
        padding: 10,
        borderRadius: 12,
        backgroundColor: '#fff',
        // shadowColor: '#000',
        // shadowOpacity: 0.2,
        // shadowOffset: {width: 0, height: 2},
        // shadowRadius: 5,
        // elevation: 3,
    },
    postContent: {
        fontSize: 16,
        color: '#333',
        marginTop: 5,
    },
    avatar: {
        backgroundColor: '#3498db',
    },
});
