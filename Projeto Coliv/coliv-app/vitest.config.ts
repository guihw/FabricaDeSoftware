import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    // Usa processos separados para cada arquivo de teste (fork mode).
    // Isso garante que zone.js possa inicializar corretamente seu estado
    // global (incluindo ProxyZoneSpec para fakeAsync) sem conflitos entre
    // workers que compartilham memória.
    pool: 'forks',
  },
});
