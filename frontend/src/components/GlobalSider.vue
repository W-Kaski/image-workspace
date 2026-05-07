<template>
  <div id="globalSider">
    <a-layout-sider
      v-if="loginUserStore.loginUser.id"
      width="200"
      breakpoint="lg"
      collapsed-width="0"
    >
      <a-menu
        v-model:selectedKeys="current"
        mode="inline"
        :items="menuItems"
        @click="doMenuClick"
      />
    </a-layout-sider>
  </div>
</template>

<script lang="ts" setup>
import { computed, h, ref, watchEffect } from 'vue'
import { PictureOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRouter } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'
import { listMyTeamSpaceUsingPost } from '@/api/spaceUserController.ts'
import { message } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()

// Fixed menu items
const fixedMenuItems = [
  {
    key: '/',
    icon: () => h(PictureOutlined),
    label: 'Public Gallery',
  },
  {
    key: '/my_space',
    label: 'My Space',
    icon: () => h(UserOutlined),
  },
  {
    key: '/add_space?type=' + SPACE_TYPE_ENUM.TEAM,
    label: 'Create Team',
    icon: () => h(TeamOutlined),
  },
]

const teamSpaceList = ref<API.SpaceUserVO[]>([])
const menuItems = computed(() => {
  // If the user has no team spaces, only show fixed menu
  if (teamSpaceList.value.length < 1) {
    return fixedMenuItems
  }
  // If the user has team spaces, show fixed menu + team spaces
  const teamSpaceSubMenus = teamSpaceList.value.map((spaceUser) => {
    const space = spaceUser.space
    return {
      key: '/space/' + spaceUser.spaceId,
      label: space?.spaceName,
    }
  })
  const teamSpaceMenuGroup = {
    type: 'group',
    label: 'My Teams',
    key: 'teamSpace',
    children: teamSpaceSubMenus,
  }
  return [...fixedMenuItems, teamSpaceMenuGroup]
})

// Load team space list
const fetchTeamSpaceList = async () => {
  const res = await listMyTeamSpaceUsingPost()
  if (res.data.code === 0 && res.data.data) {
    teamSpaceList.value = res.data.data
  } else {
    message.error('Failed to load my team spaces: ' + res.data.message)
  }
}

/**
 * Watch variables and reload data on changes
 */
watchEffect(() => {
  // Only load when logged in
  if (loginUserStore.loginUser.id) {
    fetchTeamSpaceList()
  }
})

const router = useRouter()
// Currently highlighted menu
const current = ref<string[]>([])
// Watch route changes to update highlighted menu
router.afterEach((to, from, next) => {
  current.value = [to.path]
})

// Menu click navigation
const doMenuClick = ({ key }: { key: string }) => {
  router.push(key)
}
</script>

<style scoped>
#globalSider .ant-layout-sider {
  background: none;
}
</style>
