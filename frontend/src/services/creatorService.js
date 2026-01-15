import { apiRequest } from './apiClient'

const creatorService = {
  async getCreator(creatorName) {
    return await apiRequest(`/creators/${creatorName}`)
  }
}

export default creatorService

