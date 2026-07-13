import { chunkText } from './chunking'
import type { LocalDocument, SourceChunk } from './types'

export const MAX_FILE_BYTES = 25 * 1024 * 1024
export const MAX_PDF_PAGES = 400
export const ACCEPTED_FILE_TYPES = '.pdf,.txt,.md,text/plain,text/markdown,application/pdf'

function id() { return crypto.randomUUID() }

async function extractPdf(file: File): Promise<Array<{ text: string; location: string }>> {
  const pdfjs = await import('pdfjs-dist')
  pdfjs.GlobalWorkerOptions.workerSrc = new URL('pdfjs-dist/build/pdf.worker.min.mjs', import.meta.url).toString()
  const bytes = new Uint8Array(await file.arrayBuffer())
  const loadingTask = pdfjs.getDocument({ data: bytes })
  try {
    const pdf = await loadingTask.promise
    if (pdf.numPages > MAX_PDF_PAGES) throw new Error(`PDFs are limited to ${MAX_PDF_PAGES} pages.`)
    const pages = []
    for (let pageNumber = 1; pageNumber <= pdf.numPages; pageNumber += 1) {
      const page = await pdf.getPage(pageNumber)
      const content = await page.getTextContent()
      pages.push({ text: content.items.map((item) => 'str' in item ? item.str : '').join(' '), location: `Page ${pageNumber}` })
      page.cleanup()
    }
    return pages
  } finally {
    await loadingTask.destroy()
  }
}

export async function importLocalDocument(file: File): Promise<LocalDocument> {
  if (file.size > MAX_FILE_BYTES) throw new Error('Files are limited to 25 MB in the browser app.')
  const extension = file.name.split('.').pop()?.toLowerCase()
  if (!['pdf', 'txt', 'md'].includes(extension ?? '')) throw new Error('Use a PDF, TXT, or Markdown file.')
  const documentId = id()
  let text = ''
  let chunks: SourceChunk[] = []
  if (extension === 'pdf') {
    const pages = await extractPdf(file)
    text = pages.map((page) => page.text).join('\n\n')
    pages.forEach((page) => {
      chunkText(documentId, file.name, page.text).forEach((chunk) => chunks.push({ ...chunk, id: `${documentId}:${chunks.length}`, ordinal: chunks.length, location: page.location }))
    })
  } else {
    text = await file.text()
    chunks = chunkText(documentId, file.name, text)
  }
  if (!text.trim() || chunks.length === 0) throw new Error('No readable text was found in this file. Scanned PDFs need OCR in the Android app.')
  return { id: documentId, name: file.name.slice(0, 180), type: file.type || `text/${extension}`, size: file.size, createdAt: Date.now(), text, chunks }
}
