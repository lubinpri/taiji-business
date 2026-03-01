"use client";

import Link from 'next/link'

export default function Home() {
  const modules = [
    { name: '工作看板', path: '/dashboard', icon: '📊', desc: '数据可视化与统计' },
    { name: '商机管理', path: '/opportunities', icon: '💼', desc: '销售漏斗与跟进' },
    { name: '投标管理', path: '/bids', icon: '📝', desc: '投标与成本预算' },
    { name: '合同管理', path: '/contracts', icon: '📄', desc: '合同与回款' },
    { name: '项目管理', path: '/projects', icon: '🚀', desc: '进度与工时' },
    { name: '成本统计', path: '/cost', icon: '💰', desc: '成本与利润' },
  ]

  return (
    <div className="min-h-screen bg-gray-100">
      <header className="bg-blue-600 text-white p-6">
        <h1 className="text-3xl font-bold">事业部业务管理系统</h1>
        <p className="mt-2">欢迎使用 - 演示版本</p>
      </header>
      
      <main className="p-8">
        <h2 className="text-xl font-semibold mb-6">功能模块</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {modules.map((mod) => (
            <Link 
              key={mod.path}
              href={mod.path}
              className="block p-6 bg-white rounded-lg shadow hover:shadow-lg transition"
            >
              <div className="text-4xl mb-2">{mod.icon}</div>
              <h3 className="text-lg font-semibold text-gray-800">{mod.name}</h3>
              <p className="text-gray-600 mt-1">{mod.desc}</p>
            </Link>
          ))}
        </div>
      </main>
    </div>
  )
}
