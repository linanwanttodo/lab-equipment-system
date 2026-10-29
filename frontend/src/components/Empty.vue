<script setup lang="ts">
import UIcon from './UIcon.vue'

defineProps<{ text?: string }>()
</script>

<template>
  <div class="empty">
    <UIcon name="inbox" :size="36" />
    <div>{{ text || '暂无数据' }}</div>
  </div>
</template>
