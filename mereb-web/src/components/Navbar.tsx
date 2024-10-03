// src/components/Navbar.tsx
import React from 'react';
import {useDispatch} from 'react-redux';
import {logout} from '../features/auth/authSlice';
import {useNavigate} from 'react-router-dom';
import '../styles/NavBar.scss';
import {useAppSelector} from "../hooks/useAppSelector.ts";
import merebLogo from "../assets/logo.png";

const Navbar: React.FC = () => {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const {user} = useAppSelector((state) => state.auth); // Get the username from state
    const isAuthenticated = useAppSelector((state) => state.auth.isAuthenticated);

    const handleLogout = () => {
        dispatch(logout());
        navigate('/auth');
    };

    return (
        <nav className="navbar">
            <ul>
                {isAuthenticated && (
                    <>

                        <li><a href="/"><img
                            src={merebLogo} alt="Mereb Logo"
                            width={50} height={50}
                        /></a></li>
                        <li><span>Welcome, <a href="/profile">{user}</a></span></li>
                        {/* Display the username */}
                        <li>
                            <button onClick={handleLogout}>Logout</button>
                        </li>
                    </>
                )}
            </ul>
        </nav>
    );
};

export default Navbar;
