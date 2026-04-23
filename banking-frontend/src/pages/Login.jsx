import { loginUser } from '../services/authService'
import { useState } from 'react'
import {Eye, EyeOff} from "lucide-react"
import { useNavigate } from "react-router-dom"
    

const Login = () => {
    const [showPassword, setShowPassword] = useState(false)
    const navigate = useNavigate()
    const [mail,setMail]=useState("")
    const [password,setPassword]=useState("")
    const [loading, setLoading]=useState(false)

    const handleLogin= async (e) =>{
    e.preventDefault()
    setLoading(true)

    try{
      const token = await loginUser(mail, password)
    localStorage.setItem("token", token)
    navigate("/dashboard")
    }catch (error){
      console.error("API login error",error)
    }finally{
      setLoading(false)
    }
    
  }


  return (
    <div className="min-h-screen flex">
        
        <div className="bg-gray-900 w-1/2 flex flex-col items-center justify-center" >
          <h1 className='text-4xl font-bold text-white'>
            SecureBank
          </h1>
          <p className='text-gray-400 mt-3 text-lg'>
            Your money,  is secured
          </p>
        </div>

       
        
          <div className="bg-white w-1/2 flex items-center justify-center">
            <form onSubmit={handleLogin}>
            <div className="form-container w-96">
              <h1 className='text-3xl font-bold text-gray-800'>
                Welcome Back
              </h1>
              <p className='text-gray-500 mt-2'>
                Sign in to your account
              </p>
              <label className="block text-sm font-medium text-gray-700 mt-6">
                Email
              </label>
              <input 
                type="email"  value={mail} 
                onChange={
                  (e)=>setMail(e.target.value)
                } 
                className="w-full border border-gray-300 rounded-lg px-4 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <label className="block text-sm font-medium text-gray-700 mt-6">
                Password
              </label>
              <div className="relative mt-1">
                <input type={showPassword?"text":"password"} value={password}
                  onChange={
                    (e)=>{setPassword(e.target.value)}
                  }
                  className="w-full border border-gray-300 rounded-lg px-4 py-2 mt-1 focus:outline-none focus:ring-2 focus:ring-blue-500" />
              <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-2.5 text-gray-500 text-sm"
              >{showPassword?<EyeOff/>:<Eye/>}</button>
              </div>
              <button
                type="submit"
                className="w-full mt-6 bg-blue-600 hover:bg-blue-700 text-white font-semibold py-2 rounded-lg"
              >
                Login
              </button>
            </div>
            </form>
          </div>
        
      </div>
  )
}

export default Login
