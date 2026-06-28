import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'br.com.coliv.app',
  appName: 'Coliv',
  webDir: 'dist/coliv-app/browser',
  server: {
    androidScheme: 'http' // FORÇA O APP A RODAR EM http://localhost NO ANDROID
  },
  android: {
    allowMixedContent: true
  }
};

export default config;