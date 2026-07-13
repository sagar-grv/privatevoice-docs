import { deleteDB, openDB, type DBSchema } from 'idb'
import type { ChatMessage, LocalDocument } from '../domain/types'

interface PrivateVoiceDb extends DBSchema {
  documents: { key: string; value: LocalDocument }
  messages: { key: string; value: ChatMessage; indexes: { 'by-created': number } }
}

const DB_NAME = 'privatevoice-docs'

async function database() {
  return openDB<PrivateVoiceDb>(DB_NAME, 1, {
    upgrade(db) {
      db.createObjectStore('documents', { keyPath: 'id' })
      const messages = db.createObjectStore('messages', { keyPath: 'id' })
      messages.createIndex('by-created', 'createdAt')
    },
  })
}

export async function listDocuments() { return (await database()).getAll('documents') }
export async function saveDocument(document: LocalDocument) { await (await database()).put('documents', document) }
export async function removeDocument(id: string) { await (await database()).delete('documents', id) }
export async function listMessages() { return (await database()).getAllFromIndex('messages', 'by-created') }
export async function saveMessage(message: ChatMessage) { await (await database()).put('messages', message) }

export async function exportLocalData() {
  return JSON.stringify({
    schema: 1,
    exportedAt: new Date().toISOString(),
    warning: 'This plaintext export contains document text and conversations. It never contains provider API keys.',
    documents: await listDocuments(),
    messages: await listMessages(),
  }, null, 2)
}

export async function clearLocalData() {
  await deleteDB(DB_NAME)
  if ('caches' in globalThis) {
    const names = await caches.keys()
    await Promise.all(names.filter((name) => name.includes('privatevoice') || name.includes('workbox')).map((name) => caches.delete(name)))
  }
}
