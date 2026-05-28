import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// 从.env 文件读取环境变量
const SERVER_HOST = process.env.VITE_SERVER_HOST || 'localhost'

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
