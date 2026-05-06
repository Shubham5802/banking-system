import { useEffect, useState } from "react"
import { getAccounts, getUserIdFromToken } from "../services/accountService"
import { getTransactionHistory} from '../services/transactionService'
import Navbar from "../components/Navbar"
import TransferForm from "../components/TransferForm"

const Dashboard = () => {
  const [account,setAccount] = useState(null)
  const [loading,setLoading] = useState(true)
  const [selectedAccount,setSelectedAccount] = useState(null)
  const [transactions,setTransactions] = useState([])
  const [txnLoading, setTxnLoading] = useState(false)


  useEffect(()=>{
    const token = localStorage.getItem("token")
    const userId = getUserIdFromToken(token)
    getAccounts(userId)
      .then(data =>{
        setAccount(data)
        setLoading(false)
      })
       .catch(error=>{
        console.error("Failed to fetch accounts",error)
        setLoading(false)
       })   
  },[])

  useEffect(()=>{
    if(!selectedAccount) 
      return
    setTxnLoading(true)
    getTransactionHistory(selectedAccount)
      .then(data =>{
        setTransactions(data)
        setTxnLoading(false)
      })
      .catch(error =>{
        console.error("failed to fetch transactions",error)
      })
  },[selectedAccount])


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

                  <button onClick={()=>{setSelectedAccount(acc.accountNumber)}} 
                  className="mt-4 w-full bg-blue-600 hover:bg-blue-700 text-white text-sm py-2 rounded-lg">
                    View
                  </button>
                    
                </div>
                
              ))}
              
            </div>
            {
                      selectedAccount && (
                        <div className="mt-8">
                          <h2 className="text-xl font-bold text-gray-800 mb-4">
                            Transactions - {selectedAccount}
                          </h2>
                          {txnLoading && <p>  Loading Transaction...</p>}
                          {
                            transactions.map((txn) => (
                              <div key={txn.id} className="bg-white rounded-lg shadow-sm border border-gray-100 mb-3 overflow-hidden">
                                  <div className="flex justify-between items-center px-4 py-3 border-b border-gray-100">
                                    <span className="text-xs font-semibold uppercase tracking-wide text-blue-600 bg-blue-50 px-2 py-1 rounded">
                                      {txn.type}
                                    </span>
                                    <span className="text-xs text-gray-500">{new Date(txn.timestamp).toLocaleString()}</span>
                                    <span className={`text-xs font-semibold px-2 py-1 rounded ${txn.status === 'SUCCESS' ? 'text-green-600 bg-green-50' : 'text-red-600 bg-red-50'}`}>
                                      {txn.status}
                                    </span>
                                  </div>
                                  <div className="grid grid-cols-4 gap-4 px-4 py-3">
                                    <div>
                                      <p className="text-xs text-gray-500 mb-1">From</p>
                                      <p className="text-sm font-semibold text-gray-800">{txn.fromAccountNumber}</p>
                                    </div>
                                    <div>
                                      <p className="text-xs text-gray-500 mb-1">To</p>
                                      <p className="text-sm font-semibold text-gray-800">{txn.toAccountNumber}</p>
                                    </div>
                                    <div>
                                      <p className="text-xs text-gray-500 mb-1">Amount</p>
                                      <p className="text-sm font-bold text-blue-600">₹{txn.amount}</p>
                                    </div>
                                    <div>
                                      <p className="text-xs text-gray-500 mb-1">Fee</p>
                                      <p className="text-sm font-semibold text-gray-800">₹{txn.transactionFee}</p>
                                    </div>
                                  </div>
                              </div>
                            ))
                          }
                        </div>
                      )
                    }
            
              <TransferForm 
                accounts={account}
                onTransferSuccess={() => {
                  const token = localStorage.getItem("token")
                  const userId = getUserIdFromToken(token)
                  getAccounts(userId)
                    .then(data => setAccount(data))
                  if(selectedAccount){
                    getTransactionHistory(selectedAccount)
                    .then(data => setTransactions(data))
                  }
                }}
                    
              />
            

                    
          </div>
        </div>
  )
}

export default Dashboard
