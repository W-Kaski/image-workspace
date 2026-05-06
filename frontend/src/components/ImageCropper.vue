<template>
  <a-modal
    class="image-cropper"
    v-model:visible="visible"
    title="Edit Image"
    :footer="false"
    @cancel="closeModal"
  >
    <!-- Image cropping component -->
    <vue-cropper
      ref="cropperRef"
      :img="computedUrl"
      output-type="png"
      :info="true"
      :can-move-box="true"
      :fixed-box="false"
      :auto-crop="true"
      :center-box="true"
    />
    <div style="margin-bottom: 16px" />

    <!-- Collaborative editing actions -->
    <div class="image-edit-actions" v-if="isTeamSpace">
      <a-space>
        <a-button v-if="editingUser" disabled>{{ editingUser.userName }} is editing</a-button>
        <a-button v-if="canEnterEdit" type="primary" ghost @click="enterEdit">Enter Edit</a-button>
        <a-button v-if="canExitEdit" danger ghost @click="exitEdit">Exit Edit</a-button>
      </a-space>
    </div>
    <div style="margin-bottom: 16px" />

    <!-- Image manipulation actions -->
    <div class="image-cropper-actions">
      <a-space>
        <a-button @click="rotateLeft" :disabled="!canEdit">R Left</a-button>
        <a-button @click="rotateRight" :disabled="!canEdit">R Right</a-button>
        <a-button @click="changeScale(1)" :disabled="!canEdit">Z In</a-button>
        <a-button @click="changeScale(-1)" :disabled="!canEdit">Z Out</a-button>
        <a-button type="primary" :loading="loading" :disabled="!canEdit" @click="handleConfirm">
          Confirm
        </a-button>
      </a-space>
    </div>
  </a-modal>
</template>

<script lang="ts" setup>
import { computed, onUnmounted, ref, watchEffect } from 'vue'
import { uploadPictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import PictureEditWebSocket from '@/utils/pictureEditWebSocket.ts'
import { PICTURE_EDIT_ACTION_ENUM, PICTURE_EDIT_MESSAGE_TYPE_ENUM } from '@/constants/picture.ts'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'

interface Props {
  imageUrl?: string
  picture?: API.PictureVO
  spaceId?: number
  space?: API.SpaceVO
  onSuccess?: (newPicture: API.PictureVO) => void
}

const props = defineProps<Props>()

// Compute the image URL
const computedUrl = computed(() => {
  if (!props.imageUrl) return ''
  return props.imageUrl.startsWith('http')
    ? props.imageUrl
    : `${import.meta.env.VITE_BASE_URL}${props.imageUrl}`
})

// Check if the current space is a team space
const isTeamSpace = computed(() => {
  return props.space?.spaceType === SPACE_TYPE_ENUM.TEAM
})

// Reference to the cropper component
const cropperRef = ref()

// Change image scale (zoom)
const changeScale = (num) => {
  cropperRef.value?.changeScale(num)
  if (num > 0) {
    editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_IN)
  } else {
    editAction(PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT)
  }
}

// Rotate image left
const rotateLeft = () => {
  cropperRef.value.rotateLeft()
  editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT)
}

// Rotate image right
const rotateRight = () => {
  cropperRef.value.rotateRight()
  editAction(PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT)
}

// Confirm cropping
const handleConfirm = () => {
  cropperRef.value.getCropBlob((blob: Blob) => {
    const fileName = (props.picture?.name || 'image') + '.png'
    const file = new File([blob], fileName, { type: blob.type })
    handleUpload({ file })
  })
}

const loading = ref(false)

/**
 * Upload image
 * @param file
 */
const handleUpload = async ({ file }: any) => {
  loading.value = true
  try {
    const params: API.PictureUploadRequest = props.picture ? { id: props.picture.id } : {}
    params.spaceId = props.spaceId
    const res = await uploadPictureUsingPost(params, {}, file)
    if (res.data.code === 0 && res.data.data) {
      message.success('Image uploaded successfully')
      props.onSuccess?.(res.data.data)
      closeModal()
    } else {
      message.error('Image upload failed: ' + res.data.message)
    }
  } catch (error) {
    console.error('Image upload failed', error)
    message.error('Image upload failed: ' + error.message)
  }
  loading.value = false
}

// Modal visibility
const visible = ref(false)

// Open modal
const openModal = () => {
  visible.value = true
}

// Close modal
const closeModal = () => {
  visible.value = false
  if (websocket) {
    websocket.disconnect()
  }
  editingUser.value = undefined
}

// Expose methods to parent
defineExpose({
  openModal,
})

// --------- Real-time editing ---------
const loginUserStore = useLoginUserStore()
const loginUser = loginUserStore.loginUser

// Currently editing user
const editingUser = ref<API.UserVO>()
const canEnterEdit = computed(() => !editingUser.value)
const canExitEdit = computed(() => editingUser.value?.id === loginUser.id)
const canEdit = computed(() => {
  if (!isTeamSpace.value) return true
  return editingUser.value?.id === loginUser.id
})

// WebSocket instance
let websocket: PictureEditWebSocket | null

// Initialize WebSocket connection
const initWebsocket = () => {
  const pictureId = props.picture?.id
  if (!pictureId || !visible.value) return
  if (websocket) websocket.disconnect()
  websocket = new PictureEditWebSocket(pictureId)
  websocket.connect()

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.INFO, (msg) => {
    console.log('Info message received:', msg)
    message.info(msg.message)
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ERROR, (msg) => {
    console.log('Error message received:', msg)
    message.info(msg.message)
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT, (msg) => {
    console.log('Enter edit message received:', msg)
    message.info(msg.message)
    editingUser.value = msg.user
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION, (msg) => {
    console.log('Edit action message received:', msg)
    message.info(msg.message)
    switch (msg.editAction) {
      case PICTURE_EDIT_ACTION_ENUM.ROTATE_LEFT: rotateLeft(); break
      case PICTURE_EDIT_ACTION_ENUM.ROTATE_RIGHT: rotateRight(); break
      case PICTURE_EDIT_ACTION_ENUM.ZOOM_IN: changeScale(1); break
      case PICTURE_EDIT_ACTION_ENUM.ZOOM_OUT: changeScale(-1); break
    }
  })

  websocket.on(PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT, (msg) => {
    console.log('Exit edit message received:', msg)
    message.info(msg.message)
    editingUser.value = undefined
  })
}

// Watch for changes to initialize WebSocket in team space
watchEffect(() => {
  if (isTeamSpace.value) initWebsocket()
})

// Disconnect WebSocket on unmount
onUnmounted(() => {
  if (websocket) websocket.disconnect()
  editingUser.value = undefined
})

// Enter edit mode
const enterEdit = () => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.ENTER_EDIT,
    })
  }
}

// Exit edit mode
const exitEdit = () => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EXIT_EDIT,
    })
  }
}

// Send edit action via WebSocket
const editAction = (action: string) => {
  if (websocket) {
    websocket.sendMessage({
      type: PICTURE_EDIT_MESSAGE_TYPE_ENUM.EDIT_ACTION,
      editAction: action,
    })
  }
}
</script>

<style>
.image-cropper {
  text-align: center;
}

.image-cropper .vue-cropper {
  height: 400px !important;
}
</style>
