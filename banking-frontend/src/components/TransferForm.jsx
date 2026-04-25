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

    const handleTransfer = async (e) => {
  e.preventDefault()
  try {
    await transferMoney(form.fromAccountNumber, form.toAccountNumber, Number(form.amount))
    onTransferSuccess()
  } catch (error) {
    console.error("Transfer failed", error)
  }
}

  return (
    <div>
      <form onSubmit={handleTrasfer}>
        from Account - 
        <select value={form.fromAccountNumber} onChange={
            (e) => setForm({
                ...form, fromAccountNumber : e.target.value
            })
        }>
            {accounts.map(acc =>(
                <option key={acc.id} value={acc.accountNumber}>
                    {acc.accountNumber} - ₹{acc.balance}
                </option>
            ) )}
        </select>
        To Account - 
        <input type="text" value={form.toAccountNumber} onChange={
            (e) => setForm({
                ...form, toAccountNumber : e.target.value
            })
        } />
        Amount - 
        <input type="number" value={form.amount} onChange={
            (e) => setForm({
                ...form, amount : e.target.value
            })
        }/>
        <button type="submit" className="bg-gray-300 rounded-2xl">Transfer</button>
      </form>
    </div>
  )
}

export default TransferForm
