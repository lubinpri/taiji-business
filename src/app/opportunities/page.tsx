"use client";
import { useState, useEffect } from 'react'

const API_URL = 'https://ofojgfoexlraecsviltn.supabase.co'
const API_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9mb2pnZm9leGxyYWVjc3ZpbHRuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzIzMjUzMDQsImV4cCI6MjA4NzkwMTMwNH0.l2xwlJQE4h0_Aidn_Bq72QGfq3AL9PmQ4VLoJhtS-X8'

type Opp = { id: string; name: string; customer_name: string; amount: number; stage: string; priority: string; contact_person: string; owner_id: string; description?: string; is_large_confirmed?: boolean }
type User = { id: string; name: string }

const STAGES = ['潜在客户','需求确认','方案报价','商务谈判','已签约','已失效']
const PRIORITIES = ['高','中','低']

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

  useEffect(() => { loadUsers() }, [])
  useEffect(() => { load() }, [page, pageSize, stage, priority, search])

  const loadUsers = async () => {
    const res = await fetch(API_URL + '/rest/v1/users?select=id,name', { headers: { 'apikey': API_KEY, 'Authorization': 'Bearer ' + API_KEY } })
    const ud = await res.json()
    if (Array.isArray(ud)) { const m: Record<string,string> = {}; ud.forEach((u: User) => m[u.id] = u.name); setUsers(m) }
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

  const getU = (id: string) => users[id] || '-'
  const clr = (s: string) => ({'潜在客户':'bg-gray-200','需求确认':'bg-blue-200','方案报价':'bg-yellow-200','商务谈判':'bg-orange-200','已签约':'bg-green-200','已失效':'bg-red-200'}[s]||'bg-gray-200')
  const fmt = (v: number) => v ? (v >= 10000 ? (v/10000).toFixed(1) + '万' : v.toString()) + '元' : '-'
  
  const stats = STAGES.reduce((acc, s) => { const items = data.filter(o => o.stage === s); acc[s] = { count: items.length, amount: items.reduce((sum, o) => sum + (o.amount || 0), 0) }; return acc }, {} as Record<string, {count: number, amount: number}>)
  stats['全部'] = { count: data.length, amount: data.reduce((sum, o) => sum + (o.amount || 0), 0) }

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow p-4"><h1 className="text-2xl font-bold">商机管理</h1></header>
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
              <thead className="bg-gray-100"><tr><th className="p-3 text-left">商机名称</th><th className="p-3 text-left">客户</th><th className="p-3 text-right">金额</th><th className="p-3 text-center">阶段</th><th className="p-3 text-center">优先级</th><th className="p-3 text-center">负责人</th></tr></thead>
              <tbody>
                {data.map((o, i) => (
                  <tr key={o.id} className={i % 2 === 0 ? 'bg-white' : 'bg-gray-50'}>
                    <td className="p-3"><button onClick={() => setSelected(o)} className="text-left hover:text-blue-600 font-medium">{o.name}</button><div className="text-sm text-gray-500">{o.contact_person} {o.is_large_confirmed && <span className="text-red-500 text-xs">大额</span>}</div></td>
                    <td className="p-3">{o.customer_name}</td>
                    <td className="p-3 text-right font-medium">{fmt(o.amount)}</td>
                    <td className="p-3 text-center"><span className={'px-2 py-1 rounded-full text-xs ' + clr(o.stage)}>{o.stage}</span></td>
                    <td className="p-3 text-center">{o.priority === '高' ? '🔴' : o.priority === '中' ? '🟡' : '🟢'}</td>
                    <td className="p-3 text-center">{getU(o.owner_id)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </main>
      {selected && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center p-4" onClick={() => setSelected(null)}>
          <div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full" onClick={e => e.stopPropagation()}>
            <div className="flex justify-between items-start mb-4"><h2 className="text-xl font-bold">{selected.name}</h2><button onClick={() => setSelected(null)} className="text-gray-500 text-2xl">×</button></div>
            <div className="space-y-2">
              <div><span className="text-gray-500">客户:</span> {selected.customer_name}</div>
              <div><span className="text-gray-500">联系人:</span> {selected.contact_person}</div>
              <div><span className="text-gray-500">金额:</span> <span className="text-green-600 font-bold">{fmt(selected.amount)}</span></div>
              <div><span className="text-gray-500">阶段:</span> <span className={'px-2 py-1 rounded ' + clr(selected.stage)}>{selected.stage}</span></div>
              <div><span className="text-gray-500">优先级:</span> {selected.priority === '高' ? '🔴高' : selected.priority === '中' ? '🟡中' : '🟢低'}</div>
              <div><span className="text-gray-500">负责人:</span> {getU(selected.owner_id)}</div>
              <div><span className="text-gray-500">描述:</span> {selected.description || '-'}</div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
