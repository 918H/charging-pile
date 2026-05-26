import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 3000,
    host: true,
    proxy: {
      '/api/user': {
        target: 'http://localhost:8081',
        changeOrigin: true
      },
      '/api/charging': {
        target: 'http://localhost:8082',
        changeOrigin: true
      },
      '/api/order': {
        target: 'http://localhost:8083',
        changeOrigin: true
      },
      '/api/payment': {
        target: 'http://localhost:8084',
        changeOrigin: true
      },
      '/api/common': {
        target: 'http://localhost:8085',
        changeOrigin: true
      }
    }
  }
})
