<template>
  <div class="user-center-page">
    <div class="page-header">
      <h1>个人中心</h1>
      <p>管理您的个人信息</p>
    </div>

    <div class="user-content">
      <!-- 用户信息卡片 -->
      <div class="user-card">
        <div class="avatar-section">
          <el-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :before-upload="beforeAvatarUpload"
          >
            <el-avatar :size="100" :src="userInfo?.avatar">
              {{ userInfo?.nickname?.charAt(0) || "U" }}
            </el-avatar>
            <div class="avatar-overlay">
              <el-icon><Camera /></el-icon>
            </div>
          </el-upload>
          <div class="user-info">
            <h2>{{ userInfo?.nickname }}</h2>
            <p>@{{ userInfo?.username }}</p>
            <el-tag
              :type="userInfo?.role === 'ADMIN' ? 'danger' : 'primary'"
              size="small"
            >
              {{ userInfo?.role === "ADMIN" ? "管理员" : "普通用户" }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 功能区域 -->
      <div class="function-area">
        <el-tabs v-model="activeTab">
          <!-- 基本信息 -->
          <el-tab-pane label="基本信息" name="info">
            <el-form :model="infoForm" label-width="80px" class="info-form">
              <el-form-item label="用户名">
                <el-input :model-value="userInfo?.username" disabled />
              </el-form-item>

              <el-form-item label="昵称">
                <el-input
                  v-model="infoForm.nickname"
                  placeholder="请输入昵称"
                />
              </el-form-item>

              <el-form-item label="余额">
                <el-icon><Money /></el-icon>
                <el-tag>{{ userInfo?.balance || 0 }}</el-tag>
                <el-input-number
                  v-model="infoForm.amount"
                  min="0"
                  max="1000000"
                  step="1"
                />
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  :loading="updatingInfo"
                  @click="updateInfo"
                >
                  保存修改
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 修改密码 -->
          <el-tab-pane label="修改密码" name="password">
            <el-form
              ref="pwdFormRef"
              :model="pwdForm"
              :rules="pwdRules"
              label-width="100px"
              class="pwd-form"
            >
              <el-form-item label="原密码" prop="oldPwd">
                <el-input
                  v-model="pwdForm.oldPwd"
                  type="password"
                  show-password
                  placeholder="请输入原密码"
                />
              </el-form-item>

              <el-form-item label="新密码" prop="newPwd">
                <el-input
                  v-model="pwdForm.newPwd"
                  type="password"
                  show-password
                  placeholder="请输入新密码"
                />
              </el-form-item>

              <el-form-item label="确认新密码" prop="confirmPwd">
                <el-input
                  v-model="pwdForm.confirmPwd"
                  type="password"
                  show-password
                  placeholder="请确认新密码"
                />
              </el-form-item>

              <el-form-item>
                <el-button
                  type="primary"
                  :loading="updatingPwd"
                  @click="updatePassword"
                >
                  修改密码
                </el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>

          <!-- 账号操作 -->
          <el-tab-pane label="账号操作" name="account">
            <div class="account-actions">
              <el-card shadow="never">
                <template #header>
                  <span>退出登录</span>
                </template>
                <p>退出当前登录的账号</p>
                <el-button type="warning" @click="handleLogout"
                  >退出登录</el-button
                >
              </el-card>

              <el-card shadow="never" class="danger-card">
                <template #header>
                  <span style="color: #f56c6c">注销账号</span>
                </template>
                <p>注销后将无法恢复，数据将在30天内清除</p>
                <el-button type="danger" @click="deleteAccount"
                  >注销账号</el-button
                >
              </el-card>
      
            </div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from "vue";
import { useUserStore } from "@/stores/user";
import { userApi } from "@/api/user";
import { ElMessage, ElMessageBox } from "element-plus";
import type { FormInstance, FormRules } from "element-plus/es/components/form";
import type { UploadProps } from "element-plus/es/components/upload";
import { Camera, Money } from "@element-plus/icons-vue";

const userStore = useUserStore();

const activeTab = ref("info");
const updatingInfo = ref(false);
const updatingPwd = ref(false);

const userInfo = computed(() => userStore.userInfo);

const infoForm = reactive({
  nickname: "",
  amount: 0,
});

const pwdFormRef = ref<FormInstance>();
const pwdForm = reactive({
  oldPwd: "",
  newPwd: "",
  confirmPwd: "",
});

const validateConfirmPwd = (rule: any, value: string, callback: any) => {
  if (!value) {
    callback(new Error("请确认新密码"));
  } else if (value !== pwdForm.newPwd) {
    callback(new Error("两次输入的密码不一致"));
  } else {
    callback();
  }
};

const pwdRules: FormRules = {
  oldPwd: [{ required: true, message: "请输入原密码", trigger: "blur" }],
  newPwd: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码长度不能少于6位", trigger: "blur" },
  ],
  confirmPwd: [{ validator: validateConfirmPwd, trigger: "blur" }],
};

