<template>
  <div class="picture-upload-multi">
    <a-upload-dragger
      v-model:file-list="fileList"
      name="file"
      :multiple="true"
      :custom-request="handleUpload"
      :before-upload="beforeUpload"
      @change="handleChange"
    >
      <p class="ant-upload-drag-icon">
        <inbox-outlined></inbox-outlined>
      </p>
      <p class="ant-upload-text">Click or drag files to this area to upload</p>
      <p class="ant-upload-hint">
        Support for a single or bulk upload. Max size 10MB per image.
      </p>
    </a-upload-dragger>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue'
import { InboxOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { uploadPictureUsingPost } from '@/api/pictureController.ts'

interface Props {
  spaceId?: number
  onSuccess?: () => void
}

const props = defineProps<Props>()
const fileList = ref<any[]>([])

/**
 * Handle individual file upload
 */
const handleUpload = async ({ file, onSuccess, onError }: any) => {
  try {
    const params: API.PictureUploadRequest = {
      spaceId: props.spaceId,
    }
    const res: any = await uploadPictureUsingPost(params, {}, file)
    if (res.data.code === 0) {
      onSuccess(res.data.data)
    } else {
      onError(new Error(res.data.message))
      message.error(`${file.name} upload failed: ${res.data.message}`)
    }
  } catch (error) {
    onError(error)
    message.error(`${file.name} upload failed.`)
  }
}

/**
 * Global change handler
 */
const handleChange = (info: any) => {
  const { status } = info.file
  if (status === 'done') {
    message.success(`${info.file.name} file uploaded successfully.`)
  } else if (status === 'error') {
    // Error handled in custom-request
  }
  
  // If all files are uploaded (or failed), trigger parent success
  const allFinished = fileList.value.every(f => f.status === 'done' || f.status === 'error')
  if (allFinished && fileList.value.length > 0) {
     props.onSuccess?.()
  }
}

/**
 * Validate each file
 */
const beforeUpload = (file: any) => {
  const isAllowedFormat =
    file.type === 'image/jpeg' ||
    file.type === 'image/png' ||
    file.type === 'image/webp' ||
    file.type === 'image/gif'
  if (!isAllowedFormat) {
    message.error(`${file.name} is not a supported format (JPG/PNG/WebP/GIF only).`)
  }
  const isLt10M = file.size / 1024 / 1024 < 10
  if (!isLt10M) {
    message.error(`${file.name} must be smaller than 10MB.`)
  }
  return isAllowedFormat && isLt10M
}
</script>

<style scoped>
.picture-upload-multi {
  width: 100%;
}
</style>
