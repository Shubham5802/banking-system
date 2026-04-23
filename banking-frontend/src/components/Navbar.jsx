import { useNavigate } from "react-router-dom"

const Navbar = () => {
    const navigate=useNavigate()
    const handleLogout = () =>{
        localStorage.removeItem("token")
        navigate("/")
    }
  return (
    <nav className="bg-gray-900 flex justify-between items-center px-8 py-4">
        <div className="text-white font-bold text-xl">
            RANUSHA Bank
        </div>
        <button onClick={handleLogout}   className="text-white border border-white px-4 py-1 rounded-lg hover:bg-white hover:text-gray-900">
            logout
        </button>
    </nav>
  )
}

export default Navbar
