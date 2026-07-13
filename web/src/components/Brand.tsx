import { ShieldCheck } from 'lucide-react'

export function Brand({ compact = false }: { compact?: boolean }) {
  return <a className="brand" href="#top" aria-label="PrivateVoice Docs home"><span className="brand-mark"><ShieldCheck size={compact ? 18 : 21} /></span><span>PrivateVoice Docs</span></a>
}
