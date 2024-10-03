import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAppSelector } from '../hooks/useAppSelector';

interface PrivateRouteProps {
    children: JSX.Element;
}

const PrivateRoute: React.FC<PrivateRouteProps> = ({ children }) => {
    const isAuthenticated = useAppSelector((state) => state.auth.isAuthenticated);

    if (!isAuthenticated) {
        // Redirect to login if not authenticated
        return <Navigate to="/auth" />;
    }

    // Otherwise, render the requested page
    return children;
};

export default PrivateRoute;
