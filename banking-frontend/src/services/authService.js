import axios from "axios"

export const loginUser = async(mail,password)=>{
  const response = await axios.post("http://localhost:8080/api/users/login",
     { mail, password })
  return response.data
}

// export const loginUser = async (mail, password) => {
//   const response = await fetch("http://localhost:8080/api/users/login", {
//     method: "POST",
//     headers: { "Content-Type": "application/json" },
//     body: JSON.stringify({ mail, password })
//   })
//   return response.text()
// }