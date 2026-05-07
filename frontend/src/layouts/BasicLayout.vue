<template>
  <div id="basicLayout">
    <a-layout style="min-height: 100vh">
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout>
        <GlobalSider class="sider" />
        <a-layout-content class="content">
          <router-view />
        </a-layout-content>
      </a-layout>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalSider from '@/components/GlobalSider.vue'
</script>

<style scoped>
#basicLayout {
  background: var(--bg-color);
  min-height: 100vh;
}

#basicLayout .header {
  position: sticky;
  top: 0;
  z-index: 1000;
  padding-inline: 24px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--border-color);
  height: 64px;
  line-height: 64px;
}

#basicLayout .sider {
  background: var(--panel-bg);
  border-right: 1px solid var(--border-color);
  padding-top: 8px;
}

#basicLayout :deep(.ant-menu-root) {
  border-bottom: none !important;
  border-inline-end: none !important;
  background: transparent;
}

#basicLayout .content {
  padding: 24px;
  background: var(--bg-color);
}
</style>
