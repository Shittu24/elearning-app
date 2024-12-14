import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { EnrollmentsRoutingModule } from './enrollments-routing.module';
import { EnrollmentListComponent } from './enrollment-list/enrollment-list.component';
// import { EnrollmentFormComponent } from './enrollment-form/enrollment-form.component';
import { MatTableModule } from '@angular/material/table';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';

@NgModule({
  declarations: [
    EnrollmentListComponent
  ],
  imports: [
    CommonModule,
    EnrollmentsRoutingModule,
    MatTableModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSelectModule,
    MatCheckboxModule,
    ReactiveFormsModule
  ]
})
export class EnrollmentsModule { }
