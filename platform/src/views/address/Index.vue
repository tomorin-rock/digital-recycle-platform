<template>
  <div class="address-page">
    <div class="page-header">
      <h1>地址管理</h1>
      <p>管理您的收货地址</p>
    </div>

    <div class="address-content">
      <div class="action-bar">
        <el-button type="primary" @click="showAddDialog">
          <el-icon>
            <Plus/>
          </el-icon>
          添加地址
        </el-button>
      </div>

      <div class="address-list" v-loading="loading">
        <el-empty
            v-if="addresses.length === 0 && !loading"
            description="暂无地址"
        >
          <el-button type="primary" @click="showAddDialog">添加地址</el-button>
        </el-empty>

        <div v-else class="address-cards">
          <div
              v-for="address in addresses"
              :key="address.id"
              class="address-card"
          >
            <div class="address-main">
              <div class="receiver">
                <span class="name">{{ address.receiverName }}</span>
                <span class="phone">{{ address.receiverPhone }}</span>
                <el-tag v-if="address.isDefault" type="success" size="small"
                >默认
                </el-tag
                >
              </div>
              <div class="detail">
                {{ address.province }}{{ address.city }}{{
                  address.district
                }}{{ address.detail }}
              </div>
            </div>
            <div class="address-actions">
              <el-button
                  v-if="!address.isDefault"
                  type="primary"
                  text
                  size="small"
                  @click="setDefault(address.id)"
              >
                设为默认
              </el-button>
              <el-button
                  type="primary"
                  text
                  size="small"
                  @click="editAddress(address)"
              >
                编辑
              </el-button>
              <el-button
                  type="danger"
                  text
                  size="small"
                  @click="deleteAddress(address.id)"
              >
                删除
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加/编辑地址弹窗 -->
    <el-dialog
        v-model="dialogVisible"
        :title="isEdit ? '编辑地址' : '添加地址'"
        width="500px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="receiverName">
          <el-input
              v-model="form.receiverName"
              placeholder="请输入收货人姓名"
          />
        </el-form-item>

        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号"/>
        </el-form-item>

        <el-form-item label="所在地区" prop="region">
          <elui-china-area-dht placeholder="请选择省市区" v-model="form.region" style="width: 100%"/>
        </el-form-item>

        <el-form-item label="详细地址" prop="detail">
          <el-input
              v-model="form.detail"
              type="textarea"
              :rows="3"
              placeholder="请输入详细地址"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="submitForm"
        >确定
        </el-button
        >
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import {ref, reactive, onMounted} from "vue";
import {addressApi} from "@/api/user";
import type {Address, AddressDTO} from "@/types";
import {ElMessage, ElMessageBox} from "element-plus";
import type {FormInstance, FormRules} from "element-plus/es/components/form";
import {Plus} from "@element-plus/icons-vue";
import EluiChinaAreaDht from "elui-china-area-dht";
import ChinaArea from "elui-china-area-dht/lib/EluiChinaAreaDht/src/ChinaArea";


const loading = ref(false);
const submitting = ref(false);
const dialogVisible = ref(false);
const isEdit = ref(false);
const editId = ref<number | null>(null);

const addresses = ref<Address[]>([]);
const formRef = ref<FormInstance>();

// 创建 ChinaArea 实例用于数据转换
const chinaArea = new ChinaArea();

const form = reactive<AddressDTO & { region?: string[] }>({
  receiverName: "",
  receiverPhone: "",
  province: "",
  city: "",
  district: "",
  region: [],
  detail: "",
});

const rules: FormRules = {
  receiverName: [
    {required: true, message: "请输入收货人姓名", trigger: "blur"},
  ],
  receiverPhone: [
    {required: true, message: "请输入手机号", trigger: "blur"},
    {
      pattern: /^1[3-9]\d{9}$/,
      message: "请输入正确的手机号",
      trigger: "blur",
    },
  ],
  region: [{required: true, message: "请选择省市区", trigger: "change"}],
  detail: [{required: true, message: "请输入详细地址", trigger: "blur"}],
};


onMounted(async () => {
  await loadAddresses();
});

const loadAddresses = async () => {
  loading.value = true;
  try {
    const res = await addressApi.getList();
    addresses.value = res.data;
  } catch (error) {
    console.error("Failed to load addresses:", error);
  } finally {
    loading.value = false;
  }
};

