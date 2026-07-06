<template>
  <div class="app-container access-stat-page">
    <div class="access-stat-summary">
      <div class="summary-section">
        <div class="summary-title">总计</div>
        <div class="summary-items">
          <div class="summary-item">
            <span class="summary-label">访问</span>
            <strong v-text="totalStat.visitCount || 0"></strong>
          </div>
          <div class="summary-item">
            <span class="summary-label">登录</span>
            <strong v-text="totalStat.loginCount || 0"></strong>
          </div>
        </div>
      </div>
      <div class="summary-section">
        <div class="summary-title">今日</div>
        <div class="summary-items">
          <div class="summary-item">
            <span class="summary-label">访问</span>
            <strong v-text="todayStat.visitCount || 0"></strong>
          </div>
          <div class="summary-item">
            <span class="summary-label">登录</span>
            <strong v-text="todayStat.loginCount || 0"></strong>
          </div>
        </div>
      </div>
      <el-button class="summary-refresh" size="small" icon="el-icon-refresh" plain @click="getStat">刷新</el-button>
    </div>

    <div class="access-stat-columns">
      <section class="access-stat-panel">
        <div class="panel-title">今日访问记录</div>
        <el-table stripe border v-loading="loading" :data="stat.visits || []">
          <el-table-column label="访问时间" align="center" prop="eventTime" min-width="180" />
          <el-table-column label="IP" align="center" prop="ip" min-width="150" show-overflow-tooltip />
        </el-table>
      </section>

      <section class="access-stat-panel">
        <div class="panel-title">今日登录记录</div>
        <el-table stripe border v-loading="loading" :data="stat.logins || []">
          <el-table-column label="登录时间" align="center" prop="eventTime" min-width="180" />
          <el-table-column label="IP" align="center" prop="ip" min-width="150" show-overflow-tooltip />
          <el-table-column label="结果" align="center" width="90">
            <template slot-scope="scope">
              <el-tag :type="scope.row.success === false ? 'danger' : 'success'" size="mini">
                {{ scope.row.success === false ? '失败' : '成功' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </section>
    </div>
  </div>
</template>

<script>
export default {
  name: 'access-statistics',
  data() {
    return {
      loading: false,
      stat: {
        total: {
          visitCount: 0,
          loginCount: 0
        },
        today: {
          visitCount: 0,
          loginCount: 0
        },
        visits: [],
        logins: []
      },
      dailyRefreshTimer: null
    }
  },
  computed: {
    totalStat() {
      return this.stat.total || {}
    },
    todayStat() {
      return this.stat.today || {}
    }
  },
  mounted() {
    this.getStat()
    this.scheduleDailyRefresh()
  },
  beforeDestroy() {
    if (this.dailyRefreshTimer) {
      clearTimeout(this.dailyRefreshTimer)
    }
  },
  methods: {
    getStat() {
      this.loading = true
      this.$http.get('/api/v1/access-statistics/today').then(res => {
        this.stat = res || {}
      }).finally(() => {
        this.loading = false
      })
    },
    scheduleDailyRefresh() {
      if (this.dailyRefreshTimer) {
        clearTimeout(this.dailyRefreshTimer)
      }
      const now = new Date()
      const next = new Date(now)
      next.setHours(24, 0, 5, 0)
      const delay = Math.max(1000, next.getTime() - now.getTime())
      this.dailyRefreshTimer = setTimeout(() => {
        this.getStat()
        this.scheduleDailyRefresh()
      }, delay)
    }
  }
}
</script>

<style scoped lang="scss">
.access-stat-summary {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr)) auto;
  align-items: stretch;
  gap: 14px;
  margin-bottom: 18px;
  padding: 14px;
  border: 1px solid var(--admin-border);
  border-radius: 18px;
  background: var(--admin-panel);
  box-shadow: 0 16px 44px rgba(31, 45, 61, .07);
}

.summary-section {
  min-width: 0;
}

.summary-title {
  margin-bottom: 10px;
  color: var(--admin-text);
  font-size: 16px;
  font-weight: 900;
}

.summary-items {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.summary-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  min-height: 58px;
  padding: 0 18px;
  border-radius: 14px;
  background: rgba(255, 255, 255, .68);
  border: 1px solid rgba(28, 42, 58, .08);
}

.summary-label {
  color: var(--admin-muted);
  font-weight: 800;
}

.summary-item strong {
  color: var(--admin-primary);
  font-size: 30px;
  line-height: 1;
}

.summary-refresh {
  align-self: end;
  margin-bottom: 1px;
}

.access-stat-columns {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.access-stat-panel {
  min-width: 0;
}

.panel-title {
  margin-bottom: 10px;
  color: var(--admin-text);
  font-size: 18px;
  font-weight: 900;
}

@media (max-width: 900px) {
  .access-stat-summary,
  .access-stat-columns {
    grid-template-columns: 1fr;
  }

  .summary-refresh {
    justify-self: start;
  }
}
</style>
