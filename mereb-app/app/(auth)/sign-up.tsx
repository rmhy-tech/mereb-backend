import React, {useState} from 'react';
import {Button, StyleSheet, Text, TextInput, View} from 'react-native';
import {router} from "expo-router";
import {useSession} from "@/context";

export default function SignUpScreen() {
    const [firstName, setFirstName] = useState('Leul');
    const [lastName, setLastName] = useState('Tewolde');
    const [email, setEmail] = useState('leulwtewolde@gmail.com');
    const [username, setUsername] = useState('leulwtewolde');
    const [password, setPassword] = useState('password1234');
    const [errorMessage, setErrorMessage] = useState(null);
    const {signUp} = useSession();

    const handleSignUp = async () => {
        try {
            signUp({firstName, lastName, username, email, password, role: "USER"});
        } catch (error: any) {
            setErrorMessage(error.message);
        }
    };

    return (
        <View style={styles.container}>
            <Text style={styles.title}>Sign Up</Text>
            <TextInput
                style={styles.input}
                placeholder="Firstname"
                value={firstName}
                onChangeText={setFirstName}
                autoCapitalize="none"
            />
            <TextInput
                style={styles.input}
                placeholder="Lastname"
                value={lastName}
                onChangeText={setLastName}
                autoCapitalize="none"
            />
            <TextInput
                style={styles.input}
                placeholder="Email"
                value={email}
                onChangeText={setEmail}
                autoCapitalize="none"
            />
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
                onChangeText={setPassword}
                secureTextEntry
            />
            {errorMessage && <Text style={styles.error}>{errorMessage}</Text>}
            <Button title="Sign Up" onPress={handleSignUp}/>
            <Text onPress={() => router.replace('/sign-in')} style={styles.switchText}>
                Already have an account? Sign In
            </Text>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        justifyContent: 'center',
        padding: 20,
        backgroundColor: '#fff'
    },
    input: {
        height: 40,
        borderColor: '#ccc',
        borderWidth: 1,
        marginBottom: 10,
        paddingHorizontal: 10,
    },
    error: {
        color: 'red',
        marginBottom: 10,
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
