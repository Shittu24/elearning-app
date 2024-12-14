import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CourseListComponent } from './course-list/course-list.component';
import { CourseDetailComponent } from './course-detail/course-detail.component';
import { CourseFormComponent } from './course-form/course-form.component';
import { AdminGuard } from '../signup/admin.guard';
import { AuthGuard } from '../signup/auth.guard';

const routes: Routes = [
  { path: 'courses', component: CourseListComponent, canActivate: [AuthGuard] },
  { path: 'courses/create', component: CourseFormComponent, canActivate: [AdminGuard] },
  { path: 'courses/edit/:id', component: CourseFormComponent, canActivate: [AdminGuard] },
  { path: 'courses/:id', component: CourseDetailComponent, canActivate: [AuthGuard] }  // Course detail page with courseId
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class CoursesRoutingModule { }
