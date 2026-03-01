"use client";
import { useState, useEffect } from 'react'

const API_URL = 'https://ofojgfoexlraecsviltn.supabase.co'
const API_KEY = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im9mb2pnZm9leGxyYWVjc3ZpbHRuIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzIzMjUzMDQsImV4cCI6MjA4NzkwMTMwNH0.l2xwlJQE4h0_Aidn_Bq72QGfq3AL9PmQ4VLoJhtS-X8'

type Opp = { id: string; name: string; customer_name: string; amount: number; stage: string; priority: string; contact_person: string; owner_id: string; description?: string; is_large_confirmed?: boolean }
type User = { id: string; name: string }

const STAGES = ['潜在客户','需求确认','方案报价','商务谈判','已签约','已失效']
const PRIORITIES = ['高','中','低']
const FOLLOW = ['电话','面谈','微信','邮件','其他']
const LARGE = 500000

export default function Page() {
  const [data, setData] = useState<Opp[]>([])
  const [users, setUsers] = useState<User[]>([])
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
  const [showTrans, setShowTrans] = useState(false)
  const [transTo, setTransTo] = useState('')
  const [msg, setMsg] = useState('')
  const [role, setRole] = useState('sales')
  const [tab, setTab] = useState<'info'|'follow'|'quote'>('info')
  const [follows, setFollows] = useState<{id:string,method:string,content:string,next_date?:string,created_at:string,attachment_url?:string}[]>([])
  const [quotes, setQuotes] = useState<{id:string,amount:number,description?:string,created_at:string,file_url?:string}[]>([])
  const [showF, setShowF] = useState(false)
  const [showQ, setShowQ] = useState(false)
  const [fData, setFData] = useState({ method: '电话', content: '', next: '', file: null as File | null })
  const [qData, setQData] = useState({ amount: 0, desc: '', file: null as File | null })
  const [saving, setSaving] = useState(false)

  const canCreate = ['admin','sales_manager','sales'].includes(role)
  const canAll = ['admin','sales_manager'].includes(role)

  useEffect(()=>{loadUsers()},[])
  useEffect(()=>{load()},[page,pageSize,stage,priority,search])
  useEffect(()=>{if(selected?.id)loadDetail()},[selected?.id])

  const loadUsers = async()=>{
    const r = await fetch(API_URL+'/rest/v1/users?select=id,name',{headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY}})
    const u = await r.json()
    if(Array.isArray(u)){setUsers(u);if(u.length>0&&!newData.owner)setNewData(p=>({...p,owner:u[0].id}))}
  }

  const load = async()=>{
    setLoading(true)
    let url = API_URL+'/rest/v1/opportunities?select=*&order=created_at.desc'
    if(search)url+='&or=(name.ilike.*'+search+'*,customer_name.ilike.*'+search+'*)'
    if(stage)url+='&stage=eq.'+stage
    if(priority)url+='&priority=eq.'+priority
    url+='&offset='+((page-1)*pageSize)+'&limit='+pageSize
    const r = await fetch(url,{headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY}})
    const d = await r.json()
    if(Array.isArray(d))setData(d)
    setLoading(false)
  }

  const loadDetail = async()=>{
    if(!selected)return
    const[fr,qr]=await Promise.all([
      fetch(API_URL+'/rest/v1/follow_ups?opportunity_id=eq.'+selected.id+'&order=created_at.desc',{headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY}}),
      fetch(API_URL+'/rest/v1/quotes?opportunity_id=eq.'+selected.id+'&order=created_at.desc',{headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY}})
    ])
    const f = await fr.json()
    const q = await qr.json()
    if(Array.isArray(f))setFollows(f)
    if(Array.isArray(q))setQuotes(q)
  }

  const handleNew = async()=>{
    if(!newData.name||!newData.customer||!newData.owner){setMsg('请填必填');setTimeout(()=>setMsg(''),2000);return}
    setSaving(true)
    const r = await fetch(API_URL+'/rest/v1/opportunities',{method:'POST',headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY,'Content-Type':'application/json'},body:JSON.stringify({name:newData.name,customer_name:newData.customer,amount:newData.amount,contact_person:newData.contact,priority:newData.priority,description:newData.desc,owner_id:newData.owner,stage:'潜在客户',is_large_confirmed:newData.amount>=LARGE})})
    if(r.ok){setMsg('创建成功');setShowNew(false);setNewData({name:'',customer:'',amount:0,contact:'',priority:'中',desc:'',owner:users[0]?.id||''});load()}
    else{const e=await r.json();setMsg('创建失败:'+(e.message||''));setTimeout(()=>setMsg(''),3000)}
    setSaving(false)
  }

  const handleSave = async()=>{
    if(!editData)return
    setSaving(true)
    const r = await fetch(API_URL+'/rest/v1/opportunities?id=eq.'+editData.id,{method:'PATCH',headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY,'Content-Type':'application/json'},body:JSON.stringify({name:editData.name,customer_name:editData.customer_name,contact_person:editData.contact_person,amount:editData.amount,stage:editData.stage,priority:editData.priority,description:editData.description,is_large_confirmed:editData.is_large_confirmed,owner_id:editData.owner_id})})
    if(r.ok){setMsg('保存成功');load();setEditMode(false);setSelected(editData)}
    else{setMsg('保存失败');setTimeout(()=>setMsg(''),2000)}
    setSaving(false)
  }

  const handleDel = async()=>{
    if(!selected||!confirm('删除?'))return
    const r = await fetch(API_URL+'/rest/v1/opportunities?id=eq.'+selected.id,{method:'DELETE',headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY}})
    if(r.ok){setMsg('删除成功');load();setSelected(null)}else{setMsg('删除失败');setTimeout(()=>setMsg(''),2000)}
  }

  const handleTrans = async()=>{
    if(!selected||!transTo)return
    const r = await fetch(API_URL+'/rest/v1/opportunities?id=eq.'+selected.id,{method:'PATCH',headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY,'Content-Type':'application/json'},body:JSON.stringify({owner_id:transTo})})
    if(r.ok){setMsg('转派成功');load();setShowTrans(false);setTransTo('');setSelected({...selected,owner_id:transTo})}else{setMsg('转派失败');setTimeout(()=>setMsg(''),2000)}
  }

  const addFollow = async()=>{
    if(!selected||!fData.content)return
    setSaving(true)
    const r = await fetch(API_URL+'/rest/v1/follow_ups',{method:'POST',headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY,'Content-Type':'application/json'},body:JSON.stringify({opportunity_id:selected.id,method:fData.method,content:fData.content,next_date:fData.next||null})})
    if(r.ok){setMsg('添加成功');setShowF(false);setFData({method:'电话',content:'',next:'',file:null});loadDetail()}
    else{setMsg('添加失败');setTimeout(()=>setMsg(''),2000)}
    setSaving(false)
  }

  const addQuote = async()=>{
    if(!selected||!qData.amount)return
    setSaving(true)
    const r = await fetch(API_URL+'/rest/v1/quotes',{method:'POST',headers:{'apikey':API_KEY,'Authorization':'Bearer '+API_KEY,'Content-Type':'application/json'},body:JSON.stringify({opportunity_id:selected.id,amount:qData.amount,description:qData.desc})})
    if(r.ok){setMsg('添加成功');setShowQ(false);setQData({amount:0,desc:'',file:null});loadDetail()}
    else{setMsg('添加失败');setTimeout(()=>setMsg(''),2000)}
    setSaving(false)
  }

  const getU = (id:string)=>users.find(u=>u.id===id)?.name||'-'
  const clr = (s:string)=>({'潜在客户':'bg-gray-200','需求确认':'bg-blue-200','方案报价':'bg-yellow-200','商务谈判':'bg-orange-200','已签约':'bg-green-200','已失效':'bg-red-200'}[s]||'bg-gray-200')
  const fmt = (v:number)=>v?(v>=10000?(v/10000).toFixed(1)+'万':v.toString())+'元':'-'
  const fmtD = (d:string)=>d?new Date(d).toLocaleDateString('zh-CN'):'-'

  const stats = STAGES.reduce((a,s)=>{const i=data.filter(o=>o.stage===s);a[s]={c:i.length,a:i.reduce((t,o)=>t+(o.amount||0),0)};return a},{} as Record<string,{c:number,a:number}>)
  stats['全部']={c:data.length,a:data.reduce((t,o)=>t+(o.amount||0),0)}

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow p-4 flex justify-between items-center">
        <h1 className="text-2xl font-bold">商机管理</h1>
        <div className="flex gap-2">
          {msg&&<div className="bg-green-100 text-green-800 px-4 py-2 rounded">{msg}</div>}
          {canCreate&&users.length>0&&<button onClick={()=>setShowNew(true)} className="px-4 py-2 bg-blue-600 text-white rounded">+ 新建</button>}
          <select value={role} onChange={e=>setRole(e.target.value)} className="border px-3 py-2 rounded text-sm">
            <option value="admin">管理员</option><option value="sales_manager">销售经理</option><option value="sales">销售</option><option value="assistant">助理</option>
          </select>
        </div>
      </header>
      <main className="p-4 max-w-7xl mx-auto">
        <div className="grid grid-cols-7 gap-2 mb-4">
          <button onClick={()=>{setStage('');setPage(1)}} className={'p-3 rounded shadow text-center '+(!stage?'ring-2 ring-blue-500 bg-blue-50':'bg-white')}><div className={'text-xl font-bold '+(!stage?'text-blue-600':'')}>{stats.全部.c}</div><div className="text-xs">全部</div><div className="text-xs text-green-600">{stats.全部.a>=10000?(stats.全部.a/10000).toFixed(0)+'万':stats.全部.a}元</div></button>
          {STAGES.map(s=><button key={s} onClick={()=>{setStage(s===stage?'':s);setPage(1)}} className={'p-3 rounded shadow text-center '+(stage===s?'ring-2 ring-blue-500 bg-blue-50':'bg-white')}><div className={'text-xl font-bold '+(s==='已签约'?'text-green-600':s==='已失效'?'text-red-600':'')}>{stats[s].c}</div><div className="text-xs">{s.replace('商务谈判','谈判').replace('已签约','签约')}</div><div className={'text-xs '+(s==='已签约'?'text-green-600':s==='已失效'?'text-red-600':'text-gray-500')}>{stats[s].a>=10000?(stats[s].a/10000).toFixed(0)+'万':stats[s].a}元</div></button>)}
        </div>
        <div className="bg-white p-4 rounded shadow mb-4">
          <div className="flex gap-4 flex-wrap">
            <input type="text" placeholder="搜索" value={search} onChange={e=>{setSearch(e.target.value);setPage(1)}} className="border px-3 py-2 rounded flex-1 min-w-[200px]" />
            <select value={stage} onChange={e=>{setStage(e.target.value);setPage(1)}} className="border px-3 py-2 rounded"><option value="">全部</option>{STAGES.map(s=><option key={s} value={s}>{s}</option>)}</select>
            <select value={priority} onChange={e=>{setPriority(e.target.value);setPage(1)}} className="border px-3 py-2 rounded"><option value="">全部</option>{PRIORITIES.map(p=><option key={p} value={p}>{p}</option>)}</select>
            <select value={pageSize} onChange={e=>{setPageSize(parseInt(e.target.value));setPage(1)}} className="border px-3 py-2 rounded"><option value="10">10条</option><option value="20">20条</option><option value="30">30条</option></select>
          </div>
        </div>
        <div className="bg-white rounded shadow overflow-hidden">
          {loading?<div className="p-8 text-center">加载中...</div>:data.length===0?<div className="p-8 text-center">暂无</div>:(
            <table className="w-full">
              <thead className="bg-gray-100"><tr><th className="p-3 text-left">商机</th><th className="p-3 text-left">客户</th><th className="p-3 text-right">金额</th><th className="p-3 text-center">阶段</th><th className="p-3 text-center">优先级</th><th className="p-3 text-center">负责人</th><th className="p-3 text-center">操作</th></tr></thead>
              <tbody>
                {data.map((o,i)=>{const big=o.amount>=LARGE;return(
                  <tr key={o.id} className={i%2===0?'bg-white':'bg-gray-50'}>
                    <td className="p-3"><button onClick={()=>{setSelected(o);setEditMode(false);setTab('info')}} className="text-left hover:text-blue-600 font-medium">{o.name}</button><div className="text-sm text-gray-500">{o.contact_person} {(big||o.is_large_confirmed)&&<span className="text-red-500 text-xs">大额</span>}</div></td>
                    <td className="p-3">{o.customer_name}</td>
                    <td className="p-3 text-right font-medium">{fmt(o.amount)}</td>
                    <td className="p-3 text-center"><span className={'px-2 py-1 rounded-full text-xs '+clr(o.stage)}>{o.stage}</span></td>
                    <td className="p-3 text-center">{o.priority==='高'?'🔴':o.priority==='中'?'🟡':'🟢'}</td>
                    <td className="p-3 text-center">{getU(o.owner_id)}</td>
                    <td className="p-3 text-center"><button onClick={()=>{setSelected(o);setEditMode(true);setEditData({...o});setTab('info')}} className="text-blue-600 mr-2">编辑</button>{canAll&&<button onClick={()=>{setSelected(o);setShowTrans(true)}} className="text-green-600 mr-2">转派</button>}<button onClick={()=>{setSelected(o);setEditMode(false);setTab('info')}} className="text-gray-600">详情</button></td>
                  </tr>
                )})}
              </tbody>
            </table>
          )}
        </div>
      </main>

      {showNew&&(<div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50" onClick={()=>!saving&&setShowNew(false)}><div className="bg-white rounded-lg shadow-xl max-w-xl w-full p-6" onClick={e=>e.stopPropagation()}>
        <div className="flex justify-between mb-4"><h2 className="text-xl font-bold">新建</h2><button onClick={()=>setShowNew(false)} className="text-2xl">&times;</button></div>
        <div className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div><label className="text-sm">名称*</label><input value={newData.name} onChange={e=>setNewData({...newData,name:e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
            <div><label className="text-sm">客户*</label><input value={newData.customer} onChange={e=>setNewData({...newData,customer:e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
            <div><label className="text-sm">联系人</label><input value={newData.contact} onChange={e=>setNewData({...newData,contact:e.target.value})} className="w-full border px-3 py-2 rounded" /></div>
            <div><label className="text-sm">金额*</label><input type="number" value={newData.amount} onChange={e=>setNewData({...newData,amount:parseInt(e.target.value)||0})} className="w-full border px-3 py-2 rounded" /></div>
            <div><label className="text-sm">负责人*</label><select value={newData.owner} onChange={e=>setNewData({...newData,owner:e.target.value})} className="w-full border px-3 py-2 rounded">{users.map(u=><option key={u.id} value={u.id}>{u.name}</option>)}</select></div>
            <div><label className="text-sm">优先级</label><select value={newData.priority} onChange={e=>setNewData({...newData,priority:e.target.value})} className="w-full border px-3 py-2 rounded">{PRIORITIES.map(p=><option key={p} value={p}>{p}</option>)}</select></div>
          </div>
          <div><label className="text-sm">描述</label><textarea value={newData.desc} onChange={e=>setNewData({...newData,desc:e.target.value})} className="w-full border px-3 py-2 rounded h-20" /></div>
        </div>
        <div className="mt-6 flex gap-2"><button onClick={handleNew} disabled={saving} className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50">{saving?'提交中...':'创建'}</button><button onClick={()=>setShowNew(false)} disabled={saving} className="px-4 py-2 bg-gray-200 rounded">取消</button></div>
      </div></div>)}

      {selected&&!showNew&&!showTrans&&(<div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50" onClick={()=>setSelected(null)}><div className="bg-white rounded-lg shadow-xl max-w-2xl w-full p-6 max-h-[85vh] overflow-auto" onClick={e=>e.stopPropagation()}>
        <div className="flex justify-between mb-4"><h2 className="text-xl font-bold">{editMode?'编辑':'详情'}</h2><button onClick={()=>setSelected(null)} className="text-2xl">&times;</button></div>
        <div className="flex border-b mb-4">
          <button onClick={()=>setTab('info')} className={'px-4 py-2 '+(tab==='info'?'border-b-2 border-blue-500 text-blue-600':'text-gray-500')}>基本信息</button>
          <button onClick={()=>setTab('follow')} className={'px-4 py-2 '+(tab==='follow'?'border-b-2 border-blue-500 text-blue-600':'text-gray-500')}>跟进 {follows.length>0&&<span className="ml-1 bg-blue-100 text-blue-600 text-xs px-1 rounded">{follows.length}</span>}</button>
          <button onClick={()=>setTab('quote')} className={'px-4 py-2 '+(tab==='quote'?'border-b-2 border-blue-500 text-blue-600':'text-gray-500')}>报价 {quotes.length>0&&<span className="ml-1 bg-blue-100 text-blue-600 text-xs px-1 rounded">{quotes.length}</span>}</button>
        </div>

        {tab==='info'&&(<>{editMode&&editData?(<div className="space-y-4"><div className="grid grid-cols-2 gap-4"><div><label className="text-sm">名称</label><input value={editData.name} onChange={e=>setEditData({...editData,name:e.target.value})} className="w-full border px-3 py-2 rounded" /></div><div><label className="text-sm">客户</label><input value={editData.customer_name} onChange={e=>setEditData({...editData,customer_name:e.target.value})} className="w-full border px-3 py-2 rounded" /></div><div><label className="text-sm">联系人</label><input value={editData.contact_person||''} onChange={e=>setEditData({...editData,contact_person:e.target.value})} className="w-full border px-3 py-2 rounded" /></div><div><label className="text-sm">金额</label><input type="number" value={editData.amount} onChange={e=>setEditData({...editData,amount:parseInt(e.target.value)||0})} className="w-full border px-3 py-2 rounded" /></div><div><label className="text-sm">阶段</label><select value={editData.stage} onChange={e=>setEditData({...editData,stage:e.target.value})} className="w-full border px-3 py-2 rounded">{STAGES.map(s=><option key={s} value={s}>{s}</option>)}</select></div><div><label className="text-sm">优先级</label><select value={editData.priority||''} onChange={e=>setEditData({...editData,priority:e.target.value})} className="w-full border px-3 py-2 rounded">{PRIORITIES.map(p=><option key={p} value={p}>{p}</option>)}</select></div><div><label className="text-sm">负责人</label><select value={editData.owner_id} onChange={e=>setEditData({...editData,owner_id:e.target.value})} className="w-full border px-3 py-2 rounded">{users.map(u=><option key={u.id} value={u.id}>{u.name}</option>)}</select></div><div className="flex items-center"><label className="text-sm mr-2">已确认:</label><input type="checkbox" checked={editData.is_large_confirmed||false} onChange={e=>setEditData({...editData,is_large_confirmed:e.target.checked})} /></div></div><div><label className="text-sm">描述</label><textarea value={editData.description||''} onChange={e=>setEditData({...editData,description:e.target.value})} className="w-full border px-3 py-2 rounded h-20" /></div></div>):(<div className="grid grid-cols-2 gap-4"><div><div className="text-gray-500 text-sm">客户</div><div className="font-medium">{selected.customer_name}</div></div><div><div className="text-gray-500 text-sm">联系人</div><div className="font-medium">{selected.contact_person}</div></div><div><div className="text-gray-500 text-sm">金额</div><div className="font-medium text-lg text-green-600">{fmt(selected.amount)} {selected.is_large_confirmed&&<span className="text-xs text-red-500">已确认</span>}</div></div><div><div className="text-gray-500 text-sm">优先级</div><div className="font-medium">{selected.priority==='高'?'🔴高':selected.priority==='中'?'🟡中':'🟢低'}</div></div><div><div className="text-gray-500 text-sm">阶段</div><div className="font-medium"><span className={'px-2 py-1 rounded '+clr(selected.stage)}>{selected.stage}</span></div></div><div><div className="text-gray-500 text-sm">负责人</div><div className="font-medium">{getU(selected.owner_id)}</div></div><div className="col-span-2"><div className="text-gray-500 text-sm">描述</div><div className="font-medium">{selected.description||'-'}</div></div></div>)}</>)}

        {tab==='follow'&&(<div>{canCreate&&<button onClick={()=>setShowF(true)} className="mb-4 px-4 py-2 bg-blue-600 text-white rounded">+ 添加</button>}{follows.length===0?<div className="text-center text-gray-500 py-8">暂无</div>:<div className="space-y-3 max-h-64 overflow-auto">{follows.map(f=>(<div key={f.id} className="border rounded p-3"><div className="flex justify-between"><div><span className="bg-blue-100 text-blue-600 px-2 py-0.5 rounded text-xs">{f.method}</span> <span className="text-gray-500 text-sm">{fmtD(f.created_at)}</span></div>{f.next_date&&<span className="text-orange-500 text-sm">下次:{fmtD(f.next_date)}</span>}</div><div className="mt-2">{f.content}</div></div>))}</div>}</div>)}

        {tab==='quote'&&(<div>{canCreate&&<button onClick={()=>setShowQ(true)} className="mb-4 px-4 py-2 bg-blue-600 text-white rounded">+ 添加报价</button>}{quotes.length===0?<div className="text-center text-gray-500 py-8">暂无</div>:<div className="space-y-3 max-h-64 overflow-auto">{quotes.map(q=>(<div key={q.id} className="border rounded p-3"><div className="text-lg font-bold text-green-600">{fmt(q.amount)}</div><div className="text-gray-500 text-sm">{fmtD(q.created_at)}</div>{q.description&&<div className="text-gray-600 mt-1">{q.description}</div>}</div>))}</div>}</div>)}

        <div className="mt-6 flex gap-2 flex-wrap">
          {editMode?(<><button onClick={handleSave} disabled={saving} className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50">{saving?'保存中...':'保存'}</button><button onClick={()=>{setEditMode(false);setEditData(null)}} className="px-4 py-2 bg-gray-200 rounded">取消</button></>):(<><button onClick={()=>{setEditMode(true);setEditData({...selected})}} className="px-4 py-2 bg-blue-600 text-white rounded">编辑</button>{canAll&&<><button onClick={handleDel} className="px-4 py-2 bg-red-600 text-white rounded">删除</button><button onClick={()=>setShowTrans(true)} className="px-4 py-2 bg-green-600 text-white rounded">转派</button></>}<button onClick={()=>setSelected(null)} className="px-4 py-2 bg-gray-200 rounded">关闭</button></>)}
        </div>
      </div></div>)}

      {showTrans&&selected&&(<div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50"><div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full"><h3 className="text-xl font-bold mb-4">转派</h3><div className="mb-4"><label className="text-sm">新负责人</label><select value={transTo} onChange={e=>setTransTo(e.target.value)} className="w-full border px-3 py-2 rounded"><option value="">选择...</option>{users.filter(u=>u.id!==selected.owner_id).map(u=><option key={u.id} value={u.id}>{u.name}</option>)}</select></div><div className="flex gap-2"><button onClick={handleTrans} disabled={!transTo} className="px-4 py-2 bg-green-600 text-white rounded disabled:opacity-50">确认</button><button onClick={()=>{setShowTrans(false);setTransTo('')}} className="px-4 py-2 bg-gray-200 rounded">取消</button></div></div></div>)}

      {showF&&(<div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50"><div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full"><h3 className="text-xl font-bold mb-4">添加跟进</h3><div className="mb-4"><label className="text-sm">方式</label><select value={fData.method} onChange={e=>setFData({...fData,method:e.target.value})} className="w-full border px-3 py-2 rounded">{FOLLOW.map(m=><option key={m} value={m}>{m}</option>)}</select></div><div className="mb-4"><label className="text-sm">内容*</label><textarea value={fData.content} onChange={e=>setFData({...fData,content:e.target.value})} className="w-full border px-3 py-2 rounded h-20" /></div><div className="mb-4"><label className="text-sm">下次跟进</label><input type="date" value={fData.next} onChange={e=>setFData({...fData,next:e.target.value})} className="w-full border px-3 py-2 rounded" /></div><div className="flex gap-2"><button onClick={addFollow} disabled={!fData.content||saving} className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50">{saving?'提交中':'添加'}</button><button onClick={()=>{setShowF(false);setFData({method:'电话',content:'',next:'',file:null})}} className="px-4 py-2 bg-gray-200 rounded">取消</button></div></div></div>)}

      {showQ&&(<div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50"><div className="bg-white rounded-lg shadow-xl p-6 max-w-md w-full"><h3 className="text-xl font-bold mb-4">添加报价</h3><div className="mb-4"><label className="text-sm">金额*</label><input type="number" value={qData.amount} onChange={e=>setQData({...qData,amount:parseInt(e.target.value)||0})} className="w-full border px-3 py-2 rounded" /></div><div className="mb-4"><label className="text-sm">说明</label><textarea value={qData.desc} onChange={e=>setQData({...qData,desc:e.target.value})} className="w-full border px-3 py-2 rounded h-20" /></div><div className="flex gap-2"><button onClick={addQuote} disabled={!qData.amount||saving} className="px-4 py-2 bg-blue-600 text-white rounded disabled:opacity-50">{saving?'提交中':'添加'}</button><button onClick={()=>{setShowQ(false);setQData({amount:0,desc:'',file:null})}} className="px-4 py-2 bg-gray-200 rounded">取消</button></div></div></div>)}
    </div>
  )
}
