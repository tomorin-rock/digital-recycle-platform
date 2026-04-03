<template>
  <div class="admin-products">
    <div class="page-header">
      <h1>产品管理</h1>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索产品型号"
          style="width: 200px; margin-right: 10px"
          clearable
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch">
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
        <el-button @click="handleReset" style="margin-right: 10px">
          <el-icon><Refresh /></el-icon>
          重置
        </el-button>
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加产品
        </el-button>
      </div>
    </div>

    <div class="content-card">
      <el-table :data="products" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="modelName" label="型号" min-width="150" />
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image
              v-if="row.imgUrl"
              :src="row.imgUrl"
              :alt="row.modelName"
              fit="cover"
              style="width: 80px; height: 80px"
            />
            <span v-else>无图片</span>
          </template>
        </el-table-column>
        <el-table-column prop="marketPrice" label="市场价" width="100">
          <template #default="{ row }"> ¥{{ row.marketPrice }} </template>
        </el-table-column>
        <el-table-column prop="recycleBasePrice" label="回收价" width="100">
          <template #default="{ row }"> ¥{{ row.recycleBasePrice }} </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="isNew" label="新品" width="80">
          <template #default="{ row }">
            <el-tag :type="row.isNew ? 'danger' : 'info'" size="small">
              {{ row.isNew ? "是" : "否" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? "上架" : "下架" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 1"
              type="warning"
              text
              size="small"
              @click="changeStatus(row.id, 0)"
            >
              下架
            </el-button>
            <el-button
              v-else
              type="success"
              text
              size="small"
              @click="changeStatus(row.id, 1)"
            >
              上架
            </el-button>
            <el-button
              type="primary"
              text
              size="small"
              @click="editProduct(row)"
            >
              编辑
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        :page-size="pageSize"
        :total="total"
        layout="total, prev, pager, next"
        @current-change="loadProducts"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </div>

    <!-- 添加/编辑产品弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑产品' : '添加产品'"
      width="600px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="型号名称" prop="modelName">
          <el-input v-model="form.modelName" placeholder="请输入型号名称" />
        </el-form-item>

        <el-form-item label="产品图片" prop="imgUrl">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :show-file-list="false"
            :on-success="handleImageUpload"
            :before-upload="beforeUpload"
            :disabled="!editId"
            :headers="uploadHeaders"
            accept="image/*"
          >
            <img
              v-if="form.imgUrl"
              :src="form.imgUrl"
              :alt="form.modelName"
              class="avatar"
            />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
          <div v-if="!editId" class="upload-tip">
            请先保存产品信息，然后再上传图片
          </div>
        </el-form-item>

        <el-form-item label="产品描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入产品描述"
          />
        </el-form-item>

        <el-form-item label="品牌" prop="brandId">
          <el-select
            v-model="form.brandId"
            placeholder="请选择品牌"
            style="width: 100%"
          >
            <el-option
              v-for="brand in brands"
              :key="brand.id"
              :label="brand.name"
              :value="brand.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-cascader
            v-model="categoryCascaderValue"
            :options="cascaderCategories"
            :props="{ expandTrigger: 'click' }"
            placeholder="请选择分类"
            style="width: 100%"
            @change="handleCategoryChange"
          />
        </el-form-item>

        <el-row>
          <el-col :span="12">
            <el-form-item label="市场价" prop="marketPrice">
              <el-input-number
                v-model="form.marketPrice"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="回收价" prop="recycleBasePrice">
              <el-input-number
                v-model="form.recycleBasePrice"
                :min="0"
                :precision="2"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="库存" prop="stock">
              <el-input-number
                v-model="form.stock"
                :min="0"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否新品">
              <el-switch
                v-model="form.isNew"
                :active-value="1"
                :inactive-value="0"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm"
          >确定</el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from "vue";
import { adminProductApi, adminBrandApi, adminCategoryApi } from "@/api/admin";
import type { Product, ProductBrand, ProductCategory } from "@/types";
import { ElMessage } from "element-plus";
import type { FormInstance, FormRules } from "element-plus/es/components/form";
import { Plus, Search, Refresh } from "@element-plus/icons-vue";

const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const editId = ref<number | null>(null);
const uploadUrl = ref("");
const uploadHeaders = ref({});

// 搜索关键词
const searchKeyword = ref("");

// 确保每次都获取最新的token
const updateUploadHeaders = () => {
  uploadHeaders.value = {
    Authorization: `Bearer ${localStorage.getItem("token") || ""}`,
  };
};

// 初始化时更新
updateUploadHeaders();

const products = ref<Product[]>([]);
const total = ref(0);
const pageNum = ref(1);
const pageSize = ref(10);
const formRef = ref<FormInstance>();

// 品牌和分类数据
const brands = ref<ProductBrand[]>([]);
const categories = ref<ProductCategory[]>([]);

// 转换分类数据为级联选择器格式
const cascaderCategories = computed(() => {
  const result: any[] = [];
  // 先找出所有父分类（parentId为null或0）
  const parentCats = categories.value.filter(
    (cat) => cat.parentId === null || cat.parentId === 0,
  );

  // 为每个父分类添加子分类
  parentCats.forEach((parent) => {
    // 查找该父分类下的所有子分类
    const childCats = categories.value
      .filter((child) => child.parentId === parent.id)
      .map((child) => ({
        value: child.id,
        label: child.name,
      }));

    // 无论是否有子分类，都添加父分类
    // 这样即使没有子分类，也能在选择器中看到父分类
    const parentItem = {
      value: parent.id,
      label: parent.name,
      children: childCats,
    };
    result.push(parentItem);
  });

  return result;
});

// 加载品牌列表
const loadBrands = async () => {
  try {
    const res = await adminBrandApi.getList();
    brands.value = res.data?.list || [];
  } catch (error) {
    console.error("Failed to load brands:", error);
  }
};

// 加载分类列表
const loadCategories = async () => {
  try {
    // 加载所有分类，使用较大的pageSize确保获取全部数据
    const res = await adminCategoryApi.getList(1, 100);
    categories.value = res.data?.list || [];
  } catch (error) {
    console.error("Failed to load categories:", error);
  }
};

const form = reactive({
  id: 0,
  modelName: "",
  imgUrl: "",
  description: "",
  marketPrice: 0,
  recycleBasePrice: 0,
  stock: 0,
  isNew: 1,
  categoryId: 0,
  brandId: 0,
  status: 1,
});

// 级联选择器的值
const categoryCascaderValue = ref<number[]>([]);

const rules: FormRules = {
  modelName: [{ required: true, message: "请输入型号名称", trigger: "blur" }],
  marketPrice: [{ required: true, message: "请输入市场价", trigger: "blur" }],
  recycleBasePrice: [
    { required: true, message: "请输入回收价", trigger: "blur" },
  ],
  brandId: [{ required: true, message: "请选择品牌", trigger: "change" }],
  categoryId: [{ required: true, message: "请选择分类", trigger: "change" }],
};

onMounted(async () => {
  await loadProducts();
  await loadBrands();
  await loadCategories();
});

const loadProducts = async () => {
  loading.value = true;
  try {
    const res = await adminProductApi.getList(
      pageNum.value,
      pageSize.value,
      searchKeyword.value || undefined,
    );
    // 后端返回的是分页对象，需要访问list属性获取产品数组
    products.value = res.data?.list || [];
    total.value = res.data?.total || 0;
    pageNum.value = res.data?.pageNum || 1;
    pageSize.value = res.data?.pageSize || 10;
  } catch (error) {
    console.error("Failed to load products:", error);
  } finally {
    loading.value = false;
  }
};

// 处理搜索
const handleSearch = () => {
  pageNum.value = 1; // 搜索时重置页码
  loadProducts();
};

// 处理重置
const handleReset = () => {
  searchKeyword.value = ""; // 清空搜索关键词
  pageNum.value = 1; // 重置页码
  loadProducts(); // 重新加载产品列表
};

const showAddDialog = () => {
  isEdit.value = false;
  editId.value = null;
  uploadUrl.value = "";
  Object.assign(form, {
    id: 0,
    modelName: "",
    imgUrl: "",
    description: "",
    marketPrice: 0,
    recycleBasePrice: 0,
    stock: 0,
    isNew: 1,
    categoryId: 0,
    brandId: 0,
    status: 1,
  });
  categoryCascaderValue.value = [];
  dialogVisible.value = true;
};

const editProduct = (product: Product) => {
  isEdit.value = true;
  editId.value = product.id;
  uploadUrl.value = `/api/admin/products/${editId.value}/image`;

  // 确保分类数据已加载
  if (categories.value.length === 0) {
    // 重新加载分类数据
    loadCategories().then(() => {
      setCategoryValue(product);
    });
  } else {
    setCategoryValue(product);
  }

  Object.assign(form, {
    id: product.id,
    modelName: product.modelName,
    imgUrl: product.imgUrl || "",
    description: product.description,
    marketPrice: product.marketPrice,
    recycleBasePrice: product.recycleBasePrice,
    stock: product.stock,
    isNew: product.isNew,
    categoryId: product.categoryId || 0,
    brandId: product.brandId || 0,
    status: product.status,
  });
  dialogVisible.value = true;
};

// 设置分类值的辅助函数
const setCategoryValue = (product: Product) => {
  // 查找当前分类的父分类
  const currentCategory = categories.value.find(
    (cat) => cat.id === product.categoryId,
  );
  if (currentCategory) {
    const parentCategoryId = currentCategory.parentId || 0;
    // 设置级联选择器的值
    if (parentCategoryId && parentCategoryId !== 0 && product.categoryId) {
      categoryCascaderValue.value = [parentCategoryId, product.categoryId];
    } else if (product.categoryId) {
      // 如果是父分类本身，只设置一个值
      categoryCascaderValue.value = [product.categoryId];
    } else {
      categoryCascaderValue.value = [];
    }
  } else {
    categoryCascaderValue.value = [];
  }
};

const submitForm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      submitting.value = true;
      try {
        if (isEdit.value && editId.value) {
          // 更新产品信息
          await adminProductApi.update(editId.value, form);
          ElMessage.success("产品更新成功");

          const index = products.value.findIndex((p) => p.id === editId.value);
          if (index !== -1) {
            products.value[index] = { ...products.value[index], ...form };
          }
        } else {
          // 添加产品
          const res = await adminProductApi.add(form);
          ElMessage.success("产品添加成功");
          // 获取新创建的产品ID
          if (res.data && typeof res.data === "object") {
            const data = res.data as any;
            if (data.id) {
              editId.value = data.id;
              isEdit.value = true;
              // 设置上传URL
              uploadUrl.value = `/api/admin/products/${editId.value}/image`;
              // 更新上传头信息
              updateUploadHeaders();
              ElMessage.info("请上传产品图片");
            } else {
              // 如果返回的数据格式不符合预期，直接关闭对话框并刷新列表
              dialogVisible.value = false;
              await loadProducts();
            }
          } else {
            // 直接关闭对话框
            dialogVisible.value = false;
            // 重新加载列表以获取最新数据
            await loadProducts();
          }
        }
        if (!editId.value) {
          dialogVisible.value = false;
          await loadProducts();
        }
      } catch (error) {
        console.error("Failed to submit:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};

const changeStatus = async (id: number, status: number) => {
  try {
    await adminProductApi.updateStatus(id, status);
    if(status === 1) {
      ElMessage.success("商品已上架")
    }else if(status === 0) {
      ElMessage.success("商品已下架")
    }
    await loadProducts();
  } catch (error) {
    console.error("Failed to update status:", error);
  }
};

const handleImageUpload = (response: any) => {
  form.imgUrl = response.data.imageUrl;
  ElMessage.success("图片上传成功");
};

// 处理分类变更
const handleCategoryChange = (value: number[]) => {
  if (value && value.length > 0) {
    // 如果有两个值，使用第二个值（子分类）
    // 如果只有一个值，使用第一个值（父分类）
    form.categoryId = value.length === 2 ? value[1] : value[0];
  } else {
    form.categoryId = 0;
  }
};

const beforeUpload = (file: File) => {
  const isJpgOrPng = file.type === "image/jpeg" || file.type === "image/png";
  if (!isJpgOrPng) {
    ElMessage.error("只能上传 JPG/PNG 图片!");
    return false;
  }
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isLt2M) {
    ElMessage.error("图片大小不能超过 2MB!");
    return false;
  }
  return true;
};
</script>

<style scoped>
.admin-products {
  padding: 0;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  color: #303133;
}

.header-actions {
  display: flex;
  align-items: center;
}

.header-actions el-input {
  margin-right: 10px;
}

.content-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  width: 100%;
  max-width: none; /* 移除最大宽度限制 */
}

.avatar-uploader el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: border-color 0.3s;
}

.avatar-uploader el-upload:hover {
  border-color: #409eff;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}
</style>
