<template>
  <div id="pictureManagePage">
    <a-flex justify="space-between">
      <h2>Picture Management</h2>
      <a-space>
        <a-button type="primary" href="/projects/image-workspace/add_picture" target="_blank">
          + Create
        </a-button>
        <a-button type="primary" href="/projects/image-workspace/add_picture/batch" target="_blank" ghost>
          + Batch Creation
        </a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!-- 搜索表单 -->
    <a-form layout="inline" :model="searchParams" @finish="doSearch">
      <a-form-item label="Keyword">
        <a-input
          v-model:value="searchParams.searchText"
          placeholder="Search keyword"
          allow-clear
        />
      </a-form-item>
      <a-form-item label="Category">
        <a-input v-model:value="searchParams.category" placeholder="Search Category" allow-clear />
      </a-form-item>
      <a-form-item label="Label">
        <a-select
          v-model:value="searchParams.tags"
          mode="tags"
          placeholder="Search label"
          style="min-width: 180px"
          allow-clear
        />
      </a-form-item>
      <a-form-item name="reviewStatus" label="Review status">
        <a-select
          v-model:value="searchParams.reviewStatus"
          style="min-width: 180px"
          placeholder="Search review status"
          :options="PIC_REVIEW_STATUS_OPTIONS"
          allow-clear
        />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">Search</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />
    <!-- 表格 -->
    <a-table
      :columns="columns"
      :data-source="dataList"
      :pagination="pagination"
      @change="doTableChange"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'url'">
          <a-image :src="record.url" :width="120" />
        </template>
        <template v-if="column.dataIndex === 'tags'">
          <a-space wrap>
            <a-tag v-for="tag in JSON.parse(record.tags || '[]')" :key="tag">
              {{ tag }}
            </a-tag>
          </a-space>
        </template>
        <template v-if="column.dataIndex === 'picInfo'">
          <div>Format：{{ record.picFormat }}</div>
          <div>Width：{{ record.picWidth }}</div>
          <div>Height：{{ record.picHeight }}</div>
          <div>Aspect Ratio：{{ record.picScale }}</div>
          <div>Size：{{ (record.picSize / 1024).toFixed(2) }}KB</div>
        </template>
        <template v-if="column.dataIndex === 'reviewMessage'">
          <div>Review Status：{{ (PIC_REVIEW_STATUS_MAP as any)[record.reviewStatus ?? 0] }}</div>
          <div>Review Message：{{ record.reviewMessage }}</div>
          <div>Reviewer ID：{{ record.reviewerId }}</div>
          <div v-if="record.reviewTime">
            审核时间：{{ dayjs(record.reviewTime).format('YYYY-MM-DD HH:mm:ss') }}
          </div>
        </template>
        <template v-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-if="column.dataIndex === 'editTime'">
          {{ dayjs(record.editTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.PASS"
              type="link"
              @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.PASS)"
            >
              Pass
            </a-button>
            <a-button
              v-if="record.reviewStatus !== PIC_REVIEW_STATUS_ENUM.REJECT"
              type="link"
              danger
              @click="handleReview(record, PIC_REVIEW_STATUS_ENUM.REJECT)"
            >
              Reject
            </a-button>
            <a-button type="link" :href="`/add_picture?id=${record.id}`" target="_blank">
              Edit
            </a-button>
            <a-button danger @click="doDelete(record.id)">Delete</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>
<script lang="ts" setup>
import { computed, onMounted, reactive, ref } from 'vue'
import {
  deletePictureUsingPost,
  doPictureReviewUsingPost,
  listPictureByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import {
  PIC_REVIEW_STATUS_ENUM,
  PIC_REVIEW_STATUS_MAP,
  PIC_REVIEW_STATUS_OPTIONS,
} from '../../constants/picture.ts'
import dayjs from 'dayjs'

const columns = [
  {
    title: 'ID',
    dataIndex: 'id',
    width: 80,
  },
  {
    title: 'Picture',
    dataIndex: 'url',
  },
  {
    title: 'Name',
    dataIndex: 'name',
  },
  {
    title: 'Introduction',
    dataIndex: 'introduction',
    ellipsis: true,
  },
  {
    title: 'Category',
    dataIndex: 'category',
  },
  {
    title: 'Tags',
    dataIndex: 'tags',
  },
  {
    title: 'Picture Info',
    dataIndex: 'picInfo',
  },
  {
    title: 'User ID',
    dataIndex: 'userId',
    width: 80,
  },
  // {
  //   title: 'Space ID',
  //   dataIndex: 'spaceId',
  //   width: 80,
  // },
  {
    title: 'Review Message',
    dataIndex: 'reviewMessage',
  },
  {
    title: 'Create Time',
    dataIndex: 'createTime',
  },
  {
    title: 'Edit Time',
    dataIndex: 'editTime',
  },
  {
    title: 'Action',
    key: 'action',
  },
]

// 定义数据
const dataList = ref<API.Picture[]>([])
const total = ref(0)

// 搜索条件
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 10,
  sortField: 'createTime',
  sortOrder: 'descend',
})

// 获取数据
const fetchData = async () => {
  const res = await listPictureByPageUsingPost({
    ...searchParams,
    nullSpaceId: true,
  })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data.records ?? []
    total.value = res.data.data.total ?? 0
  } else {
    message.error('Failed to fetch data，' + res.data.message)
  }
}

// 页面加载时获取数据，请求一次
onMounted(() => {
  fetchData()
})

// 分页参数
const pagination = computed(() => {
  return {
    current: searchParams.current,
    pageSize: searchParams.pageSize,
    total: total.value,
    showSizeChanger: true,
    showTotal: (total: number) => `${total} in total`,
  }
})

// 表格变化之后，重新获取数据
const doTableChange = (page: any) => {
  searchParams.current = page.current
  searchParams.pageSize = page.pageSize
  fetchData()
}

// 搜索数据
const doSearch = () => {
  // 重置页码
  searchParams.current = 1
  fetchData()
}

// 删除数据
const doDelete = async (id: number) => {
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success')
    // 刷新数据
    fetchData()
  } else {
    message.error('Failed to delete，' + res.data.message)
  }
}

// 审核图片
const handleReview = async (record: API.Picture, reviewStatus: number) => {
  const reviewMessage =
    reviewStatus === PIC_REVIEW_STATUS_ENUM.PASS ? 'Admin Pass' : 'Admin Reject'
  const res = await doPictureReviewUsingPost({
    id: record.id,
    reviewStatus,
    reviewMessage,
  })
  if (res.data.code === 0) {
    message.success(`${reviewMessage} success`)
    // 重新获取列表数据
    fetchData()
  } else {
    message.error('Failed to review，' + res.data.message)
  }
}
</script>
