import type { SourceChunk } from './types'

const NORMALIZE_SPACE = /\s+/g

export function chunkText(documentId: string, documentName: string, rawText: string, maxWords = 180): SourceChunk[] {
  const text = rawText.replace(NORMALIZE_SPACE, ' ').trim()
  if (!text) return []
  const words = text.split(' ')
  const overlap = Math.min(30, Math.floor(maxWords / 5))
  const chunks: SourceChunk[] = []
  for (let start = 0, ordinal = 0; start < words.length; start += maxWords - overlap, ordinal += 1) {
    const content = words.slice(start, start + maxWords).join(' ')
    chunks.push({
      id: `${documentId}:${ordinal}`,
      documentId,
      documentName,
      text: content,
      location: `Section ${ordinal + 1}`,
      ordinal,
    })
    if (start + maxWords >= words.length) break
  }
  return chunks
}
