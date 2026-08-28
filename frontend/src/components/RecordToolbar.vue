<!-- Copyright 2026 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ -->
<script setup>
import { reactive } from 'vue'
import { api } from '../api'

const props = defineProps({ module: { type: String, required: true } })
const emit = defineEmits(['loaded'])
const filters = reactive({ keyword: '', status: '', riskLevel: '', overdue: false })
let busy = false

function params() {
  return { module: props.module, keyword: filters.keyword, status: filters.status, riskLevel: filters.riskLevel, overdue: filters.overdue || '' }
}
async function search() {
  if (busy) return
  busy = true
  try { emit('loaded', (await api.search(params())).items) }
  finally { busy = false }
}
async function reset() {
  Object.assign(filters, { keyword: '', status: '', riskLevel: '', overdue: false })
  await search()
}
async function download() { await api.exportRecords(params()) }
</script>

<template>
  <section class="toolbar">
    <label class="keyword"><span>快速检索</span><input v-model="filters.keyword" placeholder="编号、事项、对象、责任人或说明" @keyup.enter="search"></label>
    <label><span>状态</span><input v-model="filters.status" placeholder="全部状态" @keyup.enter="search"></label>
    <label><span>风险</span><select v-model="filters.riskLevel"><option value="">全部风险</option><option>正常</option><option>关注</option><option>高风险</option></select></label>
    <label class="check"><input v-model="filters.overdue" type="checkbox">仅看逾期</label>
    <div class="tools"><button @click="search">查询</button><button class="plain" @click="reset">重置</button><button class="plain" @click="download">导出 CSV</button></div>
  </section>
</template>

<style scoped>
.toolbar{display:grid;grid-template-columns:minmax(240px,1.6fr) minmax(130px,.7fr) minmax(130px,.7fr) auto auto;gap:14px;align-items:end;margin:0 0 14px;padding:18px 20px;background:#fff;border:1px solid #e2e8eb;border-radius:12px}.toolbar label{display:flex;flex-direction:column;gap:7px;font-size:12px;color:#697981}.toolbar input,.toolbar select{box-sizing:border-box;width:100%;height:39px;border:1px solid #ccd6db;border-radius:7px;padding:0 10px;background:#fbfcfd}.toolbar .check{flex-direction:row;align-items:center;height:39px;white-space:nowrap}.toolbar .check input{width:16px;height:16px}.tools{display:flex;gap:8px}.tools button{height:39px;border:0;border-radius:7px;padding:0 14px;background:#234f66;color:#fff;white-space:nowrap;cursor:pointer}.tools button.plain{border:1px solid #cbd5da;background:#fff;color:#45545c}@media(max-width:1100px){.toolbar{grid-template-columns:1fr 1fr}.tools,.keyword{grid-column:1/-1}}@media(max-width:640px){.toolbar{grid-template-columns:1fr}.tools,.keyword{grid-column:auto;flex-wrap:wrap}}
</style>
