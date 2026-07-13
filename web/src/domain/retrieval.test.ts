import { describe, expect, it } from 'vitest'
import { retrieve } from './retrieval'
import type { SourceChunk } from './types'

const chunks: SourceChunk[] = [
  { id: 'a', documentId: 'd', documentName: 'Heat.pdf', text: 'Urban tree canopy reduces heat and provides shade.', location: 'Page 2', ordinal: 0 },
  { id: 'b', documentId: 'd', documentName: 'Heat.pdf', text: 'Public transit changes commuting behavior.', location: 'Page 8', ordinal: 1 },
]

describe('retrieve', () => {
  it('ranks matching source chunks deterministically', () => expect(retrieve('How does tree shade reduce heat?', chunks)[0].id).toBe('a'))
  it('returns no source for an unrelated question', () => expect(retrieve('quantum entanglement', chunks)).toEqual([]))
})
