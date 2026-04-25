import axiosInstance from "./axiosInstance";

export const getTransactionHistory = async (accountNumber) => {
    const response = await axiosInstance.get(`/api/transactions/history/${accountNumber}`)
    return response.data
}

export const transferMoney = async (fromAccountNumber,toAccountNumber,amount) =>{
    const response = await axiosInstance.post(`/api/transactions/transfer`,{
        fromAccountNumber,
        toAccountNumber,
        amount
    })
    return response.data
}

// export const getTransactionHistory = async (token,accountNumber) =>{
//     const response = await fetch(`http://localhost:8080/api/transactions/history/${accountNumber}`,{
//         method: "GET",
//         headers:{
//             "Authorization": `Bearer ${token}`
//         }
//     }
//     )
//     return response.json()
// }