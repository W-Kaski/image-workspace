<template>
  <div class="picture-list">
    <a-list
      :grid="{ gutter: 20, xs: 1, sm: 2, md: 3, lg: 4, xl: 5, xxl: 6 }"
      :data-source="dataList"
      :loading="loading"
    >
      <template #renderItem="{ item: picture }">
        <a-list-item style="padding: 0">
          <!-- Gallery Card -->
          <div class="gallery-card" @click="doClickPicture(picture)">
            <div class="image-wrapper">
              <img
                :alt="picture.name"
                :src="picture.thumbnailUrl ?? picture.url"
                class="gallery-image"
              />
              <!-- Subtle Overlay for Actions -->
              <div v-if="showOp" class="card-overlay" @click.stop>
                <a-space :size="12" class="overlay-actions">
                  <div class="action-icon" @click="doShare(picture, $event)"><ShareAltOutlined /></div>
                  <div class="action-icon" @click="doSearch(picture, $event)"><SearchOutlined /></div>
                  <div v-if="canEdit" class="action-icon" @click="doEdit(picture, $event)"><EditOutlined /></div>
                  <div v-if="canDelete" class="action-icon delete" @click="doDelete(picture, $event)"><DeleteOutlined /></div>
                </a-space>
              </div>
            </div>

            <!-- Metadata Area -->
            <div class="card-info">
              <div class="card-title">{{ picture.name }}</div>
              <div class="card-meta">
                <span class="category-tag">{{ picture.category ?? 'Default' }}</span>
                <div class="tag-group">
                  <span v-for="tag in (picture.tags as string[])" :key="tag" class="mini-tag">
                    #{{ tag }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </a-list-item>
      </template>
    </a-list>
    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  DeleteOutlined,
  EditOutlined,
  SearchOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import ShareModal from '@/components/ShareModel.vue'
import { ref } from 'vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  canEdit?: boolean
  canDelete?: boolean
  onReload?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  showOp: false,
  canEdit: false,
  canDelete: false,
})

const router = useRouter()

const doClickPicture = (picture: API.PictureVO) => {
  router.push({ path: `/picture/${picture.id}` })
}

const doSearch = (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
  window.open(`/search_picture?pictureId=${picture.id}`)
}

const doEdit = (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
  router.push({
    path: '/add_picture',
    query: { id: picture.id, spaceId: picture.spaceId },
  })
}

const doDelete = async (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
  const id = picture.id
  if (!id) return
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('Delete success')
    props.onReload?.()
  } else {
    message.error('Delete failed')
  }
}

const shareModalRef = ref()
const shareLink = ref<string>('')
const doShare = (picture: API.PictureVO, e: Event) => {
  e.stopPropagation()
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}
</script>

<style scoped>
.gallery-card {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  background: transparent;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.gallery-card:hover {
  transform: translateY(-8px);
}

.image-wrapper {
  position: relative;
  aspect-ratio: 4 / 3;
  overflow: hidden;
  border-radius: 12px;
  background: #f8fafc;
}

.gallery-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.gallery-card:hover .gallery-image {
  transform: scale(1.1);
}

.card-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.2);
  opacity: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s ease;
}

.gallery-card:hover .card-overlay {
  opacity: 1;
}

.action-icon {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.9);
  color: #1e293b;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  transition: all 0.2s;
}

.action-icon:hover {
  background: white;
  transform: scale(1.1);
  color: var(--primary-color);
}

.action-icon.delete:hover {
  color: #ef4444;
}

.card-info {
  padding: 12px 4px;
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-meta {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-tag {
  font-size: 11px;
  text-transform: uppercase;
  color: var(--text-secondary);
  font-weight: 700;
  letter-spacing: 0.5px;
}

.tag-group {
  display: flex;
  gap: 4px;
  overflow: hidden;
}

.mini-tag {
  font-size: 11px;
  color: var(--primary-color);
  opacity: 0.8;
}
</style>
