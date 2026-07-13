import { useEffect, useMemo, useRef, useState } from 'react'
import { Bot, CheckCircle2, ChevronDown, Database, Download, FilePlus2, FileText, FolderOpen, Globe2, HardDrive, Info, Laptop, LoaderCircle, LockKeyhole, MessageSquareText, PanelRightClose, Send, Settings, ShieldCheck, Trash2, Upload, X } from 'lucide-react'
import { clearLocalData, exportLocalData, listDocuments, listMessages, removeDocument, saveDocument, saveMessage } from '../data/database'
import { ACCEPTED_FILE_TYPES, importLocalDocument } from '../domain/documents'
import { askProvider } from '../domain/providers'
import { retrieve } from '../domain/retrieval'
import type { ChatMessage, LocalDocument, ProviderConfig, ProviderKind, SourceChunk } from '../domain/types'
import { Brand } from './Brand'

type View = 'chat' | 'documents' | 'models' | 'settings'
const providerDefaults: Record<ProviderKind, ProviderConfig> = {
  ollama: { kind: 'ollama', label: 'Ollama', endpoint: 'http://localhost:11434', model: 'llama3.2' },
  openrouter: { kind: 'openrouter', label: 'OpenRouter', endpoint: 'https://openrouter.ai/api/v1', model: 'openrouter/free' },
  custom: { kind: 'custom', label: 'OpenAI compatible', endpoint: 'https://api.openai.com/v1', model: 'gpt-4.1-mini' },
}

const demoText = `Urban heat mitigation works best as a portfolio. Expanding urban tree canopy and shaded corridors reduces radiant heat and improves outdoor comfort. Cool roofs and high-albedo pavement reduce absorbed solar energy. Green and blue infrastructure combines parks, vegetation, and water features. Building efficiency and waste-heat management reduce anthropogenic heat. Plans should prioritize neighborhoods with the highest heat exposure and least adaptive capacity.`

function makeDemoDocument(): LocalDocument {
  const id = crypto.randomUUID()
  const parts = [
    'Expanding urban tree canopy is the most cost-effective strategy for reducing mean radiant temperature and improving outdoor comfort in dense urban neighborhoods.',
    'Cool roofs and high-albedo pavements can reduce surface temperatures during peak summer conditions.',
    'Equitable implementation requires prioritizing heat-vulnerable communities that face the greatest exposure and have the fewest resources to adapt.',
  ]
  return { id, name: 'Urban Heat Study.txt', type: 'text/plain', size: demoText.length, createdAt: Date.now(), text: demoText, chunks: parts.map((text, ordinal) => ({ id: `${id}:${ordinal}`, documentId: id, documentName: 'Urban Heat Study.txt', text, location: `Section ${ordinal + 1}`, ordinal })) }
}

function formatBytes(bytes: number) { return bytes < 1024 ? `${bytes} B` : bytes < 1024 ** 2 ? `${(bytes / 1024).toFixed(1)} KB` : `${(bytes / 1024 ** 2).toFixed(1)} MB` }

