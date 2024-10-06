import {Tabs} from 'expo-router';
import React from 'react';
import { Image } from 'react-native';
import {TabBarIcon} from '@/components/navigation/TabBarIcon';
import {Colors} from '@/constants/Colors';
import {useColorScheme} from '@/hooks/useColorScheme';
import {useSession} from "@/context";

export default function TabLayout() {
    const colorScheme = useColorScheme();

    const {username} = useSession();

    return (
        <Tabs
            screenOptions={{
                tabBarActiveTintColor: Colors[colorScheme ?? 'light'].tint,
                headerShown: false,
                headerTitleAlign: 'center',
            }}>
            <Tabs.Screen
                name="index"
                options={{
                    headerTitle: () => (
                        <Image
                            source={require('@/assets/images/mereb_ico_2.png')} // Replace with your image path
                            style={{ width: 100, height: 50 }} // Adjust the size of the image
                            resizeMode="contain" // Ensure the image scales correctly
                        />
                    ),
                    headerShown: true,
                    tabBarIcon: ({color, focused}) => (
                        <TabBarIcon name={focused ? 'home' : 'home-outline'} color={color}/>
                    ),
                }}
            />
            <Tabs.Screen
                name="profile"
                options={{
                    title: `@${username}`,
                    headerShown: true,
                    tabBarIcon: ({color, focused}) => (
                        <TabBarIcon name={focused ? 'person' : 'person-outline'} color={color}/>
                    ),
                }}
            />
        </Tabs>
    );
}
