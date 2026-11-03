/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        radar: {
          dark: '#0B0F19',
          card: '#111827',
          surface: '#1F2937',
          border: '#374151',
          cyan: '#06B6D4',
          cyanGlow: '#22D3EE',
          amber: '#F59E0B',
          emerald: '#10B981',
          purple: '#A855F7',
          rose: '#F43F5E'
        }
      }
    },
  },
  plugins: [],
}
