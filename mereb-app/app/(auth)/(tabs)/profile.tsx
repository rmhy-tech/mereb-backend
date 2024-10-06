import {ActivityIndicator, Button, StyleSheet, Text, View} from 'react-native';
import React, {useEffect, useState} from "react";
import {useSession} from "@/context";
import { MaterialIcons } from '@expo/vector-icons';

interface UserData {
    id: number;
    firstName: string;
    lastName: string;
    username: string;
    email: string;
    role: string;
}

export default function TabTwoScreen() {
    const { username, session, signOut } = useSession();
    const [userData, setUserData] = useState<UserData | null>(null);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (session && username) {
            setIsLoading(true);
            fetch(`http://192.168.1.109:8082/api/users/${username}`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${session}`,
                    'Content-Type': 'application/json',
                },
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error('Failed to fetch user data');
                    }
                    return response.json();
                })
                .then((data: UserData) => {
                    setUserData(data);
                    setIsLoading(false);
                })
                .catch((error) => {
                    console.error(error);
                    setError('Failed to load user data.');
                    setIsLoading(false);
                });
        }
    }, [session, username]);

    if (isLoading) {
        return (
            <View style={styles.centered}>
                <ActivityIndicator size="large" color="#3498db" />
            </View>
        );
    }

    if (error) {
        return (
            <View style={styles.centered}>
                <Text style={styles.error}>{error}</Text>
            </View>
        );
    }

    if (!session) {
        return (
            <View style={styles.centered}>
                <Text style={styles.message}>You are not logged in!</Text>
            </View>
        );
    }

    return (
        <View style={styles.container}>
            {userData && (
                <View style={styles.cardContainer}>
                    {/* Name */}
                    <View style={styles.card}>
                        <MaterialIcons name="person" size={24} color="#3498db" />
                        <Text style={styles.value}>{userData.firstName} {userData.lastName}</Text>
                    </View>
                    {/* Username */}
                    <View style={styles.card}>
                        <MaterialIcons name="account-circle" size={24} color="#3498db" />
                        <Text style={styles.value}>@{userData.username}</Text>
                    </View>

                    {/* Email */}
                    <View style={styles.card}>
                        <MaterialIcons name="email" size={24} color="#3498db" />
                        <Text style={styles.value}>{userData.email}</Text>
                    </View>

                    {/* Role */}
                    <View style={styles.card}>
                        <MaterialIcons name="admin-panel-settings" size={24} color="#3498db" />
                        <Text style={styles.value}>{userData.role}</Text>
                    </View>
                </View>
            )}
            <View style={styles.buttonContainer}>
                <Button title="Sign Out" onPress={signOut} color="#e74c3c" />
            </View>
        </View>
    );
};

const styles = StyleSheet.create({
    container: {
        flex: 1,
        padding: 20,
        backgroundColor: '#ecf0f1',
    },
    centered: {
        flex: 1,
        justifyContent: 'center',
        alignItems: 'center',
    },
    header: {
        fontSize: 30,
        fontWeight: 'bold',
        color: '#2c3e50',
        marginBottom: 20,
    },
    cardContainer: {
        marginVertical: 10,
    },
    card: {
        flexDirection: 'row',
        alignItems: 'center',
        backgroundColor: '#fff',
        padding: 15,
        borderRadius: 10,
        marginBottom: 10,
        shadowColor: '#000',
        shadowOpacity: 0.1,
        shadowOffset: { width: 0, height: 3 },
        shadowRadius: 5,
        elevation: 5,
    },
    value: {
        marginLeft: 10,
        fontSize: 16,
        color: '#2c3e50',
    },
    error: {
        fontSize: 18,
        color: 'red',
    },
    message: {
        fontSize: 18,
        textAlign: 'center',
    },
    buttonContainer: {
        marginTop: 20,
    },
});