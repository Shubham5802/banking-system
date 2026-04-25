import axiosInstance from "./axiosInstance"

export const getUserIdFromToken = (token) =>{
    const payload = JSON.parse(atob(token.split('.')[1]))
    return payload.userId
}

export const getAccounts = async (userId) =>{
    const response = await axiosInstance.get(`/api/accounts/${userId}`)
    return response.data
}

// export const getAccounts = async (token,userId) =>{
//     const response= await fetch(`http://localhost:8080/api/accounts/${userId}`,{
//         method: "GET",
//         headers:{
//             "Authorization": `Bearer ${token}`
//                 }
//             })
//         return response.json()
//         }
