<template>
  <div id="app">
    <a-config-provider
      :theme="{
        token: {
          colorPrimary: '#64748b',
          borderRadius: 4,
          colorLink: '#64748b',
          colorBgLayout: '#f8fafc',
          fontFamily: 'Inter, -apple-system, sans-serif'
        },
      }"
    >
      <BasicLayout />
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import BasicLayout from '@/layouts/BasicLayout.vue'
</script>

<style>
#app {
  height: 100vh;
}
</style>
