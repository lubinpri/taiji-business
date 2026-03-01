"use client";
import { useState, useEffect } from 'react'

const API_URL = 'https://ofojgfoexlraecsviltn.supabase.co'
const API_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9mb2pnZm9leGxyYWVjc3ZpbHRuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzIzMjUzMDQsImV4cCI6MjA4NzkwMTMwNH0.l2xwlJQE4h0_Aidn_Bq72QGfq3AL9PmQ4VLoJhtS-X8'

type Opp = { id: string; name: string; customer_name: string; amount: number; stage: string; priority: string; contact_person: string; owner_id: string; description?: string; is_large_confirmed?: boolean }
type User = { id: string; name: string }

const STAGES = ['潜在客户','需求确认','方案报价','商务谈判','已签约','已失效']
const PRIORITIES = ['高','中','低']
const LARGE = 500000

export default function Page() {
  const [data, setData] = useState<Opp[]>([])
  const [users, setUsers] = useState<Record<string,string>>({})
  const [loading, setLoading] = useState(true)
  const [search, setSearch] = useState('')
  const [stage, setStage] = useState('')
  const [priority, setPriority] = useState('')
  const [pageSize, setPageSize] = useState(10)
  const [page, setPage] = useState(1)
  const [selected, setSelected] = useState<Opp | null>(null)
  const [editMode, setEditMode] = useState(false)
  const [editData, setEditData] = useState<Opp | null>(null)
  const [showNew, setShowNew] = useState(false)
  const [newData, setNewData] = useState({ name: '', customer: '', amount: 0, contact: '', priority: '中', desc: '', owner: '' })
  const [msg, setMsg] = useState('')
  const [role, setRole] = useState('sales')

  const canCreate = ['admin','sales_manager','sales'].includes(role)
  const canEditAll = ['admin','sales_manager'].includes(role)

  useEffect(() => { loadUsers() }, [])
  useEffect(() => { load() }, [page, pageSize, stage, priority, search])

  const loadUsers = async () => {
    const res = await fetch(API_URL + '/rest/v1/users?select=id,name', { headers: { 'apikey': API_KEY, 'Authorization': 'Bearer ' + API_KEY } })
    const ud = await res.json()
    if (Array.isArray(ud)) { const m: Record<string,string> = {}; ud.forEach((u: User) => m[u.id] = u.name); setUsers(m) }
    if (ud?.length > 0 && !newData.owner) setNewData(p => ({ ...p, owner: ud[0].id }))
  }

  const load = async () => {
    setLoading(true)
    let url = API_URL + '/rest/v1/opportunities?select=*&order=created_at.desc'
    if (search) url += '&or=(name.ilike.*' + encodeURIComponent(search) + '*,customer_name.ilike.*' + encodeURIComponent(search) + '*)'
    if (stage) url += '&stage=eq.' + stage
    if (priority) url += '&priority=eq.' + priority
    url += '&offset=' + ((page-1)*pageSize) + '&limit=' + pageSize
    const res = await fetch(url, { headers: { 'apikey': API_KEY, 'Authorization': 'Bearer ' + API_KEY } })
    const d = await res.json()
    if (Array.isArray(d)) setData(d)
    setLoading(false)
  }

  const handleCreate = async () => {
    if (!newData.name || !newData.customer || !newData.owner) { setMsg('请填写必填字段'); setTimeout(() => setMsg(''), 2000); return }
    const res = await fetch(API_URL + '/rest/v1/opportunities', {
      method: 'POST',
      headers: { 'apikey': API_KEY, 'Authorization': 'Bearer ' + API_KEY, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: newData.name, customer_name: newData.customer, amount: newData.amount,
        contact_person: newData.contact, priority: newData.priority, description: newData.desc,
        owner_id: newData.owner, stage: '潜在客户', is_large_confirmed: newData.amount >= LARGE
      })
    })
    if (res.ok) { setMsg('创建成功'); setShowNew(false); setNewData({ name: '', customer: '', amount: 0, contact: '', priority: '中', desc: '', owner: Object.keys(users)[0]||'' }); load() }
    else { setMsg('创建失败'); setTimeout(() => setMsg(''), 2000) }
  }

  const handleSave = async () => {
    if (!editData) return
    const res = await fetch(API_URL + '/rest/v1/opportunities?id=eq.' + editData.id, {
      method: 'PATCH',
      headers: { 'apikey': API_KEY, 'Authorization': 'Bearer ' + API_KEY, 'Content-Type': 'application/json' },
      body: JSON.stringify({
        name: editData.name, customer_name: editData.customer_name, contact_person: editData.contact_person,
        amount: editData.amount, stage: editData.stage, priority: editData.priority,
        description: editData.description, is_large_confirmed: editData.is_large_confirmed, owner_id: editData.owner_id
      })
    })
    if (res.ok) { setMsg('保存成功'); load(); setEditMode(false); setSelected(editData) }
    else { setMsg('保存失败'); setTimeout(() => setMsg(''), 2000) }
  }

  const handleDelete = async () => {
    if (!selected || !confirm('确定删除?')) return
    const res = await fetch(API_URL + '/rest/v1/opportunities?id=eq.' + selected.id, { method: 'DELETE', headers: { 'apikey': API_KEY, 'Authorization': 'Bearer ' + API_KEY } })
    if (res.ok) { setMsg('删除成功'); load(); setSelected(null) }
    else { setMsg('删除失败'); setTimeout(() => setMsg(''), 2000) }
  }

  const getU = (id: string) => users[id] || '-'
  const clr = (s: string) => ({'潜在客户':'bg-gray-200','需求确认':'bg-blue-200','方案报价':'bg-yellow-200','商务谈判':'bg-orange-200','已签约':'bg-green-200','已失效':'bg-red-200'}[s]||'bg-gray-200')
  const fmt = (v: number) => v ? (v >= 10000 ? (v/10000).toFixed(1) + '万' : v.toString()) + '元' : '-'
  
  const stats = STAGES.reduce((acc, s) => { const items = data.filter(o => o.stage === s); acc[s] = { count: items.length, amount: items.reduce((sum, o) => sum + (o.amount || 0), 0) }; return acc }, {} as Record<string, {count: number, amount: number}>)
  stats['全部'] = { count: data.length, amount: data.reduce((sum, o) => sum + (o.amount || 0), 0) }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow p-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold">商机管理</h1>
        <div className="flex gap-2">
          {msg && <div className="bg-green-100 text-green-800 px-4 py-2 rounded">{msg}</div>}
          {canCreate && <button onClick={() => setShowNew(true)} className="px-4 py-2 bg-blue-600 text-white rounded">+ 新建</button>}
          <select value={role} onChange={e => setRole(e.target.value)} className="border px-3 py-2 rounded text-sm">
            <option value="admin">管理员</option><option value="sales_manager">销售经理</option><option value="sales">销售</option><option value="assistant">助理</option>
          </select>
        </div>
      </header>
      <main className="p-4 max-w-7xl mx-auto">
        <div className="grid grid-cols-7 gap-2 mb-4">
          <button onClick={() => {setStage(''); setPage(1)}} className={'p-3 rounded shadow text-center ' + (!stage?'ring-2 ring-blue-500 bg-blue-50':'bg-white')}>
            <div className={'text-xl font-bold ' + (!stage?'text-blue-600':'')}>{stats.全部.count}</div>
            <div className="text-xs text-gray-600">全部</div>
            <div className="text-xs text-green-600 font-medium">{stats.全部.amount >= 10000 ? (stats.全部.amount/10000).toFixed(0) + '万' : stats.全部.amount}元</div>
          </button>
          {STAGES.map(s => (
            <button key={s} onClick={() => {setStage(s === stage ? '' : s); setPage(1)}} className={'p-3 rounded shadow text-center ' + (stage===s?'ring-2 ring-blue-500 bg-blue-50':'bg-white')}>
              <div className={'text-xl font-bold ' + (s==='已签约'?'text-green-600':s==='已失效'?'text-red-600':'')}>{stats[s].count}</div>
              <div className="text-xs text-gray-600">{s.replace('商务谈判','谈判').replace('已签约','签约')}</div>
              <div className={'text-xs ' + (s==='已签约'?'text-green-600':s==='已失效'?'text-red-600':'text-gray-500')}>{stats[s].amount >= 10000 ? (stats[s].amount/10000).toFixed(0) + '万' : stats[s].amount}元</div>
            </button>
          ))}
        </div>
        <div className="bg-white p-4 rounded shadow mb-4">
          <div className="flex gap-4 flex-wrap">
            <input type="text" placeholder="搜索商机/客户" value={search} onChange={e => {setSearch(e.target.value); setPage(1)}} className="border px-3 py-2 rounded flex-1 min-w-[200px]" />
            <select value={stage} onChange={e => {setStage(e.target.value); setPage(1)}} className="border px-3 py-2 rounded"><option value="">全部阶段</option>{STAGES.map(s => <option key={s} value={s}>{s}</option>)}</select>
            <select value={priority} onChange={e => {setPriority(e.target.value); setPage(1)}} className="border px-3 py-2 rounded"><option value="">全部优先级</option>{PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}</select>
            <select value={pageSize} onChange={e => {setPageSize(parseInt(e.target.value)); setPage(1)}} className="border px-3 py-2 rounded"><option value="10">10条/页</option><option value="20">20条/页</option><option value="30">30条/页</option></select>
          </div>
        </div>
        <div className="bg-white rounded shadow overflow-hidden">
          {loading ? <div className="p-8 text-center">加载中...</div> : data.length === 0 ? <div className="p-8 text-center">暂无数据</div> : (
            <table className="w-full">
              <thead className="bg-gray-100"><tr><th className="p-3 text-left">商机</th><th className="p-3 text-left">客户</th><th className="p-3 text-right">金额</th><th className="p-3 text-center">阶段</th><th className="p-3 text-center">优先级</th><th className="p-3 text-center">负责人</th><th className="p-3 text-center">操作</th></tr></thead>
              <tbody>
                {data.map((o, i) => {
                  const isLarge = o.amount >= LARGE
                  return (
                    <tr key={o.id} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                      <td className="p-3"><button onClick={() => {setSelected(o); setEditMode(false)}} className="text-left hover:text-blue-600 font-medium">{o.name}</button><div className="text-sm text-gray-500">{o.contact_person} {(isLarge || o.is_large_confirmed) && <span className="text-red-500 text-xs">大额</span>}</div></td>
                      <td className="p-3">{o.customer_name}</td>
                      <td className="p-3 text-right font-medium">{fmt(o.amount)}</td>
                      <td className="p-3 text-center"><span className={'px-2 py-1 rounded-full text-xs ' + clr(o.stage)}>{o.stage}</span></td>
                      <td className="p-3 text-center">{o.priority === '高' ? '🔴' : o.priority === '中' ? '🟡' : '🟢'}</td>
                      <td className="p-3 text-center">{getU(o.owner_id)}</td>
                      <td className="p-3 text-center">
                        <button onClick={() => {setSelected(o); setEditMode(true); setEditData({...o})}} className="text-blue-600 mr-2">编辑</button>
                        {canEditAll && <button onClick={() => {setSelected(o); setEditMode(false)}} className="text-green-600 mr-2">转派</button>}
                        <button onClick={() => {setSelected(o); setEditMode(false)}} className="text-gray-600">详情</button>
                      </td>
                    </tr>
                  )
                })}
              </tbody>
            </table>
          )}
        </div>
      </main>

      {/* 新建弹窗 */}
      {showNew && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50" onClick={() => setShowNew(false)}>
          <div className="bg-white rounded-lg shadow-xl max-w-xl w-full p-6" onClick={e => e.stopPropagation()}>
            <div className="flex justify-between mb-4"><h2 className="text-xl font-bold">新建商机</h2><button onClick={() => setShowNew(false)} className="text-2xl text-gray-500">×</button></div>
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4">
                <div><label className="text-sm text-gray-500">商机名称*</label><input value={newData.name} onChange={e => setNewData({...newData, name: e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
                <div><label className="text-sm text-gray-500">客户名称*</label><input value={newData.customer} onChange={e => setNewData({...newData, customer: e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
                <div><label className="text-sm text-gray-500">联系人</label><input value={newData.contact} onChange={e => setNewData({...newData, contact: e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
                <div><label className="text-sm text-gray-500">金额*</label><input type="number" value={newData.amount} onChange={e => setNewData({...newData, amount: parseInt(e.target.value)||0})} className="w-full border px-3 py-2 rounded" /></div>
                <div><label className="text-sm text-gray-500">负责人*</label><select value={newData.owner} onChange={e => setNewData({...newData, owner: e.target.value})} className="w-full border px-3 py-2 rounded">{Object.entries(users).map(([id,n]) => <option key={id} value={id}>{n}</option>)}</select></div>
                <div><label className="text-sm text-gray-500">优先级</label><select value={newData.priority} onChange={e => setNewData({...newData, priority: e.target.value})} className="w-full border px-3 py-2 rounded">{PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}</select></div>
              </div>
              <div><label className="text-sm text-gray-500">描述</label><textarea value={newData.desc} onChange={e => setNewData({...newData, desc: e.target.value})} className="w-full border px-3 py-2 rounded h-20" /></div>
            </div>
            <div className="mt-6 flex gap-2"><button onClick={handleCreate} className="px-4 py-2 bg-blue-600 text-white rounded">创建</button><button onClick={() => setShowNew(false)} className="px-4 py-2 bg-gray-200 rounded">取消</button></div>
          </div>
        </div>
      )}

      {/* 详情/编辑弹窗 */}
      {selected && !showNew && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4 z-50" onClick={() => setSelected(null)}>
          <div className="bg-white rounded-lg shadow-xl max-w-xl w-full p-6 max-h-[80vh] overflow-auto" onClick={e => e.stopPropagation()}>
            <div className="flex justify-between mb-4"><h2 className="text-xl font-bold">{editMode ? '编辑商机' : '商机详情'}</h2><button onClick={() => setSelected(null)} className="text-2xl text-gray-500">×</button></div>
            
            {editMode && editData ? (
              <div className="space-y-4">
                <div className="grid grid-cols-2 gap-4">
                  <div><label className="text-sm text-gray-500">名称</label><input value={editData.name} onChange={e => setEditData({...editData, name: e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
                  <div><label className="text-sm text-gray-500">客户</label><input value={editData.customer_name} onChange={e => setEditData({...editData, customer_name: e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
                  <div><label className="text-sm text-gray-500">联系人</label><input value={editData.contact_person||''} onChange={e => setEditData({...editData, contact_person: e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
                  <div><label className="text-sm text-gray-500">金额</label><input type="number" value={editData.amount} onChange={e => setEditData({...editData, amount: parseInt(e.target.value)||0})} className="w-full border px-3 py-2 rounded" /></div>
                  <div><label className="text-sm text-gray-500">阶段</label><select value={editData.stage} onChange={e => setEditData({...editData, stage: e.target.value})} className="w-full border px-3 py-2 rounded">{STAGES.map(s => <option key={s} value={s}>{s}</option>)}</select></div>
                  <div><label className="text-sm text-gray-500">优先级</label><select value={editData.priority||''} onChange={e => setEditData({...editData, priority: e.target.value})} className="w-full border px-3 py-2 rounded">{PRIORITIES.map(p => <option key={p} value={p}>{p}</option>)}</select></div>
                  <div><label className="text-sm text-gray-500">负责人</label><select value={editData.owner_id} onChange={e => setEditData({...editData, owner_id: e.target.value})} className="w-full border px-3 py-2 rounded">{Object.entries(users).map(([id,n]) => <option key={id} value={id}>{n}</option>)}</select></div>
                  <div className="flex items-center"><label className="text-sm text-gray-500 mr-2">已确认大额:</label><input type="checkbox" checked={editData.is_large_confirmed||false} onChange={e => setEditData({...editData, is_large_confirmed: e.target.checked})} /></div>
                </div>
                <div><label className="text-sm text-gray-500">描述</label><textarea value={editData.description||''} onChange={e => setEditData({...editData, description: e.target.value})} className="w-full border px-3 py-2 rounded h-20" /></div>
              </div>
            ) : (
              <div className="grid grid-cols-2 gap-4">
                <div><div className="text-gray-500 text-sm">客户</div><div className="font-medium">{selected.customer_name}</div></div>
                <div><div className="text-gray-500 text-sm">联系人</div><div className="font-medium">{selected.contact_person}</div></div>
                <div><div className="text-gray-500 text-sm">金额</div><div className="font-medium text-lg text-green-600">{fmt(selected.amount)} {selected.is_large_confirmed && <span className="text-xs text-red-500">已确认</span>}</div></div>
                <div><div className="text-gray-500 text-sm">优先级</div><div className="font-medium">{selected.priority === '高' ? '🔴高' : selected.priority === '中' ? '🟡中' : '🟢低'}</div></div>
                <div><div className="text-gray-500 text-sm">阶段</div><div className="font-medium"><span className={'px-2 py-1 rounded ' + clr(selected.stage)}>{selected.stage}</span></div></div>
                <div><div className="text-gray-500 text-sm">负责人</div><div className="font-medium">{getU(selected.owner_id)}</div></div>
                <div className="col-span-2"><div className="text-gray-500 text-sm">描述</div><div className="font-medium">{selected.description || '-'}</div></div>
              </div>
            )}

            <div className="mt-6 flex gap-2 flex-wrap">
              {editMode ? (
                <><button onClick={handleSave} className="px-4 py-2 bg-blue-600 text-white rounded">保存</button><button onClick={() => {setEditMode(false); setEditData(null)}} className="px-4 py-2 bg-gray-200 rounded">取消</button></>
              ) : (
                <><button onClick={() => {setEditMode(true); setEditData({...selected})}} className="px-4 py-2 bg-blue-600 text-white rounded">编辑</button>
                {canEditAll && <><button onClick={handleDelete} className="px-4 py-2 bg-red-600 text-white rounded">删除</button><button className="px-4 py-2 bg-green-600 text-white rounded">转派</button></>}
                <button onClick={() => setSelected(null)} className="px-4 py-2 bg-gray-200 rounded">关闭</button></>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
