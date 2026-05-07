<template>
  <div id="homePage">
    <!-- Category Filter Bar -->
    <div class="category-filter-wrapper">
      <a-tabs v-model:active-key="selectedCategory" @change="doSearch" class="category-tabs">
        <a-tab-pane key="all" tab="All" />
        <a-tab-pane v-for="category in categoryList" :tab="category" :key="category" />
      </a-tabs>
    </div>

    <!-- Tag Filter Bar -->
    <div class="tag-filter-wrapper">
      <a-space :size="[0, 8]" wrap>
        <div class="filter-icon"><FilterOutlined /></div>
        <a-checkable-tag
          v-for="(tag, index) in tagList"
          :key="tag"
          v-model:checked="selectedTagList[index]"
          @change="doSearch"
        >
          {{ tag }}
        </a-checkable-tag>
      </a-space>
    </div>

    <!-- Picture list -->
    <div class="content-container">
      <PictureList :dataList="dataList" :loading="loading" />
    </div>

    <!-- Pagination -->
    <div class="pagination-wrapper">
      <a-pagination
        v-if="total > 0"
        v-model:current="searchParams.current"
        v-model:pageSize="searchParams.pageSize"
        :total="total"
        :show-total="(t: number) => `${t} items`"
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { FilterOutlined } from '@ant-design/icons-vue'
import { useRoute } from 'vue-router'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import PictureList from '@/components/pictureRelated/PictureList.vue'

const route = useRoute()

// Data
const dataList = ref<API.PictureVO[]>([])
const total = ref(0)
const loading = ref(true)

// Search parameters
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
  searchText: '',
})

// Sync search text with URL
watch(() => route.query.searchText, (newVal) => {
  searchParams.searchText = (newVal as string) || ''
  doSearch()
})

// Fetch data
const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      ...searchParams,
      tags: [] as string[],
    }
    if (selectedCategory.value !== 'all') {
      params.category = selectedCategory.value
    }
    selectedTagList.value.forEach((useTag, index) => {
      if (useTag) {
        params.tags.push(tagList.value[index])
      }
    })
    const res = await listPictureVoByPageUsingPost(params)
    if (res.data.code === 0 && res.data.data) {
      dataList.value = res.data.data.records ?? []
      total.value = res.data.data.total ?? 0
    } else {
      message.error('Data loading failed: ' + res.data.message)
    }
  } catch (err: any) {
    message.error('System Error: Database connection restored, please refresh.')
  } finally {
    loading.value = false
  }
}

// Fetch data on mount
onMounted(() => {
  if (route.query.searchText) {
    searchParams.searchText = route.query.searchText as string
  }
  fetchData()
})

// Pagination
const onPageChange = (page: number, pageSize: number) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  fetchData()
}

// Search
const doSearch = () => {
  searchParams.current = 1
  fetchData()
}

// Tag and category lists
const categoryList = ref<string[]>([])
const selectedCategory = ref<string>('all')
const tagList = ref<string[]>([])
const selectedTagList = ref<boolean[]>([])

const getTagCategoryOptions = async () => {
  try {
    const res = await listPictureTagCategoryUsingGet()
    if (res.data.code === 0 && res.data.data) {
      tagList.value = res.data.data.tagList ?? []
      categoryList.value = res.data.data.categoryList ?? []
      // Initialize tag selection state
      selectedTagList.value = new Array(tagList.value.length).fill(false)
    }
  } catch (e) {
    console.error('Failed to load filters', e)
  }
}

onMounted(() => {
  getTagCategoryOptions()
})
</script>

<style scoped>
#homePage {
  max-width: 1440px;
  margin: 0 auto;
}

.category-filter-wrapper {
  margin-bottom: 24px;
}

:deep(.category-tabs) {
  .ant-tabs-nav {
    margin-bottom: 0;
    &::before {
      display: none;
    }
  }
  .ant-tabs-tab {
    padding: 12px 0;
    margin: 0 20px 0 0;
    font-size: 15px;
    font-weight: 500;
    color: var(--text-secondary);
  }
  .ant-tabs-tab-active {
    .ant-tabs-tab-btn {
      color: var(--text-primary) !important;
    }
  }
  .ant-tabs-ink-bar {
    background: var(--text-primary);
    height: 2px;
  }
}

.tag-filter-wrapper {
  margin-bottom: 32px;
  padding: 8px 0;
  display: flex;
  align-items: center;
}

.filter-icon {
  font-size: 14px;
  color: var(--text-secondary);
  margin-right: 8px;
  display: flex;
  align-items: center;
}

:deep(.ant-tag-checkable) {
  background: transparent;
  border: 1px solid var(--border-color);
  color: var(--text-secondary);
  border-radius: 6px;
  padding: 4px 12px;
  font-size: 13px;
  transition: all 0.2s;

  &:hover {
    border-color: var(--text-primary);
    color: var(--text-primary);
  }

  &.ant-tag-checkable-checked {
    background: var(--text-primary) !important;
    border-color: var(--text-primary) !important;
    color: white !important;
  }
}

.content-container {
  min-height: 400px;
}

.empty-placeholder {
  padding: 100px 0;
  display: flex;
  justify-content: center;
}

.pagination-wrapper {
  margin-top: 48px;
  display: flex;
  justify-content: center;
}
</style>
