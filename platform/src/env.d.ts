/// <reference types="vite/client" />

declare module '*.vue' {
  import type { DefineComponent } from 'vue'
  const component: DefineComponent<{}, {}, any>
  export default component
}

declare module 'element-plus' {
  import type { App } from 'vue'
  const ElementPlus: {
    install: (app: App) => void
  }
  export default ElementPlus
  export const ElMessage: any
  export const ElMessageBox: any
}
