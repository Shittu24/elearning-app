import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { LessonsRoutingModule } from './lessons-routing.module';
import { LessonListComponent } from './lesson-list/lesson-list.component';
import { LessonFormComponent } from './lesson-form/lesson-form.component';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { LessonDetailComponent } from './lesson-detail/lesson-detail.component';
import { SafeUrlPipe } from '../pipes/safe-url.pipe';
import { QuillModule } from 'ngx-quill';

@NgModule({
  declarations: [
    LessonListComponent,
    LessonFormComponent,
    LessonDetailComponent,
    SafeUrlPipe
  ],
  imports: [
    CommonModule,
    LessonsRoutingModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    ReactiveFormsModule,
    QuillModule.forRoot()  
  ]
})
export class LessonsModule { }
