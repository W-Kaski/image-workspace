
export const PIC_REVIEW_STATUS_ENUM = {
  REVIEWING: 0,
  PASS: 1,
  REJECT: 2,
}

/**
 * 图片审核状态文案
 */
export const PIC_REVIEW_STATUS_MAP = {
  0: 'Not Reviewed',
  1: 'Pass',
  2: 'Reject',
}

/**
 * 图片审核下拉表单选项
 */
export const PIC_REVIEW_STATUS_OPTIONS = Object.keys(PIC_REVIEW_STATUS_MAP).map((key) => {
  return {
    label: (PIC_REVIEW_STATUS_MAP as any)[key],
    value: Number(key),
  }
})


/**
 * 图片编辑消息类型枚举
 */
export const PICTURE_EDIT_MESSAGE_TYPE_ENUM = {
  INFO: 'INFO',
  ERROR: 'ERROR',
  ENTER_EDIT: 'ENTER_EDIT',
  EXIT_EDIT: 'EXIT_EDIT',
  EDIT_ACTION: 'EDIT_ACTION',
};


/**
 * 图片编辑操作枚举
 */
export const PICTURE_EDIT_ACTION_ENUM = {
  ZOOM_IN: 'ZOOM_IN',
  ZOOM_OUT: 'ZOOM_OUT',
  ROTATE_LEFT: 'ROTATE_LEFT',
  ROTATE_RIGHT: 'ROTATE_RIGHT',
};
