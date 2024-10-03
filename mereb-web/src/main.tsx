import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import { Provider } from 'react-redux';
import { store } from './app/store'
import App from './App.tsx'
// import './index.css'

import * as Sentry from "@sentry/react";

Sentry.init({
    dsn: "https://b971c34776fafaa5706dae9abfe9e94c@o1098442.ingest.us.sentry.io/4508055554228224",
    integrations: [
        Sentry.browserTracingIntegration(),
        Sentry.replayIntegration(),
    ],
    // Tracing
    tracesSampleRate: 1.0, //  Capture 100% of the transactions
    // Set 'tracePropagationTargets' to control for which URLs distributed tracing should be enabled
    tracePropagationTargets: ["localhost", /^https:\/\/yourserver\.io\/api/],
    // Session Replay
    replaysSessionSampleRate: 0.1, // This sets the sample rate at 10%. You may want to change it to 100% while in development and then sample at a lower rate in production.
    replaysOnErrorSampleRate: 1.0, // If you're not already sampling the entire session, change the sample rate to 100% when sampling sessions where errors occur.
});

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <Provider store={store}>
            <App/>
        </Provider>
    </StrictMode>
)