const showAddDialog = () => {
  isEdit.value = false;
  editId.value = null;
  resetForm();
  dialogVisible.value = true;
};

const editAddress = (address: Address) => {
  isEdit.value = true;
  editId.value = address.id;

  // 查找省市区对应的编码
  const regionCodes = findRegionCodes(address.province, address.city, address.district);

  Object.assign(form, {
    receiverName: address.receiverName,
    receiverPhone: address.receiverPhone,
    province: address.province,
    city: address.city,
    district: address.district,
    region: regionCodes,
    detail: address.detail,
  });
  dialogVisible.value = true;
};

// 根据省市区名称查找对应的编码
const findRegionCodes = (province: string, city: string, district: string): string[] => {
  const codes: string[] = [];

  // 查找省份编码
  for (const code in chinaArea.chinaAreaflat) {
    if (chinaArea.chinaAreaflat[code].label === province && !chinaArea.chinaAreaflat[code].parent) {
      codes.push(code);
      break;
    }
  }

  // 查找城市编码
  if (codes.length > 0) {
    for (const code in chinaArea.chinaAreaflat) {
      if (chinaArea.chinaAreaflat[code].label === city && chinaArea.chinaAreaflat[code].parent === codes[0]) {
        codes.push(code);
        break;
      }
    }
  }

  // 查找区县编码
  if (codes.length > 1) {
    for (const code in chinaArea.chinaAreaflat) {
      if (chinaArea.chinaAreaflat[code].label === district && chinaArea.chinaAreaflat[code].parent === codes[1]) {
        codes.push(code);
        break;
      }
    }
  }

  return codes;
};

const resetForm = () => {
  Object.assign(form, {
    receiverName: "",
    receiverPhone: "",
    province: "",
    city: "",
    district: "",
    region: [],
    detail: "",
  });
  formRef.value?.resetFields();
};

const submitForm = async () => {
  if (!formRef.value) return;

  await formRef.value.validate(async (valid: boolean) => {
    if (valid) {
      if (form.region && form.region.length === 3) {
        form.province = chinaArea.chinaAreaflat[form.region[0]]?.label || "";
        form.city = chinaArea.chinaAreaflat[form.region[1]]?.label || "";
        form.district = chinaArea.chinaAreaflat[form.region[2]]?.label || "";
      }

      submitting.value = true;
      try {
        if (isEdit.value && editId.value) {
          const updateData = { ...form, id: editId.value };
          await addressApi.update(editId.value, updateData);
          ElMessage.success("地址更新成功");
        } else {
          await addressApi.add(form);
          ElMessage.success("地址添加成功");
        }
        dialogVisible.value = false;
        await loadAddresses();
      } catch (error) {
        console.error("Failed to submit:", error);
      } finally {
        submitting.value = false;
      }
    }
  });
};


const setDefault = async (id: number) => {
  try {
    await addressApi.setDefault(id);
    ElMessage.success("默认地址设置成功");
    await loadAddresses();
  } catch (error) {
    console.error("Failed to set default:", error);
  }
};

const deleteAddress = async (id: number) => {
  try {
    await ElMessageBox.confirm("确定要删除该地址吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });

    await addressApi.delete(id);
    ElMessage.success("地址删除成功");
    await loadAddresses();
  } catch (error) {
    if (error !== "cancel") {
      console.error("Failed to delete:", error);
    }
  }
};
</script>

<style scoped>
.address-page {
  max-width: 900px;
  margin: 0 auto;
}

.page-header {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  border-radius: 12px;
  padding: 40px;
  color: #fff;
  text-align: center;
  margin-bottom: 24px;
}

.page-header h1 {
  font-size: 28px;
  margin-bottom: 8px;
}

.address-content {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
}

.action-bar {
  margin-bottom: 20px;
}

.address-card {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  margin-bottom: 16px;
  transition: all 0.3s;
}

.address-card:hover {
  border-color: #409eff;
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.1);
}

.address-card:last-child {
  margin-bottom: 0;
}

.receiver {
  margin-bottom: 8px;
}

.receiver .name {
  font-size: 16px;
  font-weight: 500;
  margin-right: 12px;
}

.receiver .phone {
  color: #606266;
  margin-right: 12px;
}

.detail {
  color: #909399;
  font-size: 14px;
}

.address-actions {
  display: flex;
  gap: 8px;
}
</style>