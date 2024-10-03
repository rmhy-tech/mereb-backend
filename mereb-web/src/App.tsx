import {BrowserRouter as Router, Route, Routes} from 'react-router-dom';
import HomePage from './pages/HomePage';
import AuthPage from './pages/AuthPage';
import PrivateRoute from "./components/PrivateRoute.tsx";
import Navbar from "./components/Navbar.tsx";
import './styles/main.scss';
import {useAppSelector} from "./hooks/useAppSelector.ts";
import ProfilePage from "./pages/ProfilePage.tsx";

function App() {
    const isAuthenticated = useAppSelector((state) => state.auth.isAuthenticated);

    return (
        <Router>
            {isAuthenticated && <Navbar/>}
            <Routes>
                <Route path="/" element={
                    <PrivateRoute>
                        <HomePage/>
                    </PrivateRoute>
                }/>
                <Route path="/profile" element={
                    <PrivateRoute>
                        <ProfilePage/>
                    </PrivateRoute>
                }/>
                <Route path="/auth" element={<AuthPage/>}/>
            </Routes>
        </Router>
    )
}

export default App
