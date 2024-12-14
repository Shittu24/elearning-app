import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { EnrollmentListComponent } from './enrollment-list/enrollment-list.component';
import { AuthGuard } from '../signup/auth.guard';

const routes: Routes = [
  { path: 'enrollments', component: EnrollmentListComponent, canActivate: [AuthGuard] },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class EnrollmentsRoutingModule { }
