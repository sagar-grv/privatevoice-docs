export type ProviderKind = 'ollama' | 'openrouter' | 'custom'

export interface SourceChunk {
  id: string
  documentId: string
  documentName: string
  text: string
  location: string
  ordinal: number
}

export interface LocalDocument {
  id: string
  name: string
  type: string
  size: number
  createdAt: number
  text: string
  chunks: SourceChunk[]
}

export interface ChatMessage {
  id: string
  role: 'user' | 'assistant'
  text: string
  createdAt: number
  sources?: SourceChunk[]
}

export interface ProviderConfig {
  kind: ProviderKind
  label: string
  endpoint: string
  model: string
  apiKey?: string
}
