import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'
import viteCompression from 'vite-plugin-compression'

// 统一使用固定 IP: 192.168.56.180
const SERVER_HOST = '192.168.56.180'

export default defineConfig({
  plugins: [
    vue(),
    // Gzip 压缩
    viteCompression({
      algorithm: 'gzip',
      ext: '.gz',
      threshold: 10240,
      deleteOriginFile: false
    }),
    // Brotli 压缩
    viteCompression({
      algorithm: 'brotliCompress',
      ext: '.br',
      threshold: 10240,
      deleteOriginFile: false
    })
  ],
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
  },
  build: {
    // 目标浏览器
    target: 'es2015',
    // 代码分割大小限制
    chunkSizeWarningLimit: 500,
    // 滚动配置
    rollupOptions: {
      output: {
        // 手动拆分代码块
        manualChunks: {
          'vue-vendor': ['vue', 'vue-router', 'pinia'],
          'element-plus': ['element-plus'],
          'axios': ['axios']
        },
        // 资源文件命名
        chunkFileNames: 'static/js/[name]-[hash].js',
        entryFileNames: 'static/js/[name]-[hash].js',
        assetFileNames: 'static/[ext]/[name]-[hash].[ext]'
      }
    },
    // 压缩选项
    minify: 'terser',
    terserOptions: {
      compress: {
        // 生产环境移除 console.log
        drop_console: true,
        drop_debugger: true
      }
    }
  },
  // 预加载
  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'element-plus', 'axios']
  }
})