onMounted(() => {
  if (userInfo.value) {
    infoForm.nickname = userInfo.value.nickname;
  }
});

const beforeAvatarUpload: UploadProps["beforeUpload"] = async (file) => {
  const isImage = ["image/jpeg", "image/png", "image/gif"].includes(file.type);
  const isLt10M = file.size / 1024 / 1024 < 10;

  if (!isImage) {
    ElMessage.error("只能上传 JPG/PNG/GIF 格式的图片");
    return false;
  }
  if (!isLt10M) {
    ElMessage.error("图片大小不能超过 10MB");
    return false;
  }

  try {
    await userApi.uploadAvatar(file);
    ElMessage.success("头像上传成功");
    await userStore.fetchUserInfo();
  } catch (error) {
    console.error("Failed to upload avatar:", error);
  }

  return false;
};

const updateInfo = async () => {
  updatingInfo.value = true;
  try {
    await userApi.updateInfo({
      nickname: infoForm.nickname,
      amount: infoForm.amount,
    });
    ElMessage.success("信息更新成功");
    await userStore.fetchUserInfo();
  } catch (error) {
    console.error("Failed to update info:", error);
  } finally {
    updatingInfo.value = false;
  }
};

const updatePassword = async () => {
  if (!pwdFormRef.value) return;

  await pwdFormRef.value.validate(async (valid) => {
    if (valid) {
      updatingPwd.value = true;
      try {
        await userApi.updatePassword({
          oldPwd: pwdForm.oldPwd,
          newPwd: pwdForm.newPwd,
          confirmPwd: pwdForm.confirmPwd,
        });
        ElMessage.success("密码修改成功，请重新登录");
        await userStore.logout();
      } catch (error) {
        console.error("Failed to update password:", error);
      } finally {
        updatingPwd.value = false;
      }
    }
  });
};

const handleLogout = () => {
  userStore.logout();
};

const deleteAccount = async () => {
  try {
    await ElMessageBox.confirm(
      "注销后账号将无法恢复，数据将在30天内清除。确定要注销吗？",
      "警告",
      {
        confirmButtonText: "确定注销",
        cancelButtonText: "取消",
        type: "warning",
        confirmButtonClass: "el-button--danger",
      },
    );

    await userApi.deleteAccount();
    ElMessage.success("账号已注销");
    await userStore.logout();
  } catch (error) {
    if (error !== "cancel") {
      console.error("Failed to delete account:", error);
    }
  }
};
</script>

<style scoped>
.user-center-page {
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

.user-content {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
}

.user-card {
  padding: 30px;
  border-bottom: 1px solid #e4e7ed;
}

.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
}

.avatar-uploader {
  position: relative;
  cursor: pointer;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay el-icon {
  font-size: 24px;
  color: #fff;
}

.user-info h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 4px;
}

.user-info p {
  color: #909399;
  margin-bottom: 8px;
}

.function-area {
  padding: 20px 30px;
}

.info-form,
.pwd-form {
  max-width: 400px;
}

.account-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.danger-card {
  border: 1px solid #fde2e2;
}
</style>
