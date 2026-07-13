import type { SourceChunk } from './types'

const TOKEN = /[\p{L}\p{N}]{2,}/gu
const STOP = new Set(['the', 'and', 'for', 'that', 'with', 'from', 'this', 'what', 'are', 'was', 'were', 'your', 'about'])

export function tokenize(text: string): string[] {
  return (text.toLowerCase().match(TOKEN) ?? []).filter((word) => !STOP.has(word))
}

export function retrieve(query: string, chunks: SourceChunk[], limit = 4): SourceChunk[] {
  const terms = new Set(tokenize(query))
  if (!terms.size) return []
  return chunks
    .map((chunk) => {
      const words = tokenize(chunk.text)
      const counts = new Map<string, number>()
      words.forEach((word) => counts.set(word, (counts.get(word) ?? 0) + 1))
      let score = 0
      terms.forEach((term) => {
        const frequency = counts.get(term) ?? 0
        score += frequency ? 1 + Math.log(frequency) : 0
      })
      return { chunk, score: score / Math.sqrt(Math.max(words.length, 1)) }
    })
    .filter(({ score }) => score > 0)
    .sort((a, b) => b.score - a.score || a.chunk.ordinal - b.chunk.ordinal)
    .slice(0, limit)
    .map(({ chunk }) => chunk)
}
