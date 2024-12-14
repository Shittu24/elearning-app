import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CourseService } from '../course.service';
import { AuthService } from 'src/app/signup/auth.service';

@Component({
  selector: 'app-course-form',
  templateUrl: './course-form.component.html',
  styleUrls: ['./course-form.component.css']
})
export class CourseFormComponent implements OnInit {
  courseForm: FormGroup;
  isEditMode = false;
  courseId: number | null = null;
  selectedFile: File | null = null;
  existingImagePath: string | undefined = undefined;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    public router: Router,
    private route: ActivatedRoute,
    private authService: AuthService
  ) {
    this.courseForm = this.fb.group({
      title: ['', Validators.required],
      description: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    if (!this.authService.isAdmin()) {
      this.router.navigate(['/courses']);
      return;
    }

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.courseId = +id;
      this.courseService.getCourseById(this.courseId).subscribe(data => {
        this.existingImagePath = data.imagePath; // Load the existing image path if any
        this.courseForm.patchValue({
          title: data.title,
          description: data.description
        });
      });
    }
  }

  onFileSelected(event: Event): void {
    const file = (event.target as HTMLInputElement).files?.[0];
    if (file) {
      this.selectedFile = file;

      const reader = new FileReader();
      reader.onload = () => {
        const imagePreview = reader.result as string;
        // Display the image preview on the UI
      };
      reader.readAsDataURL(this.selectedFile);
    }
  }

  onSubmit(): void {
    if (this.courseForm.valid) {
      const formData = new FormData();
      formData.append('title', this.courseForm.get('title')?.value);
      formData.append('description', this.courseForm.get('description')?.value);

      if (this.selectedFile) {
        formData.append('image', this.selectedFile);
      }

      if (this.isEditMode && this.courseId) {
        this.courseService.updateCourse(this.courseId, formData).subscribe({
          next: () => this.router.navigate(['/courses']),
          error: (err) => console.error('Update error: ', err)
        });
      } else {
        this.courseService.createCourse(formData).subscribe({
          next: () => this.router.navigate(['/courses']),
          error: (err) => console.error('Create error: ', err)
        });
      }
    }
  }
}