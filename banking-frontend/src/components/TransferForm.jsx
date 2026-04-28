import { useState } from "react"
import { transferMoney } from "../services/transactionService"

const TransferForm = ({
    accounts, onTransferSuccess
}) => {
    const [form,setForm] = useState({
        fromAccountNumber: "",
        toAccountNumber: "",
        amount: ""
    })

    const [error,setError] = useState("")
    const [loading,setLoading] = useState(false)

    const handleTransfer = async (e) => {
  e.preventDefault()
  setLoading(true)
  try {
    await transferMoney(form.fromAccountNumber, form.toAccountNumber, Number(form.amount))
    onTransferSuccess()
    setForm({fromAccountNumber: "", toAccountNumber: "", amount: ""})
  } catch (error) {
    setError("Transfer Failed. Please try again.")
  } finally{
    setLoading(false)
  }
}

  return (
      <div className="mt-8 bg-white p-6 rounded-xl shadow-md border border-gray-100 max-w-md">
        <h2 className="text-xl font-bold text-gray-800 mb-6">Transfer Money</h2>
        <form onSubmit={handleTransfer}>
            {/* from Account */}
            <label className="block text-sm font-medium text-gray-700 mb-1">From Account</label>
            <select value={form.fromAccountNumber} onChange={
                (e) => setForm({
                    ...form, fromAccountNumber : e.target.value
                })
            }
            className="w-full border border-gray-300 rounded-lg px-4 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500">
                    <option>--select--</option>
                {accounts.map(acc =>(
                    <option key={acc.id} value={acc.accountNumber}>
                        {acc.accountNumber} - ₹{acc.balance}
                    </option>
                ) )}
            </select>
            
            {/* toAccount */}
            <label className="block text-sm font-medium text-gray-700 mb-1">To Account Number</label>
            <input type="text" value={form.toAccountNumber} 
                onChange={(e) => setForm({
                            ...form, toAccountNumber : e.target.value
                            })
                        } 
            className="w-full border border-gray-300 rounded-lg px-4 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter recipient account number"/>
            
             {/* Amount */}
            <label className="block text-sm font-medium text-gray-700 mb-1">Amount (₹)</label>
            <input type="number" value={form.amount}
            onChange={(e) => setForm(
                        {...form, amount: e.target.value}
                    )}
            className="w-full border border-gray-300 rounded-lg px-4 py-2 mb-4 focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter amount" />

            {error && <p className="text-red-500 text-sm mt-2">{error}</p>}

            <button type="submit" disabled={loading}
                className="w-full bg-blue-600 hover:bg-blue-700 disabled:bg-blue-300 text-white font-semibold py-2 rounded-lg">
                {loading ? "Transferring..." : "Transfer"}
            </button>
        </form>
    </div>
  )
}

export default TransferForm
