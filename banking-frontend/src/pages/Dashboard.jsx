import { useEffect, useState } from "react"
import { getAccounts, getUserIdFromToken } from "../services/accountService"
import Navbar from "../components/Navbar"

const Dashboard = () => {
  const [account,setAccount]=useState(null)
  const [loading,setLoading]=useState(true)
  
  useEffect(()=>{
    const token = localStorage.getItem("token")
    const userId = getUserIdFromToken(token)
    console.log(userId);
    console.log("token : "+token);
    getAccounts(token,userId)
      .then(data =>{
        setAccount(data)
        setLoading(false)
        console.log(data)
      })
       .catch(error=>{
        console.error("Failed to fetch accounts",error)
        setLoading(false)
       })   
  },[])


  if(loading) 
    return <div className="p-8">Loading...</div>

  return (
        <div className="min-h-screen bg-gray-50">
          <Navbar />
          <div className="p-8">
            <h1 className="text-2xl font-bold text-gray-800">
              Your Accounts
            </h1>
            <div className="flex flex-wrap gap-6 mt-6">
              {account && account.map((acc) => (
                <div key={acc.id} className="p-6 bg-white shadow-md rounded-xl border border-gray-100 w-72">
                  
                  <p className="text-sm text-gray-500 mb-1">Account Number</p>
                  <p className="font-semibold text-gray-800 mb-3">{acc.accountNumber}</p>

                  <p className="text-sm text-gray-500 mb-1">Balance</p>
                  <p className="text-2xl font-bold text-blue-600 mb-3">₹{acc.balance}</p>

                  <p className="text-sm text-gray-500 mb-1">Type</p>
                  <p className="font-semibold text-gray-800">{acc.accountType}</p>

                </div>
              ))}
            </div>
          </div>
        </div>
  )
}

export default Dashboard
