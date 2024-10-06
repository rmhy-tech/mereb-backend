import React, {useState} from 'react';
import {Button, StyleSheet, Text, TextInput, View} from 'react-native';
import {router} from "expo-router";
import {useSession} from "@/context";

export default function SignInScreen() {
    const [username, setUsername] = useState('leulwtewolde');
    const [password, setPassword] = useState('password1234');
    const {signIn, error} = useSession();

    const handleSignIn = () => signIn(username, password);

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Sign In</Text>
            {error && <Text style={styles.error}>{error}</Text>}
            <TextInput
                style={styles.input}
                placeholder="Username"
                value={username}
                onChangeText={setUsername}
                autoCapitalize="none"
            />
            <TextInput
                style={styles.input}
                placeholder="Password"
                value={password}
                autoCapitalize="none"
                onChangeText={setPassword}
                secureTextEntry
            />
            <Button title="Sign In" onPress={handleSignIn}/>
            <Text onPress={() => router.replace("/sign-up")} style={styles.switchText}>
                Don't have an account? Sign Up
            </Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        padding: 20,
        backgroundColor: '#fff',
    },
    input: {
        height: 40,
        // backgroundColor: '#ccc',
        borderColor: '#ccc',
        borderWidth: 1,
        marginBottom: 10,
        paddingHorizontal: 10,
    },
    error: {
        color: 'red',
        marginBottom: 15,
        textAlign: 'center',
    },
    switchText: {
        marginTop: 15,
        color: 'blue',
        textAlign: 'center',
    },
    title: {
        marginBottom: 25,
        fontSize: 25,
        textAlign: 'center'
    }
});