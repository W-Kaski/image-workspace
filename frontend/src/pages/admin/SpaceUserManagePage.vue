<template>
  <div id="spaceManagePage">
    <a-flex justify="space-between">
      <h2>Space Member Management</h2>
      <a-space>
        <a-button type="primary" href="/add_space" target="_blank">+ Create Space</a-button>
        <a-button type="primary" ghost href="/space_analyze?queryPublic=1" target="_blank">
          Analyze Public Gallery
        </a-button>
        <a-button type="primary" ghost href="/space_analyze?queryAll=1" target="_blank">
          Analyze All Spaces
        </a-button>
      </a-space>
    </a-flex>
    <div style="margin-bottom: 16px" />
    <!-- Add Member Form -->
    <a-form layout="inline" :model="formData" @finish="handleSubmit">
      <a-form-item label="User ID" name="userId">
        <a-input v-model:value="formData.userId" placeholder="Enter user ID" allow-clear />
      </a-form-item>
      <a-form-item>
        <a-button type="primary" html-type="submit">Add User</a-button>
      </a-form-item>
    </a-form>
    <div style="margin-bottom: 16px" />
    <!-- Table -->
    <a-table :columns="columns" :data-source="dataList">
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'userInfo'">
          <a-space>
            <a-avatar :src="record.user?.userAvatar" />
            {{ record.user?.userName }}
          </a-space>
        </template>
        <template v-if="column.dataIndex === 'spaceRole'">
          <a-select
            v-model:value="record.spaceRole"
            :options="SPACE_ROLE_OPTIONS"
            @change="(value) => editSpaceRole(value, record)"
          />
        </template>
        <template v-else-if="column.dataIndex === 'createTime'">
          {{ dayjs(record.createTime).format('YYYY-MM-DD HH:mm:ss') }}
        </template>
        <template v-else-if="column.key === 'action'">
          <a-space wrap>
            <a-button type="link" danger @click="doDelete(record.id)">Delete</a-button>
          </a-space>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script lang="ts" setup>
import { onMounted, reactive, ref } from 'vue'
import { message } from 'ant-design-vue'
import { SPACE_ROLE_OPTIONS } from '../../constants/space.ts'
import {
  addSpaceUserUsingPost,
  deleteSpaceUserUsingPost,
  editSpaceUserUsingPost,
  listSpaceUserUsingPost,
} from '@/api/spaceUserController.ts'
import dayjs from 'dayjs'

interface Props {
  id: string
}

const props = defineProps<Props>()

const columns = [
  {
    title: 'User',
    dataIndex: 'userInfo',
  },
  {
    title: 'Role',
    dataIndex: 'spaceRole',
  },
  {
    title: 'Created At',
    dataIndex: 'createTime',
  },
  {
    title: 'Action',
    key: 'action',
  },
]

// Data
const dataList = ref<API.SpaceUserVO[]>([])

// Fetch data
const fetchData = async () => {
  const spaceId = props.id
  if (!spaceId) return

  const res = await listSpaceUserUsingPost({ spaceId })
  if (res.data.code === 0 && res.data.data) {
    dataList.value = res.data.data ?? []
  } else {
    message.error('Failed to fetch data: ' + res.data.message)
  }
}

onMounted(() => {
  fetchData()
})

// Add member form
const formData = reactive<API.SpaceUserAddRequest>({})

// Add member
const handleSubmit = async () => {
  const spaceId = props.id
  if (!spaceId) return

  const res = await addSpaceUserUsingPost({
    spaceId,
    ...formData,
  })
  if (res.data.code === 0) {
    message.success('User added successfully')
    fetchData()
  } else {
    message.error('Failed to add user: ' + res.data.message)
  }
}

// Edit member role
const editSpaceRole = async (value, record) => {
  const res = await editSpaceUserUsingPost({
    id: record.id,
    spaceRole: value,
  })
  if (res.data.code === 0) {
    message.success('Role updated successfully')
  } else {
    message.error('Failed to update role: ' + res.data.message)
  }
}

// Delete member
const doDelete = async (id: string) => {
  if (!id) return

  const res = await deleteSpaceUserUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Deleted successfully')
    fetchData()
  } else {
    message.error('Failed to delete')
  }
}
</script>

