import {
    createBrowserRouter,
    RouterProvider
} from "react-router-dom";
import EnvEditor from './components/EnvEditor';
import DockerComposeEditor from './components/DockerComposeEditor';
import ScriptRunner from './components/ScriptRunner';
import ErrorPage from "./components/ErrorPage.jsx";
import './App.css';

const router = createBrowserRouter([
    {
        path: "/",
        element: <div><h2>Welcome to Docker Control UI</h2></div>,
        errorElement: <ErrorPage />
    },
    {
        path: "/env-editor",
        element: <EnvEditor />
    },
    {
        path: "/docker-compose-editor",
        element: <DockerComposeEditor />
    },
    {
        path: "/script-runner",
        element: <ScriptRunner />
    }
]);

function App() {
    return (
        <div className="app">
            <h1>Docker Control UI</h1>
            <nav>
                <a href="/env-editor">Edit .env</a> |
                <a href="/docker-compose-editor">Edit Docker Compose</a> |
                <a href="/script-runner">Run Script</a>
            </nav>
            <RouterProvider router={router} />
        </div>
    );
}

export default App;
