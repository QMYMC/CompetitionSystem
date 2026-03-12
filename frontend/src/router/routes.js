import {
  Bell,
  DataAnalysis,
  Document,
  DocumentChecked,
  HomeFilled,
  Key,
  Reading,
  User,
  UserFilled,
  WarningFilled,
} from '@element-plus/icons-vue'
import AwardReviewView from '@/views/AwardReviewView.vue'
import CompetitionManagementView from '@/views/CompetitionManagementView.vue'
import DashboardView from '@/views/DashboardView.vue'
import LoginView from '@/views/LoginView.vue'
import ModulePlaceholderView from '@/views/ModulePlaceholderView.vue'
import NoticeCenterView from '@/views/NoticeCenterView.vue'
import NotFoundView from '@/views/NotFoundView.vue'
import RegistrationReviewView from '@/views/RegistrationReviewView.vue'
import StatisticsView from '@/views/StatisticsView.vue'
import StudentAwardView from '@/views/StudentAwardView.vue'
import StudentRegistrationView from '@/views/StudentRegistrationView.vue'
import StudentTeamView from '@/views/StudentTeamView.vue'
import UnauthorizedView from '@/views/UnauthorizedView.vue'
import UserManagementView from '@/views/UserManagementView.vue'

export const layoutChildrenRoutes = [
  {
    path: 'dashboard',
    name: 'dashboard',
    component: DashboardView,
    meta: {
      title: '系统首页',
      requiresAuth: true,
      menu: true,
      icon: HomeFilled,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: '展示当前账号信息、角色权限和系统功能概览。',
    },
  },
  {
    path: 'users',
    name: 'users',
    component: UserManagementView,
    meta: {
      title: '用户管理',
      requiresAuth: true,
      menu: true,
      icon: User,
      roles: ['ADMIN'],
      description: '维护系统用户、角色归属和基础资料。',
    },
  },
  {
    path: 'competitions',
    name: 'competitions',
    component: CompetitionManagementView,
    meta: {
      title: '竞赛管理',
      requiresAuth: true,
      menu: true,
      icon: Document,
      roles: ['ADMIN'],
      description: '维护竞赛分类、级别、报名时间和赛制。',
    },
  },
  {
    path: 'reviews',
    name: 'reviews',
    component: RegistrationReviewView,
    meta: {
      title: '报名审核',
      requiresAuth: true,
      menu: true,
      icon: DocumentChecked,
      roles: ['ADMIN', 'COLLEGE_AUDITOR'],
      description: '查看报名记录，填写院级审核意见，并执行通过或驳回。',
    },
  },
  {
    path: 'awards/review',
    name: 'award-review',
    component: AwardReviewView,
    meta: {
      title: '获奖审核',
      requiresAuth: true,
      menu: true,
      icon: DocumentChecked,
      roles: ['ADMIN', 'COLLEGE_AUDITOR'],
      description: '审核学生提交的获奖信息，并记录院级审核意见。',
    },
  },
  {
    path: 'student/registrations',
    name: 'student-registrations',
    component: StudentRegistrationView,
    meta: {
      title: '学生报名',
      requiresAuth: true,
      menu: true,
      icon: Reading,
      roles: ['STUDENT'],
      description: '学生查看开放竞赛，完成个人赛报名并跟踪审核状态。',
    },
  },
  {
    path: 'student/teams',
    name: 'student-teams',
    component: StudentTeamView,
    meta: {
      title: '团队管理',
      requiresAuth: true,
      menu: true,
      icon: UserFilled,
      roles: ['STUDENT'],
      description: '创建团队、维护成员与指导教师，并提交院级审核。',
    },
  },
  {
    path: 'student/awards',
    name: 'student-awards',
    component: StudentAwardView,
    meta: {
      title: '获奖填报',
      requiresAuth: true,
      menu: true,
      icon: Document,
      roles: ['STUDENT'],
      description: '学生填报获奖信息，查看院级审核状态和审核意见。',
    },
  },
  {
    path: 'notices',
    name: 'notices',
    component: NoticeCenterView,
    meta: {
      title: '公告中心',
      requiresAuth: true,
      menu: true,
      icon: Bell,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: '管理员可发布和维护公告，其他角色可查看已发布公告。',
    },
  },
  {
    path: 'stats',
    name: 'stats',
    component: StatisticsView,
    meta: {
      title: '统计分析',
      requiresAuth: true,
      menu: true,
      icon: DataAnalysis,
      roles: ['ADMIN', 'COLLEGE_AUDITOR'],
      description: '展示竞赛、报名、获奖和学院维度统计，并以图表方式呈现。',
    },
  },
  {
    path: 'profile',
    name: 'profile',
    component: ModulePlaceholderView,
    meta: {
      title: '个人信息',
      requiresAuth: true,
      menu: true,
      icon: Key,
      roles: ['ADMIN', 'TEACHER', 'STUDENT', 'COLLEGE_AUDITOR'],
      description: '查看当前登录用户的基础资料和角色身份。',
    },
  },
]

export const baseRoutes = [
  {
    path: '/login',
    name: 'login',
    component: LoginView,
    meta: {
      title: '登录',
      guestOnly: true,
    },
  },
  {
    path: '/403',
    name: 'unauthorized',
    component: UnauthorizedView,
    meta: {
      title: '无权限访问',
      requiresAuth: true,
      icon: WarningFilled,
    },
  },
  {
    path: '/:pathMatch(.*)*',
    name: 'not-found',
    component: NotFoundView,
    meta: {
      title: '页面不存在',
    },
  },
]

export function hasPermission(route, roles = []) {
  const routeRoles = route.meta?.roles
  if (!routeRoles?.length) {
    return true
  }
  return routeRoles.some((role) => roles.includes(role))
}

export function filterMenus(routes, roles = []) {
  return routes.filter((route) => route.meta?.menu && hasPermission(route, roles))
}