export function WebApp() {
  const [view, setView] = useState<View>('chat')
  const [documents, setDocuments] = useState<LocalDocument[]>([])
  const [messages, setMessages] = useState<ChatMessage[]>([])
  const [sources, setSources] = useState<SourceChunk[]>([])
  const [provider, setProvider] = useState<ProviderConfig>(providerDefaults.ollama)
  const [question, setQuestion] = useState('')
  const [busy, setBusy] = useState(false)
  const [notice, setNotice] = useState('')
  const [sourcesOpen, setSourcesOpen] = useState(true)
  const inputRef = useRef<HTMLInputElement>(null)

  useEffect(() => { Promise.all([listDocuments(), listMessages()]).then(([docs, savedMessages]) => { setDocuments(docs); setMessages(savedMessages) }).catch(() => setNotice('Local storage could not be opened. Check browser privacy settings.')) }, [])
  const allChunks = useMemo(() => documents.flatMap((document) => document.chunks), [documents])
  const isLocal = provider.kind === 'ollama'

  async function addFiles(files: FileList | File[]) {
    const selected = Array.from(files).slice(0, 10)
    if (!selected.length) return
    setBusy(true); setNotice('')
    const failures: string[] = []
    for (const file of selected) {
      try {
        const document = await importLocalDocument(file)
        await saveDocument(document)
        setDocuments((current) => [...current.filter((item) => item.id !== document.id), document])
      } catch (error) { failures.push(`${file.name}: ${error instanceof Error ? error.message : 'Import failed.'}`) }
    }
    setNotice(failures.length ? failures.join(' ') : `${selected.length} document${selected.length === 1 ? '' : 's'} indexed locally.`)
    setBusy(false)
  }

  async function addDemo() {
    const document = makeDemoDocument(); await saveDocument(document); setDocuments((current) => [...current, document]); setNotice('Private demo document added locally.')
  }

  async function deleteDocument(document: LocalDocument) {
    await removeDocument(document.id); setDocuments((current) => current.filter((item) => item.id !== document.id)); setSources((current) => current.filter((item) => item.documentId !== document.id))
  }

  async function sendQuestion() {
    const trimmed = question.trim()
    if (!trimmed || busy) return
    const matched = retrieve(trimmed, allChunks)
    if (!matched.length) { setNotice('No relevant local excerpts were found, so nothing was sent to a model. Try a more specific question.'); return }
    const userMessage: ChatMessage = { id: crypto.randomUUID(), role: 'user', text: trimmed, createdAt: Date.now() }
    setMessages((current) => [...current, userMessage]); await saveMessage(userMessage); setQuestion(''); setSources(matched); setBusy(true); setNotice('')
    try {
      const text = await askProvider(provider, trimmed, matched)
      const assistant: ChatMessage = { id: crypto.randomUUID(), role: 'assistant', text, createdAt: Date.now(), sources: matched }
      setMessages((current) => [...current, assistant]); await saveMessage(assistant)
    } catch (error) {
      const message = error instanceof Error ? error.message : 'The model request failed.'
      setNotice(provider.kind === 'ollama' && /fetch|timed out|abort/i.test(message)
        ? 'Could not reach Ollama. Start Ollama, confirm the model is installed, and allow this app origin with OLLAMA_ORIGINS.'
        : message)
    }
    finally { setBusy(false) }
  }

  async function downloadExport() {
    const blob = new Blob([await exportLocalData()], { type: 'application/json' }); const url = URL.createObjectURL(blob)
    const anchor = document.createElement('a'); anchor.href = url; anchor.download = `privatevoice-docs-${new Date().toISOString().slice(0, 10)}.json`; anchor.click(); URL.revokeObjectURL(url)
    setNotice('Plaintext data export created. Keep it somewhere private.')
  }

  async function deleteEverything() {
    if (!confirm('Delete all local documents, indexes, conversations, cached app data, and the in-memory API key?')) return
    await clearLocalData(); setDocuments([]); setMessages([]); setSources([]); setProvider(providerDefaults.ollama); setNotice('All local data and provider secrets were cleared.')
  }

  function changeProvider(kind: ProviderKind) { setProvider(providerDefaults[kind]); setNotice(kind === 'ollama' ? 'Local route selected. Ollama must allow this browser origin.' : 'Network route selected. Retrieved excerpts will go directly to this provider.') }

  return <div className="web-app">
    <header className="app-titlebar"><Brand compact /><a href="#top" className="back-link">Landing page</a></header>
    <div className="app-grid">
      <nav className="app-nav" aria-label="Application">
        {([['chat', MessageSquareText, 'Chat'], ['documents', FileText, 'Documents'], ['models', Bot, 'Models'], ['settings', Settings, 'Settings']] as const).map(([id, Icon, label]) => <button key={id} className={view === id ? 'active' : ''} onClick={() => setView(id)}><Icon /><span>{label}</span></button>)}
        <div className="storage-state"><HardDrive /><span>Local storage<small><i />Connected</small></span></div>
      </nav>
      <aside className="document-rail">
        <div className="rail-heading"><h2>Documents</h2><span>{documents.length}</span></div>
        <button className="button add-doc" onClick={() => inputRef.current?.click()}><FilePlus2 />Add documents</button>
        <input ref={inputRef} hidden type="file" multiple accept={ACCEPTED_FILE_TYPES} onChange={(event) => event.target.files && addFiles(event.target.files)} />
        <small className="file-limits">PDF, TXT, MD · 25 MB each · 10 at a time</small>
        <div className="document-list">
          {documents.map((document) => <article key={document.id}><FileText /><button className="doc-name" onClick={() => setView('documents')}><strong>{document.name}</strong><small>{formatBytes(document.size)} · {document.chunks.length} sections</small><span><CheckCircle2 />Indexed</span></button><button className="icon-button danger" onClick={() => deleteDocument(document)} aria-label={`Delete ${document.name}`}><Trash2 /></button></article>)}
          {!documents.length && <button className="empty-mini" onClick={() => inputRef.current?.click()}><Upload /><strong>Add your first document</strong><span>Files never go to our server.</span></button>}
        </div>
        {!documents.length && <button className="text-button" onClick={addDemo}>Load a local demo</button>}
        <div className="rail-summary"><span>{documents.length} documents</span><span>{formatBytes(documents.reduce((sum, item) => sum + item.size, 0))}</span></div>
      </aside>
      <main className="app-main">
        {view === 'chat' && <>
          <div className="chat-header"><div><h1>Ask your documents</h1><p>Get answers grounded in your files.</p></div><button className="provider-chip" onClick={() => setView('models')}><Bot />{provider.label} · {provider.model}<ChevronDown /></button><span className={`route-state ${isLocal ? '' : 'network'}`}>{isLocal ? <LockKeyhole /> : <Globe2 />}{isLocal ? 'Local only' : 'Direct provider'}<i /></span><Info /></div>
          <div className="conversation">
            {!messages.length && <div className="chat-empty"><ShieldCheck /><h2>Private context starts here.</h2><p>Add a document, choose a model, and ask a question. Retrieval always happens on this device.</p><button className="button" onClick={() => inputRef.current?.click()}><FolderOpen />Choose documents</button></div>}
            {messages.map((message) => <article className={`message ${message.role}`} key={message.id}><div className="avatar">{message.role === 'user' ? 'You' : <ShieldCheck />}</div><div><p>{message.text}</p>{message.sources?.length ? <button className="source-link" onClick={() => { setSources(message.sources ?? []); setSourcesOpen(true) }}>{message.sources.length} grounded sources</button> : null}</div></article>)}
            {busy && <div className="thinking"><LoaderCircle />Asking {provider.label}…</div>}
          </div>
          <div className="composer"><textarea value={question} onChange={(event) => setQuestion(event.target.value)} onKeyDown={(event) => { if (event.key === 'Enter' && !event.shiftKey) { event.preventDefault(); sendQuestion() } }} placeholder="Ask a question about your documents…" aria-label="Question" /><button onClick={sendQuestion} disabled={busy || !question.trim()} aria-label="Send question"><Send /></button><small>Enter to send · Shift+Enter for a new line</small></div>
        </>}
        {view === 'documents' && <section className="panel-page"><h1>Documents</h1><p>Extraction, chunking, and retrieval stay in this browser.</p><div className="drop-zone" onDragOver={(event) => event.preventDefault()} onDrop={(event) => { event.preventDefault(); addFiles(event.dataTransfer.files) }}><FilePlus2 /><h2>Add documents</h2><p>Drag and drop files here, or choose PDF, TXT, and Markdown files.</p><button className="button" onClick={() => inputRef.current?.click()}>Browse files</button></div></section>}
        {view === 'models' && <section className="panel-page models-page"><h1>Choose a model route</h1><p>Keys are held only in memory, never IndexedDB, exports, URLs, logs, or the service worker cache.</p><div className="provider-options">{(['ollama', 'openrouter', 'custom'] as ProviderKind[]).map((kind) => <button key={kind} onClick={() => changeProvider(kind)} className={provider.kind === kind ? 'selected' : ''}>{kind === 'ollama' ? <Laptop /> : kind === 'openrouter' ? <Globe2 /> : <Database />}<span><strong>{providerDefaults[kind].label}</strong><small>{kind === 'ollama' ? 'Free and local' : kind === 'openrouter' ? 'Free routes + BYOK' : 'BYOK · CORS required'}</small></span>{provider.kind === kind && <CheckCircle2 />}</button>)}</div><label>Endpoint<input value={provider.endpoint} onChange={(event) => setProvider({ ...provider, endpoint: event.target.value })} disabled={provider.kind === 'openrouter'} /></label><label>Model<input value={provider.model} onChange={(event) => setProvider({ ...provider, model: event.target.value })} /></label>{!isLocal && <label>API key<input type="password" value={provider.apiKey ?? ''} onChange={(event) => setProvider({ ...provider, apiKey: event.target.value })} placeholder="Held until this page reloads" autoComplete="off" /></label>}<div className={`boundary-note ${isLocal ? '' : 'network'}`}>{isLocal ? <LockKeyhole /> : <Globe2 />}<span><strong>{isLocal ? 'Local route' : 'Network boundary'}</strong>{isLocal ? 'Ollama calls localhost. From the hosted app, configure OLLAMA_ORIGINS and browser Private Network Access if required.' : 'Your question and selected excerpts go directly to the configured provider. Browser CORS support is required.'}</span></div></section>}
        {view === 'settings' && <section className="panel-page settings-page"><h1>Privacy & data</h1><p>Manage the only persistent data PrivateVoice Docs stores.</p><article><Download /><span><strong>Export local data</strong><small>Downloads plaintext document text and conversations. API keys are excluded.</small></span><button className="button secondary" onClick={downloadExport}>Export JSON</button></article><article className="danger-zone"><Trash2 /><span><strong>Delete everything</strong><small>Clears documents, indexes, conversations, app caches, and the current in-memory key.</small></span><button className="button danger-button" onClick={deleteEverything}>Delete local data</button></article><article><ShieldCheck /><span><strong>No project backend</strong><small>This static web app has no account, analytics, ads, upload server, or API-key proxy.</small></span></article></section>}
        {notice && <div className="notice" role="status"><span>{notice}</span><button onClick={() => setNotice('')} aria-label="Dismiss"><X /></button></div>}
      </main>
      {view === 'chat' && sourcesOpen && <aside className="sources-panel"><div><h2>Sources</h2><button className="icon-button" onClick={() => setSourcesOpen(false)} aria-label="Close sources"><PanelRightClose /></button></div><p>{sources.length ? `${sources.length} sources used` : 'Citations appear here.'}</p>{sources.map((source, index) => <article key={source.id}><header><span>{index + 1}</span><strong>{source.documentName}</strong><small>{source.location}</small></header><p>“{source.text}”</p></article>)}{!sources.length && <div className="sources-empty"><FileText /><span>Ask a grounded question to inspect the exact excerpts sent to your model.</span></div>}</aside>}
    </div>
  </div>
}
