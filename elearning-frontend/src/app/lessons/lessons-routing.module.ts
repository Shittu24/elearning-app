import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LessonListComponent } from './lesson-list/lesson-list.component';
import { LessonFormComponent } from './lesson-form/lesson-form.component';
import { LessonDetailComponent } from './lesson-detail/lesson-detail.component';
import { AuthGuard } from '../signup/auth.guard';
import { AdminGuard } from '../signup/admin.guard';

const routes: Routes = [
  { path: 'lessons', component: LessonListComponent, canActivate: [AuthGuard] },  // Route for all lessons
  { path: 'courses/:courseId/lessons', component: LessonListComponent, canActivate: [AuthGuard] },  // Route for lessons within a specific course
  { path: 'lessons/create', component: LessonFormComponent, canActivate: [AdminGuard] },  // Route for creating a lesson (generic)
  { path: 'lessons/create-course/:courseId', component: LessonFormComponent, canActivate: [AdminGuard] },  // Route for creating a lesson with a courseId
  { path: 'lessons/edit/:id', component: LessonFormComponent, canActivate: [AdminGuard] },  // Route for editing a lesson
  { path: 'lessons/:id', component: LessonDetailComponent, canActivate: [AuthGuard] }  // Route for viewing a lesson by ID
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LessonsRoutingModule { }
