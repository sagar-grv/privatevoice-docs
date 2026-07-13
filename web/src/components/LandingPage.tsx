import { ArrowRight, Bot, Check, Cpu, FileText, Globe2, HardDrive, Laptop, LockKeyhole, MessageSquareText, ShieldCheck, Smartphone } from 'lucide-react'
import { Brand } from './Brand'

const appHref = '#/app'

function ProductPreview() {
  return <div className="product-preview" aria-label="PrivateVoice Docs product preview">
    <div className="preview-top"><Brand compact /><span>Ollama · llama3.2</span></div>
    <div className="preview-body">
      <aside><strong>Documents</strong><span className="selected"><FileText size={14} />Urban Heat Study.pdf</span><span><FileText size={14} />Climate Policy.txt</span><span><FileText size={14} />Research Notes.md</span><small><i />Stored on this device</small></aside>
      <main><p className="question">What interventions reduce urban heat?</p><div className="answer"><strong>Three approaches appear consistently:</strong><ol><li>Expand tree canopy and shade. <sup>1</sup></li><li>Use cool roofs and surfaces. <sup>2</sup></li><li>Prioritize vulnerable neighborhoods. <sup>3</sup></li></ol></div><div className="preview-composer">Ask about your documents <ArrowRight size={15} /></div></main>
    </div>
  </div>
}

export function LandingPage() {
  return <div className="landing" id="top">
    <header className="site-header"><Brand /><nav><a href="#workflow">How it works</a><a href="#privacy">Privacy</a><a href="#models">Models</a><a className="button small" href={appHref}>Open app</a></nav></header>
    <main>
      <section className="hero"><div className="hero-copy"><h1>Your documents.<br />Your models.<br />Your device.</h1><p>Turn private files into useful answers with citations—locally when you can, through your own provider when you choose.</p><div className="hero-actions"><a className="button" href={appHref}><Globe2 size={17} />Open web app</a><a className="button secondary" href="https://github.com/sagar-grv/privatevoice-docs/releases"><Smartphone size={17} />Get Android app</a></div></div><ProductPreview /></section>
      <section className="workflow section" id="workflow"><h2>How it works</h2><div className="steps"><article><FileText /><b>1</b><h3>Add documents</h3><p>Import PDF, TXT, or Markdown. Extraction and indexing happen in your browser.</p></article><ArrowRight className="step-arrow" /><article><Cpu /><b>2</b><h3>Choose a model</h3><p>Run Ollama locally or connect directly to a provider you trust.</p></article><ArrowRight className="step-arrow" /><article><MessageSquareText /><b>3</b><h3>Ask with sources</h3><p>Get answers grounded in selected excerpts with visible citations.</p></article></div></section>
      <section className="privacy-band" id="privacy"><div><h2>Privacy by architecture,<br />not by policy.</h2><p>Your documents and index stay in your browser. Provider calls are optional, direct, and clearly marked.</p></div><div className="privacy-map"><article><Laptop /><span><strong>Your device</strong><small>Documents and index stay local</small></span></article><div className="route"><span><LockKeyhole />Local route</span><span className="optional"><ShieldCheck />Optional direct provider route</span></div><article className="provider"><Bot /><span><strong>Your chosen provider</strong><small>Only retrieved excerpts are sent</small></span></article></div></section>
      <section className="models section" id="models"><h2>Use the models you trust</h2><p>Switch routes any time. PrivateVoice Docs never supplies or proxies your key.</p><div className="model-row"><article><Laptop /><div><h3>Ollama on your laptop</h3><p>Run open models locally. Browser access may require Ollama origin configuration.</p></div></article><article><Globe2 /><div><h3>OpenRouter free models</h3><p>Use models with the <code>:free</code> route through your own account.</p></div></article><article><Cpu /><div><h3>OpenAI-compatible API</h3><p>Connect endpoints that explicitly allow requests from your browser origin.</p></div></article></div></section>
      <section className="focus section"><div><h2>A workspace<br />built for focus</h2><p>Chat, inspect sources, and keep context without losing the privacy boundary.</p><ul><li><Check />Citations with source excerpts</li><li><Check />Local lexical retrieval</li><li><Check />Keyboard-first composer</li><li><Check />Offline shell with local models</li></ul></div><ProductPreview /></section>
      <section className="install section"><h2>Install anywhere</h2><div><article><Smartphone /><span><h3>Android app</h3><p>Native app-private document storage with no Internet permission.</p></span></article><article><HardDrive /><span><h3>Installable web app</h3><p>Use it in a browser or install the PWA on your laptop.</p></span></article></div><aside><ShieldCheck /><strong>Your documents. Your choice. Privacy by design.</strong><a className="button" href={appHref}>Open PrivateVoice Docs</a></aside></section>
    </main>
    <footer><Brand compact /><div><a href="#privacy">Privacy</a><a href="https://github.com/sagar-grv/privatevoice-docs">GitHub</a><a href="https://github.com/sagar-grv/privatevoice-docs/blob/main/SECURITY.md">Security</a></div></footer>
  </div>
}
