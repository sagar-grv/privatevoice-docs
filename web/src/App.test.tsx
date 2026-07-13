import { render, screen } from '@testing-library/react'
import { afterEach, describe, expect, it } from 'vitest'
import App from './App'

describe('routing', () => {
  afterEach(() => { window.location.hash = '' })
  it('shows the product offer on the landing route', () => {
    render(<App />)
    expect(screen.getByRole('heading', { name: /your documents/i })).toBeInTheDocument()
    expect(screen.getByRole('link', { name: /open web app/i })).toHaveAttribute('href', '#/app')
  })
  it('opens the local document workspace through the hash route', () => {
    window.location.hash = '#/app'
    render(<App />)
    expect(screen.getByRole('heading', { name: 'Ask your documents' })).toBeInTheDocument()
    expect(screen.getByText('Files never go to our server.')).toBeInTheDocument()
  })
})
