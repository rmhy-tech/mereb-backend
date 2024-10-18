import {BACKEND_URL} from '../constants.js';

const ScriptRunner = () => {

    const handleRunScript = () => {
        fetch(BACKEND_URL + '/run-script', {method: 'POST'})
            .then(res => res.json())
            .then(data => alert(data.message));
    };

    return (
        <div className="runner-container">
            <h2>Control Script</h2>
            <button onClick={handleRunScript}>Run Script</button>
        </div>
    );
};

export default ScriptRunner;
