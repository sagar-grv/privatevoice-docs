import { useEffect, useState } from 'react'
import { LandingPage } from './components/LandingPage'
import { WebApp } from './components/WebApp'

function route() { return window.location.hash.startsWith('#/app') ? 'app' : 'landing' }

export default function App() {
  const [page, setPage] = useState(route)
  useEffect(() => { const onHash = () => setPage(route()); addEventListener('hashchange', onHash); return () => removeEventListener('hashchange', onHash) }, [])
  return page === 'app' ? <WebApp /> : <LandingPage />
}
