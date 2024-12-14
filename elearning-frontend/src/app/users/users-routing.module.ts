import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { UserListComponent } from './user-list/user-list.component';
import { UserFormComponent } from './user-form/user-form.component';
import { AdminGuard } from '../signup/admin.guard';

const routes: Routes = [
  { path: 'users', component: UserListComponent, canActivate: [AdminGuard] },
  { path: 'users/create', component: UserFormComponent, canActivate: [AdminGuard] },
  { path: 'users/edit/:id', component: UserFormComponent, canActivate: [AdminGuard] },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class UsersRoutingModule { }
