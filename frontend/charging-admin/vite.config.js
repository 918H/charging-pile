import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// 统一使用固定 IP: 192.168.56.180
const SERVER_HOST = '192.168.56.180'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 3000,
    host: '0.0.0.0',
    proxy: {
      '/api/user': {
        target: `http://${SERVER_HOST}:8081`,
        changeOrigin: true
      },
      '/api/charging': {
        target: `http://${SERVER_HOST}:8082`,
        changeOrigin: true
      },
      '/api/order': {
        target: `http://${SERVER_HOST}:8083`,
        changeOrigin: true
      },
      '/api/payment': {
        target: `http://${SERVER_HOST}:8084`,
        changeOrigin: true
      },
      '/api/coupon': {
        target: `http://${SERVER_HOST}:8085`,
        changeOrigin: true
      },
      '/api/common': {
        target: `http://${SERVER_HOST}:8086`,
        changeOrigin: true
      }
    }
  }
})
