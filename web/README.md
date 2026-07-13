# PrivateVoice Docs Web

React + TypeScript + Vite PWA. The app has no backend: PDF/text extraction, chunking, lexical retrieval, documents, and conversations stay in the browser.

```powershell
npm install
npm run dev
npm test
npm run lint
npm run build
```

Routes use the URL hash (`#/app`) so direct navigation works on static GitHub Pages. Provider keys are React state only. Do not add key persistence, analytics, remote fonts, or request caching without a privacy review.

Ollama calls `http://localhost:11434/api/chat`; OpenRouter and custom providers use the OpenAI-compatible chat-completions shape. Browser CORS and Private Network Access rules apply.
