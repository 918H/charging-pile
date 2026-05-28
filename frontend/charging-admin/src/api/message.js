import request from '@/utils/request'

/**
 * 消息通知 API
 */

// 查询消息列表
export function getMessageList(userId, pageNum = 1, pageSize = 10) {
  return request({
    url: '/message/list',
    method: 'get',
    params: { userId, pageNum, pageSize }
  })
}

// 查询未读消息数
export function getUnreadCount(userId) {
  return request({
    url: '/message/unread',
    method: 'get',
    params: { userId }
  })
}

// 标记消息已读
export function markAsRead(messageId) {
  return request({
    url: `/message/${messageId}/read`,
    method: 'put'
  })
}

// 批量标记已读
export function markAllAsRead(userId) {
  return request({
    url: '/message/read-all',
    method: 'put',
    params: { userId }
  })
}

// 发送消息
export function sendMessage(userId, title, content, type = 'system') {
  return request({
    url: '/message/send',
    method: 'post',
    params: { userId, title, content, type }
  })
}

// 删除消息
export function deleteMessage(messageId) {
  return request({
    url: `/message/${messageId}`,
    method: 'delete'
  })
}
