import { useState, useEffect, useCallback } from "react";
import { BACKEND_URL } from "../constants.js";

const EnvEditor = () => {
    const [envVars, setEnvVars] = useState({});
    const [newKey, setNewKey] = useState("");
    const [newValue, setNewValue] = useState("");

    // Fetch environment variables on mount
    useEffect(() => {
        fetch(BACKEND_URL + "/get-env")
            .then((res) => res.json())
            .then((data) => setEnvVars(data.env_vars));
    }, []);

    // Update .env file
    const handleUpdateEnv = () => {
        fetch(BACKEND_URL + "/update-env", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ env_vars: envVars }),
        })
            .then((res) => res.json())
            .then((data) => alert(data.message));
    };

    // Add new variable
    const handleAddEnv = () => {
        if (newKey && newValue) {
            setEnvVars((prevVars) => ({
                ...prevVars,
                [newKey]: newValue.includes(",") ? newValue.split(",") : newValue,
            }));
            setNewKey("");
            setNewValue("");
        }
    };

    // Update existing variable
    const handleInputChange = useCallback((key, value) => {
        setEnvVars((prevVars) => ({
            ...prevVars,
            [key]: value.includes(",") ? value.split(",") : value,
        }));
    }, []);

    // Delete a variable
    const handleDelete = useCallback((keyToDelete) => {
        setEnvVars((prevVars) => {
            const updatedVars = { ...prevVars };
            delete updatedVars[keyToDelete];
            return updatedVars;
        });
    }, []);

    return (
        <div className="editor-container">
            <h2>Edit Environment Variables</h2>
            <table>
                <thead>
                <tr>
                    <th>Key</th>
                    <th>Value</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {Object.entries(envVars).map(([key, value]) => (
                    <tr key={key}>
                        <td>{key}</td>
                        <td>
                            <input
                                type="text"
                                value={Array.isArray(value) ? value.join(",") : value}
                                onChange={(e) => handleInputChange(key, e.target.value)}
                            />
                        </td>
                        <td>
                            <button onClick={() => handleDelete(key)} className="delete-btn">
                                Delete
                            </button>
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            <div className="add-env-section">
                <h3>Add New Variable</h3>
                <div className="add-env-form">
                    <div className="input-group">
                        <label htmlFor="newKey">Key</label>
                        <input
                            id="newKey"
                            type="text"
                            placeholder="Enter key"
                            value={newKey}
                            onChange={(e) => setNewKey(e.target.value)}
                        />
                    </div>
                    <div className="input-group">
                        <label htmlFor="newValue">Value</label>
                        <input
                            id="newValue"
                            type="text"
                            placeholder="Enter value (comma-separated for lists)"
                            value={newValue}
                            onChange={(e) => setNewValue(e.target.value)}
                        />
                    </div>
                    <button onClick={handleAddEnv} className="add-btn">
                        Add Variable
                    </button>
                </div>
            </div>

            <button onClick={handleUpdateEnv} className="update-btn">
                Update .env File
            </button>
        </div>
    );
};

export default EnvEditor;
