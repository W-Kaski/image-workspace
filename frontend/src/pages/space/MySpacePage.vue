<template>
  <div id="mySpacePage">
    <p>Redirecting, please wait...</p>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { listSpaceVoByPageUsingPost } from '@/api/spaceController.ts'
import { message } from 'ant-design-vue'
import { onMounted } from 'vue'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'

const router = useRouter()
const loginUserStore = useLoginUserStore()

// Check if the user has a personal space
const checkUserSpace = async () => {
  // If the user is not logged in, redirect to the login page
  const loginUser = loginUserStore.loginUser
  if (!loginUser?.id) {
    router.replace('/user/login')
    return
  }

  // If logged in, get the spaces created by the user
  const res = await listSpaceVoByPageUsingPost({
    userId: loginUser.id,
    current: 1,
    pageSize: 1,
    spaceType: SPACE_TYPE_ENUM.PRIVATE,
  })

  if (res.data.code === 0) {
    // If there is a space, enter the first one
    if (res.data.data && res.data.data.records && res.data.data.records.length > 0) {
      const space = res.data.data.records[0]
      router.replace(`/space/${space.id}`)
    } else {
      // If no space, redirect to the create space page
      router.replace('/add_space')
      message.warn('Please create a space first')
    }
  } else {
    message.error('Failed to load my spaces: ' + res.data.message)
  }
}

// Check user space on page load
onMounted(() => {
  checkUserSpace()
})
</script>
