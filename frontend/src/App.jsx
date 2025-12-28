import { useState, useEffect } from 'react'
import axios from 'axios'

function App() {
  const [userId, setUserId] = useState(1)
  const [purchases, setPurchases] = useState([])
  const [history, setHistory] = useState([])
  const [notifications, setNotifications] = useState([])
  const [activeTab, setActiveTab] = useState('purchases')

  // Form state
  const [merchantName, setMerchantName] = useState('')
  const [itemName, setItemName] = useState('')
  const [purchaseDate, setPurchaseDate] = useState('')
  const [returnWindowDays, setReturnWindowDays] = useState(30)

  const fetchData = async () => {
    try {
      const [purchasesRes, historyRes, notificationsRes] = await Promise.all([
        axios.get(`/users/${userId}/purchases`),
        axios.get(`/users/${userId}/purchases/history`),
        axios.get(`/users/${userId}/notifications`)
      ])
      setPurchases(purchasesRes.data)
      setHistory(historyRes.data)
      setNotifications(notificationsRes.data)
    } catch (error) {
      console.error('Error fetching data:', error)
    }
  }

  useEffect(() => {
    fetchData()
  }, [userId])

  const handleSubmit = async (e) => {
    e.preventDefault()
    try {
      await axios.post(`/users/${userId}/purchases`, {
        merchantName,
        itemName,
        purchaseDate,
        returnWindowDays: parseInt(returnWindowDays)
      })
      // Clear form
      setMerchantName('')
      setItemName('')
      setPurchaseDate('')
      setReturnWindowDays(30)
      // Refresh data
      fetchData()
    } catch (error) {
      console.error('Error creating purchase:', error)
    }
  }

  const getDaysUntilDeadline = (deadline) => {
    const today = new Date()
    const deadlineDate = new Date(deadline)
    const diffTime = deadlineDate - today
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
  }

  const getUrgencyColor = (deadline) => {
    const days = getDaysUntilDeadline(deadline)
    if (days <= 1) return 'bg-red-100 border-red-500'
    if (days <= 3) return 'bg-orange-100 border-orange-500'
    if (days <= 7) return 'bg-yellow-100 border-yellow-500'
    return 'bg-green-100 border-green-500'
  }

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header */}
      <header className="bg-blue-600 text-white p-4 shadow-md">
        <div className="max-w-4xl mx-auto flex justify-between items-center">
          <h1 className="text-2xl font-bold">ReturnRemind</h1>
          <div className="flex items-center gap-2">
            <label className="text-sm">User ID:</label>
            <input
              type="number"
              value={userId}
              onChange={(e) => setUserId(e.target.value)}
              className="w-16 px-2 py-1 rounded text-black"
              min="1"
            />
          </div>
        </div>
      </header>

      <main className="max-w-4xl mx-auto p-4">
        {/* Tabs */}
        <div className="flex gap-2 mb-4">
          <button
            onClick={() => setActiveTab('purchases')}
            className={`px-4 py-2 rounded-t-lg font-medium ${
              activeTab === 'purchases'
                ? 'bg-white text-blue-600 border-b-2 border-blue-600'
                : 'bg-gray-200 text-gray-600'
            }`}
          >
            Active Purchases ({purchases.length})
          </button>
          <button
            onClick={() => setActiveTab('history')}
            className={`px-4 py-2 rounded-t-lg font-medium ${
              activeTab === 'history'
                ? 'bg-white text-blue-600 border-b-2 border-blue-600'
                : 'bg-gray-200 text-gray-600'
            }`}
          >
            History ({history.length})
          </button>
          <button
            onClick={() => setActiveTab('notifications')}
            className={`px-4 py-2 rounded-t-lg font-medium ${
              activeTab === 'notifications'
                ? 'bg-white text-blue-600 border-b-2 border-blue-600'
                : 'bg-gray-200 text-gray-600'
            }`}
          >
            Notifications ({notifications.length})
          </button>
          <button
            onClick={() => setActiveTab('add')}
            className={`px-4 py-2 rounded-t-lg font-medium ${
              activeTab === 'add'
                ? 'bg-white text-blue-600 border-b-2 border-blue-600'
                : 'bg-gray-200 text-gray-600'
            }`}
          >
            + Add Purchase
          </button>
        </div>

        {/* Content */}
        <div className="bg-white rounded-lg shadow p-4">
          {/* Active Purchases */}
          {activeTab === 'purchases' && (
            <div>
              <h2 className="text-xl font-semibold mb-4">Active Purchases</h2>
              {purchases.length === 0 ? (
                <p className="text-gray-500">No active purchases</p>
              ) : (
                <div className="space-y-3">
                  {purchases.map((purchase) => (
                    <div
                      key={purchase.id}
                      className={`p-4 rounded-lg border-l-4 ${getUrgencyColor(purchase.returnDeadline)}`}
                    >
                      <div className="flex justify-between items-start">
                        <div>
                          <h3 className="font-semibold">{purchase.itemName}</h3>
                          <p className="text-gray-600">{purchase.merchantName}</p>
                        </div>
                        <div className="text-right">
                          <p className="font-medium">
                            {getDaysUntilDeadline(purchase.returnDeadline)} days left
                          </p>
                          <p className="text-sm text-gray-500">
                            Deadline: {purchase.returnDeadline}
                          </p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}

          {/* History */}
          {activeTab === 'history' && (
            <div>
              <h2 className="text-xl font-semibold mb-4">Purchase History</h2>
              {history.length === 0 ? (
                <p className="text-gray-500">No purchase history</p>
              ) : (
                <div className="space-y-3">
                  {history.map((purchase) => (
                    <div
                      key={purchase.id}
                      className="p-4 rounded-lg border-l-4 bg-gray-100 border-gray-400"
                    >
                      <div className="flex justify-between items-start">
                        <div>
                          <h3 className="font-semibold">{purchase.itemName}</h3>
                          <p className="text-gray-600">{purchase.merchantName}</p>
                        </div>
                        <div className="text-right">
                          <p className="text-sm text-gray-500">
                            Expired: {purchase.returnDeadline}
                          </p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                )}
                            </div>
                          )}

                          {/* Notifications */}
                          {activeTab === 'notifications' && (
                            <div>
                              <h2 className="text-xl font-semibold mb-4">Upcoming Notifications</h2>
                              {notifications.length === 0 ? (
                                <p className="text-gray-500">No upcoming notifications</p>
                              ) : (
                                <div className="space-y-3">
                                  {notifications.map((notification) => (
                                    <div
                                      key={notification.id}
                                      className="p-4 rounded-lg border bg-blue-50 border-blue-200"
                                    >
                                      <div className="flex justify-between items-start">
                                        <div>
                                          <h3 className="font-semibold">
                                            {notification.purchase.itemName}
                                          </h3>
                                          <p className="text-gray-600">
                                            {notification.type.replace(/_/g, ' ')}
                                          </p>
                                        </div>
                                        <div className="text-right">
                                          <p className="text-sm text-gray-500">
                                            Scheduled: {notification.scheduledFor.replace('T', ' ')}
                                          </p>
                                        </div>
                                      </div>
                                    </div>
                                  ))}
                                </div>
                              )}
                            </div>
                          )}

                          {/* Add Purchase Form */}
                          {activeTab === 'add' && (
                            <div>
                              <h2 className="text-xl font-semibold mb-4">Add New Purchase</h2>
                              <form onSubmit={handleSubmit} className="space-y-4">
                                <div>
                                  <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Merchant Name
                                  </label>
                                  <input
                                    type="text"
                                    value={merchantName}
                                    onChange={(e) => setMerchantName(e.target.value)}
                                    className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="e.g., Amazon, Target, Zara"
                                    required
                                  />
                                </div>
                                <div>
                                  <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Item Name
                                  </label>
                                  <input
                                    type="text"
                                    value={itemName}
                                    onChange={(e) => setItemName(e.target.value)}
                                    className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    placeholder="e.g., Headphones, Winter Coat"
                                    required
                                  />
                                </div>
                                <div>
                                  <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Purchase Date
                                  </label>
                                  <input
                                    type="date"
                                    value={purchaseDate}
                                    onChange={(e) => setPurchaseDate(e.target.value)}
                                    className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    required
                                  />
                                </div>
                                <div>
                                  <label className="block text-sm font-medium text-gray-700 mb-1">
                                    Return Window (days)
                                  </label>
                                  <input
                                    type="number"
                                    value={returnWindowDays}
                                    onChange={(e) => setReturnWindowDays(e.target.value)}
                                    className="w-full px-3 py-2 border rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                                    min="0"
                                    required
                                  />
                                </div>
                                <button
                                  type="submit"
                                  className="w-full bg-blue-600 text-white py-2 px-4 rounded-lg hover:bg-blue-700 transition-colors font-medium"
                                >
                                  Add Purchase
                                </button>
                              </form>
                            </div>
                          )}
                        </div>
                      </main>
                    </div>
                  )
                }

                export default App