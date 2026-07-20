import { useState, useEffect } from 'react';
import './App.css';

function App() {
  // Auth State
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('');
  const [authHeader, setAuthHeader] = useState('');
  const [loginError, setLoginError] = useState('');

  // Asset & Form State
  const [assets, setAssets] = useState([]);
  const [assetName, setAssetName] = useState('');
  const [amount, setAmount] = useState('');

  // Filter, Sort & Pagination State
  const [searchName, setSearchName] = useState('');
  const [minAmount, setMinAmount] = useState('');
  const [sortField, setSortField] = useState('amount');
  const [sortDirection, setSortDirection] = useState('desc');
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  const API_URL = 'http://localhost:8080/api/assets';

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoginError('');

    const token = btoa(`${username}:${password}`);
    const generatedAuthHeader = `Basic ${token}`;

    try {
      const response = await fetch(`${API_URL}/search?page=0&size=5&sort=amount,desc`, {
        headers: { 'Authorization': generatedAuthHeader }
      });

      if (response.ok) {
        setAuthHeader(generatedAuthHeader);
        setIsLoggedIn(true);
        setRole(username === 'admin' ? 'ADMIN' : 'USER');

        const data = await response.json();
        setAssets(data.content);
        setTotalPages(data.totalPages);
      } else if (response.status === 401) {
        setLoginError('Kullanıcı adı veya şifre hatalı!');
      } else {
        setLoginError('Sunucu bağlantı hatası oluştu.');
      }
    } catch (error) {
      setLoginError('Backend sunucusu çalışmıyor olabilir!');
    }
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setUsername('');
    setPassword('');
    setAuthHeader('');
    setRole('');
    setAssets([]);
    setPage(0);
  };

  const fetchAssets = async () => {
    if (!authHeader) return;

    let url = `${API_URL}/search?page=${page}&size=5&sort=${sortField},${sortDirection}`;
    if (searchName) url += `&name=${encodeURIComponent(searchName)}`;
    if (minAmount) url += `&minAmount=${minAmount}`;

    try {
      const response = await fetch(url, {
        headers: { 'Authorization': authHeader }
      });

      if (response.ok) {
        const data = await response.json();
        setAssets(data.content);
        setTotalPages(data.totalPages);
      }
    } catch (error) {
      console.error("Veri çekme hatası:", error);
    }
  };

  useEffect(() => {
    if (isLoggedIn) {
      fetchAssets();
    }
  }, [page, sortField, sortDirection, isLoggedIn]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchAssets();
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!assetName || !amount) return;

    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': authHeader
        },
        body: JSON.stringify({ assetName, amount: parseFloat(amount) }),
      });

      if (response.ok) {
        setAssetName('');
        setAmount('');
        fetchAssets();
      } else {
        const errData = await response.json();
        alert(`Hata: ${errData.message || 'Ekleme başarısız'}`);
      }
    } catch (error) {
      console.error("Ekleme hatası:", error);
    }
  };

  const handleDelete = async (id) => {
    try {
      const response = await fetch(`${API_URL}/${id}`, {
        method: 'DELETE',
        headers: { 'Authorization': authHeader }
      });

      if (response.ok) {
        alert("Varlık başarıyla silindi!");
        fetchAssets();
      } else if (response.status === 403) {
        alert("YETKİSİZ İŞLEM: Sadece ADMIN rolüne sahip kullanıcılar varlık silebilir!");
      } else {
        alert("Silme işlemi sırasında bir hata oluştu.");
      }
    } catch (error) {
      console.error("Silme hatası:", error);
    }
  };

  if (!isLoggedIn) {
    return (
      <div style={{ padding: '40px', fontFamily: 'sans-serif', maxWidth: '400px', margin: '50px auto', border: '1px solid #ddd', borderRadius: '8px' }}>
        <h2 style={{ textAlign: 'center' }}>Yatırım Cüzdanı Giriş</h2>
        {loginError && <p style={{ color: 'red', textAlign: 'center' }}>{loginError}</p>}
        <form onSubmit={handleLogin} style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          <input
            type="text" placeholder="Kullanıcı Adı (user/admin)" value={username}
            onChange={(e) => setUsername(e.target.value)} style={{ padding: '10px' }} required
          />
          <input
            type="password" placeholder="Şifre" value={password}
            onChange={(e) => setPassword(e.target.value)} style={{ padding: '10px' }} required
          />
          <button type="submit" style={{ padding: '10px', backgroundColor: '#007bff', color: 'white', border: 'none', borderRadius: '4px' }}>
            Giriş Yap
          </button>
        </form>
      </div>
    );
  }

  return (
    <div style={{ padding: '30px', fontFamily: 'sans-serif', maxWidth: '700px', margin: '0 auto' }}>
      {/* Top Bar */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', backgroundColor: '#f1f3f5', padding: '10px 15px', borderRadius: '6px' }}>
        <span>Kullanıcı: <strong>{username}</strong> (<b style={{ color: role === 'ADMIN' ? 'red' : 'green' }}>{role}</b>)</span>
        <button onClick={handleLogout} style={{ backgroundColor: '#dc3545', color: 'white', border: 'none', padding: '6px 12px', borderRadius: '4px', cursor: 'pointer' }}>
          Çıkış Yap
        </button>
      </div>

      <h2>Yatırım Cüzdanı (Wallet Management)</h2>


      <form onSubmit={handleSubmit} style={{ display: 'flex', gap: '10px', marginBottom: '20px' }}>
        <input
          type="text" placeholder="Varlık Adı (Örn: Bitcoin)" value={assetName}
          onChange={(e) => setAssetName(e.target.value)} style={{ flex: 1, padding: '8px' }}
        />
        <input
          type="number" step="any" placeholder="Miktar" value={amount}
          onChange={(e) => setAmount(e.target.value)} style={{ width: '100px', padding: '8px' }}
        />
        <button type="submit" style={{ backgroundColor: '#28a745', color: 'white', border: 'none', padding: '8px 16px', borderRadius: '4px' }}>
          Ekle
        </button>
      </form>


      <div style={{ backgroundColor: '#e9ecef', padding: '15px', borderRadius: '6px', marginBottom: '20px' }}>
        <h4 style={{ margin: '0 0 10px 0' }}>Arama & Sıralama Filtreleri</h4>
        <form onSubmit={handleSearchSubmit} style={{ display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
          <input
            type="text" placeholder="İsme göre ara..." value={searchName}
            onChange={(e) => setSearchName(e.target.value)} style={{ padding: '6px' }}
          />
          <input
            type="number" placeholder="Min Bakiye" value={minAmount}
            onChange={(e) => setMinAmount(e.target.value)} style={{ width: '100px', padding: '6px' }}
          />

          <select value={sortField} onChange={(e) => setSortField(e.target.value)} style={{ padding: '6px' }}>
            <option value="amount">Miktara Göre</option>
            <option value="assetName">İsme Göre</option>
          </select>

          <select value={sortDirection} onChange={(e) => setSortDirection(e.target.value)} style={{ padding: '6px' }}>
            <option value="desc">Azalan (Tavan → Taban)</option>
            <option value="asc">Artan (Taban → Tavan)</option>
          </select>

          <button type="submit" style={{ backgroundColor: '#17a2b8', color: 'white', border: 'none', padding: '6px 12px', borderRadius: '4px' }}>
            Filtrele
          </button>
        </form>
      </div>


      <h3>Varlıklarınız</h3>
      {assets.length === 0 ? (
        <p>Aramanıza uygun varlık bulunamadı.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {assets.map((asset) => (
            <li key={asset.id} style={{ display: 'flex', justifyContent: 'space-between', padding: '10px', borderBottom: '1px solid #ddd' }}>
              <span><strong>{asset.assetName}</strong>: {asset.amount}</span>
              <button
                onClick={() => handleDelete(asset.id)}
                style={{ backgroundColor: '#dc3545', color: 'white', border: 'none', padding: '4px 8px', borderRadius: '4px', opacity: role === 'ADMIN' ? 1 : 0.6 }}
              >
                Sil
              </button>
            </li>
          ))}
        </ul>
      )}


      {totalPages > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '15px', marginTop: '20px' }}>
          <button
            disabled={page === 0}
            onClick={() => setPage((prev) => prev - 1)}
            style={{ padding: '6px 12px' }}
          >
            &laquo; Önceki
          </button>
          <span>Sayfa {page + 1} / {totalPages}</span>
          <button
            disabled={page + 1 >= totalPages}
            onClick={() => setPage((prev) => prev + 1)}
            style={{ padding: '6px 12px' }}
          >
            Sonraki &raquo;
          </button>
        </div>
      )}
    </div>
  );
}

export default App;