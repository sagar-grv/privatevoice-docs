import { describe, expect, it } from 'vitest'
import { chunkText } from './chunking'

describe('chunkText', () => {
  it('normalizes text and produces overlapping bounded chunks', () => {
    const chunks = chunkText('doc', 'Study.txt', Array.from({ length: 240 }, (_, index) => `word${index}`).join(' '), 100)
    expect(chunks).toHaveLength(3)
    expect(chunks[0].location).toBe('Section 1')
    expect(chunks[0].text.split(' ')).toHaveLength(100)
    expect(chunks[1].text).toContain('word80')
  })

  it('rejects whitespace-only documents', () => expect(chunkText('x', 'empty.txt', ' \n ')).toEqual([]))
})
