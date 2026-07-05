<template>
  <div class="app-container recycle-container">
    <el-row type="flex" justify="space-between" class="recycle-toolbar">
      <div class="recycle-left">
        <el-radio-group v-model="activeType" size="small" @change="switchType">
          <el-radio-button label="category">分类</el-radio-button>
          <el-radio-button label="card">卡片</el-radio-button>
        </el-radio-group>
        <el-form ref="queryForm" :model="queryParam"
                 @submit.native.prevent
                 :inline="true" label-width="0">
          <el-form-item>
            <el-input :placeholder="activeType === 'category' ? '请输入分类名称' : '请输入卡片标题或内容'"
                      style="width: 340px"
                      v-model="queryParam.keywords"
                      v-trim
                      clearable
                      @clear="getList"
                      @keyup.enter.native="getList">
              <el-button slot="append" icon="el-icon-search" @click="getList"></el-button>
            </el-input>
          </el-form-item>
        </el-form>
      </div>
      <el-row :gutter="10" class="mb8 mt5">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            size="small"
            icon="el-icon-circle-plus-outline"
            v-btn-multiple="selections"
            @click="handleRecover()"
          >还原
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="danger"
            plain
            size="small"
            icon="el-icon-delete"
            v-btn-multiple="selections"
            @click="handleDelete()"
          >永久删除
          </el-button>
        </el-col>
      </el-row>
    </el-row>
    <el-table
      v-if="activeType === 'category'"
      stripe
      border
      ref="categoryTable"
      v-loading="loading"
      @selection-change="selections = $event"
      :data="table.dataList">
      <el-table-column label="序号" type="index" width="55" align="center" :index="table.index"/>
      <el-table-column type="selection" align="center" width="55"></el-table-column>
      <el-table-column label="分类" prop="name" show-overflow-tooltip/>
      <el-table-column prop="icon" label="图标" align="center" width="100">
        <template slot-scope="scope">
          <svg-icon v-if="scope.row.icon" :icon-class="scope.row.icon"/>
        </template>
      </el-table-column>
      <el-table-column label="卡片数" align="center" prop="cardCount" show-overflow-tooltip/>
      <el-table-column label="可查看角色（不配置则任何人都可查看）" align="center" prop="roles">
        <template slot-scope="scope">
          <span>{{ scope.row.roles && scope.row.roles.map(item => (item.roleName)).join(',') }}</span>
        </template>
        <template slot="header">
          <span>
             <el-tooltip content='不设置则为公开分类，不登录也可以看到，设置后需要登录且拥有对应角色才能看到' placement="top">
                <i class="el-icon-question"></i>
             </el-tooltip>
             所属角色
          </span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-plus-outline"
            @click="handleRecover(scope.row)"
          >还原
          </el-button>
          <el-button
            slot="reference"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >永久删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-table
      v-else
      stripe
      border
      ref="cardTable"
      v-loading="loading"
      @selection-change="selections = $event"
      :data="table.dataList">
      <el-table-column label="序号" type="index" width="55" align="center" :index="table.index"/>
      <el-table-column type="selection" width="50" align="center"/>
      <el-table-column label="分类" align="center" prop="categoryName" show-overflow-tooltip/>
      <el-table-column label="图标" align="center" width="80" prop="icon" class-name="narrow-padding">
        <template slot-scope="scope">
          <ivu-avatar v-if="scope.row.icon && scope.row.icon.src"
                      :src="scope.row.icon.src"
                      style="vertical-align: middle"></ivu-avatar>
          <ivu-avatar v-else-if="scope.row.icon"
                      style="vertical-align: middle"
                      :style="{background: scope.row.icon.color}">{{ scope.row.icon.text }}
          </ivu-avatar>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" show-overflow-tooltip/>
      <el-table-column label="内容" align="center" prop="content" show-overflow-tooltip/>
      <el-table-column label="链接" align="center" prop="url" show-overflow-tooltip>
        <template slot-scope="scope">
          <span class="recycle-card-url" :title="scope.row.url">{{ scope.row.url }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-plus-outline"
            @click="handleRecover(scope.row)"
          >还原
          </el-button>
          <el-button
            slot="reference"
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >永久删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination
      v-show="table.total>0"
      :total="table.total"
      :page.sync="queryParam.pageIndex"
      :limit.sync="queryParam.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
export default {
  name: "category-invalid",
  data() {
    return {
      activeType: 'category',
      loading: true,
      selections: [],
      queryParam: {
        pageIndex: 1,
        pageSize: 10,
        keywords: ''
      },
      table: {
        total: 0,
        index: 1,
        dataList: []
      }
    };
  },
  mounted() {
    this.getList();
  },
  methods: {
    switchType() {
      this.selections = [];
      this.queryParam.pageIndex = 1;
      this.queryParam.keywords = '';
      this.table = {
        total: 0,
        index: 1,
        dataList: []
      };
      this.getList();
    },
    getList() {
      return this.activeType === 'category' ? this.getCategoryList() : this.getCardList();
    },
    getCategoryList() {
      this.loading = true;
      this.$http.get(`/api/v1/category/page`, {params: this.queryParam})
        .then(res => {
          this.table.dataList = res.content;
          this.table.total = res.totalElements;
          this.table.index = res.pageable.offset + 1;
        }).finally(() => {
          this.loading = false;
        });
    },
    getCardList() {
      this.loading = true;
      this.$http.get(`/api/v1/card/recycle/page`, {params: this.queryParam})
        .then(res => {
          this.table.dataList = res.content;
          this.table.total = res.totalElements;
          this.table.index = res.pageable.offset + 1;
        }).finally(() => {
          this.loading = false;
        });
    },
    handleRecover(row) {
      const ids = row ? [row.id] : this.selections.map(item => item.id);
      const url = this.activeType === 'category' ? `/api/v1/category/valid/true` : `/api/v1/card/valid/true`;
      this.$http.patch(url, ids)
        .then(() => {
          this.$modal.msgSuccess('已还原');
          this.queryParam.pageIndex = 1;
          this.getList();
        });
    },
    handleDelete(row) {
      const ids = row ? row.id : this.selections.map(item => item.id).join(',');
      const typeName = this.activeType === 'category' ? '分类' : '卡片';
      const url = this.activeType === 'category' ? `/api/v1/category/${ids}` : `/api/v1/card/${ids}/actions/permanent`;
      this.$modal.confirm(`将永久删除选中的${typeName}，是否继续？`).then(() => {
        return this.$http.delete(url);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      });
    },
  }
}
</script>

<style scoped lang="scss">
.recycle-left {
  display: flex;
  align-items: center;
  gap: 14px;
}

.recycle-left ::v-deep .el-form-item {
  margin-bottom: 0;
}

.recycle-toolbar ::v-deep .el-radio-button__inner {
  height: 38px;
  padding: 0 18px;
  border-color: rgba(28, 42, 58, .12);
  background: rgba(255, 255, 255, .72);
  color: #526071;
  line-height: 38px;
  font-weight: 800;
}

.recycle-toolbar ::v-deep .el-radio-button__orig-radio:checked + .el-radio-button__inner {
  border-color: rgba(79, 124, 255, .76);
  background: linear-gradient(135deg, #4f7cff, #49a7e9);
  box-shadow: 0 10px 22px rgba(79, 124, 255, .18);
  color: #fff;
}

.recycle-card-url {
  display: block;
  max-width: 100%;
  overflow: hidden;
  color: #526071;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
