/* Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ */
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
export default defineConfig({plugins:[vue()],server:{proxy:{'/api':process.env.VITE_API_PROXY||'http://localhost:8080'}}})
