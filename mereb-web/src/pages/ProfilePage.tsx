import { useEffect } from "react";
import { useAppDispatch } from "../hooks/useAppDispatch.ts";
import { useAppSelector } from "../hooks/useAppSelector.ts";
import { getUser } from "../features/auth/authSlice.ts";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faEnvelope, faUserTag, faIdBadge } from "@fortawesome/free-solid-svg-icons";
import "../styles/profile.scss";

export default function ProfilePage() {

    const dispatch = useAppDispatch();
    const currentUser = useAppSelector((state) => state.auth.currentUser);

    useEffect(() => {
        dispatch(getUser());
    }, [dispatch]);

    return (
        <div className="profile-container">
            <div className="profile-card">
                <h1>Profile</h1>
                {currentUser && (
                    <div className="profile-details">
                        <div className="detail-item">
                            <FontAwesomeIcon icon={faUser} className="icon"/>
                            <p><strong>First Name:</strong> {currentUser.firstName}</p>
                        </div>
                        <div className="detail-item">
                            <FontAwesomeIcon icon={faUser} className="icon"/>
                            <p><strong>Last Name:</strong> {currentUser.lastName}</p>
                        </div>
                        <div className="detail-item">
                            <FontAwesomeIcon icon={faUserTag} className="icon"/>
                            <p><strong>Username:</strong> {currentUser.username}</p>
                        </div>
                        <div className="detail-item">
                            <FontAwesomeIcon icon={faEnvelope} className="icon"/>
                            <p><strong>Email:</strong> {currentUser.email}</p>
                        </div>
                        <div className="detail-item">
                            <FontAwesomeIcon icon={faIdBadge} className="icon"/>
                            <p><strong>Role:</strong> {currentUser.role}</p>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}
