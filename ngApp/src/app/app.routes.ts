import { Routes } from '@angular/router';
import {LoginComponent} from "./pages/login/login.component";

import {RegisterComponent} from "./pages/register/register.component";
import {DashboardComponent} from "./pages/dashboard/dashboard.component";
import {authGuard} from "./services/auth.guard";
import {unauthGuard} from "./services/unauth.guard";
import {SidebarComponent} from "./pages/sidebar/sidebar.component";


export const routes: Routes = [
  {
    path:"login",
    component:LoginComponent,
    title:"Login",
    children:[],
    canActivate:[unauthGuard]
  },
  {
    path:"register",
    component:RegisterComponent,
    title:"Register",

  },
  {
    path:"dashboard",
    component:SidebarComponent,
    title:"Dashboard",
    canActivate:[authGuard]

  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];
