import axios from 'axios'


// separate development and production environment
const DEV_BASE_URL = "";
const PROD_BASE_URL = "https://api.anio.me/image"

const myAxios = axios.create({
  baseURL: import.meta.env.DEV ? DEV_BASE_URL : PROD_BASE_URL,
  timeout: 10000,
  withCredentials: true,
});
import {message} from "ant-design-vue";

// response interceptor
myAxios.interceptors.response.use(
  function (response) {
    const { data } = response
    // not login
    if (data.code === 40100) {
      if (
        !response.request.responseURL.includes('user/get/login') &&
        !window.location.pathname.includes('/user/login')
      ) {
        message.warning('please login')
        window.location.href = `/user/login?redirect=${window.location.href}`
      }
    }
    return response
  },
  function (error) {
    // Handle specific network errors
    console.error("Request Error:", error);
    if (error.message === 'Network Error') {
      message.error('Backend connection failed. Please check if Docker backend is running on 18123.');
    }
    return Promise.reject(error)
  },
)

export default myAxios;
