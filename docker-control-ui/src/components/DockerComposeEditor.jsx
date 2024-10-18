import {useState, useEffect} from 'react';
import {BACKEND_URL} from '../constants.js';
import { Controlled as CodeMirror } from 'react-codemirror2';
import 'codemirror/lib/codemirror.css'; // CodeMirror core styles
import 'codemirror/theme/material.css';  // Material theme
import 'codemirror/mode/yaml/yaml';      // YAML mode

const DockerComposeEditor = () => {
    const [dockerCompose, setDockerCompose] = useState('');

    useEffect(() => {
        fetch(BACKEND_URL + '/get-docker-compose')
            .then(res => res.json())
            .then(data => setDockerCompose(data.docker_compose));
    }, []);

    const handleUpdateDockerCompose = () => {
        fetch(BACKEND_URL + '/update-docker-compose', {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify({docker_compose: dockerCompose}),
        })
            .then(res => res.json())
            .then(data => alert(data.message));
    };

    return (
        <div className="editor-container">
            <h2>Edit Docker Compose File</h2>
            <div className="code-editor">
                <CodeMirror
                    value={dockerCompose}
                    options={{
                        mode: 'yaml',
                        lineNumbers: true,
                        lineWrapping: false,
                        theme: 'material',
                        smartIndent: true
                    }}
                    onBeforeChange={(editor, data, value) => {
                        setDockerCompose(value);
                    }}
                />
            </div>
            <button onClick={handleUpdateDockerCompose} className="update-btn">
                Update Docker Compose File
            </button>
        </div>
    );
};

export default DockerComposeEditor;
