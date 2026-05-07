import { createRouter, createWebHistory } from 'vue-router'
import HomePage from '@/pages/HomePage.vue'
import UserLoginPage from '@/pages/user/UserLoginPage.vue'
import UserRegisterPage from '@/pages/user/UserRegisterPage.vue'
import UserManagePage from '@/pages/admin/UserManagePage.vue'
import AddPicturePage from '@/pages/picture/AddPicturePage.vue'
import PictureManagePage from '@/pages/admin/PictureManagePage.vue'
import PictureDetailPage from '@/pages/picture/PictureDetailPage.vue'
import AddPictureBatchPage from '@/pages/picture/AddPictureBatchPage.vue'
import SpaceManagePage from '@/pages/admin/SpaceManagePage.vue'
import AddSpacePage from '@/pages/space/AddSpacePage.vue'
import MySpacePage from '@/pages/space/MySpacePage.vue'
import SpaceDetailPage from '@/pages/space/SpaceDetailPage.vue'
import SpaceAnalyzePage from '@/pages/space/SpaceAnalyzePage.vue'
import SpaceUserManagePage from '@/pages/admin/SpaceUserManagePage.vue'

const router = createRouter({
  history: createWebHistory('/'),
  routes: [
    {
      path: '/',
      name: 'Home',
      component: HomePage,
    },
    {
      path: '/user/login',
      name: 'User Login',
      component: UserLoginPage,
    },
    {
      path: '/user/register',
      name: 'User Register',
      component: UserRegisterPage,
    },
    {
      path: '/admin/userManage',
      name: 'User Admin Manage',
      component: UserManagePage,
    },
    {
      path: '/add_picture',
      name: 'Create Picture',
      component: AddPicturePage,
    },
    {
      path: '/admin/pictureManage',
      name: 'Picture Admin Manage',
      component: PictureManagePage,
    },
    {
      path: '/picture/:id',
      name: 'Picture Detail',
      component: PictureDetailPage,
      props: true,
    },
    {
      path: '/add_picture/batch',
      name: 'Add Picture by Batch',
      component: AddPictureBatchPage,
    },
    {
      path: '/admin/spaceManage',
      name: 'Space Manage',
      component: SpaceManagePage,
    },
    {
      path: '/add_space',
      name: 'Create Space',
      component: AddSpacePage,
    },
    {
      path: '/my_space',
      name: 'My Space',
      component: MySpacePage,
    },
    {
      path: '/space/:id',
      name: 'Space Detail',
      component: SpaceDetailPage,
      props: true,
    },
    {
      path: '/space_analyze',
      name: 'Space Analyze',
      component: SpaceAnalyzePage,
    },
    {
      path: '/spaceUserManage/:id',
      name: 'Space User Manage',
      component: SpaceUserManagePage,
      props: true,
    },
  ],
})

export default router
