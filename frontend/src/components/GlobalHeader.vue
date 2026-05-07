<template>
  <div id="globalHeader">
    <a-row :wrap="false" align="middle">
      <!-- Logo Area -->
      <a-col flex="240px">
        <router-link to="/">
          <div class="logo-area">
            <img class="logo" src="../assets/logo.svg" alt="logo" />
            <span class="brand-text">image-workspace</span>
          </div>
        </router-link>
      </a-col>

      <!-- Global Search Bar -->
      <a-col flex="auto">
        <div class="header-search">
          <a-input-search
            v-model:value="searchText"
            placeholder="Search inspiration..."
            @search="onSearch"
            style="max-width: 480px"
          />
        </div>
      </a-col>

      <!-- Nav & User Area -->
      <a-col flex="300px">
        <div class="right-nav">
          <a-menu
            v-model:selectedKeys="current"
            mode="horizontal"
            :items="items"
            @click="doMenuClick"
            class="header-menu"
          />

          <div class="user-area">
            <div v-if="loginUserStore.loginUser.id">
              <a-dropdown placement="bottomRight">
                <a-avatar
                  :src="loginUserStore.loginUser.userAvatar"
                  class="user-avatar"
                />
                <template #overlay>
                  <a-menu>
                    <a-menu-item key="space">
                      <router-link to="/my_space">
                        <UserOutlined />
                        <span style="margin-left: 8px">My Space</span>
                      </router-link>
                    </a-menu-item>
                    <a-menu-divider />
                    <a-menu-item key="logout" @click="doLogout" danger>
                      <LogoutOutlined />
                      <span style="margin-left: 8px">Logout</span>
                    </a-menu-item>
                  </a-menu>
                </template>
              </a-dropdown>
            </div>
            <div v-else>
              <a-button type="primary" shape="round" href="/user/login">Login</a-button>
            </div>
          </div>
        </div>
      </a-col>
    </a-row>
  </div>
</template>

<script lang="ts" setup>
import { computed, h, ref, watch } from 'vue'
import { HomeOutlined, LogoutOutlined, UserOutlined, SearchOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useRouter, useRoute } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore'
import { userLogoutUsingPost } from '@/api/userController.ts'

const loginUserStore = useLoginUserStore()
const router = useRouter()
const route = useRoute()

// Search Logic
const searchText = ref('')
const onSearch = () => {
  router.push({
    path: '/',
    query: {
      ...route.query,
      searchText: searchText.value,
    }
  })
}

// Sync search text with URL
watch(() => route.query.searchText, (newVal) => {
  searchText.value = (newVal as string) || ''
}, { immediate: true })

const originItems = [
  {
    key: '/',
    label: 'Home',
    title: 'Home',
  },
  {
    key: '/add_picture',
    label: 'Post',
    title: 'Post Image',
  },
  {
    key: '/admin/pictureManage',
    label: 'Admin',
    title: 'Admin',
  }
]

const filterMenus = (menus = [] as any[]) => {
  return menus?.filter((menu) => {
    if (menu?.key?.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      return loginUser && loginUser.userRole === 'admin'
    }
    return true
  })
}

const items = computed(() => filterMenus(originItems))

const doMenuClick = ({ key }: { key: string }) => {
  if (key === 'others') return
  router.push({ path: key })
}

const current = ref<string[]>([])
router.afterEach((to) => {
  current.value = [to.path]
})

const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({ userName: 'not logged in' })
    message.success('Logged out')
    router.push('/user/login')
  } else {
    message.error('Logout failed: ' + res.data.message)
  }
}
</script>

<style scoped>
#globalHeader {
  background: transparent;
}

.logo-area {
  display: flex;
  align-items: center;
  gap: 12px;
  padding-left: 4px;
}

.logo {
  height: 32px;
  width: 32px;
  object-fit: contain;
}

.brand-text {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.5px;
  font-family: 'Inter', sans-serif;
}

.header-search {
  display: flex;
  justify-content: center;
}

:deep(.ant-input-search) {
  .ant-input {
    background: #f1f5f9;
    border: none !important;
    border-radius: 20px;
    padding-left: 16px;
  }
  .ant-input-search-button {
    border: none !important;
    background: transparent !important;
    color: var(--text-secondary);
    box-shadow: none !important;
  }
}

.right-nav {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 24px;
}

.header-menu {
  line-height: 64px;
  border-bottom: none !important;
  background: transparent;
  min-width: 150px;
}

.user-area {
  display: flex;
  align-items: center;
}

.user-avatar {
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.2s;
}

.user-avatar:hover {
  border-color: var(--primary-color);
  transform: scale(1.05);
}
</style>
