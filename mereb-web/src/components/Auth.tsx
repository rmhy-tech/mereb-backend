import {FormEvent, useEffect, useState} from 'react';
import { useNavigate } from 'react-router-dom';
import { useAppDispatch } from "../hooks/useAppDispatch.ts";
import { useAppSelector } from '../hooks/useAppSelector';
import { login, register } from '../features/auth/authSlice';
import '../styles/Auth.scss';

const Auth = () => {
    const [firstName, setFirstName] = useState('');
    const [lastName, setLastName] = useState('');
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');

    const [isLogin, setIsLogin] = useState(true);

    const dispatch = useAppDispatch();
    const auth = useAppSelector((state) => state.auth);
    const navigate = useNavigate();

    useEffect(() => {
        if (auth.isAuthenticated) {
            navigate('/');
        }
    }, [auth.isAuthenticated, navigate]);

    const handleSubmit = (e: FormEvent) => {
        e.preventDefault();
        if(isLogin) {
            const credentials = {username, password};
            dispatch(login(credentials));
        } else {
            const role = 'USER';
            const credentials = {firstName,lastName, email, username, password, role};
            dispatch(register(credentials));
        }
    }

    return (
        <div className="auth-container">
            <div className="auth-box">
                <h2>{isLogin ? 'Login' : 'Register'}</h2>
                {auth.error && <p>{auth.error}</p>}
                <form onSubmit={handleSubmit} className="auth-form">
                    {!isLogin && (<>
                        <input
                            type="firstname"
                            placeholder="First Name"
                            value={firstName}
                            onChange={(e) => setFirstName(e.target.value)}
                        />
                        <input
                            type="lastname"
                            placeholder="Last Name"
                            value={lastName}
                            onChange={(e) => setLastName(e.target.value)}
                        />
                        <input
                            type="email"
                            placeholder="Email"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                        />
                    </>)}
                    <input
                        type="username"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />
                    <button type="submit">{isLogin ? 'Login' : 'Register'}</button>
                </form>
                <button className="toggle-button" onClick={() => setIsLogin(!isLogin)}>
                    {isLogin ? 'Need an account? Register' : 'Have an account? Login'}
                </button>
            </div>
        </div>
    );
}

export default Auth;