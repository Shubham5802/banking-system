import axios from "axios";

const axiosInstance=axios.create({
    baseURL:"http://localhost:8080"
})

// interceptor — runs before every request, attaches token
debugger
axiosInstance.interceptors.request.use((config)=>{
    const token=localStorage.getItem("token")
    if(token){
        config.headers.Authorization=`Bearer ${token}`
    }
    return config
})

export default axiosInstance