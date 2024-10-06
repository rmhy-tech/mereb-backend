import {DarkTheme, DefaultTheme, ThemeProvider} from '@react-navigation/native';
import {useFonts} from 'expo-font';
import {Stack, useSegments} from 'expo-router';
import * as SplashScreen from 'expo-splash-screen';
import {useEffect} from 'react';
import 'react-native-reanimated';
import {GestureHandlerRootView} from 'react-native-gesture-handler';
import {Provider as PaperProvider} from 'react-native-paper';

import {useColorScheme} from '@/hooks/useColorScheme';
import {SessionProvider} from "@/context";

SplashScreen.preventAutoHideAsync();

export default function RootLayout() {
    const colorScheme = useColorScheme();
    const segments = useSegments();

    const [loaded] = useFonts({
        SpaceMono: require('../assets/fonts/SpaceMono-Regular.ttf'),
    });

    useEffect(() => {
        if (loaded) {
            SplashScreen.hideAsync();
        }
    }, [loaded]);

    if (!loaded) {
        return null;
    }

    console.log("loaded Root");
    return (
        <PaperProvider>
            <GestureHandlerRootView style={{flex: 1}}>
                <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
                    <SessionProvider>
                        <Stack>
                            <Stack.Screen name="(auth)" options={{headerShown: false}}/>
                        </Stack>
                    </SessionProvider>
                </ThemeProvider>
            </GestureHandlerRootView>
        </PaperProvider>
    );
}
