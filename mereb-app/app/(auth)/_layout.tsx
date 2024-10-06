import {Stack, useRouter} from 'expo-router';
import {useSession} from "@/context";
import {Text} from "react-native";
import React, {useEffect, useState} from "react";

const AuthLayout = () => {

    const {isLoading, session} = useSession();
    const router = useRouter();
    const [isMounted, setIsMounted] = useState(false); // Track whether the component is mounted

    useEffect(() => {
        setIsMounted(true);
    }, []);

    useEffect(() => {
        if (isMounted && !isLoading) {
            if (session) {
                router.replace("/(tabs)");
            } else {
                router.replace("/sign-in");
            }
        }
    }, [isMounted, isLoading, session]);

    if (isLoading) {
        return <Text>Loading...</Text>;
    }


    return <Stack>
        <Stack.Screen
            name="sign-in"
            options={{
                headerShown: false,
                presentation: 'modal'
            }}
        />
        <Stack.Screen
            name="sign-up"
            options={{
                headerShown: false,
                presentation: 'modal'
            }}
        />
        <Stack.Screen name="(tabs)" options={{headerShown: false}}/>
    </Stack>;
};
export default AuthLayout;